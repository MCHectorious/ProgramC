package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenFlashcardAdapter;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenEditAdapter;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenSentencesAdapter;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;
import java.util.List;
//TODO: implement string resources
public class CoursePointsScreen extends AppCompatActivity {

    private List<CoursePoint> coursePoints;// Stores the course points that will be displayed //TODO: remove as field
    private int courseID;// Stores the ID of the course in the database
    private RecyclerView editPointsRecyclerView;// Stores the recycler view which allows the courses points to be edited

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when the screen is created
        super.onCreate(savedInstanceState);// TODO: research what this does
        setContentView(R.layout.course_points_screen);// Links the XML file which defines the layout of the screen
        editPointsRecyclerView = findViewById(R.id.editsRecyclerView);//Initialises the recycler view which allows the course points to be edited
        courseID = getIntent().getExtras().getInt("course ID",0); //gets the course ID from the previous screen //TODO: make sure it handles null
        new getCoursePointsFromDatabase().execute();// Gets the course points for the course and then after displays them
    }

    private class getCoursePointsFromDatabase extends AsyncTask<Void, Void, Void> {// gets the course points for the course from the database in a background thread //TODO: make static
        ProgressDialog progressDialog;// Stores the dialog which shows the user that a background task is running
        @Override
        protected void onPreExecute() {//Shows the user that a long-running background task is running
            super.onPreExecute();// TODO: research what this does
            progressDialog = new ProgressDialog(CoursePointsScreen.this);//Idealises the dialog
            progressDialog.setTitle("Loading Testable Material ");// Explains what this task is doing
            progressDialog.setMessage("This should only take a moment");// TODO: change and then explain
            progressDialog.setIndeterminate(false);// The dialog shows an animation which doesn't represent how far throught the task is //TODO: improve description
            progressDialog.show();//Shows the dialog on the screen
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(CoursePointsScreen.this, MainDatabase.class, "my-db").build();//Accesses the database
            coursePoints = database.customDao().getCoursePointsForCourse(courseID);//Gets the course
            return null;//In order to fulfil the implementation
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();//hides the alert to the user
            if(coursePoints.size()==0){// Checks whether there are any course points available
                AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises the builder that will create a warning to the user
                TextView noCoursesWarningTextView = new TextView(CoursePointsScreen.this); // Initialises the text view which contains the warning to screen //TODO: do i need a text view
                String noCoursesWarning = getString(R.string.you_have_no_courses_points)+ System.getProperty("line.separator")+getString(R.string.no_courses_points_instructions); //Gets the warning
                noCoursesWarningTextView.setText(noCoursesWarning); // Sets the text of the text view to the warning
                noCoursesAlertDialogBuilder.setView(noCoursesWarningTextView); // adds the text view to the alert dialog
                noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //Adds an action button which the user can be pressed to add a course point
                        showAddCoursePointDialog(false);//Shows a dialog which forces the user to add a course point
                    }
                });
                noCoursesAlertDialogBuilder.create().show();// Shows the alert dialog
            }else{
                final RecyclerView flashcardsRecyclerView = findViewById(R.id.cardsRecyclerView);// Initialises recycler view which contains the course points in their flashcard form
                flashcardsRecyclerView.setVisibility(View.GONE);//Hides the recycler view with shows the flashcard form of the course points at the start
                flashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
                flashcardsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));//TODO: add description

                editPointsRecyclerView.setVisibility(View.GONE);// Hides the recycler view with shows course points and allows them to be edited
                editPointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this)); // Shows the course points in a vertical list
                editPointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this, courseID)); //TODO: add description

                final FloatingActionButton addCoursePointButton = findViewById(R.id.addCoursePointButton);
                addCoursePointButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddCoursePointDialog(true);
                    }
                });
                addCoursePointButton.setVisibility(View.GONE);

                final RecyclerView sentencesRecyclerView = findViewById(R.id.sentencesRecyclerView);
                sentencesRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                sentencesRecyclerView.setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Starts in Sentence

                BottomNavigationView navigationForCoursePointsPerspective =  findViewById(R.id.navigation);
                navigationForCoursePointsPerspective.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sentenceListNav:
                                sentencesRecyclerView.setVisibility(View.VISIBLE);
                                flashcardsRecyclerView.setVisibility(View.GONE);
                                editPointsRecyclerView.setVisibility(View.GONE);
                                addCoursePointButton.setVisibility(View.GONE);
                                return true;
                            case R.id.cardListNav:
                                sentencesRecyclerView.setVisibility(View.GONE);
                                flashcardsRecyclerView.setVisibility(View.VISIBLE);
                                editPointsRecyclerView.setVisibility(View.GONE);
                                addCoursePointButton.setVisibility(View.GONE);
                                return true;
                            case R.id.editNav:
                                sentencesRecyclerView.setVisibility(View.GONE);
                                flashcardsRecyclerView.setVisibility(View.GONE);
                                editPointsRecyclerView.setVisibility(View.VISIBLE);
                                addCoursePointButton.setVisibility(View.VISIBLE);
                                return true;
                        }
                        return false;
                    }
                });

                AlertDialog.Builder machineLearningWarningAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);
                TextView machineLearningWarningTextView = new TextView(CoursePointsScreen.this);
                String machineLearningWarningText = getString(R.string.machine_generated_sentences_warning)+ System.getProperty("line.separator")+getString(R.string.edit_tab_instructions);
                machineLearningWarningTextView.setText(machineLearningWarningText);
                machineLearningWarningAlertDialogBuilder.setView(machineLearningWarningTextView);
                machineLearningWarningAlertDialogBuilder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });
                machineLearningWarningAlertDialogBuilder.create().show();
            }
        }
    }

    public void showAddCoursePointDialog(boolean cancelable){
        final AlertDialog.Builder AddCoursePointAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);
        AddCoursePointAlertDialogBuilder.setTitle("Add New Course Point");
        LinearLayout layoutForAlertDialog = new LinearLayout(CoursePointsScreen.this);
        layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL);
        final EditText cardFrontEditableTextView = new EditText(CoursePointsScreen.this);
        cardFrontEditableTextView.setHint("Enter the front of the point's flashcard form" );
        layoutForAlertDialog.addView(cardFrontEditableTextView);
        final EditText cardBackEditableTextView = new EditText(CoursePointsScreen.this);
        cardBackEditableTextView.setHint("Enter the back of the point's flashcard form" );
        layoutForAlertDialog.addView(cardBackEditableTextView);
        final EditText sentenceEditableTextView = new EditText(CoursePointsScreen.this);
        sentenceEditableTextView.setHint("Enter the point's sentence form" );
        layoutForAlertDialog.addView(sentenceEditableTextView);
        AddCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);
        AddCoursePointAlertDialogBuilder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] coursePointComponents = {cardFrontEditableTextView.getText().toString(),cardBackEditableTextView.getText().toString(),sentenceEditableTextView.getText().toString()};
                new addCoursePointToDatabase().execute(coursePointComponents);
            }
        });
        if(cancelable){
            AddCoursePointAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        AddCoursePointAlertDialogBuilder.setCancelable(cancelable);
        AddCoursePointAlertDialogBuilder.create().show();
    }

    private class addCoursePointToDatabase extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(CoursePointsScreen.this, MainDatabase.class, "my-db").build();
            database.customDao().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent refreshScreen = new Intent(CoursePointsScreen.this, CoursePointsScreen.class);
            refreshScreen.putExtra("course ID", courseID);
            startActivity(refreshScreen);
        }
    }
}