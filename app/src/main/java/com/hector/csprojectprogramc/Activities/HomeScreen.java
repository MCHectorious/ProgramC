package com.hector.csprojectprogramc.Activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetAllCoursesFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.RecyclerViewAdapters.HomeScreenCoursesRecyclerAdapter;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.R;

import java.util.List;

public class HomeScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        setSupportActionBar(toolbar);

        FloatingActionButton toExamBoardScreenButton =  findViewById(R.id.fab);
        toExamBoardScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });

        new GetAllCoursesFromDatabase(HomeScreen.this, new WhenTaskCompleteShowCourses()).execute();

    }

    public static void showNoCoursesAlertDialog(final Context context){
        AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(context);
        TextView noCoursesWarningTextView = new TextView(context);
        String noCoursesWarning = context.getString(R.string.you_have_no_courses)+ System.getProperty("line.separator")+context.getString(R.string.no_courses_instructions);
        noCoursesWarningTextView.setText(noCoursesWarning);
        noCoursesAlertDialogBuilder.setView(noCoursesWarningTextView);
        noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton( context.getString(R.string.okay) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toExamBoardScreen = new Intent(context, ExamBoardScreen.class);
                context.startActivity(toExamBoardScreen);
            }
        });
        noCoursesAlertDialogBuilder.create().show();
    }

    private class WhenTaskCompleteShowCourses implements AsyncTaskCompleteListener<List<Course>>{
        public void onAsyncTaskComplete(List<Course> courses){
            RecyclerView coursesRecyclerView = findViewById(R.id.cardList);
            if(courses.size()>0){
                coursesRecyclerView.setLayoutManager(new LinearLayoutManager(HomeScreen.this));
                coursesRecyclerView.setAdapter(new HomeScreenCoursesRecyclerAdapter(courses, HomeScreen.this));
            }else{
                showNoCoursesAlertDialog(HomeScreen.this);
            }
        }

    }


}
