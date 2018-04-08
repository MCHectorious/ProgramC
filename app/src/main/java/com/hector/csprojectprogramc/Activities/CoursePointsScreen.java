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
            AlertDialogHelper.showCannotAccessIntentsDialog(CoursePointsScreen.this);
        }

        new GetCoursePointsFromDatabase(CoursePointsScreen.this, courseID, new ActionsAfterGettingCoursePoints()).execute();// Gets the course points for the course and then after displays them

    }

    private class RefreshScreenWhenTaskComplete implements AsyncTaskCompleteListener<Void>{

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
                final MenuView.ItemView sentencePerspectiveNavigationItem =  findViewById(R.id.sentenceListNav);
                final MenuView.ItemView flashcardPerspectiveNavigationItem =  findViewById(R.id.cardListNav);
                final MenuView.ItemView editPerspectiveNavigationItem =  findViewById(R.id.editNav);

                hideAndSetLayoutManager(sentenceFormOfCoursePointsRecyclerView);
                sentenceFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Starts the activity by showing the sentence form of the course points

                hideAndSetLayoutManager(flashcardFormOfCoursePointsRecyclerView);
                flashcardFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));

                hideAndSetLayoutManager(editFormOfCoursePointsRecyclerView);
                editFormOfCoursePointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this, courseID));

                addCoursePointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddCoursePointDialog(true,  CoursePointsScreen.this, courseID);//Because the action was optional, the user can cancel it
                    }
                });
                hide(addCoursePointButton);

                switch (perspective){//Decides which perspective to show
                case 0:
                    show(sentenceFormOfCoursePointsRecyclerView);
                    sentencePerspectiveNavigationItem.setChecked(true); //Show the sentence navigation item as selected
                    break;
                case 1:
                    show(flashcardFormOfCoursePointsRecyclerView);
                    flashcardPerspectiveNavigationItem.setChecked(true);//Show the flashcard navigation item as selected
                    break;
                case 2:
                    show(editFormOfCoursePointsRecyclerView);
                    editPerspectiveNavigationItem.setChecked(true);//Show the edit navigation item as selected
                    break;
                }

                navigationForCoursePointsPerspective.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sentenceListNav:
                                show(sentenceFormOfCoursePointsRecyclerView);

                                hide(flashcardFormOfCoursePointsRecyclerView,editFormOfCoursePointsRecyclerView, addCoursePointButton);

                                //Only shows the course points in their sentence form

                                perspective = 0;
                                return true;
                            case R.id.cardListNav:
                                show(flashcardFormOfCoursePointsRecyclerView);

                                hide(sentenceFormOfCoursePointsRecyclerView,editFormOfCoursePointsRecyclerView, addCoursePointButton);
                                //Only shows the course points in their flashcard form

                                perspective = 1;
                                return true;
                            case R.id.editNav:
                                show(editFormOfCoursePointsRecyclerView,addCoursePointButton);

                                hide(sentenceFormOfCoursePointsRecyclerView, flashcardFormOfCoursePointsRecyclerView);
                                //Only shows the course points in the form which allows editing

                                perspective = 2;
                                return true;
                        }
                        return false;
                    }
                });


            }
        }

        private void hide(View... views){
            for(View view : views){
                view.setVisibility(View.GONE);
            }
        }

        private void show(View... views){
            for(View view : views){
                view.setVisibility(View.VISIBLE);
            }
        }

        private void hideAndSetLayoutManager(RecyclerView recyclerView){
            recyclerView.setVisibility(View.GONE);
            recyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list

        }

        void showAddCoursePointDialog(boolean cancelable, final Context context, final int courseID){
            final AlertDialog.Builder addCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the alert dialog which will allow the user to add a new course point
            addCoursePointAlertDialogBuilder.setTitle(context.getString(R.string.add_new_course_point));//Sets the title of the dialog
            LinearLayout layoutForAddCoursePointAlertDialog = new LinearLayout(context);//Organises the views in a vertical list
            layoutForAddCoursePointAlertDialog.setOrientation(LinearLayout.VERTICAL);

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
                    String userFlashcardFront = flashcardFrontEditableTextView.getText().toString();
                    String userFlashcardBack = flashcardBackEditableTextView.getText().toString();
                    String userSentence = sentenceEditableTextView.getText().toString();

                    if(userFlashcardFront.equals("")||userFlashcardBack.equals("")||userSentence.equals("")){
                        AlertDialog.Builder emptyCoursePointComponentAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);
                        emptyCoursePointComponentAlertDialogBuilder.setTitle(R.string.warning);
                        emptyCoursePointComponentAlertDialogBuilder.setMessage(R.string.blank_components_warning);
                        emptyCoursePointComponentAlertDialogBuilder.setCancelable(false);
                        emptyCoursePointComponentAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());
                        emptyCoursePointComponentAlertDialogBuilder.create().show();
                    }else{
                        CoursePoint coursePoint = new CoursePoint(courseID, userFlashcardFront, userFlashcardBack, userSentence);
                        new InsertCoursePointsToDatabase(context, new RefreshScreenWhenTaskComplete()).execute(coursePoint);//adds the new course point and refreshes the screen
                    }


                }
            });

            if(cancelable){
                addCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel) , AlertDialogHelper.onClickDismissDialog());
            }
            addCoursePointAlertDialogBuilder.setCancelable(cancelable);
            addCoursePointAlertDialogBuilder.create().show();
        }


    }



}