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
import java.util.List;


class GetMemriseCoursePoints implements CoursePointsImporter{

    private static int timeout = 100000;//the timeout is initially set at 100 seconds


    public void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener){
        new GetRelatedFlashcards(context, course,  listener, errorListener).execute(course.getExamBoard()+" "+course.getQualification()+" "+course.getColloquial_name());//Executes the async task
    }

    private static class GetRelatedFlashcards extends AsyncTask<String,Void,ArrayList<String>>{

        private ProgressDialog progressDialog;//Shows the user that a background task is occurring
        private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
        private Course course;//Course that the course points belong to
        private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what should occur after the task is complete
        private AsyncTaskErrorListener errorListener;//Handles what should occur if a known error occurs

        private GetRelatedFlashcards(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener, AsyncTaskErrorListener errorListener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
            this.errorListener = errorListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();//Runs the generic code that should run before the async task
            progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
            progressDialog.setTitle(context.get().getString(R.string.get_memrise_courses));//Explains what the background task is doing
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains what the user should do and why
            progressDialog.setIndeterminate(false);//Show an icon that doesn't represent the progress
            progressDialog.show();//Show progress dialog
        }

        @Override
        protected ArrayList<String> doInBackground(String... urls) {
            ArrayList<String> relatedWebsites = new ArrayList<>();//Initialises the output

            for ( String string: urls) {
                String url = "https://www.memrise.com/courses/english/?q="+ GeneralStringUtilities.convertSpacesToPluses(string);//Generate the website of the search query
                try {
                    Document document = Jsoup.connect(url).timeout(timeout).get();//Connect to the website of the search query
                    Elements flashcardSection = document.select("div[class=row]").select("div[class=course-box-wrapper col-xs-12 col-sm-6 col-md-4]");//Get the section with the flashcards

                    for (Element element:flashcardSection ) {
                        Element courseNameElement = element.select("a[class=inner]").first();//Get the courseName element
                        String courseName = courseNameElement.text().toLowerCase();//Get the course name
                        String categoryName = element.select("a[class=category]").first().text().toLowerCase();//Get category

                        if (courseName.contains(course.getColloquial_name().toLowerCase()) ||
                                categoryName.contains(course.getColloquial_name().toLowerCase())){//If the if the course or category name includes the cooloquial name for the course, the Memrise course is treated as relevant

                            String website = courseNameElement.attr("href");
                            if(website.length()>8){

                                if(website.substring(0,8).equals("/course/")){
                                    relatedWebsites.add(website);//Add the course to the output

                                    if (relatedWebsites.size()==5){//Stop when 5 related courses have been found
                                        return relatedWebsites;
                                    }

                                }
                            }

                        }
                    }

                }catch (SocketTimeoutException e) {//Will occur if the connection times out
                    AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());
                    timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);//Explains the issue
                    timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));//Explains how the issue can be resolved
                    timeoutAlertDialogBuilder.setCancelable(false);//Prohibits the user from doing anything else to avoid further issues
                    timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            timeout *= 2;//doubles the timeout
                            doInBackground();// recursively re-attemps the method
                        }
                    });
                    timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            errorListener.onAsyncTaskError();//Handles what to do in this situation
                        }
                    });
                    timeoutAlertDialogBuilder.create().show();//Show alert

                }catch (IOException exception){//Can occur with a malformed URL
                    Log.w("Memrise courses",exception.getMessage());//Log the error to check the cause of the issue
                }
            }
            return relatedWebsites;//return the output
        }

        @Override
        protected void onPostExecute(ArrayList<String> relatedWebsites){
            progressDialog.dismiss();//Close the progress dailog
            if(relatedWebsites == null){//Checks whether any related websites were found
                onCompleteListener.onAsyncTaskComplete(null);//skips finding course points if no Memrise courses were found
            }else{
                new GetFlashcardsFromRelatedMemriseCourses(context.get(), course, onCompleteListener).execute(relatedWebsites.toArray(new String[0]));//Get course points
            }
        }
    }

    private static class GetFlashcardsFromRelatedMemriseCourses extends AsyncTask<String,Void,Void>{

        private ProgressDialog progressDialog;//Shows the user that a background task is occurring
        private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
        private Course course;//The course the course points belong to
        private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what should occur after the task  is complete

        private GetFlashcardsFromRelatedMemriseCourses(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener){
            this.context = new WeakReference<>(context);
            this.course = course;
            this.onCompleteListener = onCompleteListener;
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();//Runs the generic code that should occur before the task
            progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
            progressDialog.setTitle(context.get().getString(R.string.getting_information_from_memrise_courses));//Explains what the background task is doing
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains what the user should do and why
            progressDialog.setIndeterminate(false);//Show an icon which doesn't represent the progress
            progressDialog.show();//Show the progress dialog
        }

        @Override
        protected Void doInBackground(String... urls) {
            List<CoursePoint> coursePoints = new ArrayList<>();//Initialise the output

            boolean foundCard = false;//Initialise a boolean representing whether a flashcard found has been found to false

            for (String url: urls) {
                try {
                    Document courseWebsite = Jsoup.connect("https://www.memrise.com"+url).get();//Connect to the Memrise course website
                    Elements flashcardSection = courseWebsite.select("div[class=levels clearfix]").select("a[href]");//Get the levels section
                    for (Element element: flashcardSection) {
                        Document levelWebsite = Jsoup.connect("https://www.memrise.com"+element.attr("href")).get();//Get the website for a level of the Memrise course
                        Elements informationSection = levelWebsite.select("div[class=things clearfix]").select("div[class=thing text-text]");//Get the flashcard section
                        for(Element div:informationSection){
                            String flashcardFront = div.select("div[class=col_a col text]").select("div[class=text]").first().text();//gets the front of the flashcard
                            String flashcardBack = div.select("div[class=col_b col text]").select("div[class=text]").first().text();//gets the back of the flashcard
                            String sentence = FlashcardToSentenceModel.convertFlashcardToSentence(flashcardFront,flashcardBack);//Generate the sentence
                            foundCard = true;//a flashcard has been found
                            coursePoints.add(new CoursePoint(course.getCourse_ID(),flashcardFront,flashcardBack,sentence));//Add course point to output
                        }

                    }
                } catch (IOException exception) {
                    Log.w(context.get().getString(R.string.issue_with_memrise_course), exception.getMessage());//Log error to find out cause
                }
            }

            if (foundCard){
                new InsertCoursePointsToDatabase(context.get(), new WhenTaskCompleteDoNothing()).execute(coursePoints.toArray(new CoursePoint[0]));//Insert the course points into the database
            }else{
                Log.w("No Cram courses for",course.getOfficial_name());//Logs the error to find it's cause

            }
            return null;//To conform to its superclass
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);
            progressDialog.dismiss();//Close the progress dialog

            onCompleteListener.onAsyncTaskComplete(result);//Run the code that needs to run after the task is complete

        }
    }

    private static class WhenTaskCompleteDoNothing implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            //Do nothing
        }
    }

}
