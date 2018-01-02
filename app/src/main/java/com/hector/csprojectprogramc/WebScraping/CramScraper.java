package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

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

public class CramScraper{
    private Context context;
    private ArrayList<String> relatedWebsites = new ArrayList<>();
    private ArrayList<Flashcard> output = new ArrayList<>();
    private GetFlashcardsFromWebsite getFlashcardsFromWebsite = new GetFlashcardsFromWebsite();

    /*public ArrayList<Flashcard> getFlashcardRelatedTo(String topic) {

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
        GetRelatedFlashcards getRelatedFlashcards = new GetRelatedFlashcards();
        getRelatedFlashcards.execute(builder.toString());
        //ArrayList<Flashcard> cards = getFlashcardRelatedTo(builder.toString());
        MultiThreading.waitUntilFinished(getFlashcardsFromWebsite);
        for (Flashcard card: output) {
            String sentence = FlashcardToSentenceModel.convertToSentence(card);
            database.customDao().insertCoursePoint(new CoursePoints(course.getCourse_ID(),card.getFront(),card.getBack(),sentence));
        }
    }

    private class GetRelatedFlashcards extends AsyncTask<String,Void,Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Getting Cram Courses Related to This Course");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }


        @Override
        protected Void doInBackground(String... strings) {
            for ( String string: strings) {

                StringBuilder builder = new StringBuilder();
                builder.append("http://www.cram.com/search?query=");
                builder.append(StringManipulation.convertSpacesToPluses(string));
                builder.append("&search_in%5B%5D=title&search_in%5B%5D=body&search_in%5B%5D=subject&search_in%5B%5D=username&image_filter=exclude_imgs&period=any");

                String url = builder.toString();
                try {
                    Document document = Jsoup.connect(url).get();
                    Elements section = document.select("div[id=searchResults]").select("a[href]");

                    for (Element element:section ) {
                        String website = element.attr("href");
                        if (website.length()>12){
                            if(website.substring(0,12).equals("/flashcards/")){
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
            progressDialog.setTitle("Getting Information From These Cram Courses");
            progressDialog.setMessage("Please wait");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("http://www.cram.com"+url).get();
                    Elements FlashCardSection = courseWebsite.select("table[class=flashCardsListingTable]").select("tr");
                    for (Element e: FlashCardSection){
                        String front = e.select("div[class=front_text card_text]").text();
                        String back = e.select("div[class=back_text card_text]").text();
                        output.add(new Flashcard(front,back));
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
