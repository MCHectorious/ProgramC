package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.hector.csprojectprogramc.Adapter.HomeScreenRecyclerAdapter;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

import java.util.List;

public class HomeScreen extends AppCompatActivity {
    private boolean haveSubjects;
    private List<Course> savedCourses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        new getSavedCourses().execute();

    }

    public void showNoSubjectAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setText("You currently have no courses."+ System.getProperty("line.separator") + "To add courses and continue click OKAY");
        alertDialogBuilder.setView(textView);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    private class deleteTables extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            MyDatabase database = Room.databaseBuilder(HomeScreen.this, MyDatabase.class, "my-db").build();
            List<Course> courses = database.customDao().getAllSavedCourses();
            for (Course course:courses){
                database.customDao().deleteCourse(course);
            }
            List<CoursePoints> points = database.customDao().getAllSavedCoursePoints();
            for (CoursePoints point:points) {
                database.customDao().deleteCoursePoint(point);
            }
            return null;

        }
    }

    private class getSavedCourses extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeScreen.this);
            progressDialog.setTitle("Initialising App");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MyDatabase database = Room.databaseBuilder(HomeScreen.this, MyDatabase.class, "my-db").build();
            savedCourses = database.customDao().getAllSavedCourses();
            Log.i("No. of Saved Courses",Integer.toString(savedCourses.size()));
            haveSubjects = savedCourses.size()>0;
            //haveSubjects = false;

            Log.i("haveSubjects",Boolean.toString(haveSubjects) );
            return null;

        }

        @Override
        protected void onPostExecute(Void result){
           progressDialog.dismiss();
            if(haveSubjects){
                FloatingActionButton fab =  findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                        startActivity(toExamBoardScreen);
                    }
                });

                RecyclerView recyclerView = findViewById(R.id.cardList);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(HomeScreen.this);
                recyclerView.setLayoutManager(linearLayoutManager);
                HomeScreenRecyclerAdapter adapter = new HomeScreenRecyclerAdapter(savedCourses, HomeScreen.this);
                recyclerView.setAdapter(adapter);
            }else{
                showNoSubjectAlertDialog();
            }
        }
    }

}
