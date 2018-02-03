package com.hector.csprojectprogramc.Activities;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
        final Bundle bundle = getIntent().getExtras();

        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(bundle.getString("Colloqial Name"));
        setSupportActionBar(toolbar);

        /*Toolbar toolbar =  findViewById(R.id.custom_toolbar);
        TextView title =  findViewById(R.id.custom_toolbar_title_view);

        setSupportActionBar(toolbar);
        title.setText(bundle.getString("Colloqial Name"));

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        */

        //MyDatabase database = Room.databaseBuilder(CourseScreen.this, MyDatabase.class, "my-db").build();


        //final Course course = database.customDao().getInformationFromCourse(courseID);

        TextView qualificationTV =  findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTV.setText(bundle.getString("Qualification"));

        TextView examboardTV =  findViewById(R.id.examboardSpecificNameInCourse);
        examboardTV.setText(bundle.getString("Exam Board"));

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
                //Log.i("Got this far","Opening Revision Screen");
                startActivity(toRevisionScreen);
            }
        });


    }

}
