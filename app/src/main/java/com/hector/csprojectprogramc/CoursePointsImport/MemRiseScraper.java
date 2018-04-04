package com.hector.csprojectprogramc.CoursePointsImport;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.CoursePointsImport.CramScraper;
import com.hector.csprojectprogramc.FlashcardToSentenceModelUtilities.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.ArrayList;


public class MemRiseScraper{


    public void insertCoursePointsToDataBase(Context context, Course course, Context appContext){
        new GetRelatedFlashcards(context, course, appContext).execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());
    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>>{
        private ProgressDialog progressDialog;
        private WeakReference<Context> context, appContext;
        private Course course;

        private GetRelatedFlashcards(Context context, Course course, Context appContext){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.appContext = new WeakReference<>(appContext);
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
                String url = "https://www.memrise.com/courses/english/?q="+GeneralStringUtils.convertSpacesToPluses(string);
                Log.w("URL",url);
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements section = document.select("div[class=row]").select("div[class=course-box-wrapper col-xs-12 col-sm-6 col-md-4]");
                    //Elements section  = document.select("div[class=col-sm-12 col-md-9]").select("div[class=course-box ]");
                    Log.w("section no", Integer.toString(section.size() ));
                    for (Element element:section ) {
                        Element courseNameElement = element.select("a[class=inner]").first();
                        String courseName = courseNameElement.text().toLowerCase();
                        Log.w("course name", courseName);
                        String categoryName = element.select("a[class=category]").first().text().toLowerCase();
                        Log.w("category name", categoryName);
                        Log.w("colloquial name", course.getColloquial_name().toLowerCase());

                        if (courseName.contains(course.getColloquial_name().toLowerCase()) ||
                                categoryName.contains(course.getColloquial_name().toLowerCase())){

                            String website = courseNameElement.attr("href");
                            if(website.length()>8){
                                if(website.substring(0,8).equals("/course/")){
                                    relatedWebsites.add(website);
                                    Log.w("Website",website);
                                    if (relatedWebsites.size()==5){
                                        return relatedWebsites;
                                    }
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.e("Issue MemRise Overall",course.getOfficial_name());
                    Log.e("Error",e.getMessage());
                }
            }
            return relatedWebsites;
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            progressDialog.dismiss();
            new GetFlashcardsFromRelatedMemRiseCourses(context.get(), course, appContext.get()).execute(relatedWebsites.toArray(new String[0]));
        }
    }

    private static class GetFlashcardsFromRelatedMemRiseCourses extends AsyncTask<String,Void,Void>{
        private ProgressDialog progressDialog;
        private WeakReference<Context> context, appContext;
        private Course course;

        private GetFlashcardsFromRelatedMemRiseCourses(Context context, Course course, Context appContext){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.appContext = new WeakReference<>(appContext);
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
            MainDatabase database = Room.databaseBuilder(appContext.get(),MainDatabase.class,"my-db").build();
            boolean foundCard = false;

            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("https://www.memrise.com"+url).get();
                    Elements section = courseWebsite.select("div[class=levels clearfix]").select("a[href]");
                    for (Element element: section) {
                        Document levelWebsite = Jsoup.connect("https://www.memrise.com"+element.attr("href")).get();
                        Elements informationSection = levelWebsite.select("div[class=things clearfix]").select("div[class=thing text-text]");
                        for(Element div:informationSection){
                            String front = div.select("div[class=col_a col text]").select("div[class=text]").first().text();
                            String back = div.select("div[class=col_b col text]").select("div[class=text]").first().text();
                            String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(front,back);
                            foundCard = true;
                            database.customDao().insertCoursePoint(new CoursePoint(course.getCourse_ID(),front,back,sentence));
                        }

                    }
                } catch (Exception e) {
                    Log.e("Issue with MemRise",course.getOfficial_name());
                    Log.e("Error",e.getMessage());                }

            }
            database.close();
            if (!foundCard){
                Log.e("No MemRise for",course.getOfficial_name());
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            new CramScraper().insertCoursePointsInDataBase(context.get(), course,appContext.get());
        }
    }
}
