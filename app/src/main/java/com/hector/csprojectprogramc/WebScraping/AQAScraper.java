package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.Util.MultiThreading;
import com.hector.csprojectprogramc.Util.StringManipulation;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;

/**
 * Created by Hector - New on 24/12/2017.
 */

public class AQAScraper {

    private String colloquialName, officialName, website, examBoard, qualification, nextKeyDate, nextKeyDateDetails;
    private Context context,appContext;
    private GetInformationFromOnlineAndAddCourse getInformation;
    private Course course;
    //private MyDatabase database;

    public AQAScraper(String url, Context currentContext, Context appContext, String genericQualification, String officialName){
        context = currentContext;
        getInformation = new GetInformationFromOnlineAndAddCourse();
        getInformation.execute(url);
        this.appContext = appContext;
        this.qualification = genericQualification;
        this.officialName = officialName;
    }

    /*public AsyncTask getTask(){
        return getInformation;
    }*/

    /*public Course getCourse(){
        MultiThreading.waitUntilFinished(getInformation);
        Log.i("Got this far","Finished waiting");

        return new Course(colloquialName,
                officialName,website,examBoard,
                qualification,nextKeyDate,
                nextKeyDateDetails);
    }*/

    private class GetInformationFromOnlineAndAddCourse extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Getting Additional Information From AQA");
            progressDialog.setMessage("Please wait");
            progressDialog.setIndeterminate(false);
            progressDialog.show();

            Log.i("Got this far","Finished AQA pre-execute");
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (String string: strings) {
                website = string;
                try {
                    Document document = Jsoup.connect(string).timeout(100000).get();
                    Log.i("Got this far","connected to AQA website");
                    //officialName = (document.select("h1[class=mainTitle]").size()>0)? document.select("h1[class=mainTitle]").first().text():"";
                    Elements codesAndReferences = document.select("table[class=tableCodes]");
                    colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): StringManipulation.convertOfficalToColloquial(officialName);
                    //qualification = (codesAndReferences.select("tr").size()>0)? codesAndReferences.select("tr").get(0).select("td").text():"";
                    if(codesAndReferences.select("tr").size()>0){
                        qualification = codesAndReferences.select("tr").get(0).select("td").text();
                    }
                    examBoard = "AQA";
                    Element keyDateSection = document.select("ul[class=listEvents]").select("li").first();
                    nextKeyDate = keyDateSection.select("span[class=timestamp]").text();
                    nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());
                    //Log.i("Got this far", "Collected All Details");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            MyDatabase database = Room.databaseBuilder(appContext,MyDatabase.class,"my-db").build();
            List<Course> courses = database.customDao().getAllSavedCourses();



            course = new Course(courses.size()+1,colloquialName,
                    officialName,website,examBoard,
                    qualification,nextKeyDate,
                    nextKeyDateDetails);
            //Log.i("New Course ID", Integer.toString(course.getCourse_ID()));
            database.customDao().insertCourse(course);
            List<Course> newCourses = database.customDao().getAllSavedCourses();
            for (Course course1:newCourses){
                Log.i("Course",course1.getOfficial_name());
            }
            //Log.i("New Course ID", Integer.toString(course.getCourse_ID()));

            //Log.i("CourseSize", Integer.toString(courses.size()) );

            /*for(Course course:courses){
                Log.i("Course ID", Integer.toString(course.getCourse_ID()) );
            }*/

            database.close();
            Log.i("Got this far","Finished AQA background");
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i("Got this far","Finished AQA");
            progressDialog.dismiss();

            Log.i("Got this far","Starting MemRise");
            new MemRiseScraper().insertCoursePointsInDataBase(context, course,appContext);
            //new CramScraper().insertCoursePointsInDataBase(context, course,appContext);


        }
    }



}

