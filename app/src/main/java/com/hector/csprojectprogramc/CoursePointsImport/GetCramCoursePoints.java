package com.hector.csprojectprogramc.CoursePointsImport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCoursePointsToDatabase;
import com.hector.csprojectprogramc.FlashcardToSentenceModelUtilities.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

class GetCramCoursePoints implements CoursePointsImporter{

    private static int timeout = 10000;

    public void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener){
        GetRelatedFlashcards getRelatedFlashcards = new GetRelatedFlashcards(context, course, listener, errorListener);
        getRelatedFlashcards.execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());

    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>> {

        private ProgressDialog progressDialog;
        private WeakReference<Context> context;
        private Course course;
        private AsyncTaskCompleteListener<Void> onCompleteListener;
        private AsyncTaskErrorListener errorListener;


        private GetRelatedFlashcards(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener, AsyncTaskErrorListener errorListener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
            this.errorListener = errorListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context.get());
            progressDialog.setTitle(context.get().getString(R.string.get_cram_courses));
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> relatedWebsites = new ArrayList<>();
            for ( String string: strings) {

                String url = "http://www.cram.com/search?query="+ GeneralStringUtilities.convertSpacesToPluses(string)+"&search_in%5B%5D=title&search_in%5B%5D=body&search_in%5B%5D=subject&search_in%5B%5D=username&image_filter=exclude_imgs&period=any";
                try {
                    Log.i("Overall Cram Website",url);
                    Document overallCramWebsite = Jsoup.connect(url).timeout(timeout).get();
                    Elements section = overallCramWebsite.select("div[id=searchResults]").select("a[href]");

                    for (Element element:section ) {
                        String website = element.attr("href");
                        if (website.length()>12){
                            if(website.substring(0,12).equals("/flashcards/")){

                                relatedWebsites.add(website);
                                if (relatedWebsites.size()==5){
                                    return null;
                                }
                            }
                        }
                    }

                }catch (SocketTimeoutException e) {
                    AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());
                    timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);
                    timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));
                    timeoutAlertDialogBuilder.setCancelable(false);
                    timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timeout *= 2;
                            doInBackground();
                        }
                    });
                    timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            errorListener.onAsyncTaskError();
                        }
                    });
                    timeoutAlertDialogBuilder.create().show();
                }catch (IOException exception){
                    Log.w(context.get().getString(R.string.issue_with_getting_cram_courses),exception.getMessage());
                }

            }
            return relatedWebsites;
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            progressDialog.dismiss();
            new GetFlashcardsFromRelatedCramCourses(context.get(), course, onCompleteListener).execute(relatedWebsites.toArray(new String[0]));
        }
    }

    private static class GetFlashcardsFromRelatedCramCourses extends AsyncTask<String,Void,Void>{

        private ProgressDialog progressDialog;
        private WeakReference<Context> context;
        private Course course;
        private AsyncTaskCompleteListener<Void> onCompleteListener;

        private GetFlashcardsFromRelatedCramCourses(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context.get());
            progressDialog.setTitle(context.get().getString(R.string.get_information_from_cram_courses));
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {

            ArrayList<CoursePoint> coursePoints = new ArrayList<>();
            boolean foundCard = false;

            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("http://www.cram.com"+url).timeout(timeout).get();
                    Elements FlashCardSection = courseWebsite.select("table[class=flashCardsListingTable]").select("tr");
                    for (Element e: FlashCardSection){
                        String front = e.select("div[class=front_text card_text]").text();
                        String back = e.select("div[class=back_text card_text]").text();
                        String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(front,back);

                        foundCard = true;
                        coursePoints.add(new CoursePoint(course.getCourse_ID(),front,back,sentence));
                    }
                } catch (IOException exception) {
                    Log.w(context.get().getString(R.string.issue_with_getting_cram_courses),exception.getMessage());


                }

            }

            if (foundCard){
                new InsertCoursePointsToDatabase(context.get(), new WhenTaskCompleteDoNothing()).execute(coursePoints.toArray(new CoursePoint[0]));
            }else{
                Log.w("No Cram courses for",course.getOfficial_name());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();
            onCompleteListener.onAsyncTaskComplete(result);

        }
    }

    private static class WhenTaskCompleteDoNothing implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {

        }
    }
}
