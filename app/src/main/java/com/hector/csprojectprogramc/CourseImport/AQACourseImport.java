package com.hector.csprojectprogramc.CourseImport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtilities;
import com.hector.csprojectprogramc.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.SocketTimeoutException;

public class AQACourseImport extends ExamBoardCourseImporter {
    private String officialName, qualification, website;//The official name, generic qualification and official website for the course
    private int timeout = 10000;//The initial timeout is 10 seconds
    private AsyncTaskErrorListener onErrorListener;//Handle what should happen if a known error occurs

    public AQACourseImport(Context context, String officialName, String qualification, String website, AsyncTaskErrorListener onErrorListener){//Initialises the fields
        this.context = new WeakReference<>(context);
        this.officialName = officialName;
        this.qualification = qualification;
        this.website = website;
        this.onErrorListener = onErrorListener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();//Runs the generic code for what should happen before the background task is started
        progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
        progressDialog.setTitle(context.get().getString(R.string.get_information_from_aqa));//Explains to the user what the task is doing
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains to the user that they have to wait
        progressDialog.setIndeterminate(false);//Show an icon which doesn't portray progress
        progressDialog.show();//Show the dialog
    }

    @Override
    protected Course doInBackground(Void... input) {
        String colloquialName="",examBoard="",nextKeyDate="",nextKeyDateDetails="";//Initialise the fields

        try {
            Document AQAWebsite = Jsoup.connect(website).timeout(timeout).get();//Connect to the courses official website
            Elements codesAndReferences = AQAWebsite.select("table[class=tableCodes]");//Find the section which includes the colloquial name of the course and its qualification
            colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): GeneralStringUtilities.convertOfficialCourseNameToColloquialCourseName(officialName);//If a colloquial name is provided use it, otherwise use a function a created to convert the official name to a colloquial name
            if(codesAndReferences.select("tr").size()>0){//If codes and references exists
                qualification = codesAndReferences.select("tr").get(0).select("td").text();//Get the qualification mention on the website
            }
            examBoard = context.get().getString(R.string.aqa);//Set the exam board to AQA
            Element keyDateSection = AQAWebsite.select("ul[class=listEvents]").select("li").first();//Find the first key date mentioned
            if(keyDateSection != null){//If a key date is mention
                nextKeyDate = keyDateSection.select("span[class=timestamp]").text();//Get the date mentioned
                nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());//The details of the date mentioned
            }

        } catch (SocketTimeoutException e) {//If the connection timed out
            AlertDialog.Builder timeoutAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder which will create an alert about the connection timing out
            timeoutAlertDialogBuilder.setTitle(R.string.connection_timed_out);//Explains the issue
            timeoutAlertDialogBuilder.setMessage(context.get().getString(R.string.timeout_instructions));//Explains what the user can do to fix the issue
            timeoutAlertDialogBuilder.setCancelable(false);//Prohibit the user doing anything else as it may cause further issues
            timeoutAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the button is pressed
                    timeout *= 2;//Double the timeout
                    doInBackground();//Recursively call the function again to re-attempt it
                }
            });
            timeoutAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                    onErrorListener.onAsyncTaskError();//Handles what code should be run in this situation
                }
            });
            timeoutAlertDialogBuilder.create().show();//Show the alert dialog
        }catch (IOException exception){//Will occur if the URL is malformed, for example
            AlertDialog.Builder errorAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder which will create an alert saying that an error occurred
            errorAlertDialogBuilder.setTitle(R.string.error_acessing_AQA_website);//Explains the issue
            errorAlertDialogBuilder.setMessage(R.string.error_accessing_AQA_website_instructions);//Explains how the user can try to resolve the issue
            errorAlertDialogBuilder.setCancelable(false);//Prohibit the user from continuing to avoid further issues
            errorAlertDialogBuilder.setPositiveButton(R.string.double_the_timeout, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the user presses the  button
                    doInBackground();//recursively re-attempt the function
                }
            });
            errorAlertDialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                    onErrorListener.onAsyncTaskError();//Handles what should occur in this situation
                }
            });
            errorAlertDialogBuilder.create().show();//Show alert dialog
        }

        return new Course(colloquialName,officialName,website,examBoard,qualification,nextKeyDate,nextKeyDateDetails);//Return the course

    }

}

