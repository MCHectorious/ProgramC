package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.MachineLearningModels.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Utilities.GeneralStringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;


public class MemRiseScraper{
    private Context context,appContext;
    private ArrayList<String> relatedWebsites = new ArrayList<>();
    private GetFlashcardsFromRelatedMemRiseCourses getFlashcardsFromWebsite = new GetFlashcardsFromRelatedMemRiseCourses();
    private Course course;
    private int courseID;

    public void insertCoursePointsToDataBase(Context context, Course course, Context appContext){
        this.context = context;
        this.appContext = appContext;
        this.course = course;
        courseID = course.getCourse_ID();
        StringBuilder builder = new StringBuilder();
        builder.append(course.getExamBoard()).append(" ");
        builder.append(course.getQualification()).append(" ");
        builder.append(course.getColloquial_name());
        new GetRelatedFlashcards().execute(builder.toString());
    }

    private class GetRelatedFlashcards extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.get_memrise_courses));
            progressDialog.setMessage(context.getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for ( String string: strings) {
                StringBuilder builder = new StringBuilder();
                builder.append("https://www.memrise.com/courses/english/?q=");
                builder.append(GeneralStringUtils.convertSpacesToPluses(string));
                String url = builder.toString();
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
                                        return null;
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
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            getFlashcardsFromWebsite.execute(relatedWebsites.toArray(new String[0]));
        }
    }

    private class GetFlashcardsFromRelatedMemRiseCourses extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.getting_information_from_memrise_courses));
            progressDialog.setMessage(context.getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(appContext,MainDatabase.class,"my-db").build();
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
                            database.customDao().insertCoursePoint(new CoursePoint(courseID,front,back,sentence));//TODO:Change Back
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
            new CramScraper().insertCoursePointsInDataBase(context, course,appContext);
        }
    }
}
