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
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCoursePointsToDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenFlashcardAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenEditAdapter;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CoursePointsScreenSentencesAdapter;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.R;

import java.util.List;
public class CoursePointsScreen extends AppCompatActivity {

    private int courseID;// The id of the course in the course table of the database
    private int perspective;// A numerical value representing whether to show the sentence form of the course points, the flashcard form of the course points or to show the course points in a way which allows the user to edit them

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when the screen is created
        super.onCreate(savedInstanceState);//Allows standard code for creating a screen to occur
        setContentView(R.layout.course_points_screen);// Links the XML file which defines the layout of the screen

        try{
            Bundle intentsBundle = getIntent().getExtras();//Gets information from the previous screen
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            courseID = intentsBundle.getInt(getString(R.string.course_id)); //gets the course ID from the previous screen
            perspective = intentsBundle.getInt(getString(R.string.perspective),0); //Gets the desired perspective from the previous screen and defaults to 0 (showing the sentence form)
        }catch (NullPointerException exception){//Will occur if these intents are not provided by the previous screen
            AlertDialogHelper.showCannotAccessIntentsDialog(CoursePointsScreen.this);//Warns the user that information from the previous screen is unavailable and allow them to go back to the home screen
        }

        new GetCoursePointsFromDatabase(CoursePointsScreen.this, courseID, new ActionsAfterGettingCoursePoints()).execute();// Gets the course points for the course and then after displays them

    }

    private class RefreshScreenWhenTaskComplete implements AsyncTaskCompleteListener<Void>{//Allows object to created which will refresh the screen after an async task has occurred

        @Override
        public void onAsyncTaskComplete(Void result) {//Will run once the async task has finished
            Intent refreshScreen = new Intent(CoursePointsScreen.this, CoursePointsScreen.class);//Gets a connection to this screen
            refreshScreen.putExtra(getString(R.string.course_id), courseID);//makes sure that the screen will be for the same course
            refreshScreen.putExtra(getString(R.string.perspective), perspective);//Allows the same perspective to be shown after the refresh
            startActivity(refreshScreen);//Refreshes the screen

        }
    }


    private class ActionsAfterGettingCoursePoints implements AsyncTaskCompleteListener<List<CoursePoint>> {//Allows an object to be created which handled what occurs after getting the course points from the database


        public void onAsyncTaskComplete(List<CoursePoint> coursePoints){//When the course points have been gathered from the database

            if(coursePoints.size()==0){// Checks whether there are any course points available
                AlertDialog.Builder noCoursePointsAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises the builder that will create a warning to the user
                String noCoursePointsWarning = getString(R.string.you_have_no_courses_points)+ System.getProperty("line.separator")+getString(R.string.no_courses_points_instructions); //Gets the warning
                noCoursePointsAlertDialogBuilder.setMessage(noCoursePointsWarning);//Shows the message
                noCoursePointsAlertDialogBuilder.setCancelable(false).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //Adds an action button which the user can be pressed to add a course point
                        showAddCoursePointDialog(false, CoursePointsScreen.this,courseID);//Shows a dialog which forces the user to add a course point
                    }
                });
                noCoursePointsAlertDialogBuilder.create().show();// Shows the alert dialog
            }else{
                final RecyclerView flashcardFormOfCoursePointsRecyclerView = findViewById(R.id.cardsRecyclerView);// Initialises recycler view which contains the course points in their flashcard form
                final RecyclerView editFormOfCoursePointsRecyclerView = findViewById(R.id.editsRecyclerView);//Initialises the recycler view which allows the course points to be edited
                final FloatingActionButton addCoursePointButton = findViewById(R.id.addCoursePointButton);//Initialises the button which allows the user to add a course point
                final RecyclerView sentenceFormOfCoursePointsRecyclerView = findViewById(R.id.sentencesRecyclerView); //Initialises recycler view which contains the course points in their sentence form

                BottomNavigationView navigationForCoursePointsPerspective =  findViewById(R.id.navigation);//Initialises the navigation which allows the user to pick which form of course points to show
                final MenuView.ItemView sentencePerspectiveNavigationItem =  findViewById(R.id.sentenceListNav);//Initialises the button which allows the user to switch to the sentence perspective
                final MenuView.ItemView flashcardPerspectiveNavigationItem =  findViewById(R.id.cardListNav);// Initialises the button which allows the user to switch to the flashcard perspective
                final MenuView.ItemView editPerspectiveNavigationItem =  findViewById(R.id.editNav);// Initialises the button which allows the user to switch to the edit perspective

                hideAndSetLayoutManager(sentenceFormOfCoursePointsRecyclerView);//Hides the recycler view for the sentence form of the course points and says that the course pints should be shown in a vertical list
                sentenceFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Decides how the course points are handled in the sentence perspective

                hideAndSetLayoutManager(flashcardFormOfCoursePointsRecyclerView);//Hides the recycler view for the flashcard form of the course points and says that the course pints should be shown in a vertical list
                flashcardFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));// Decides how the course points are handled in the flashcard perspective

                hideAndSetLayoutManager(editFormOfCoursePointsRecyclerView);// Hides the recycler view for the form of course points which can be edited
                editFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this, courseID));// Decides how the course points are handled in the edit perspective

                addCoursePointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddCoursePointDialog(true,  CoursePointsScreen.this, courseID);//Because the action was optional, the user can cancel it
                    }
                });
                hide(addCoursePointButton);//Hides the button which allows the user to add a new course point

                switch (perspective){//Decides which perspective to show
                case 0://Sentence perspective
                    show(sentenceFormOfCoursePointsRecyclerView);//Shows the recycler view for the sentence form of course points
                    sentencePerspectiveNavigationItem.setChecked(true); //Show the sentence navigation item as selected
                    break;//Because it doesn't need to check the other cases
                case 1://Flashcard perspective
                    show(flashcardFormOfCoursePointsRecyclerView);//Shows the recycler view for the flashcard form of the course points
                    flashcardPerspectiveNavigationItem.setChecked(true);//Show the flashcard navigation item as selected
                    break;//Because it doesn't need to check the other cases
                case 2://Edit perspective
                    show(editFormOfCoursePointsRecyclerView);//Shows the recycler view for the course points in a form which can be edited
                    editPerspectiveNavigationItem.setChecked(true);//Show the edit navigation item as selected
                    break;//Because it doesn't need to check the other cases
                }

                navigationForCoursePointsPerspective.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {//Decides what should happen the user presses on one of the buttons to change perspective
                        switch (item.getItemId()) {//Which item was pressed
                            case R.id.sentenceListNav://Sentence perspective
                                show(sentenceFormOfCoursePointsRecyclerView);
                                hide(flashcardFormOfCoursePointsRecyclerView,editFormOfCoursePointsRecyclerView, addCoursePointButton);
                                //Only shows the course points in their sentence form

                                perspective = 0;//Sets the perspective to the sentence perspective
                                return true;// Declares that a relevant item was pressed
                            case R.id.cardListNav://Flashcard perspective
                                show(flashcardFormOfCoursePointsRecyclerView);
                                hide(sentenceFormOfCoursePointsRecyclerView,editFormOfCoursePointsRecyclerView, addCoursePointButton);
                                //Only shows the course points in their flashcard form

                                perspective = 1;//Sets the perspective to the sentence perspective
                                return true;// Declares that a relevant item was pressed
                            case R.id.editNav://Edit Perspective
                                show(editFormOfCoursePointsRecyclerView,addCoursePointButton);
                                hide(sentenceFormOfCoursePointsRecyclerView, flashcardFormOfCoursePointsRecyclerView);
                                //Only shows the course points in the form which allows editing

                                perspective = 2;//Sets the perspective to the edit perspective
                                return true;// Declares that a relevant item was pressed
                        }
                        return false;//Declares that a relevant was not pressed
                    }
                });


            }
        }

        private void hide(View... views){//Hides all the Views it has been given as parameters
            for(View view : views){
                view.setVisibility(View.GONE);//Hides the view
            }
        }

        private void show(View... views){//Shows all the Views it has been given as parameters
            for(View view : views){
                view.setVisibility(View.VISIBLE);//Shows the view
            }
        }

        private void hideAndSetLayoutManager(RecyclerView recyclerView){//Hides the recycler view and says that it should list the course points in a vertical list
            recyclerView.setVisibility(View.GONE);//Hides the recycler
            recyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list

        }

        void showAddCoursePointDialog(boolean cancelable, final Context context, final int courseID){//Allows the user to add a new course pi=oint
            final AlertDialog.Builder addCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the alert dialog which will allow the user to add a new course point
            addCoursePointAlertDialogBuilder.setTitle(context.getString(R.string.add_new_course_point));//Sets the title of the dialog
            LinearLayout layoutForAddCoursePointAlertDialog = new LinearLayout(context);//Organises the views in a vertical list
            layoutForAddCoursePointAlertDialog.setOrientation(LinearLayout.VERTICAL);//Shows the components in a vertical list

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
                public void onClick(DialogInterface dialog, int which) {//If the button it pressed it should attempts to add the new course point
                    String userFlashcardFront = flashcardFrontEditableTextView.getText().toString();//The user's decision for what the front of the flashcard form of the course point should be
                    String userFlashcardBack = flashcardBackEditableTextView.getText().toString();//The user's decision for what the back of the flashcard form of the course point should be
                    String userSentence = sentenceEditableTextView.getText().toString();//The user's decision for what the sentence form of the course point should be

                    if(userFlashcardFront.equals("")||userFlashcardBack.equals("")||userSentence.equals("")){//Checks whether one of the components has been left blank
                        AlertDialog.Builder emptyCoursePointComponentAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises a builder which create an alert saying that none of the components can be blank
                        emptyCoursePointComponentAlertDialogBuilder.setTitle(R.string.warning);//Explains what type of message this is
                        emptyCoursePointComponentAlertDialogBuilder.setMessage(R.string.blank_components_warning);//Explains why this warning was necessary
                        emptyCoursePointComponentAlertDialogBuilder.setCancelable(false);//The user cannot escape this alert as this could cause further issues
                        emptyCoursePointComponentAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());//Pressing the button will hide the alert so that the user can input something into the blank component
                        emptyCoursePointComponentAlertDialogBuilder.create().show();//Shows the alert
                    }else{
                        CoursePoint coursePoint = new CoursePoint(courseID, userFlashcardFront, userFlashcardBack, userSentence);//Creates a new course point object
                        new InsertCoursePointsToDatabase(context, new RefreshScreenWhenTaskComplete()).execute(coursePoint);//adds the new course point and refreshes the screen
                    }


                }
            });

            if(cancelable){//If the user is able to not add this course point without it possible causing further errors
                addCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel) , AlertDialogHelper.onClickDismissDialog());//Pressing the cancel button hides the alert allowing the user to add a new course
            }
            addCoursePointAlertDialogBuilder.setCancelable(cancelable);// Decides whether the user can escape this alert
            addCoursePointAlertDialogBuilder.create().show();//Shows the alert
        }


    }



}