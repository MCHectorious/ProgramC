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

        int courseID = 1;//Find automatically
        MyDatabase database = Room.databaseBuilder(CourseScreen.this, MyDatabase.class, "my-db").build();
        final Course course = database.customDao().getInformationFromCourse(courseID);

        TextView qualificationTV = (TextView) findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTV.setText(course.getQualification());

        TextView examboardTV = (TextView) findViewById(R.id.examboardSpecificNameInCourse);
        examboardTV.setText(course.getExamBoard());

        TextView nextKeyDate = (TextView) findViewById(R.id.dateCardInCourse);
        nextKeyDate.setText(course.getNext_key_date());

        TextView nextKeyDateDetail = (TextView) findViewById(R.id.dateSpecificCardInCourse);
        nextKeyDateDetail.setText(course.getNext_key_date_detail());

        CardView coursePointsCard = (CardView) findViewById(R.id.coursePoints);
        coursePointsCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePoints = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePoints.putExtra("course ID",course.getCourse_ID());
                startActivity(toCoursePoints);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CourseScreen.this, RevisionScreen.class);
                startActivity(intent);
            }
        });
    }

}
