package com.hector.csprojectprogramc.CoursePointsImport;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.FlashcardToSentenceModelUtilities.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtilities;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

class GetCramCoursePoints implements CoursePointsImporter{


    public void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener){
        GetRelatedFlashcards getRelatedFlashcards = new GetRelatedFlashcards(context, course, appContext, listener);
        getRelatedFlashcards.execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());

    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>> {
        private ProgressDialog progressDialog;
        private WeakReference<Context> context, appContext;
        private Course course;
        private AsyncTaskCompleteListener<Void> listener;

        private GetRelatedFlashcards(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.appContext = new WeakReference<>(appContext);
            this.listener = listener;
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
                    Document document = Jsoup.connect(url).get();
                    Elements section = document.select("div[id=searchResults]").select("a[href]");

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
                } catch (Exception e) {
                    Log.e("Issue with Cram Overall",course.getOfficial_name());
                    Log.e("Error",e.getMessage());
                }
            }
            return relatedWebsites;
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            progressDialog.dismiss();
            new GetFlashcardsFromRelatedCramCourses(context.get(), course, appContext.get(),listener).execute(relatedWebsites.toArray(new String[0]));
        }
    }

    private static class GetFlashcardsFromRelatedCramCourses extends AsyncTask<String,Void,Void>{

        private ProgressDialog progressDialog;
        private WeakReference<Context> context, appContext;
        private Course course;
        private AsyncTaskCompleteListener<Void> listener;

        private GetFlashcardsFromRelatedCramCourses(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.appContext = new WeakReference<>(appContext);
            this.listener = listener;
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
            MainDatabase database = Room.databaseBuilder(appContext.get(),MainDatabase.class,context.get().getString(R.string.database_location)).build();

            boolean foundCard = false;

            //Log.w("Got this far","yes");

            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("http://www.cram.com"+url).get();
                    Elements FlashCardSection = courseWebsite.select("table[class=flashCardsListingTable]").select("tr");
                    for (Element e: FlashCardSection){
                        String front = e.select("div[class=front_text card_text]").text();
                        String back = e.select("div[class=back_text card_text]").text();

                        //Log.w( (front.length()>10)? front.substring(0,10):front,(back.length()>10)? back.substring(0,10):back);
                        String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(front,back);
                        //Log.w("Sentence","Yes");

                        //Log.w("Sentence", (sentence.length()>30)? sentence.substring(0,30):sentence);
                        foundCard = true;
                        database.customDao().insertCoursePoint(new CoursePoint(course.getCourse_ID(),front,back,sentence));
                    }
                } catch (IOException e) {
                    Log.e("Issue with Cram",course.getOfficial_name());
                    Log.e("Error",e.getMessage());


                }

            }
            database.close();
            if (!foundCard){
                Log.e("No Cram for",course.getOfficial_name());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();

            listener.onAsyncTaskComplete(result);




        }
    }
}
