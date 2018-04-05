package com.hector.csprojectprogramc.CourseImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtilities;
import com.hector.csprojectprogramc.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;

public class AQACourseImport extends ExamBoardCourseImporter {
    private String officialName, qualification, website;

    public AQACourseImport(Context context, String officialName, String qualification, String website){
        this.context = new WeakReference<>(context);
        this.officialName = officialName;
        this.qualification = qualification;
        this.website = website;
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
            Document document = Jsoup.connect(website).timeout(100000).get();//TODO:do i need header
            Elements codesAndReferences = document.select("table[class=tableCodes]");
            colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): GeneralStringUtilities.convertOfficialCourseNameToColloquialCourseName(officialName);
            if(codesAndReferences.select("tr").size()>0){
                qualification = codesAndReferences.select("tr").get(0).select("td").text();
            }
            examBoard = context.get().getString(R.string.aqa);
            Element keyDateSection = document.select("ul[class=listEvents]").select("li").first();
            if(keyDateSection != null){
                nextKeyDate = keyDateSection.select("span[class=timestamp]").text();
                nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());
            }
        } catch (Exception e) {
            Log.e("Issue with AQA",officialName);
            Log.e("Error",e.getMessage());
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

