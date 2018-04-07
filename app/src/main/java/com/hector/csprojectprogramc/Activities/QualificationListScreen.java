package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import com.hector.csprojectprogramc.R;

public class QualificationListScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.qualification_list_screen);

        CardView GCSECard = findViewById(R.id.GCSECard);
        GCSECard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationListScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(getString(R.string.qualification), getString(R.string.gcse));
                startActivity(toCourseListScreen);
            }
        });

        CardView ALevelCard =  findViewById(R.id.ALevelCard);
        ALevelCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toCourseListScreen = new Intent(QualificationListScreen.this, CourseListScreen.class);
                toCourseListScreen.putExtra(getString(R.string.qualification), getString(R.string.as_and_a_level));
                startActivity(toCourseListScreen);
            }
        });

    }
}
