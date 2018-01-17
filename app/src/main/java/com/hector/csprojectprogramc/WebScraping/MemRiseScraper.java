package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.Activities.CourseListScreen;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.MLModel.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.Util.Flashcard;
import com.hector.csprojectprogramc.Util.MultiThreading;
import com.hector.csprojectprogramc.Util.StringManipulation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Hector - New on 23/12/2017.
 */

public class MemRiseScraper{
    Context context,appContext;
    ArrayList<String> relatedWebsites = new ArrayList<>();
    ArrayList<Flashcard> output = new ArrayList<>();
    GetFlashcardsFromWebsite getFlashcardsFromWebsite = new GetFlashcardsFromWebsite();
    Course course;
    int courseID;
   /* private ArrayList<Flashcard> getFlashcardRelatedTo(String topic) {

        new getRelatedCourses().execute(topic);
        getFlashcardFromWebsite flashcardFromWebsite = new getFlashcardFromWebsite();
        flashcardFromWebsite.execute(relatedWebsites.toArray(new String[0]));
        return output;
    }*/

    public void insertCoursePointsInDataBase(Context context, Course course, Context appContext){
        this.context = context;
        this.appContext = appContext;
        this.course = course;
        Log.i("Course ID being added", Integer.toString(course.getCourse_ID())  );
        courseID = course.getCourse_ID();
        StringBuilder builder = new StringBuilder();
        builder.append(course.getExamBoard()).append(" ");
        builder.append(course.getQualification()).append(" ");
        builder.append(course.getColloquial_name());
        new GetRelatedFlashcards().execute(builder.toString());
        //MultiThreading.waitUntilFinished(getFlashcardsFromWebsite);
        //ArrayList<Flashcard> cards = getFlashcardRelatedTo(builder.toString());
        //for (Flashcard card: output) {
            //String sentence = FlashcardToSentenceModel.convertToSentence(card);
            //database.customDao().insertCoursePoint(new CoursePoints(course.getCourse_ID(),card.getFront(),card.getBack(),sentence));
        //}
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
                builder.append(StringManipulation.convertSpacesToPluses(string));
                String url = builder.toString();
                try {
                    //Log.i("Overall MemRiseWebsite",url);
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
            Log.i("Got this far","Found MemRise Courses");

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
            MyDatabase database = Room.databaseBuilder(appContext,MyDatabase.class,"my-db").build();
            //Log.i()
            //Log.i("Got this far","Started MemRise Background");

            boolean foundCard = false;

            for (String url: strings) {
                try {
                    //Log.i("MemRise URL","https://www.memrise.com"+url);
                    Document courseWebsite = Jsoup.connect("https://www.memrise.com"+url).get();
                    Elements section = courseWebsite.select("div[class=levels clearfix]").select("a[href]");
                    for (Element element: section) {
                        Document levelWebsite = Jsoup.connect("https://www.memrise.com"+element.attr("href")).get();
                        Elements informationSection = levelWebsite.select("div[class=things clearfix]").select("div[class=thing text-text]");
                        for(Element div:informationSection){
                            String front = div.select("div[class=col_a col text]").select("div[class=text]").first().text();
                            String back = div.select("div[class=col_b col text]").select("div[class=text]").first().text();
                            //output.add(new Flashcard(front,back));
                            String sentence = FlashcardToSentenceModel.convertToSentence(front,back);
                            //Log.i("Points", front+" | "+back);
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

            //Log.i("Got this far","Finished MemRise");

            new CramScraper().insertCoursePointsInDataBase(context, course,appContext);



        }
    }
}
