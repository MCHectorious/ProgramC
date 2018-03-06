package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.Util.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.util.List;

public class AQAScraper {

    private String colloquialName, officialName, website, examBoard, qualification, nextKeyDate, nextKeyDateDetails;
    private Context context,appContext;
    private Course course;

    public AQAScraper(String url, Context currentContext, Context appContext, String genericQualification, String officialName){
        context = currentContext;
        this.appContext = appContext;
        this.qualification = genericQualification;
        this.officialName = officialName;
        GetInformationFromOnlineAndAddCourse getInformation = new GetInformationFromOnlineAndAddCourse();
        getInformation.execute(url);

    }


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
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (String string: strings) {
                website = string;
                try {
                    Document document = Jsoup.connect(string).timeout(100000).get();
                    Elements codesAndReferences = document.select("table[class=tableCodes]");
                    colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): StringUtils.convertOfficialToColloquial(officialName);
                    if(codesAndReferences.select("tr").size()>0){
                        qualification = codesAndReferences.select("tr").get(0).select("td").text();
                    }
                    examBoard = "AQA";
                    Element keyDateSection = document.select("ul[class=listEvents]").select("li").first();
                    if(keyDateSection != null){
                        nextKeyDate = keyDateSection.select("span[class=timestamp]").text();
                        nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());
                    }
                } catch (Exception e) {
                    Log.e("Issue with AQA",officialName);
                    Log.e("Error",e.getMessage());
                }
            }
            MainDatabase database = Room.databaseBuilder(appContext,MainDatabase.class,"my-db").build();
            List<Course> courses = database.customDao().getAllSavedCourses();

            course = new Course(courses.size()+1,colloquialName,
                    officialName,website,examBoard,
                    qualification,nextKeyDate,
                    nextKeyDateDetails);
            database.customDao().insertCourse(course);
            //List<Course> newCourses = database.customDao().getAllSavedCourses();
            //for (Course course1:newCourses){
              //  Log.i("Course",course1.getOfficial_name());
            //}
            //Log.i("New Course ID", Integer.toString(course.getCourse_ID()));

            //Log.i("CourseSize", Integer.toString(courses.size()) );

            /*for(Course course:courses){
                Log.i("Course ID", Integer.toString(course.getCourse_ID()) );
            }*/

            database.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            new MemRiseScraper().insertCoursePointsInDataBase(context, course,appContext);
        }
    }



}

