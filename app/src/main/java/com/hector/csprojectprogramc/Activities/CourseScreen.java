package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import com.hector.csprojectprogramc.R;

public class CourseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_screen);
        final Bundle bundle = getIntent().getExtras();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(bundle.getString("Colloqial Name"));
        setSupportActionBar(toolbar);

        TextView qualificationTV =  findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTV.setText(bundle.getString("Qualification"));

        TextView examBoardTV =  findViewById(R.id.examboardSpecificNameInCourse);
        examBoardTV.setText(bundle.getString("Exam Board"));

        TextView nextKeyDate =  findViewById(R.id.dateCardInCourse);
        nextKeyDate.setText(bundle.getString("Key Date"));

        TextView nextKeyDateDetail =  findViewById(R.id.dateSpecificCardInCourse);
        nextKeyDateDetail.setText(bundle.getString("Key Date Details"));

        CardView coursePointsCard =  findViewById(R.id.coursePoints);
        coursePointsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePoints = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePoints.putExtra("course ID",bundle.getInt("Course ID"));
                startActivity(toCoursePoints);
            }
        });

        FloatingActionButton fab =  findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRevisionScreen = new Intent(CourseScreen.this, RevisionScreen.class);
                toRevisionScreen.putExtra("course ID",bundle.getInt("Course ID"));
                startActivity(toRevisionScreen);
            }
        });


    }

}
