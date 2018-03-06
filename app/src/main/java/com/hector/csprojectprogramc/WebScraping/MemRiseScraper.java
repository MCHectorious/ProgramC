package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.MachineLearningModels.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.Util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;


public class MemRiseScraper{
    private Context context,appContext;
    private ArrayList<String> relatedWebsites = new ArrayList<>();
    private GetFlashcardsFromWebsite getFlashcardsFromWebsite = new GetFlashcardsFromWebsite();
    private Course course;
    private int courseID;

    public void insertCoursePointsInDataBase(Context context, Course course, Context appContext){
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
            progressDialog.setTitle("Getting MemRise Courses Related to This Course");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for ( String string: strings) {
                StringBuilder builder = new StringBuilder();
                builder.append("https://www.memrise.com/courses/english/?q=");
                builder.append(StringUtils.convertSpacesToPluses(string));
                String url = builder.toString();
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements section = document.select("div[class=col-sm-12 col-md-9]").select("div[class=course-box ]");
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

    private class GetFlashcardsFromWebsite extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Getting Information From These MemRise Courses");
            progressDialog.setMessage("Please wait");
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
                            String sentence = FlashcardToSentenceModel.convertToSentence(front,back);
                            foundCard = true;
                            database.customDao().insertCoursePoint(new CoursePoints(courseID,front,back,sentence));//TODO:Change Back
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
