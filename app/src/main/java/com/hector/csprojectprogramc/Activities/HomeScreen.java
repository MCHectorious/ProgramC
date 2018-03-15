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
    private boolean hasCourses;
    private List<Course> courses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);//TODO: Do i need get string
        setSupportActionBar(toolbar);

        FloatingActionButton toExamBoardScreenButton =  findViewById(R.id.fab);
        toExamBoardScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });

        new getAllCoursesFromDatabase().execute();

    }

    public void showNoCoursesAlertDialog(){
        AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(this);
        TextView noCoursesWarningTextView = new TextView(this);
        String noCoursesWarningText = getString(R.string.you_have_no_courses)+ System.getProperty("line.separator")+getString(R.string.no_courses_instructions);
        noCoursesWarningTextView.setText(noCoursesWarningText);
        noCoursesAlertDialogBuilder.setView(noCoursesWarningTextView);
        noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton( getString(R.string.okay) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });
        noCoursesAlertDialogBuilder.create().show();
    }


    private class clearDatabase extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(HomeScreen.this, MainDatabase.class, "my-db").build();
            List<Course> coursesFromDatabase = database.customDao().getAllCourses();
            for (Course course:coursesFromDatabase){
                database.customDao().deleteCourse(course);
            }
            List<CoursePoint> coursePoints = database.customDao().getAllCoursePoints();//TODO: Check whether any remaining at this point
            for (CoursePoint point:coursePoints) {
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
            progressDialog.setTitle(getString(R.string.initialising_app));
            progressDialog.setMessage(getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(HomeScreen.this, MainDatabase.class, "my-db").build();
            courses = database.customDao().getAllCourses();
            hasCourses = courses.size()>0;
            return null;

        }

        @Override
        protected void onPostExecute(Void result){
           progressDialog.dismiss();
            if(hasCourses){


                RecyclerView CoursesRecyclerView = findViewById(R.id.cardList);
                CoursesRecyclerView.setLayoutManager(new LinearLayoutManager(HomeScreen.this));
                CoursesRecyclerView.setAdapter(new HomeScreenCoursesRecyclerAdapter(courses, HomeScreen.this));
            }else{
                showNoCoursesAlertDialog();
            }
        }
    }

}
