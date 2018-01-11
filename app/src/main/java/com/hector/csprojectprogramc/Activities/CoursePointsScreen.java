package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
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
import android.widget.TextView;
import android.widget.Toast;

import com.hector.csprojectprogramc.Adapter.CardCoursePointsAdapter;
import com.hector.csprojectprogramc.Adapter.EditCoursePointsAdapter;
import com.hector.csprojectprogramc.Adapter.SentencesCoursePointsAdapter;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

import java.util.ArrayList;
import java.util.List;

public class CoursePointsScreen extends AppCompatActivity {

    List<CoursePoints> points;
    int CourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_points);


        Bundle bundle = getIntent().getExtras();
        CourseID = bundle.getInt("course ID");

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
            MyDatabase database = Room.databaseBuilder(CoursePointsScreen.this, MyDatabase.class, "my-db").build();
            points = database.customDao().getPointsForCourse(CourseID);

            return null;

        }

        @Override
        protected void onPostExecute(Void result) {
            progressDialog.dismiss();

            //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CoursePointsScreen.this);

            final RecyclerView cardsRV = findViewById(R.id.cardsRecyclerView);
            cardsRV.setVisibility(View.GONE);
            cardsRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
            CardCoursePointsAdapter cardsAdapter = new CardCoursePointsAdapter(points, CoursePointsScreen.this);
            cardsRV.setAdapter(cardsAdapter);



            final RecyclerView editRV = findViewById(R.id.editsRecyclerView);
            editRV.setVisibility(View.GONE);
            editRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
            EditCoursePointsAdapter editAdapter = new EditCoursePointsAdapter(points, CoursePointsScreen.this);
            editRV.setAdapter(editAdapter);

            final RecyclerView sentencesRV = findViewById(R.id.sentencesRecyclerView);
            sentencesRV.setLayoutManager(new LinearLayoutManager(CoursePointsScreen.this));
            SentencesCoursePointsAdapter sentencesAdapter = new SentencesCoursePointsAdapter(points, CoursePointsScreen.this);
            sentencesRV.setAdapter(sentencesAdapter);//Starts in Sentence

            //final TextView NavigationMessage =  findViewById(R.id.message);
            BottomNavigationView navigation =  findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.sentenceListNav:
                            //NavigationMessage.setText("Sentences");
                            sentencesRV.setVisibility(View.VISIBLE);
                            cardsRV.setVisibility(View.GONE);
                            editRV.setVisibility(View.GONE);
                            return true;
                        case R.id.cardListNav:
                            //NavigationMessage.setText("Cards");
                            sentencesRV.setVisibility(View.GONE);
                            cardsRV.setVisibility(View.VISIBLE);
                            editRV.setVisibility(View.GONE);
                            return true;
                        case R.id.editNav:
                            //NavigationMessage.setText("Edit");
                            sentencesRV.setVisibility(View.GONE);
                            cardsRV.setVisibility(View.GONE);
                            editRV.setVisibility(View.VISIBLE);
                            return true;
                    }
                    return false;
                }
            });


        }


    }
}