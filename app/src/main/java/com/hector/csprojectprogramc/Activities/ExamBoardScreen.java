package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.hector.csprojectprogramc.R;

public class ExamBoardScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exam_board_screen);
        CardView AQACardView =  findViewById(R.id.AQACard);
        AQACardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toQualificationScreen = new Intent(ExamBoardScreen.this, QualificationScreen.class);
                startActivity(toQualificationScreen);
            }
        });

    }
}
