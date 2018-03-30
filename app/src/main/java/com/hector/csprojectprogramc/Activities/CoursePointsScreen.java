package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenFlashcardAdapter;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenEditAdapter;
import com.hector.csprojectprogramc.Adapter.CoursePointsScreenSentencesAdapter;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;
public class CoursePointsScreen extends AppCompatActivity {

    //private int courseID;// Stores the ID of the course in the database
    //private int perspective;

    @Override
    protected void onCreate(Bundle savedInstanceState) { // Runs when the screen is created
        super.onCreate(savedInstanceState);// TODO: research what this does
        setContentView(R.layout.course_points_screen);// Links the XML file which defines the layout of the screen

        Bundle intentsBundle = getIntent().getExtras();

        int courseID = intentsBundle.getInt(getString(R.string.course_id),0); //gets the course ID from the previous screen //TODO: make sure it handles null
        int perspective = intentsBundle.getInt(getString(R.string.perspective),0);

        final RecyclerView flashcardsRecyclerView = findViewById(R.id.cardsRecyclerView);// Initialises recycler view which contains the course points in their flashcard form
        final RecyclerView editPointsRecyclerView = findViewById(R.id.editsRecyclerView);//Initialises the recycler view which allows the course points to be edited
        final FloatingActionButton addCoursePointButton = findViewById(R.id.addCoursePointButton);//Initialises the button which allows the user to add a course point
        final RecyclerView sentencesRecyclerView = findViewById(R.id.sentencesRecyclerView); //Initialises recycler view which contains the course points in their sentence form
        BottomNavigationView navigationForCoursePointsPerspective =  findViewById(R.id.navigation);//Intialises the navigation which allows the user to pick which form of course points to show  //TODO: bottom navigation?
        final MenuView.ItemView sentenceListNav =  findViewById(R.id.sentenceListNav);
        final MenuView.ItemView cardListNav =  findViewById(R.id.cardListNav);
        final MenuView.ItemView editNav =  findViewById(R.id.editNav);

        new getCoursePointsFromDatabase(perspective, CoursePointsScreen.this, courseID).execute();// Gets the course points for the course and then after displays them
    }

    private static class getCoursePointsFromDatabase extends AsyncTask<Void, Void, List<CoursePoint>> {// gets the course points for the course from the database in a background thread //TODO: make static
        private ProgressDialog progressDialog;// Stores the dialog which shows the user that a background task is running
        private int perspective, courseID;
        private WeakReference<Context> context;
        private WeakReference<RecyclerView> flashcardsRecyclerView, editPointsRecyclerView,sentencesRecyclerView;
        private WeakReference<FloatingActionButton> addCoursePointButton;
        private WeakReference<BottomNavigationView> navigationForCoursePointsPerspective;
        private WeakReference<MenuView.ItemView> sentenceListNav, cardListNav, editNav;

        private getCoursePointsFromDatabase(int perspective, Context context, int courseID, RecyclerView flashcardsRecyclerView, RecyclerView editPointsRecyclerView, RecyclerView sentencesRecyclerView,
                                            FloatingActionButton addCoursePointFAB, BottomNavigationView navigationForCoursePointsPerspective,MenuView.ItemView sentenceListNav, MenuView.ItemView cardListNav,
                                            MenuView.ItemView editNav){
            this.perspective = perspective;
            this.context = new WeakReference<>(context);
            this.courseID = courseID;
            this.flashcardsRecyclerView = new WeakReference<>(flashcardsRecyclerView);
            this.editPointsRecyclerView = new WeakReference<>(editPointsRecyclerView);
            this.addCoursePointButton = new WeakReference<>(addCoursePointFAB);
            this.sentencesRecyclerView = new WeakReference<>(sentencesRecyclerView);
            this.navigationForCoursePointsPerspective = new WeakReference<>(navigationForCoursePointsPerspective);
            this.sentenceListNav = new WeakReference<>(sentenceListNav);
            this.cardListNav = new WeakReference<>(cardListNav);
            this.editNav = new WeakReference<>(editNav);

        }

        @Override
        protected void onPreExecute() {//Shows the user that a long-running background task is running
            super.onPreExecute();// TODO: research what this does
            progressDialog = new ProgressDialog(context.get());//Idealises the dialog
            progressDialog.setTitle(getString(R.string.loading_course_points));// Explains what this task is doing
            progressDialog.setMessage(getString(R.string.this_should_be_quick));// TODO: change and then explain
            progressDialog.setIndeterminate(false);// The dialog shows an animation which doesn't represent how far throught the task is //TODO: improve description
            progressDialog.show();//Shows the dialog on the screen
        }

        @Override
        protected List<CoursePoint> doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();//Accesses the database
            return database.customDao().getCoursePointsForCourse(courseID);//In order to fulfil the implementation
        }

        @Override
        protected void onPostExecute(List<CoursePoint> coursePoints) {
            progressDialog.dismiss();//hides the alert to the user
            if(coursePoints.size()==0){// Checks whether there are any course points available
                AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder that will create a warning to the user
                String noCoursesWarning = context.get().getString(R.string.you_have_no_courses_points)+ System.getProperty("line.separator")+context.get().getString(R.string.no_courses_points_instructions); //Gets the warning
                noCoursesAlertDialogBuilder.setMessage(noCoursesWarning);//Shows the message
                noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { //Adds an action button which the user can be pressed to add a course point
                        showAddCoursePointDialog(false);//Shows a dialog which forces the user to add a course point
                    }
                });
                noCoursesAlertDialogBuilder.create().show();// Shows the alert dialog
            }else{
                flashcardsRecyclerView.get().setVisibility(View.GONE);//Hides the recycler view with shows the flashcard form of the course points at the start
                flashcardsRecyclerView.get().setLayoutManager(new LinearLayoutManager(context.get())); // Shows the course points in a vertical list
                flashcardsRecyclerView.get().setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));//TODO: add description

                editPointsRecyclerView.get().setVisibility(View.GONE);// Hides the recycler view with shows course points and allows them to be edited
                editPointsRecyclerView.get().setLayoutManager(new LinearLayoutManager(context.get())); // Shows the course points in a vertical list
                editPointsRecyclerView.get().setAdapter(new CoursePointsScreenEditAdapter(coursePoints, context.get(), courseID)); //TODO: add description

                addCoursePointButton.get().setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddCoursePointDialog(true);//Because the action was optional, the user can cancel it
                    }
                });
                addCoursePointButton.get().setVisibility(View.GONE);//TODO: should I always show this?

                sentencesRecyclerView.get().setLayoutManager(new LinearLayoutManager(context.get())); // Shows the course points in a vertical list
                sentencesRecyclerView.get().setAdapter(new CoursePointsScreenSentencesAdapter(coursePoints));//Starts the activity by showing the sentence form of the course points
                sentencesRecyclerView.get().setVisibility(View.GONE);

                switch (perspective){//Decides which perspective to show
                    case 0:
                        sentencesRecyclerView.get().setVisibility(View.VISIBLE); //Shows sentence perspective
                        findViewById(R.id.sentenceListNav).setSelected(true); //Show the sentence navigation item as selected
                        break;
                    case 1:
                        flashcardsRecyclerView.get().setVisibility(View.VISIBLE); //Shows flashcard perspective
                        findViewById(R.id.cardListNav).setSelected(true);//Show the flashcard navigation item as selected
                        break;
                    case 2:
                        editPointsRecyclerView.get().setVisibility(View.VISIBLE); //Show edit perspective
                        findViewById(R.id.editNav).setSelected(true);//Show the edit navigation item as selected
                        break;
                }

                navigationForCoursePointsPerspective.get().setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sentenceListNav:
                                sentencesRecyclerView.get().setVisibility(View.VISIBLE);
                                flashcardsRecyclerView.get().setVisibility(View.GONE);
                                editPointsRecyclerView.get().setVisibility(View.GONE);
                                addCoursePointButton.get().setVisibility(View.GONE); //TODO: should I always show this?
                                //Only shows the course points in their sentence form

                                perspective = 0;
                                return true;
                            case R.id.cardListNav:
                                sentencesRecyclerView.get().setVisibility(View.GONE);
                                flashcardsRecyclerView.get().setVisibility(View.VISIBLE);
                                editPointsRecyclerView.get().setVisibility(View.GONE);
                                addCoursePointButton.get().setVisibility(View.GONE);
                                //Only shows the course points in their flashcard form

                                perspective = 1;
                                return true;
                            case R.id.editNav:
                                sentencesRecyclerView.get().setVisibility(View.GONE);
                                flashcardsRecyclerView.get().setVisibility(View.GONE);
                                editPointsRecyclerView.get().setVisibility(View.VISIBLE);
                                addCoursePointButton.get().setVisibility(View.VISIBLE);
                                //Only shows the course points in the form which allows editing

                                perspective = 2;
                                return true;
                        }
                        return false;
                    }
                });


            }
        }
    }

    public void showAddCoursePointDialog(boolean cancelable, final int perspective){
        final AlertDialog.Builder AddCoursePointAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);//Initialises the alert dialog which will allow the user to add a new course point
        AddCoursePointAlertDialogBuilder.setTitle(getString(R.string.add_new_course_point));//Sets the title of the dialog
        LinearLayout layoutForAlertDialog = new LinearLayout(CoursePointsScreen.this);//Organises the views in a vertical list
        layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL); //TODO: do i need to do this
        final EditText cardFrontEditableTextView = new EditText(CoursePointsScreen.this);// Initialises the area where the user can add the front of the flashcard form of the course point
        cardFrontEditableTextView.setHint( getString(R.string.enter_flashcard_front) );//Provides instructions for the user
        layoutForAlertDialog.addView(cardFrontEditableTextView);//Adds view to the layout
        final EditText cardBackEditableTextView = new EditText(CoursePointsScreen.this);// Initialises the area where the user can add the back of the flashcard form of the course point
        cardBackEditableTextView.setHint(getString(R.string.enter_flashcard_back) );//Provides instructions for the user
        layoutForAlertDialog.addView(cardBackEditableTextView);//Adds view to the layout
        final EditText sentenceEditableTextView = new EditText(CoursePointsScreen.this); // Initialises the area where the user can add the sentence form of the course point
        sentenceEditableTextView.setHint(getString(R.string.enter_sentence) );//Provides instructions for the user
        layoutForAlertDialog.addView(sentenceEditableTextView);//Adds view to the layout
        AddCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);//Adds the list of view to the dialog
        AddCoursePointAlertDialogBuilder.setPositiveButton(getString(R.string.add), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] coursePointComponents = {cardFrontEditableTextView.getText().toString(),cardBackEditableTextView.getText().toString(),sentenceEditableTextView.getText().toString()};//The aspects of the new course point
                new addCoursePointToDatabase(courseID, CoursePointsScreen.this, perspective).execute(coursePointComponents);//adds the new course point and refreshes the screen
            }
        });
        if(cancelable){
            AddCoursePointAlertDialogBuilder.setNegativeButton( getString(R.string.cancel) , new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//Just closes the dialog if it is cancelled
                }
            });
        }
        AddCoursePointAlertDialogBuilder.setCancelable(cancelable);
        AddCoursePointAlertDialogBuilder.create().show();
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