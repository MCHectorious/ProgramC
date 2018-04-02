package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
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
import android.view.View;
import android.widget.TextView;
import com.hector.csprojectprogramc.Adapter.HomeScreenCoursesRecyclerAdapter;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class HomeScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.home);
        setSupportActionBar(toolbar);

        FloatingActionButton toExamBoardScreenButton =  findViewById(R.id.fab);
        toExamBoardScreenButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent toExamBoardScreen = new Intent(HomeScreen.this, ExamBoardScreen.class);
                startActivity(toExamBoardScreen);
            }
        });

        RecyclerView CoursesRecyclerView = findViewById(R.id.cardList);
        new getAllCoursesFromDatabase(HomeScreen.this,CoursesRecyclerView).execute();

    }

    public static void showNoCoursesAlertDialog(final Context context){
        AlertDialog.Builder noCoursesAlertDialogBuilder = new AlertDialog.Builder(context);
        TextView noCoursesWarningTextView = new TextView(context);
        String noCoursesWarningText = context.getString(R.string.you_have_no_courses)+ System.getProperty("line.separator")+context.getString(R.string.no_courses_instructions);
        noCoursesWarningTextView.setText(noCoursesWarningText);
        noCoursesAlertDialogBuilder.setView(noCoursesWarningTextView);
        noCoursesAlertDialogBuilder.setCancelable(false).setPositiveButton( context.getString(R.string.okay) , new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent toExamBoardScreen = new Intent(context, ExamBoardScreen.class);
                context.startActivity(toExamBoardScreen);
            }
        });
        noCoursesAlertDialogBuilder.create().show();
    }


    private static class getAllCoursesFromDatabase extends AsyncTask<Void,Void,List<Course>>{
        private ProgressDialog progressDialog;
        private WeakReference<Context> context;
        private WeakReference<RecyclerView>  CoursesRecyclerView;

        private getAllCoursesFromDatabase(Context context,RecyclerView CoursesRecyclerView){
            this.context = new WeakReference<>(context);
            this.CoursesRecyclerView = new WeakReference<>(CoursesRecyclerView);
        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context.get());
            progressDialog.setTitle(context.get().getString(R.string.initialising_app));
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected List<Course> doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();
            return database.customDao().getAllCourses();

        }

        @Override
        protected void onPostExecute(List<Course> courses){
           progressDialog.dismiss();
            if(courses.size()>0){



                CoursesRecyclerView.get().setLayoutManager(new LinearLayoutManager(context.get()));
                CoursesRecyclerView.get().setAdapter(new HomeScreenCoursesRecyclerAdapter(courses, context.get()));
            }else{
                showNoCoursesAlertDialog(context.get());
            }
        }
    }

}
