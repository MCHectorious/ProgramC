package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.hector.csprojectprogramc.R;

public class QualificationScreen extends AppCompatActivity {
    public final String qualificationExtraName = "Qualification";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qualifications_screen);

        CardView GCSECard = findViewById(R.id.GCSECard);
        GCSECard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(qualificationExtraName, "GCSE");
                startActivity(toCourseListScreen);
            }
        });

        CardView ALevelCard =  findViewById(R.id.ALevelCard);
        ALevelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(qualificationExtraName, "AS and A-Level");
                startActivity(toCourseListScreen);
            }
        });

    }
}
