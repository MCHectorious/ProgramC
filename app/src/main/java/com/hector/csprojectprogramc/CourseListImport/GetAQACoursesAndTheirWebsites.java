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

        private int timeout = 10000;

        private AsyncTaskErrorListener errorListener;

        public GetAQACoursesAndTheirWebsites(Context context, String qualification, AsyncTaskCompleteListener<HashMap<String,String>> listener, AsyncTaskErrorListener errorListener){
            this.context = new WeakReference<>(context) ;
            this.qualification = qualification;
            this.onCompleteListener = listener;
            this.errorListener = errorListener;
        }

        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String,String> courseNamesAndWebsites = new HashMap<>();

            String HTMLDividerClassForQualification = "";
            if(qualification.equals(context.get().getString(R.string.gcse))){
                HTMLDividerClassForQualification = "panelInner gcse-header"; //Sets the divider to the name of the section which includes the GCSE courses
            }
            if (qualification.equals(context.get().getString(R.string.as_and_a_level))){
                HTMLDividerClassForQualification = "panelInner as_and_a-level-header"; //Sets the divider to the name of the section which includes the A-Level courses
            }

            try{
                Document AQAQualificationWebsite = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(timeout).get();//Loads the aqa website
                Elements links = AQAQualificationWebsite.select("div[class="+ HTMLDividerClassForQualification +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
                for(Element element: links){ //Runs through each course
                    String courseName = element.text();
                    String courseWebsite = element.attr("href");
                    courseNamesAndWebsites.put(courseName,courseWebsite);
                }

            } catch (SocketTimeoutException exception) {
                AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());
                timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);
                timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));
                timeoutAlertDialogBuilder.setCancelable(false);
                timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timeout *= 2;
                        doInBackground();
                    }
                });
                timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        errorListener.onAsyncTaskError();
                    }
                });
                timeoutAlertDialogBuilder.create().show();
            }catch (IOException exception){
                AlertDialog.Builder errorAlertDialogBuilder = new AlertDialog.Builder(context.get());
                errorAlertDialogBuilder.setTitle(R.string.error_acessing_AQA_website);
                errorAlertDialogBuilder.setMessage(R.string.error_accessing_AQA_website_instructions);
                errorAlertDialogBuilder.setCancelable(false);
                errorAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        doInBackground();
                    }
                });
                errorAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        errorListener.onAsyncTaskError();
                    }
                });
                errorAlertDialogBuilder.create().show();
            }

            return courseNamesAndWebsites;
        }

    }