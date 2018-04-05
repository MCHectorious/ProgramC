package com.hector.csprojectprogramc.CourseListImport;

import android.content.Context;

import android.util.Log;

import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.HashMap;


public class GetAQACoursesAndTheirWebsites extends ExamBoardCourseListImporter { //Gets the list of courses from the AQA website


        public GetAQACoursesAndTheirWebsites(Context context, String qualification, AsyncTaskCompleteListener<HashMap<String,String>> listener){
            this.context = new WeakReference<>(context) ;
            this.qualification = qualification;
            this.listener = listener;
        }



        @Override
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String,String> courseNameAndWebsite = new HashMap<>();

            String HTMLDividerClassForQualification = "";
            if(qualification.equals(context.get().getString(R.string.gcse))){
                HTMLDividerClassForQualification = "panelInner gcse-header"; //Sets the divider to the name of the section which includes the GCSE courses
            }
            if (qualification.equals(context.get().getString(R.string.as_and_a_level))){
                HTMLDividerClassForQualification = "panelInner as_and_a-level-header"; //Sets the divider to the name of the section which includes the A-Level courses
            }

            try{
                Document document = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(1000000).get();//Loads the aqa website
                Elements links = document.select("div[class="+ HTMLDividerClassForQualification +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
                for(Element element: links){ //Runs through each course
                    String courseName = element.text();
                    String courseWebsite = element.attr("href");
                    courseNameAndWebsite.put(courseName,courseWebsite);


                }
            }catch (IOException exception){//TODO: improve
                Log.e("Error","A IOException occurred");// Displays that an error has occurred
            }
            return courseNameAndWebsite;//So that it implements the method correctly
        }

    }