package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;

import com.hector.csprojectprogramc.R;

public class ExamBoardScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {//When the screen is created
        super.onCreate(savedInstanceState);// Allows the program to do general code for when a screen is created
        setContentView(R.layout.exam_board_screen);//Creates a link to the XMl file which describes how the screen should be laid out
        CardView AQACard =  findViewById(R.id.AQACard);//Initialises the card for AQA
        AQACard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//When the AQA card is pressed
                Intent toQualificationScreen = new Intent(ExamBoardScreen.this, QualificationListScreen.class);//Creates a connection between this screen and the Qualification List screen
                startActivity(toQualificationScreen);//Goes the Qualification List Screen
            }
        });

    }
}
