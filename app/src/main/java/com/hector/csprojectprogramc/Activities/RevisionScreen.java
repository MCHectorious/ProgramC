package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CommonWordsChecker;
import com.hector.csprojectprogramc.Util.StringDistance;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RevisionScreen extends AppCompatActivity {

    private CommonWordsChecker check;

    private boolean includeQAQuestions, includeGapQuestions;
    private Random random = new Random();
    private List<CoursePoint> points;
    private String prompt, correctAnswer;
    private TextView promptView;
    private EditText answerView;
    private int CourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_screen);
        //Toolbar toolbar =  findViewById(R.id.toolbar);

        check = new CommonWordsChecker();

        promptView =  findViewById(R.id.questionText);
        answerView =  findViewById(R.id.answerText);

        Bundle bundle = getIntent().getExtras();
        CourseID = bundle.getInt("course ID",0);

        new getPoints().execute();
    }

    private void generateQuestion(){
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

    private void generateGapQuestion(){
        CoursePoint coursePoint = points.get(random.nextInt(points.size()));
        String sentence = coursePoint.getSentence();
        String[] words = sentence.split(" ");
        int missingWordIndex = random.nextInt(words.length);
        String missingWord = words[missingWordIndex];
        if (check.checkIfCommonWord(missingWord)){
            generateGapQuestion();
        }else {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < words.length; i++) {
                if (i == missingWordIndex) {
                    for (int j = 0; j < missingWord.length(); j++) {
                        builder.append("_");
                    }
                } else {
                    builder.append(words[i]);
                }
                builder.append(" ");
            }
            prompt = builder.toString();
            correctAnswer = missingWord;
        }
    }

    private void generateQAQuestion(){
        CoursePoint coursePoint = points.get(random.nextInt(points.size()));
        prompt = coursePoint.getFlashcard_front();
        correctAnswer = coursePoint.getFlashcard_back();
    }

    private class getPoints extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progressDialog;

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
            MainDatabase database = Room.databaseBuilder(RevisionScreen.this, MainDatabase.class, "my-db").build();
            points = database.customDao().getCoursePointsForCourse(CourseID);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();

            Log.i("Got this far","Finished Revision Screen Background");

            FloatingActionButton fab = findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(RevisionScreen.this);

                    TextView textView = new TextView(RevisionScreen.this);

                    double similarity = 100*StringDistance.getNormalisedSimilarity(answerView.getText().toString().toLowerCase(), correctAnswer.toLowerCase());

                    if(similarity>90){
                        builder.setTitle("Well Done - "+ String.format(Locale.UK,"%.2f",similarity)  + "% Similar!");

                    } else if(similarity>50){
                        builder.setTitle("Almost - "+ String.format(Locale.UK,"%.2f",similarity) + "% Similar");
                    }else{
                        builder.setTitle("Wrong - Only "+ String.format(Locale.UK,"%.2f",similarity) + "% Similar");

                    }

                    String textViewText = R.string.the_answer_is+correctAnswer+"\".";
                    textView.setText(textViewText);

                    builder.setView(textView);
                    builder.setPositiveButton("Generate New Question", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            generateQuestion();
                        }
                    });
                    builder.create().show();
                }
            });
            includeGapQuestions = true;
            includeQAQuestions = true;

            final TextView QuestionAnswerOption = findViewById(R.id.QuestionAnswerOption);
            final ImageView QuestionAnswerIcon = findViewById(R.id.QuestionAnswerIcon);
            final TextView GapOption = findViewById(R.id.FillInTheGapOption);
            final ImageView GapIcon = findViewById(R.id.FillInTheGapIcon);

            View.OnClickListener QuestionAnswerOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (includeGapQuestions){
                        includeQAQuestions = !includeQAQuestions;
                        QuestionAnswerOption.setTextColor((includeQAQuestions)? getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.black));
                        QuestionAnswerIcon.setImageResource((includeQAQuestions)? R.drawable.question_answer_icon_accent:R.drawable.question_answer_icon_black);
                    }
                }
            };

            View.OnClickListener GapOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (includeQAQuestions){
                        includeGapQuestions = !includeGapQuestions;
                        GapOption.setTextColor((includeGapQuestions)? getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.black));
                        GapIcon.setImageResource((includeGapQuestions)? R.drawable.fill_in_the_gap_icon_accent:R.drawable.fill_in_the_gap_icon_black);

                    }
                }
            };

            QuestionAnswerIcon.setOnClickListener(QuestionAnswerOnClick);
            QuestionAnswerOption.setOnClickListener(QuestionAnswerOnClick);

            GapIcon.setOnClickListener(GapOnClick);
            GapOption.setOnClickListener(GapOnClick);

            generateQuestion();
        }
    }

}
