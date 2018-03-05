package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.MLModel.FlashcardToSentenceModel;
import com.hector.csprojectprogramc.Util.StringManipulation;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.ArrayList;

public class CramScraper{
    private Context context, appContext;
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
        GetRelatedFlashcards getRelatedFlashcards = new GetRelatedFlashcards();
        getRelatedFlashcards.execute(builder.toString());
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
            MyDatabase database = Room.databaseBuilder(appContext,MyDatabase.class,"my-db").build();

            boolean foundCard = false;

            for (String url: strings) {
                try {
                    Document courseWebsite = Jsoup.connect("http://www.cram.com"+url).get();
                    Elements FlashCardSection = courseWebsite.select("table[class=flashCardsListingTable]").select("tr");
                    for (Element e: FlashCardSection){
                        String front = e.select("div[class=front_text card_text]").text();
                        String back = e.select("div[class=back_text card_text]").text();
                        String sentence = FlashcardToSentenceModel.convertToSentence(front,back);
                        foundCard = true;
                        database.customDao().insertCoursePoint(new CoursePoints(courseID,front,back,sentence));
                    }
                } catch (Exception e) {
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
            Log.i("Got this far","Finished Cram");
            Intent intent = new Intent(context, HomeScreen.class);
            context.startActivity(intent);
        }
    }
}
