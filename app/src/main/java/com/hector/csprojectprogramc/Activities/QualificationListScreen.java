package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import com.hector.csprojectprogramc.R;

public class QualificationListScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {//When the screen is created
        super.onCreate(savedInstanceState);//Allows general code for a screen starting to be run

        setContentView(R.layout.qualification_list_screen);//Links to an XML file which describes the layout of this screen

        CardView GCSECard = findViewById(R.id.GCSECard);//Initialises the card representing GCSEs
        GCSECard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//When the user presses on this button
                Intent toCourseListScreen = new Intent(QualificationListScreen.this, CourseListScreen.class);//Creates a connection between this screen and the course list screen
                toCourseListScreen.putExtra(getString(R.string.qualification), getString(R.string.gcse));//Says that the qualification of the courses to be shown should be GCSE
                startActivity(toCourseListScreen);//Starts the course list screen
            }
        });

        CardView ALevelCard =  findViewById(R.id.ALevelCard);//Initialises the card representing AS and A-Levels
        ALevelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//When the user pressing on this button
                Intent toCourseListScreen = new Intent(QualificationListScreen.this, CourseListScreen.class);//Create a connection between this screen and the course list screen
                toCourseListScreen.putExtra(getString(R.string.qualification), getString(R.string.as_and_a_level));//Says that the qualification of the courses to be shown should be AS and A-Level
                startActivity(toCourseListScreen);//Starts the course list screen
            }
        });

    }
}
