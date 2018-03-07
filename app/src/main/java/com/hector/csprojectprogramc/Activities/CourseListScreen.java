package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Adapter.CourseListScreenCoursesAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CourseListScreen extends AppCompatActivity {
    private String qualification, HTMLDividerClassForQualification;
    private ArrayList<String> courseNames = new ArrayList<>();
    private ArrayList<String> courseWebsites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_screen);
        qualification = getIntent().getStringExtra("Qualification");
        if(qualification.equals("GCSE")){
            HTMLDividerClassForQualification = "panelInner gcse-header";
        }
        if (qualification.equals("AS and A-Level")){
            HTMLDividerClassForQualification = "panelInner as_and_a-level-header";
        }
        new getCoursesAndTheirWebsites().execute();
    }

    private class getCoursesAndTheirWebsites extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(CourseListScreen.this);
            progressDialog.setTitle("Getting the Latest List of "+qualification+" courses");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document document = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(1000000).get();
                Elements links = document.select("div[class="+ HTMLDividerClassForQualification +"]").select("a[href]");
                for(Element element: links){
                    courseNames.add(element.text());
                    courseWebsites.add(element.attr("href"));
                }
            }catch (IOException exception){
                Log.e("Error","A IOException occurred");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView);
            recyclerViewForCourses.setHasFixedSize(true);
            LinearLayoutManager layoutManagerForCoursesRecyclerView = new LinearLayoutManager(CourseListScreen.this);
            recyclerViewForCourses.setLayoutManager(layoutManagerForCoursesRecyclerView);
            CourseListScreenCoursesAdapter AdapterForCoursesRecylerView = new CourseListScreenCoursesAdapter(courseNames, courseWebsites, CourseListScreen.this, getApplicationContext(), qualification);
            recyclerViewForCourses.setAdapter(AdapterForCoursesRecylerView);
        }
    }
}
