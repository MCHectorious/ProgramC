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
import java.util.List;


class GetMemRiseCoursePoints implements CoursePointsImporter{

    private static int timeout = 10000;


    public void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener){
        new GetRelatedFlashcards(context, course,  listener, errorListener).execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());
    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>>{

        private ProgressDialog progressDialog;
        private WeakReference<Context> context;
        private Course course;
        private AsyncTaskCompleteListener<Void> listener;
        private AsyncTaskErrorListener errorListener;

        private GetRelatedFlashcards(Context context, Course course, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.listener = listener;
            this.errorListener = errorListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context.get());
            progressDialog.setTitle(context.get().getString(R.string.get_memrise_courses));
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> relatedWebsites = new ArrayList<>();

            for ( String string: strings) {
                String url = "https://www.memrise.com/courses/english/?q="+ GeneralStringUtilities.convertSpacesToPluses(string);
                try {
                    Document document = Jsoup.connect(url).timeout(timeout).get();
                    Elements section = document.select("div[class=row]").select("div[class=course-box-wrapper col-xs-12 col-sm-6 col-md-4]");

                    for (Element element:section ) {
                        Element courseNameElement = element.select("a[class=inner]").first();
                        String courseName = courseNameElement.text().toLowerCase();
                        String categoryName = element.select("a[class=category]").first().text().toLowerCase();

                        if (courseName.contains(course.getColloquial_name().toLowerCase()) ||
                                categoryName.contains(course.getColloquial_name().toLowerCase())){

                            String website = courseNameElement.attr("href");
                            if(website.length()>8){

                                if(website.substring(0,8).equals("/course/")){
                                    relatedWebsites.add(website);

                                    if (relatedWebsites.size()==5){
                                        return relatedWebsites;
                                    }

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
                    Log.w("Memrise courses",exception.getMessage());
                }
            }
            return relatedWebsites;
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            progressDialog.dismiss();
            if(relatedWebsites == null){
                listener.onAsyncTaskComplete(null);
            }else{
                new GetFlashcardsFromRelatedMemRiseCourses(context.get(), course, listener).execute(relatedWebsites.toArray(new String[0]));
            }
        }
    }

    private static class GetFlashcardsFromRelatedMemRiseCourses extends AsyncTask<String,Void,Void>{

        private ProgressDialog progressDialog;
        private WeakReference<Context> context;
        private Course course;
        private AsyncTaskCompleteListener<Void> listener;

        private GetFlashcardsFromRelatedMemRiseCourses(Context context, Course course, AsyncTaskCompleteListener<Void> listener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.listener = listener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context.get());
            progressDialog.setTitle(context.get().getString(R.string.getting_information_from_memrise_courses));
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            List<CoursePoint> coursePoints = new ArrayList<>();

            boolean foundCard = false;

            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("https://www.memrise.com"+url).get();
                    Elements section = courseWebsite.select("div[class=levels clearfix]").select("a[href]");
                    for (Element element: section) {
                        Document levelWebsite = Jsoup.connect("https://www.memrise.com"+element.attr("href")).get();
                        Elements informationSection = levelWebsite.select("div[class=things clearfix]").select("div[class=thing text-text]");
                        for(Element div:informationSection){
                            String flashcardFront = div.select("div[class=col_a col text]").select("div[class=text]").first().text();
                            String flashcardBack = div.select("div[class=col_b col text]").select("div[class=text]").first().text();
                            String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(flashcardFront,flashcardBack);
                            foundCard = true;
                            coursePoints.add(new CoursePoint(course.getCourse_ID(),flashcardFront,flashcardBack,sentence));
                        }

                    }
                } catch (IOException exception) {
                    Log.w(context.get().getString(R.string.issue_with_memrise_course), exception.getMessage());
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
            progressDialog.dismiss();

            listener.onAsyncTaskComplete(result);

        }
    }

    private static class WhenTaskCompleteDoNothing implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {

        }
    }

}
