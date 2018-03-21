package com.hector.csprojectprogramc.WebScraping;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.hector.csprojectprogramc.Activities.HomeScreen;
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

import java.io.IOException;
import java.util.ArrayList;

public class CramScraper{
    private Context context, appContext;
    private ArrayList<String> relatedWebsites = new ArrayList<>();
    private GetFlashcardsFromRelatedCramCourses getFlashcardsFromWebsite = new GetFlashcardsFromRelatedCramCourses();
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
            progressDialog.setTitle(context.getString(R.string.get_cram_courses));
            progressDialog.setMessage(context.getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            for ( String string: strings) {

                StringBuilder builder = new StringBuilder();
                builder.append("http://www.cram.com/search?query=");
                builder.append(GeneralStringUtils.convertSpacesToPluses(string));
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

    private class GetFlashcardsFromRelatedCramCourses extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle(context.getString(R.string.get_information_from_cram_courses));
            progressDialog.setMessage(context.getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(appContext,MainDatabase.class,context.getString(R.string.database_location)).build();

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
                        database.customDao().insertCoursePoint(new CoursePoint(courseID,front,back,sentence));
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

            AlertDialog.Builder machineLearningWarningAlertDialogBuilder = new AlertDialog.Builder(context);// Initialises the alert dialog which will warn the user that some sentences may be machine generated
            TextView machineLearningWarningTextView = new TextView(context); //TODO: Do i need a separate text view
            String machineLearningWarningText =  context.getString(R.string.you_can_view_course_points)+ System.getProperty("line.separator")+ context.getString(R.string.machine_generated_sentences_warning)+ System.getProperty("line.separator")+context.getString(R.string.edit_tab_instructions);//The warning to the user and instruction as to how to resolve them
            machineLearningWarningTextView.setText(machineLearningWarningText);
            machineLearningWarningAlertDialogBuilder.setView(machineLearningWarningTextView);
            machineLearningWarningAlertDialogBuilder.setCancelable(false).setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {//Clicking on the button just closes the dialog
                    Intent intent = new Intent(context, HomeScreen.class);
                    context.startActivity(intent);
                }
            });
            machineLearningWarningAlertDialogBuilder.create().show();//Shows the warning on the screen


        }
    }
}
