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

import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetAllCoursesFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.RecyclerViewAdapters.HomeScreenCoursesRecyclerAdapter;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.R;

import java.util.List;

public class HomeScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {//When the scree is created
        super.onCreate(savedInstanceState);// Allows the program to do general code for when a screen is created
        setContentView(R.layout.home_screen_layout);//Links to an XML file which describes the layout of the screen
        Toolbar toolbar =  findViewById(R.id.toolbar);//Gets the top part of the screen
        toolbar.setTitle(R.string.home);//Sets the title of the screen to "Home"
        setSupportActionBar(toolbar);//Displays the custom toolbar

        FloatingActionButton toExamBoardScreenButton =  findViewById(R.id.fab);//Initialises the button to allow the user to go to the exam boards screen
        toExamBoardScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//When the user presses the button to go the the exam board screen
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);//Creates a connection between this screen and the exam boards screen
                startActivity(toExamBoardScreen);//Starts the exam boards screen
            }
        });

        new GetAllCoursesFromDatabase(HomeScreen.this, new WhenTaskCompleteShowCourses()).execute();//Gets the courses the user has saved from the database

    }

    private static void showNoCoursesAlertDialog(final Context context){//Shows a warning if there are no courses in the database
        AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the builder which will create the art saying there are no courses available
        String noCoursesWarning = context.getString(R.string.you_have_no_courses)+ System.getProperty("line.separator")+context.getString(R.string.no_courses_instructions);//Provides the warning that no courses are current available
        noCoursesAlertDialogBuilder.setMessage(noCoursesWarning);//Sets the alert to show the warning
        noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton( context.getString(R.string.okay) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {//When the user presses it will go tp the exam board screen. The user cannot proceed any other way
                Intent toExamBoardScreen = new Intent(context, ExamBoardScreen.class);//Creates a connection between this screen and the exam board screen
                context.startActivity(toExamBoardScreen);//Starts the exam board screen
            }
        });
        noCoursesAlertDialogBuilder.create().show();//Shows the alert
    }

    private class WhenTaskCompleteShowCourses implements AsyncTaskCompleteListener<List<Course>>{//Allows objects to be create which handle what occurs after the courses have been gather from the database
        public void onAsyncTaskComplete(List<Course> courses){//Once the courses have been collected
            RecyclerView coursesRecyclerView = findViewById(R.id.cardList);//Initialises the recycler view which will show the courses
            if(courses.size()>0){//Checks whether any courses exist
                coursesRecyclerView.setLayoutManager(new LinearLayoutManager(HomeScreen.this));//Show the courses in a vertical list
                coursesRecyclerView.setAdapter(new HomeScreenCoursesRecyclerAdapter(courses, HomeScreen.this));//Decides how the courses will be handled
            }else{
                showNoCoursesAlertDialog(HomeScreen.this);//Shows a warning to the user that they have no courses
            }
        }

    }


}
