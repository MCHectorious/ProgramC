package com.hector.csprojectprogramc.CourseImport;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

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
    private String officialName, qualification, website;
    private int timeout = 10000;
    private AsyncTaskErrorListener onErrorListener;

    public AQACourseImport(Context context, String officialName, String qualification, String website, AsyncTaskErrorListener onErrorListener){
        this.context = new WeakReference<>(context);
        this.officialName = officialName;
        this.qualification = qualification;
        this.website = website;
        this.onErrorListener = onErrorListener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressDialog = new ProgressDialog(context.get());
        progressDialog.setTitle(context.get().getString(R.string.get_information_from_aqa));
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected Course doInBackground(Void... input) {
        String colloquialName="",examBoard="",nextKeyDate="",nextKeyDateDetails="";

        try {
            Document AQAWebsite = Jsoup.connect(website).timeout(timeout).get();
            Elements codesAndReferences = AQAWebsite.select("table[class=tableCodes]");
            colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): GeneralStringUtilities.convertOfficialCourseNameToColloquialCourseName(officialName);
            if(codesAndReferences.select("tr").size()>0){
                qualification = codesAndReferences.select("tr").get(0).select("td").text();
            }
            examBoard = context.get().getString(R.string.aqa);
            Element keyDateSection = AQAWebsite.select("ul[class=listEvents]").select("li").first();
            if(keyDateSection != null){
                nextKeyDate = keyDateSection.select("span[class=timestamp]").text();
                nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());
            }

        } catch (SocketTimeoutException e) {
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
                    onErrorListener.onAsyncTaskError();
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
                    onErrorListener.onAsyncTaskError();
                }
            });
            errorAlertDialogBuilder.create().show();
        }

        Course course = new Course();
        course.setColloquial_name(colloquialName);
        course.setExamBoard(examBoard);
        course.setNext_key_date(nextKeyDate);
        course.setNext_key_date_detail(nextKeyDateDetails);
        course.setOfficial_name(officialName);
        course.setQualification(qualification);
        course.setWebsite(website);
        return course;

    }

}

