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
import android.view.View;
import android.widget.TextView;
import com.hector.csprojectprogramc.Adapter.HomeScreenCoursesRecyclerAdapter;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
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

        new getAllCoursesFromDatabase().execute();

    }

    public void showNoCoursesAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        String textViewText = R.string.you_have_no_courses+ System.getProperty("line.separator")+R.string.no_courses_instructions;
        textView.setText(textViewText);
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


    private class clearDatabase extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(HomeScreen.this, MainDatabase.class, "my-db").build();
            List<Course> courses = database.customDao().getAllCourses();
            for (Course course:courses){
                database.customDao().deleteCourse(course);
            }
            List<CoursePoint> points = database.customDao().getAllCoursePoints();
            for (CoursePoint point:points) {
                database.customDao().deleteCoursePoint(point);
            }
            return null;

        }
    }

    private class getAllCoursesFromDatabase extends AsyncTask<Void,Void,Void>{
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
            MainDatabase database = Room.databaseBuilder(HomeScreen.this, MainDatabase.class, "my-db").build();
            savedCourses = database.customDao().getAllCourses();
            haveSubjects = savedCourses.size()>0;
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
                HomeScreenCoursesRecyclerAdapter adapter = new HomeScreenCoursesRecyclerAdapter(savedCourses, HomeScreen.this);
                recyclerView.setAdapter(adapter);
            }else{
                showNoCoursesAlertDialog();
            }
        }
    }

}
