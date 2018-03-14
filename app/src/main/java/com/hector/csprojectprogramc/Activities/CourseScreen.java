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
        setContentView(R.layout.course_screen_layout);
        final Bundle bundle = getIntent().getExtras();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(bundle.getString("Colloquial Name"));//Changes the title opf the screen to the colloquial name of the course //TODO: handle nulls
        setSupportActionBar(toolbar);

        TextView qualificationTextView =  findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTextView.setText(bundle.getString("Qualification")); // Shows the qualification

        TextView examBoardTextView =  findViewById(R.id.examboardSpecificNameInCourse);
        examBoardTextView.setText(bundle.getString("Exam Board")); //Shows the exam board

        TextView nextKeyDateTextView =  findViewById(R.id.dateCardInCourse);
        nextKeyDateTextView.setText(bundle.getString("Key Date")); //Shows the date of the next key date

        TextView nextKeyDateDetailTextView =  findViewById(R.id.dateSpecificCardInCourse);
        nextKeyDateDetailTextView.setText(bundle.getString("Key Date Details")); //Shows what is happening on the next key date

        CardView coursePointsButton =  findViewById(R.id.coursePoints);
        coursePointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePoints = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePoints.putExtra("course ID",bundle.getInt("Course ID"));
                startActivity(toCoursePoints);//Opens the course points screen for this course
            }
        });

        FloatingActionButton goToRevisionScreenButton =  findViewById(R.id.fab);
        goToRevisionScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRevisionScreen = new Intent(CourseScreen.this, RevisionScreen.class);
                toRevisionScreen.putExtra("course ID",bundle.getInt("Course ID"));
                startActivity(toRevisionScreen);// Goes to the revision screen for the course
            }
        });


    }

}
