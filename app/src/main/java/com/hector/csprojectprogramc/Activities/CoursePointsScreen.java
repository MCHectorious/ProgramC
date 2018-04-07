package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetCoursePointsFromDatabase;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCoursePointToDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.CommonAlertDialogs;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenFlashcardAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenEditAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenSentencesAdapter;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.R;

import java.util.List;
public class CoursePointsScreen extends AppCompatActivity {

    private int courseID, perspective;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when the screen is created
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_points_screen);// Links the XML file which defines the layout of the screen


        try{

            Bundle intentsBundle = getIntent().getExtras();

            //noinspection ConstantConditions
            courseID = intentsBundle.getInt(getString(R.string.course_id)); //gets the course ID from the previous screen
            perspective = intentsBundle.getInt(getString(R.string.perspective),0);


        }catch (NullPointerException exception){
            CommonAlertDialogs.showCannotAccessIntentsDialog(CoursePointsScreen.this);
        }

        new GetCoursePointsFromDatabase(CoursePointsScreen.this, courseID, new ActionsAfterGettingCoursePoints()).execute();// Gets the course points for the course and then after displays them



    }

    private class RefreshScreen implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            Intent refreshScreen = new Intent(CoursePointsScreen.this, CoursePointsScreen.class);
            refreshScreen.putExtra(getString(R.string.course_id), courseID);
            refreshScreen.putExtra(getString(R.string.perspective), perspective);
            startActivity(refreshScreen);//Refreshes the screen to include te newly created course point

        }
    }


    private class ActionsAfterGettingCoursePoints implements AsyncTaskCompleteListener<List<CoursePoint>> {


        public void onAsyncTaskComplete(List<CoursePoint> coursePoints){
            final RecyclerView flashcardFormOfCoursePointsRecyclerView = findViewById(R.id.cardsRecyclerView);// Initialises recycler view which contains the course points in their flashcard form
            final RecyclerView editFormOfCoursePointsRecyclerView = findViewById(R.id.editsRecyclerView);//Initialises the recycler view which allows the course points to be edited
            final FloatingActionButton addCoursePointButton = findViewById(R.id.addCoursePointButton);//Initialises the button which allows the user to add a course point
            final RecyclerView sentenceFormOfCoursePointsRecyclerView = findViewById(R.id.sentencesRecyclerView); //Initialises recycler view which contains the course points in their sentence form
            BottomNavigationView navigationForCoursePointsPerspective =  findViewById(R.id.navigation);//Intialises the navigation which allows the user to pick which form of course points to show  //TODO: bottom navigation?
            final MenuView.ItemView sentencePerspectiveNavigationItem =  findViewById(R.id.sentenceListNav);
            final MenuView.ItemView flashcardPerspectiveNavigationItem =  findViewById(R.id.cardListNav);
            final MenuView.ItemView editPerspectiveNavigationItem =  findViewById(R.id.editNav);



            if(coursePoints.size()==0){// Checks whether there are any course points available
                AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises the builder that will create a warning to the user
                String noCoursesWarning = getString(R.string.you_have_no_courses_points)+ System.getProperty("line.separator")+getString(R.string.no_courses_points_instructions); //Gets the warning
                noCoursesAlertDialogBuilder.setMessage(noCoursesWarning);//Shows the message
                noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //Adds an action button which the user can be pressed to add a course point
                        showAddCoursePointDialog(false, CoursePointsScreen.this,courseID);//Shows a dialog which forces the user to add a course point
                    }
                });
                noCoursesAlertDialogBuilder.create().show();// Shows the alert dialog
            }else{
                flashcardFormOfCoursePointsRecyclerView.setVisibility(View.GONE);//Hides the recycler view with shows the flashcard form of the course points at the start
                flashcardFormOfCoursePointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
                flashcardFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));//TODO: add description

                editFormOfCoursePointsRecyclerView.setVisibility(View.GONE);// Hides the recycler view with shows course points and allows them to be edited
                editFormOfCoursePointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
                editFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this, courseID)); //TODO: add description

                sentenceFormOfCoursePointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
                sentenceFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Starts the activity by showing the sentence form of the course points
                sentenceFormOfCoursePointsRecyclerView.setVisibility(View.GONE);

                addCoursePointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddCoursePointDialog(true,  CoursePointsScreen.this, courseID);//Because the action was optional, the user can cancel it
                    }
                });
                addCoursePointButton.setVisibility(View.GONE);//TODO: should I always show this?


                    switch (perspective){//Decides which perspective to show
                    case 0:
                        sentenceFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE); //Shows sentence perspective
                        sentencePerspectiveNavigationItem.setChecked(true); //Show the sentence navigation item as selected
                        break;
                    case 1:
                        flashcardFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE); //Shows flashcard perspective
                        flashcardPerspectiveNavigationItem.setChecked(true);//Show the flashcard navigation item as selected
                        break;
                    case 2:
                        editFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE); //Show edit perspective
                        editPerspectiveNavigationItem.setChecked(true);//Show the edit navigation item as selected
                        break;
                }

                navigationForCoursePointsPerspective.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sentenceListNav:
                                sentenceFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE);
                                flashcardFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                editFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                addCoursePointButton.setVisibility(View.GONE); //TODO: should I always show this?
                                //Only shows the course points in their sentence form

                                perspective = 0;
                                return true;
                            case R.id.cardListNav:
                                sentenceFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                flashcardFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE);
                                editFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                addCoursePointButton.setVisibility(View.GONE);
                                //Only shows the course points in their flashcard form

                                perspective = 1;
                                return true;
                            case R.id.editNav:
                                sentenceFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                flashcardFormOfCoursePointsRecyclerView.setVisibility(View.GONE);
                                editFormOfCoursePointsRecyclerView.setVisibility(View.VISIBLE);
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
            final AlertDialog.Builder addCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the alert dialog which will allow the user to add a new course point
            addCoursePointAlertDialogBuilder.setTitle(context.getString(R.string.add_new_course_point));//Sets the title of the dialog
            LinearLayout layoutForAddCoursePointAlertDialog = new LinearLayout(context);//Organises the views in a vertical list
            layoutForAddCoursePointAlertDialog.setOrientation(LinearLayout.VERTICAL); //TODO: do i need to do this
            final EditText flashcardFrontEditableTextView = new EditText(context);// Initialises the area where the user can add the front of the flashcard form of the course point
            flashcardFrontEditableTextView.setHint( context.getString(R.string.enter_flashcard_front) );//Provides instructions for the user
            layoutForAddCoursePointAlertDialog.addView(flashcardFrontEditableTextView);//Adds view to the layout
            final EditText flashcardBackEditableTextView = new EditText(context);// Initialises the area where the user can add the back of the flashcard form of the course point
            flashcardBackEditableTextView.setHint(context.getString(R.string.enter_flashcard_back) );//Provides instructions for the user
            layoutForAddCoursePointAlertDialog.addView(flashcardBackEditableTextView);//Adds view to the layout
            final EditText sentenceEditableTextView = new EditText(context); // Initialises the area where the user can add the sentence form of the course point
            sentenceEditableTextView.setHint(context.getString(R.string.enter_sentence) );//Provides instructions for the user
            layoutForAddCoursePointAlertDialog.addView(sentenceEditableTextView);//Adds view to the layout
            addCoursePointAlertDialogBuilder.setView(layoutForAddCoursePointAlertDialog);//Adds the list of view to the dialog
            addCoursePointAlertDialogBuilder.setPositiveButton(context.getString(R.string.add), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String cardFront = flashcardFrontEditableTextView.getText().toString();
                    String cardBack = flashcardBackEditableTextView.getText().toString();
                    String sentence = sentenceEditableTextView.getText().toString();

                    if(cardFront.equals("")||cardBack.equals("")||sentence.equals("")){
                        AlertDialog.Builder emptyCoursePointComponentAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);
                        emptyCoursePointComponentAlertDialogBuilder.setTitle(R.string.warning);
                        emptyCoursePointComponentAlertDialogBuilder.setMessage(R.string.blank_components_warning);
                        emptyCoursePointComponentAlertDialogBuilder.setCancelable(false);
                        emptyCoursePointComponentAlertDialogBuilder.setPositiveButton(R.string.reattempt, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        emptyCoursePointComponentAlertDialogBuilder.create().show();
                    }else{
                        String[] coursePointComponents = {cardFront,cardBack,sentence};//The aspects of the new course point
                        new InsertCoursePointToDatabase(courseID, context, new RefreshScreen()).execute(coursePointComponents);//adds the new course point and refreshes the screen
                    }


                }
            });
            if(cancelable){
                addCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel) , new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {//Just closes the dialog if it is cancelled
                    }
                });
            }
            addCoursePointAlertDialogBuilder.setCancelable(cancelable);
            addCoursePointAlertDialogBuilder.create().show();
        }


    }



}