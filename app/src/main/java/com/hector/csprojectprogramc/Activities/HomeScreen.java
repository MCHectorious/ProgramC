package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.hector.csprojectprogramc.Adapter.HomeScreenRecyclerAdapter;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

import java.util.List;

public class HomeScreen extends AppCompatActivity {
    private boolean haveSubjects;
    private List<Course> savedCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);








        new getSavedCourses().execute();
        if(haveSubjects){
            FloatingActionButton fab =  findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                    startActivity(toExamBoardScreen);
                }
            });

            RecyclerView recyclerView = findViewById(R.id.cardList);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            HomeScreenRecyclerAdapter adapter = new HomeScreenRecyclerAdapter(savedCourses, HomeScreen.this);
            recyclerView.setAdapter(adapter);
        }else{
            showNoSubjectAlertDialog();
        }
        //List<Course> savedCourses = database.customDao().getAllSavedCourses();
        //ArrayList<Course> savedCourses = new ArrayList<>();
        //savedCourses.add(new Course("Accounting","Accounting (2120)",
                //"http://www.aqa.org.uk/subjects/accounting/as-and-a-level/accounting-2120",
                //"AQA","A Level",
                //"15 May 2018","Exam for AS and A-level Accounting 1 June 2018 series"));

        /*for(Course course : savedCourses){
            Log.i("Official Name",course.getOfficial_name());
        }*/




    }

    public void showNoSubjectAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        TextView textView = new TextView(this);
        textView.setText("You currently have no courses."+ System.getProperty("line.separator") + "To add courses and continue click OKAY");
        alertDialogBuilder.setView(textView);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OKAY", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class getSavedCourses extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(HomeScreen.this);
            progressDialog.setTitle("Initialising App");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MyDatabase database = Room.databaseBuilder(HomeScreen.this, MyDatabase.class, "my-db").build();
            savedCourses = database.customDao().getAllSavedCourses();
            //Log.i("No. of Course", Integer.toString(savedCourses.size())  );
            haveSubjects = savedCourses.size()==0;
            Log.i("haveSubjects",Boolean.toString(haveSubjects) );
            return null;

        }

        @Override
        protected void onPostExecute(Void result){
           progressDialog.dismiss();
        }
    }

}
