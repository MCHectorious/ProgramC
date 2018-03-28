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
        toolbar.setTitle(bundle.getString(getString(R.string.colloquial_name)));//Changes the title opf the screen to the colloquial name of the course //TODO: handle nulls
        setSupportActionBar(toolbar);

        TextView qualificationTextView =  findViewById(R.id.qualificationSpecificNameInCourse);
        qualificationTextView.setText(bundle.getString(getString(R.string.qualification))); // Shows the qualification

        TextView examBoardTextView =  findViewById(R.id.examboardSpecificNameInCourse);
        examBoardTextView.setText(bundle.getString(getString(R.string.exam_board))); //Shows the exam board


        String keyDate = bundle.getString(getString(R.string.key_date));
        if(keyDate.equals("")){
            findViewById(R.id.dateCard).setVisibility(View.GONE);
        }else{
            TextView nextKeyDateTextView =  findViewById(R.id.dateCardInCourse);
            nextKeyDateTextView.setText(keyDate); //Shows the date of the next key date
            TextView nextKeyDateDetailTextView =  findViewById(R.id.dateSpecificCardInCourse);
            nextKeyDateDetailTextView.setText(bundle.getString(getString(R.string.key_date_details))); //Shows what is happening on the next key date
        }




        CardView coursePointsButton =  findViewById(R.id.coursePoints);
        coursePointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toCoursePoints = new Intent(CourseScreen.this, CoursePointsScreen.class);
                toCoursePoints.putExtra(getString(R.string.course_id),bundle.getInt(getString(R.string.course_id)));
                toCoursePoints.putExtra(getString(R.string.perspective), 0);

                startActivity(toCoursePoints);//Opens the course points screen for this course
            }
        });

        FloatingActionButton goToRevisionScreenButton =  findViewById(R.id.fab);
        goToRevisionScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toRevisionScreen = new Intent(CourseScreen.this, RevisionScreen.class);
                toRevisionScreen.putExtra(getString(R.string.course_id),bundle.getInt(getString(R.string.course_id)));
                startActivity(toRevisionScreen);// Goes to the revision screen for the course
            }
        });


    }

}
