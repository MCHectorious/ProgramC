package com.hector.csprojectprogramc.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

public class CourseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //MyDatabase database = Room.databaseBuilder(CourseScreen.this, MyDatabase.class, "my-db").build();
        final Bundle bundle = getIntent().getExtras();

        //final Course course = database.customDao().getInformationFromCourse(courseID);

        TextView qualificationTV = (TextView) findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTV.setText(bundle.getString("Qualification"));

        TextView examboardTV = (TextView) findViewById(R.id.examboardSpecificNameInCourse);
        examboardTV.setText(bundle.getString("Exam Board"));

        TextView nextKeyDate = (TextView) findViewById(R.id.dateCardInCourse);
        nextKeyDate.setText(bundle.getString("Key Date"));

        TextView nextKeyDateDetail = (TextView) findViewById(R.id.dateSpecificCardInCourse);
        nextKeyDateDetail.setText(bundle.getString("Key Date Details"));

        CardView coursePointsCard = (CardView) findViewById(R.id.coursePoints);
        coursePointsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePoints = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePoints.putExtra("course ID",bundle.getInt("Course ID"));
                startActivity(toCoursePoints);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
