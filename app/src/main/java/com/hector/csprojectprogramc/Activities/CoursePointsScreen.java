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

public class CoursePointsScreen extends AppCompatActivity {

    private List<CoursePoint> coursePoints;
    private int CourseID;
    private RecyclerView editPointsRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_points);
        editPointsRecyclerView = findViewById(R.id.editsRecyclerView);
        Bundle bundle = getIntent().getExtras();
        CourseID = bundle.getInt("course ID",0);
        new getCoursePointsFromDatabase().execute();
    }

    private class getCoursePointsFromDatabase extends AsyncTask<Void, Void, Void> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(CoursePointsScreen.this);
            progressDialog.setTitle("Loading Testable Material ");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(CoursePointsScreen.this, MainDatabase.class, "my-db").build();
            coursePoints = database.customDao().getCoursePointsForCourse(CourseID);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if(coursePoints.size()==0){
                AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(CoursePointsScreen.this);
                TextView noCoursesWarningTextView = new TextView(CoursePointsScreen.this);
                String noCoursesWarning = R.string.you_have_no_courses_points+ System.getProperty("line.separator")+R.string.no_courses_points_instructions;
                noCoursesWarningTextView.setText(noCoursesWarning);
                noCoursesAlertDialogBuilder.setView(noCoursesWarningTextView);
                noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showAddCoursePointDialog(false);
                    }
                });
                noCoursesAlertDialogBuilder.create().show();
            }else{
                final RecyclerView flashcardsRecyclerView = findViewById(R.id.cardsRecyclerView);
                flashcardsRecyclerView.setVisibility(View.GONE);
                flashcardsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                flashcardsRecyclerView.setAdapter(new CoursePointsScreenFlashcardAdapter(coursePoints));

                editPointsRecyclerView.setVisibility(View.GONE);
                editPointsRecyclerView.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                editPointsRecyclerView.setAdapter(new CoursePointsScreenEditAdapter(coursePoints, CoursePointsScreen.this));
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
                String machineLearningWarningText = R.string.machine_generated_sentences_warning+ System.getProperty("line.separator")+R.string.edit_tab_instructions;
                machineLearningWarningTextView.setText(machineLearningWarningText);
                machineLearningWarningAlertDialogBuilder.setView(machineLearningWarningTextView);
                machineLearningWarningAlertDialogBuilder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        showAddCoursePointDialog(false);
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
            database.customDao().insertCoursePoint(new CoursePoint(CourseID,strings[0],strings[1],strings[2]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent refreshScreen = new Intent(CoursePointsScreen.this, CoursePointsScreen.class);
            refreshScreen.putExtra("course ID",CourseID);
            startActivity(refreshScreen);
        }
    }
}