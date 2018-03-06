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
import com.hector.csprojectprogramc.Adapter.CardCoursePointsAdapter;
import com.hector.csprojectprogramc.Adapter.EditCoursePointsAdapter;
import com.hector.csprojectprogramc.Adapter.SentencesCoursePointsAdapter;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;
import java.util.List;

public class CoursePointsScreen extends AppCompatActivity {

    private List<CoursePoints> points;
    private int CourseID;
    private RecyclerView editRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_points);
        editRV = findViewById(R.id.editsRecyclerView);
        Bundle bundle = getIntent().getExtras();
        CourseID = bundle.getInt("course ID",0);
        new getPoints().execute();
    }

    private class getPoints extends AsyncTask<Void, Void, Void> {
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
            points = database.customDao().getPointsForCourse(CourseID);
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();
            if(points.size()==0){
                AlertDialog.Builder builder = new AlertDialog.Builder(CoursePointsScreen.this);
                TextView textView = new TextView(CoursePointsScreen.this);
                String textViewText = R.string.you_have_no_courses_points+ System.getProperty("line.separator")+R.string.no_courses_points_instructions;
                textView.setText(textViewText);
                builder.setView(textView);
                builder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addCoursePointDialog(false);
                    }
                });
                builder.create().show();
            }else{
                final RecyclerView cardsRV = findViewById(R.id.cardsRecyclerView);
                cardsRV.setVisibility(View.GONE);
                cardsRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                CardCoursePointsAdapter cardsAdapter = new CardCoursePointsAdapter(points);
                cardsRV.setAdapter(cardsAdapter);

                editRV.setVisibility(View.GONE);
                editRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                EditCoursePointsAdapter editAdapter = new EditCoursePointsAdapter(points, CoursePointsScreen.this);
                editRV.setAdapter(editAdapter);
                final FloatingActionButton fab = findViewById(R.id.addCoursePointButton);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addCoursePointDialog(true);
                    }
                });
                fab.setVisibility(View.GONE);

                final RecyclerView sentencesRV = findViewById(R.id.sentencesRecyclerView);
                sentencesRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
                SentencesCoursePointsAdapter sentencesAdapter = new SentencesCoursePointsAdapter(points);
                sentencesRV.setAdapter(sentencesAdapter);//Starts in Sentence

                BottomNavigationView navigation =  findViewById(R.id.navigation);
                navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sentenceListNav:
                                sentencesRV.setVisibility(View.VISIBLE);
                                cardsRV.setVisibility(View.GONE);
                                editRV.setVisibility(View.GONE);
                                fab.setVisibility(View.GONE);
                                return true;
                            case R.id.cardListNav:
                                sentencesRV.setVisibility(View.GONE);
                                cardsRV.setVisibility(View.VISIBLE);
                                editRV.setVisibility(View.GONE);
                                fab.setVisibility(View.GONE);
                                return true;
                            case R.id.editNav:
                                sentencesRV.setVisibility(View.GONE);
                                cardsRV.setVisibility(View.GONE);
                                editRV.setVisibility(View.VISIBLE);
                                fab.setVisibility(View.VISIBLE);
                                return true;
                        }
                        return false;
                    }
                });

                AlertDialog.Builder builder = new AlertDialog.Builder(CoursePointsScreen.this);
                TextView textView = new TextView(CoursePointsScreen.this);
                String textViewText = R.string.machine_generated_sentences_warning+ System.getProperty("line.separator")+R.string.edit_tab_instructions;
                textView.setText(textViewText);
                builder.setView(textView);
                builder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addCoursePointDialog(false);
                    }
                });
                builder.create().show();
            }
        }
    }

    public void addCoursePointDialog(boolean cancelable){
        final AlertDialog.Builder builder = new AlertDialog.Builder(CoursePointsScreen.this);
        builder.setTitle("Add New Course Point");
        LinearLayout layout = new LinearLayout(CoursePointsScreen.this);
        layout.setOrientation(LinearLayout.VERTICAL);
        final EditText cardFrontEdit = new EditText(CoursePointsScreen.this);
        cardFrontEdit.setHint("Enter the front of the point's flashcard form" );
        layout.addView(cardFrontEdit);
        final EditText cardBackEdit = new EditText(CoursePointsScreen.this);
        cardBackEdit.setHint("Enter the back of the point's flashcard form" );
        layout.addView(cardBackEdit);
        final EditText sentenceEdit = new EditText(CoursePointsScreen.this);
        sentenceEdit.setHint("Enter the point's sentence form" );
        layout.addView(sentenceEdit);
        builder.setView(layout);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String[] textArray = {cardFrontEdit.getText().toString(),cardBackEdit.getText().toString(),sentenceEdit.getText().toString()};
                new addCoursePoint().execute(textArray);
            }
        });
        if(cancelable){
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });
        }
        builder.setCancelable(cancelable);
        builder.create().show();
    }

    private class addCoursePoint extends AsyncTask<String,Void,Void>{
        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(CoursePointsScreen.this, MainDatabase.class, "my-db").build();
            database.customDao().insertCoursePoint(new CoursePoints(CourseID,strings[0],strings[1],strings[2]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent intent = new Intent(CoursePointsScreen.this, CoursePointsScreen.class);
            intent.putExtra("course ID",CourseID);
            startActivity(intent);
        }
    }
}