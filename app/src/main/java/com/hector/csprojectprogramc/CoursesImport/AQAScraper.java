package com.hector.csprojectprogramc.CoursesImport;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.GeneralStringUtils;
import com.hector.csprojectprogramc.CoursePointsImport.MemRiseScraper;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.ref.WeakReference;
import java.util.List;

public class AQAScraper {

    //private String colloquialName, officialName, website, examBoard, qualification, nextKeyDate, nextKeyDateDetails;
    //private Context context,appContext;
    //private Course course;

    public AQAScraper(String url, Context currentContext, Context appContext, String genericQualification, String officialName){
        GetInformationFromCourseWebsiteAndAddCourseToDatabase getInformation = new GetInformationFromCourseWebsiteAndAddCourseToDatabase(currentContext, officialName, appContext,genericQualification);
        getInformation.execute(url);

    }


    private static class GetInformationFromCourseWebsiteAndAddCourseToDatabase extends AsyncTask<String,Void,Course>{
        private ProgressDialog progressDialog;
        private WeakReference<Context> context,appContext;
        private String officialName, qualification;

        private GetInformationFromCourseWebsiteAndAddCourseToDatabase(Context context, String officialName, Context appContext, String qualification){
            this.context = new WeakReference<>(context);
            this.officialName = officialName;
            this.appContext = new WeakReference<>(appContext);
            this.qualification = qualification;
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
        protected Course doInBackground(String... strings) {
            String website="", colloquialName="",examBoard="",nextKeyDate="",nextKeyDateDetails="";
            for (String string: strings) {
                website = string;
                try {
                    Document document = Jsoup.connect(string).timeout(100000).get();
                    Elements codesAndReferences = document.select("table[class=tableCodes]");
                    colloquialName = (codesAndReferences.select("tr").size()>1)? codesAndReferences.select("tr").get(1).select("td").text(): GeneralStringUtils.convertOfficialCourseNameToColloquialCourseName(officialName);
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
            }
            MainDatabase database = Room.databaseBuilder(appContext.get(),MainDatabase.class,context.get().getString(R.string.database_location)).build();
            List<Course> courses = database.customDao().getAllCourses();

            Course course = new Course(courses.size()+1,colloquialName,
                    officialName,website,examBoard,
                    qualification,nextKeyDate,
                    nextKeyDateDetails);
            database.customDao().insertCourse(course);
            database.close();
            return course;
        }

        @Override
        protected void onPostExecute(Course course){
            progressDialog.dismiss();
            new MemRiseScraper().insertCoursePointsToDataBase(context.get(), course,appContext.get());
        }
    }



}

