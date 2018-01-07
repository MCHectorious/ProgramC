package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RevisionScreen extends AppCompatActivity {

    boolean includeQAQuestions, includeGapQuestions;
    Random random = new Random();
    List<CoursePoints> points;
    String prompt, correctAnswer;
    TextView promptView, answerView;
    int CourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        promptView = (TextView) findViewById(R.id.questionText);
        answerView = (TextView) findViewById(R.id.answerText);


        Bundle bundle = getIntent().getExtras();
        CourseID = bundle.getInt("course ID"); //TODO: Do automatically

        new getPoints().execute();





    }

    public void generateQuestion(){
        if (includeGapQuestions&&includeQAQuestions){
            if(random.nextBoolean()){
                generateGapQuestion();
            }else {
                generateQAQuestion();
            }
        }else if (includeGapQuestions){
            generateGapQuestion();
        }else{
            generateQAQuestion();
        }
        promptView.setText(prompt);

    }

    public void generateGapQuestion(){
        CoursePoints coursePoint = points.get(random.nextInt(points.size()));
        String sentence = coursePoint.getSentence();
        String[] words = sentence.split(" ");
        int missingWordIndex = random.nextInt(words.length);
        String missingWord = words[missingWordIndex];
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if(i==missingWordIndex){
                for (int j = 0; j < missingWord.length(); j++) {
                    builder.append("_");
                }
            }else{
                builder.append(words[i]);
            }
            builder.append(" ");
        }
        prompt = builder.toString();
        correctAnswer = missingWord;


    }

    public void generateQAQuestion(){
        CoursePoints coursePoint = points.get(random.nextInt(points.size()));
        prompt = coursePoint.getFlashcard_front();
        correctAnswer = coursePoint.getFlashcard_back();
    }

    private class getPoints extends AsyncTask<Void,Void,Void>{
        ProgressDialog progressDialog;

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(RevisionScreen.this);
            progressDialog.setTitle("Loading Testable Material ");
            progressDialog.setMessage("This should only take a moment");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MyDatabase database = Room.databaseBuilder(RevisionScreen.this, MyDatabase.class, "my-db").build();
            points = database.customDao().getPointsForCourse(CourseID);

            return null;

        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();

            generateQuestion();

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(answerView.getText().equals(correctAnswer)){
                        Toast toast = Toast.makeText(getApplicationContext(),"Well done. You answered correctly.",Toast.LENGTH_LONG);
                        toast.show();
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(),"Wrong. The correct answer is "+correctAnswer+".",Toast.LENGTH_LONG);
                        toast.show();
                    }
                    generateQuestion();
                }
            });
        }
    }

}
