package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Adapter.CourseListAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CourseListScreen extends AppCompatActivity {
    private String qualification, divClassForQualification;
    private ArrayList<String> courseNames = new ArrayList<>();
    private ArrayList<String> courseWebsites = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_list_screen);


        qualification = getIntent().getStringExtra("Qualification");
        if(qualification.equals("GCSE")){
            divClassForQualification = "panelInner gcse-header";
        }
        if (qualification.equals("AS and A-Level")){
            divClassForQualification = "panelInner as_and_a-level-header";
        }

        new getCourses().execute();

    }



    private class getCourses extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;
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
                Elements links = document.select("div[class="+ divClassForQualification +"]").select("a[href]");
                for(Element element: links){
                    Log.i("Subject",element.text());
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

            final RecyclerView recyclerView =  findViewById(R.id.courseListScreenRecyclerView);
            recyclerView.setHasFixedSize(true);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CourseListScreen.this);
            recyclerView.setLayoutManager(linearLayoutManager);
            CourseListAdapter courseListAdapter = new CourseListAdapter(courseNames, courseWebsites, CourseListScreen.this, getApplicationContext(), qualification);
            recyclerView.setAdapter(courseListAdapter);

        }
    }

}
