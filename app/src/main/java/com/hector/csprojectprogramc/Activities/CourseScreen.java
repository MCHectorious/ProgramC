package com.hector.csprojectprogramc.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;

public class CourseScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {//Runs when the screen is created
        super.onCreate(savedInstanceState);//Allows the general code to be ran for create a screen
        setContentView(R.layout.course_screen_layout);//Links with XML resource dictating how this screen should like
        final Bundle intentsBundle = getIntent().getExtras();//Gets the information from the previous screen

        Toolbar toolbar =  findViewById(R.id.toolbar);//The portion of the course at the top
        try{
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            toolbar.setTitle(intentsBundle.getString(getString(R.string.colloquial_name)));//Changes the title opf the screen to the colloquial name of the course
        }catch (NullPointerException exception){//Will occur is the colloquial name of the course has not been provided by the previous screen
            toolbar.setTitle(R.string.course);//Defaults to making the title "Course"
        }finally {
            setSupportActionBar(toolbar);//Sets the top the screen to the custom value
        }

        TextView qualificationTextView =  findViewById(R.id.qualificationSpecificNameInCourse);//The text view which will state the qualification of the course
        try{
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            qualificationTextView.setText(intentsBundle.getString(getString(R.string.qualification))); // Shows the qualification
        }catch (NullPointerException exception){//Will occur if the previous screen didn't provide the qualification
            findViewById(R.id.qualificationCard).setVisibility(View.GONE);//Hides the card showing the qualification if it is not available
        }

        try{
            TextView examBoardTextView =  findViewById(R.id.examboardSpecificNameInCourse);//The text view which will state the exam board for the course
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            examBoardTextView.setText(intentsBundle.getString(getString(R.string.exam_board))); //Shows the exam board
        }catch (NullPointerException exception){//Will occur if the previous screen didn't provide the exam board
            findViewById(R.id.examboardCard).setVisibility(View.GONE);//Hides the card showing the exam board if it is not available
        }


        try{
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            String nextKeyDate = intentsBundle.getString(getString(R.string.key_date));//Gets the next key date from the previous screen
            TextView nextKeyDateTextView =  findViewById(R.id.dateCardInCourse);//Gets the text view which will state the next key date
            nextKeyDateTextView.setText(nextKeyDate); //Shows the date of the next key date
            TextView nextKeyDateDetailsTextView =  findViewById(R.id.dateSpecificCardInCourse);// Gets the text view which will state the details occurring on the next key date
            nextKeyDateDetailsTextView.setText(intentsBundle.getString(getString(R.string.key_date_details))); //Shows what is happening on the next key date
        }catch (NullPointerException exception){//Will occur if either of the next key date or the next key date details are not provided by the previous screen
            findViewById(R.id.dateCard).setVisibility(View.GONE);//Hides the card showing the next key date and its details
        }

        int courseID;//The id of the course in the course table of the SQLite database
        try{
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            courseID = intentsBundle.getInt(getString(R.string.course_id));// Gets the id of the course from the previous screen
        }catch (NullPointerException exception){//Will occur if the previous screen didn't provide the id of the course
            courseID = -1;// Initialises the courseID to an impossible value
            AlertDialogHelper.showCannotAccessIntentsDialog(CourseScreen.this);//Shows the user that the previous screen didn't provide needed information and allows the user to go back to home screen to try again
        }

        final int finalCourseID = courseID;// A final version of the id of the course so that it can be accessed by an inner class
        CardView goToCoursePointsButton =  findViewById(R.id.coursePoints);// Initialises the button which will allow the user to go to the course points screen
        goToCoursePointsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {//If the user presses the button to go to the course points screen
                Intent toCoursePointsScreen = new Intent(CourseScreen.this, CoursePointsScreen.class);// Creates a connection between this screen and the course points screen
                toCoursePointsScreen.putExtra(getString(R.string.course_id),finalCourseID);//Allows the course points screen to show the course points for the correct course
                toCoursePointsScreen.putExtra(getString(R.string.perspective), 0);//Starts the course points screen on the sentence form of the course points
                startActivity(toCoursePointsScreen);//Opens the course points screen for this course
            }
        });

        FloatingActionButton goToRevisionScreenButton =  findViewById(R.id.fab);//Initialises the button which allows the user to go to the revision screen
        goToRevisionScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//When the user presses the button to go to the revision screen
                Intent toRevisionScreen = new Intent(CourseScreen.this, RevisionScreen.class);//Creates a connection between this screen and the revision screen
                toRevisionScreen.putExtra(getString(R.string.course_id),finalCourseID);// Allows the user to revise for the appropriate course
                startActivity(toRevisionScreen);// Goes to the revision screen for the course
            }
        });


    }

}
