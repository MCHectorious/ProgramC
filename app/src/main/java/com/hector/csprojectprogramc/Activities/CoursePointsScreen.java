package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetCoursePointsFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenFlashcardAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenEditAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenSentencesAdapter;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;
public class CoursePointsScreen extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when the screen is created
        super.onCreate(savedInstanceState);// TODO: research what this does
        setContentView(R.layout.course_points_screen);// Links the XML file which defines the layout of the screen


        try{

            Bundle intentsBundle = getIntent().getExtras();

            //noinspection ConstantConditions
            int courseID = intentsBundle.getInt(getString(R.string.course_id),0); //gets the course ID from the previous screen //TODO: make sure it handles null
            int perspective = intentsBundle.getInt(getString(R.string.perspective),0);

            new GetCoursePointsFromDatabase(CoursePointsScreen.this, courseID, new ActionsAfterGettingCoursePoints(courseID,perspective)).execute();// Gets the course points for the course and then after displays them

        }catch (NullPointerException exception){
            Log.w("Error","Null pointer exception");
            //TODO: handle appropriately
        }



    }




    private class ActionsAfterGettingCoursePoints implements AsyncTaskCompleteListener<List<CoursePoint>> {

        private int courseID, perspective;

        ActionsAfterGettingCoursePoints(int courseID, int perspective){
            this.courseID = courseID;
            this.perspective = perspective;
        }

        public void onAsyncTaskComplete(List<CoursePoint> coursePoints){
            final RecyclerView flashcardsRecyclerView = findViewById(R.id.cardsRecyclerView);// Initialises recycler view which contains the course points in their flashcard form
            final RecyclerView editPointsRecyclerView = findViewById(R.id.editsRecyclerView);//Initialises the recycler view which allows the course points to be edited
            final FloatingActionButton addCoursePointButton = findViewById(R.id.addCoursePointButton);//Initialises the button which allows the user to add a course point
            final RecyclerView sentencesRecyclerView = findViewById(R.id.sentencesRecyclerView); //Initialises recycler view which contains the course points in their sentence form
            BottomNavigationView navigationForCoursePointsPerspective =  findViewById(R.id.navigation);//Intialises the navigation which allows the user to pick which form of course points to show  //TODO: bottom navigation?
            final MenuView.ItemView sentenceListNav =  findViewById(R.id.sentenceListNav);
            final MenuView.ItemView cardListNav =  findViewById(R.id.cardListNav);
            final MenuView.ItemView editNav =  findViewById(R.id.editNav);



            if(coursePoints.size()==0){// Checks whether there are any course points available
            AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises the builder that will create a warning to the user
            String noCoursesWarning = getString(R.string.you_have_no_courses_points)+ System.getProperty("line.separator")+getString(R.string.no_courses_points_instructions); //Gets the warning
            noCoursesAlertDialogBuilder.setMessage(noCoursesWarning);//Shows the message
            noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) { //Adds an action button which the user can be pressed to add a course point
                    showAddCoursePointDialog(false, CoursePointsScreen.this,courseID);//Shows a dialog which forces the user to add a course point
                }
            });
            noCoursesAlertDialogBuilder.create().show();// Shows the alert dialog
            }else{
            flashcardsRecyclerView.setVisibility(View.GONE);//Hides the recycler view with shows the flashcard form of the course points at the start
            flashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
            flashcardsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));//TODO: add description

            editPointsRecyclerView.setVisibility(View.GONE);// Hides the recycler view with shows course points and allows them to be edited
            editPointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
            editPointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this, courseID)); //TODO: add description

            addCoursePointButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showAddCoursePointDialog(true,  CoursePointsScreen.this, courseID);//Because the action was optional, the user can cancel it
                }
            });
            addCoursePointButton.setVisibility(View.GONE);//TODO: should I always show this?

            sentencesRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
            sentencesRecyclerView.setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Starts the activity by showing the sentence form of the course points
            sentencesRecyclerView.setVisibility(View.GONE);

            switch (perspective){//Decides which perspective to show
                case 0:
                    sentencesRecyclerView.setVisibility(View.VISIBLE); //Shows sentence perspective
                    sentenceListNav.setChecked(true); //Show the sentence navigation item as selected

                    break;
                case 1:
                    flashcardsRecyclerView.setVisibility(View.VISIBLE); //Shows flashcard perspective
                    cardListNav.setChecked(true);//Show the flashcard navigation item as selected
                    break;
                case 2:
                    editPointsRecyclerView.setVisibility(View.VISIBLE); //Show edit perspective
                    editNav.setChecked(true);//Show the edit navigation item as selected
                    break;
            }

            navigationForCoursePointsPerspective.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.sentenceListNav:
                            sentencesRecyclerView.setVisibility(View.VISIBLE);
                            flashcardsRecyclerView.setVisibility(View.GONE);
                            editPointsRecyclerView.setVisibility(View.GONE);
                            addCoursePointButton.setVisibility(View.GONE); //TODO: should I always show this?
                            //Only shows the course points in their sentence form

                            perspective = 0;
                            return true;
                        case R.id.cardListNav:
                            sentencesRecyclerView.setVisibility(View.GONE);
                            flashcardsRecyclerView.setVisibility(View.VISIBLE);
                            editPointsRecyclerView.setVisibility(View.GONE);
                            addCoursePointButton.setVisibility(View.GONE);
                            //Only shows the course points in their flashcard form

                            perspective = 1;
                            return true;
                        case R.id.editNav:
                            sentencesRecyclerView.setVisibility(View.GONE);
                            flashcardsRecyclerView.setVisibility(View.GONE);
                            editPointsRecyclerView.setVisibility(View.VISIBLE);
                            addCoursePointButton.setVisibility(View.VISIBLE);
                            //Only shows the course points in the form which allows editing

                            perspective = 2;
                            return true;
                    }
                    return false;
                }
            });


        }
        }

        void showAddCoursePointDialog(boolean cancelable, final Context context, final int courseID){
            final AlertDialog.Builder AddCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the alert dialog which will allow the user to add a new course point
            AddCoursePointAlertDialogBuilder.setTitle(context.getString(R.string.add_new_course_point));//Sets the title of the dialog
            LinearLayout layoutForAlertDialog = new LinearLayout(context);//Organises the views in a vertical list
            layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL); //TODO: do i need to do this
            final EditText cardFrontEditableTextView = new EditText(context);// Initialises the area where the user can add the front of the flashcard form of the course point
            cardFrontEditableTextView.setHint( context.getString(R.string.enter_flashcard_front) );//Provides instructions for the user
            layoutForAlertDialog.addView(cardFrontEditableTextView);//Adds view to the layout
            final EditText cardBackEditableTextView = new EditText(context);// Initialises the area where the user can add the back of the flashcard form of the course point
            cardBackEditableTextView.setHint(context.getString(R.string.enter_flashcard_back) );//Provides instructions for the user
            layoutForAlertDialog.addView(cardBackEditableTextView);//Adds view to the layout
            final EditText sentenceEditableTextView = new EditText(context); // Initialises the area where the user can add the sentence form of the course point
            sentenceEditableTextView.setHint(context.getString(R.string.enter_sentence) );//Provides instructions for the user
            layoutForAlertDialog.addView(sentenceEditableTextView);//Adds view to the layout
            AddCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);//Adds the list of view to the dialog
            AddCoursePointAlertDialogBuilder.setPositiveButton(context.getString(R.string.add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String[] coursePointComponents = {cardFrontEditableTextView.getText().toString(),cardBackEditableTextView.getText().toString(),sentenceEditableTextView.getText().toString()};//The aspects of the new course point
                    new addCoursePointToDatabase(courseID, context, perspective).execute(coursePointComponents);//adds the new course point and refreshes the screen
                }
            });
            if(cancelable){
                AddCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//Just closes the dialog if it is cancelled
                    }
                });
            }
            AddCoursePointAlertDialogBuilder.setCancelable(cancelable);
            AddCoursePointAlertDialogBuilder.create().show();
        }


    }

    private static class addCoursePointToDatabase extends AsyncTask<String,Void,Void>{ //Adds the course point to the database
        private WeakReference<Context> context;
        private int courseID, perspective;

        private addCoursePointToDatabase(int courseID, Context context, int perspective){
            this.courseID = courseID;
            this.perspective = perspective;
            this.context = new WeakReference<>(context);
        }

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();//Accesses the database
            database.customDao().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));//inserts the course point via an SQL statement
            return null;//In order to override the method correctly
        }

        @Override
        protected void onPostExecute(Void result){
            Intent refreshScreen = new Intent(context.get(), CoursePointsScreen.class);
            refreshScreen.putExtra(context.get().getString(R.string.course_id), courseID);
            refreshScreen.putExtra(context.get().getString(R.string.perspective), perspective);
            context.get().startActivity(refreshScreen);//Refreshes the screen to include te newly created course point
        }
    }
}