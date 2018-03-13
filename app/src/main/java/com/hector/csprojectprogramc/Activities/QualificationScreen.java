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
        setContentView(R.layout.qualification_list_screen);

        CardView GCSECardView = findViewById(R.id.GCSECard);
        GCSECardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(qualificationExtraName, "GCSE");
                startActivity(toCourseListScreen);
            }
        });

        CardView ALevelCardView =  findViewById(R.id.ALevelCard);
        ALevelCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(qualificationExtraName, "AS and A-Level");
                startActivity(toCourseListScreen);
            }
        });

    }
}
