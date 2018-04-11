package com.hector.csprojectprogramc.CourseListImport;

import android.app.AlertDialog;
import android.content.Context;

import android.content.DialogInterface;

import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;
import java.util.HashMap;


public class GetAQACoursesAndTheirWebsites extends ExamBoardCourseListImporter { //Gets the list of courses from the AQA website

        private int timeout = 10000;//The initial timeout is 10 seconds

        private AsyncTaskErrorListener errorListener;//Handles what should occur if an known error occurs

        public GetAQACoursesAndTheirWebsites(Context context, String qualification, AsyncTaskCompleteListener<HashMap<String,String>> listener, AsyncTaskErrorListener errorListener){//Initialises fields
            this.context = new WeakReference<>(context) ;
            this.qualification = qualification;
            this.onCompleteListener = listener;
            this.errorListener = errorListener;
        }


    @Override
    protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String,String> courseNamesAndWebsites = new HashMap<>();//Initialises the output

            String HTMLDividerClassForQualification = "";//Initialises the string
            if(qualification.equals(context.get().getString(R.string.gcse))){//Checks if the qualification is GCSE
                HTMLDividerClassForQualification = "panelInner gcse-header"; //Sets the divider to the name of the section which includes the GCSE courses
            }
            if (qualification.equals(context.get().getString(R.string.as_and_a_level))){//Check if the qualification is for AS and A-Levels
                HTMLDividerClassForQualification = "panelInner as_and_a-level-header"; //Sets the divider to the name of the section which includes the A-Level courses
            }

            try{
                Document AQAQualificationWebsite = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(timeout).get();//Loads the aqa website
                Elements links = AQAQualificationWebsite.select("div[class="+ HTMLDividerClassForQualification +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
                for(Element element: links){ //Runs through each course
                    String courseName = element.text();//The official name of the course
                    String courseWebsite = element.attr("href");//The URL for the official website for the course
                    courseNamesAndWebsites.put(courseName,courseWebsite);//add the course name and website to the output
                }

            } catch (SocketTimeoutException exception) {//Will occur if the connection times out
                AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder which will create an alert dialog about the timeout
                timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);//Explains the issue
                timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));//Explains how the user can resolve the issue
                timeoutAlertDialogBuilder.setCancelable(false);//Prohibit the user doing anything to avoid any further issues
                timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                        timeout *= 2;//Doubles the timeout
                        doInBackground();//Recursively re-attempts the function
                    }
                });
                timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                        errorListener.onAsyncTaskError();//Handles what should occur if an error occur
                    }
                });
                timeoutAlertDialogBuilder.create().show();//Show Alert
            }catch (IOException exception){//Will occur is the URL is malformed, for example
                AlertDialog.Builder errorAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder which will create an alert about the error
                errorAlertDialogBuilder.setTitle(R.string.error_acessing_AQA_website);//Explains the error
                errorAlertDialogBuilder.setMessage(R.string.error_accessing_AQA_website_instructions);//Explains how the user can resolve the issue
                errorAlertDialogBuilder.setCancelable(false);//Prohibit the user from doing anything else to avoid further issues
                errorAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//When the button is pressed
                        doInBackground();//Recursively re-attempt the function
                    }
                });
                errorAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//When the user presses this button
                        errorListener.onAsyncTaskError();//Handle what should occur if an error occurs
                    }
                });
                errorAlertDialogBuilder.create().show();//Show the alert dialog
            }

            return courseNamesAndWebsites;//returns the output
        }

    @Override
    protected void onPostExecute(HashMap<String, String> courseNamesAndWebsites) {
        super.onPostExecute(courseNamesAndWebsites);//Runs the generic code that should be run after the task is complete
    }
}