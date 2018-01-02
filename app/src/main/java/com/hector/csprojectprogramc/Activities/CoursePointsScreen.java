package com.hector.csprojectprogramc.Activities;

import android.arch.persistence.room.Room;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.hector.csprojectprogramc.Adapter.CardCoursePointsAdapter;
import com.hector.csprojectprogramc.Adapter.SentencesCoursePointsAdapter;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

import java.util.ArrayList;

public class CoursePointsScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_points);



        int courseID = 1;//TODO: do automatically

        MyDatabase database = Room.databaseBuilder(getApplicationContext(),MyDatabase.class,"my-db").build();
        //ArrayList<CoursePoints> points =  database.customDao().getCoursePointsForCourse(courseID);
        ArrayList<CoursePoints>  points = new ArrayList<>();
        points.add(new CoursePoints(1,"hi","bye","hi-bye"));


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        final RecyclerView cardsRV = findViewById(R.id.cardsRecyclerView);
        cardsRV.setVisibility(View.GONE);
        cardsRV.setLayoutManager(linearLayoutManager);
        CardCoursePointsAdapter cardsAdapter = new CardCoursePointsAdapter(points,this);
        cardsRV.setAdapter(cardsAdapter);

        final RecyclerView sentencesRV = findViewById(R.id.sentencesRecyclerView);

        sentencesRV.setLayoutManager(linearLayoutManager);
        SentencesCoursePointsAdapter sentencesAdapter = new SentencesCoursePointsAdapter(points, this);
        sentencesRV.setAdapter(sentencesAdapter);

        final RecyclerView editRV = findViewById(R.id.editsRecyclerView);
        editRV.setVisibility(View.GONE);
        editRV.setLayoutManager(linearLayoutManager);
        SentencesCoursePointsAdapter editAdapter = new SentencesCoursePointsAdapter(points, this);
        sentencesRV.setAdapter(editAdapter);


        final TextView NavigationMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.sentenceListNav:
                        NavigationMessage.setText("Sentences");
                        sentencesRV.setVisibility(View.VISIBLE);
                        cardsRV.setVisibility(View.GONE);
                        editRV.setVisibility(View.GONE);
                        return true;
                    case R.id.cardListNav:
                        NavigationMessage.setText("Cards");
                        sentencesRV.setVisibility(View.GONE);
                        cardsRV.setVisibility(View.VISIBLE);
                        editRV.setVisibility(View.GONE);
                        return true;
                    case R.id.editNav:
                        NavigationMessage.setText("Edit");
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
