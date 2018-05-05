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

class GetCramCoursePoints implements CoursePointsImporter{

    private static int timeout = 100000;//the timeout is initially set at 100 seconds

    public void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener){
        GetRelatedFlashcards getRelatedFlashcards = new GetRelatedFlashcards(context, course, listener, errorListener);//Initialises the async task
        getRelatedFlashcards.execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());//executes the async task with an appropriate search query

    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>> {

        private ProgressDialog progressDialog;//Shows the user that a background task is occurring
        private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
        private Course course;//Course that the course points belong to
        private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what should occur after the task is complete
        private AsyncTaskErrorListener errorListener;//Handles what should occur if a known error occurs


        private GetRelatedFlashcards(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener, AsyncTaskErrorListener errorListener){//Initialises the fields
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
            this.errorListener = errorListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();//Runs the generic code that should run before the async task
            progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
            progressDialog.setTitle(context.get().getString(R.string.get_cram_courses));//Explains to the user what the background task is doing
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains what the user should do and why
            progressDialog.setIndeterminate(false);//Show an icon that doesn't represent the progress
            progressDialog.show();//Show progress dialog
        }

        @Override
        protected ArrayList<String> doInBackground(String... strings) {
            ArrayList<String> relatedWebsites = new ArrayList<>();//Initialises the output
            for ( String string: strings) {

                String url = "http://www.cram.com/search?query="+ GeneralStringUtilities.convertSpacesToPluses(string)+"&search_in%5B%5D=title&search_in%5B%5D=body&search_in%5B%5D=subject&search_in%5B%5D=username&image_filter=exclude_imgs&period=any";//Gets the URl of the search
                try {
                    Document overallCramWebsite = Jsoup.connect(url).timeout(timeout).get();//Connect to the website of the search
                    Elements searchResults = overallCramWebsite.select("div[id=searchResults]").select("a[href]");//Select the part of the page with the search results

                    for (Element searchResult:searchResults ) {
                        String website = searchResult.attr("href");//Website of search result
                        if (website.length()>12){
                            if(website.substring(0,12).equals("/flashcards/")){//If the search result is a course

                                relatedWebsites.add(website);//Adds the course to the output
                                if (relatedWebsites.size()==5){//Stop when the 5 courses have been found
                                    return relatedWebsites;
                                }
                            }
                        }
                    }

                }catch (SocketTimeoutException e) {//Will occur if the connection times out
                    AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialise the builder which will create an alert about the timout
                    timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);//Explains the issue
                    timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));//Explains how the user can resolve the user
                    timeoutAlertDialogBuilder.setCancelable(false);//Prohibit the user from doing anything else to avoid further issues
                    timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//When the button is pressed
                            timeout *= 2;//Doubles the timeout
                            doInBackground();//Recursively re-attempt
                        }
                    });
                    timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//When the button is pressed
                            errorListener.onAsyncTaskError();//Handles the issue
                        }
                    });
                    timeoutAlertDialogBuilder.create().show();//Show the alert
                }catch (IOException exception){
                    Log.w(context.get().getString(R.string.issue_with_getting_cram_courses),exception.getMessage());//Log the issue so that the cause can be determined
                }

            }
            return relatedWebsites;//return the output
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            super.onPostExecute(relatedWebsites);//Run the generic code that should be ran after the async task has be completed
            progressDialog.dismiss();//Hide the dialog
            if(relatedWebsites == null){//Check whether any cram courses have been found
                onCompleteListener.onAsyncTaskComplete(null);//Stops looking for course points and continue doing the rest of the code
            }else{
                new GetFlashcardsFromRelatedCramCourses(context.get(), course, onCompleteListener).execute(relatedWebsites.toArray(new String[0]));//Gets course points from the related courses
            }
        }
    }

    private static class GetFlashcardsFromRelatedCramCourses extends AsyncTask<String,Void,Void>{

        private ProgressDialog progressDialog;//Shows the user that a background task is occurring
        private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
        private Course course;//The course the course points belong to 
        private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what should occur after the task  is complete

        private GetFlashcardsFromRelatedCramCourses(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener){//Initialises the fields
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();//Runs the generic code that should occur before the task
            progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
            progressDialog.setTitle(context.get().getString(R.string.get_information_from_cram_courses));//Explains what the task is doing
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains what the user should do and why
            progressDialog.setIndeterminate(false);//Show an icon which doesn't represent the progress
            progressDialog.show();//Show the progress dialog
        }

        @Override
        protected Void doInBackground(String... urls) {

            ArrayList<CoursePoint> coursePoints = new ArrayList<>();//Initialises the output
            boolean foundCard = false;//Initialise a boolean representing whether a flashcard found has been found to false

            for (String url: urls) {
                try {
                    Document courseWebsite = Jsoup.connect("http://www.cram.com"+url).timeout(timeout).get();//connect to the website representing a relevant course
                    Elements flashcardSection = courseWebsite.select("table[class=flashCardsListingTable]").select("tr");//gets the part of the page with flashcards
                    for (Element e: flashcardSection){
                        String front = e.select("div[class=front_text card_text]").text();//Gets the flashcard front
                        String back = e.select("div[class=back_text card_text]").text();//Gets the flashcard back
                        String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(front,back);//Generates the sentence from the front and back of the flashcard

                        foundCard = true;
                        coursePoints.add(new CoursePoint(course.getCourse_ID(),front,back,sentence));//Adds the course point to the output
                    }
                } catch (IOException exception) {//Will occur if the URl is malformed, for example
                    Log.w(context.get().getString(R.string.issue_with_getting_cram_courses),exception.getMessage());//Logs the error to see the issue


                }

            }

            if (foundCard){//Check that at least 1 flashcard has been  found
                new InsertCoursePointsToDatabase(context.get(), new WhenTaskCompleteDoNothing()).execute(coursePoints.toArray(new CoursePoint[0]));//Inserts the course points into the database
            }else{
                Log.w("No Cram courses for",course.getOfficial_name());//Logs that no flashcards have to found to check the cause
            }

            return null;//To conform to the superclass
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);//Runs the generic code that should be ran after the task has been completed
            progressDialog.dismiss();//Close the progress dialog
            onCompleteListener.onAsyncTaskComplete(result);//Run whatever code should occur next

        }
    }

    private static class WhenTaskCompleteDoNothing implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            //Do nothing after the course points have been added to the database
        }
    }
}
