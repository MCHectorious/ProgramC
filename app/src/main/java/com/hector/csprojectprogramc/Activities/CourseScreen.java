package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;

public class CourseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_screen_layout);
        final Bundle intentsBundle = getIntent().getExtras();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        try{
            //noinspection ConstantConditions
            toolbar.setTitle(intentsBundle.getString(getString(R.string.colloquial_name)));//Changes the title opf the screen to the colloquial name of the course
        }catch (NullPointerException exception){
            toolbar.setTitle(R.string.course);
        }finally {
            setSupportActionBar(toolbar);
        }

        TextView qualificationTextView =  findViewById(R.id.qualificationSpecificNameInCourse);
        try{
            //noinspection ConstantConditions
            qualificationTextView.setText(intentsBundle.getString(getString(R.string.qualification))); // Shows the qualification
        }catch (NullPointerException exception){
            findViewById(R.id.qualificationCard).setVisibility(View.GONE);
        }

        try{
            TextView examBoardTextView =  findViewById(R.id.examboardSpecificNameInCourse);
            //noinspection ConstantConditions
            examBoardTextView.setText(intentsBundle.getString(getString(R.string.exam_board))); //Shows the exam board
        }catch (NullPointerException exception){
            findViewById(R.id.examboardCard).setVisibility(View.GONE);
        }


        try{
            //noinspection ConstantConditions
            String nextKeyDate = intentsBundle.getString(getString(R.string.key_date));
            TextView nextKeyDateTextView =  findViewById(R.id.dateCardInCourse);
            nextKeyDateTextView.setText(nextKeyDate); //Shows the date of the next key date
            TextView nextKeyDateDetailsTextView =  findViewById(R.id.dateSpecificCardInCourse);
            nextKeyDateDetailsTextView.setText(intentsBundle.getString(getString(R.string.key_date_details))); //Shows what is happening on the next key date

        }catch (NullPointerException exception){
            findViewById(R.id.dateCard).setVisibility(View.GONE);
        }

        int courseID;
        try{
            //noinspection ConstantConditions
            courseID = intentsBundle.getInt(getString(R.string.course_id));
        }catch (NullPointerException exception){
            courseID = -1;
            AlertDialogHelper.showCannotAccessIntentsDialog(CourseScreen.this);
        }

        final int finalCourseID = courseID;
        CardView goToCoursePointsButton =  findViewById(R.id.coursePoints);
        goToCoursePointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePointsScreen = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePointsScreen.putExtra(getString(R.string.course_id),finalCourseID);
                toCoursePointsScreen.putExtra(getString(R.string.perspective), 0);
                startActivity(toCoursePointsScreen);//Opens the course points screen for this course
            }
        });

        FloatingActionButton goToRevisionScreenButton =  findViewById(R.id.fab);
        goToRevisionScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRevisionScreen = new Intent(CourseScreen.this, RevisionScreen.class);
                toRevisionScreen.putExtra(getString(R.string.course_id),finalCourseID);
                startActivity(toRevisionScreen);// Goes to the revision screen for the course
            }
        });


    }

}
