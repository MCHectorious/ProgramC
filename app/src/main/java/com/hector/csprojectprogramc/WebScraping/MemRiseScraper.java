package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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
    Context context;
    ArrayList<String> relatedWebsites = new ArrayList<>();
    ArrayList<Flashcard> output = new ArrayList<>();
    GetFlashcardsFromWebsite getFlashcardsFromWebsite = new GetFlashcardsFromWebsite();

   /* private ArrayList<Flashcard> getFlashcardRelatedTo(String topic) {

        new getRelatedCourses().execute(topic);
        getFlashcardFromWebsite flashcardFromWebsite = new getFlashcardFromWebsite();
        flashcardFromWebsite.execute(relatedWebsites.toArray(new String[0]));
        return output;
    }*/

    public void insertCoursePointsInDataBase(Context context, Course course, MyDatabase database){
        this.context = context;
        StringBuilder builder = new StringBuilder();
        builder.append(course.getExamBoard()).append(" ");
        builder.append(course.getQualification()).append(" ");
        builder.append(course.getColloquial_name());
        new GetRelatedFlashcards().execute(builder.toString());
        MultiThreading.waitUntilFinished(getFlashcardsFromWebsite);
        //ArrayList<Flashcard> cards = getFlashcardRelatedTo(builder.toString());
        for (Flashcard card: output) {
            String sentence = FlashcardToSentenceModel.convertToSentence(card);
            database.customDao().insertCoursePoint(new CoursePoints(course.getCourse_ID(),card.getFront(),card.getBack(),sentence));
        }
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
                    Document document = Jsoup.connect(url).get();
                    Elements section = document.select("div[class=col-sm-12 col-md-9]").select("a[href]");
                    for (Element element:section ) {
                        String website = element.attr("href");
                        if(website.length()>8){
                            if(website.substring(0,8).equals("/course/")){
                                relatedWebsites.add(website);
                            }
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
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
                            output.add(new Flashcard(front,back));
                        }

                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
        }
    }
}
