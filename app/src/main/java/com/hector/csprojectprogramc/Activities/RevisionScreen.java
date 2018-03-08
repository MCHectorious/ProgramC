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

    private CommonWordsChecker commonWordsChecker;

    private boolean includeQAQuestions, includeGapQuestions;
    private Random random = new Random();
    private List<CoursePoint> coursePoints;
    private String prompt, correctAnswer;
    private TextView promptTextView;
    private EditText userAnswerEditableTextView;
    private int CourseID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_revision_screen);

        commonWordsChecker = new CommonWordsChecker();

        promptTextView =  findViewById(R.id.questionText);
        userAnswerEditableTextView =  findViewById(R.id.answerText);

        Bundle IntentsBundle = getIntent().getExtras();
        CourseID = IntentsBundle.getInt("course ID",0);

        new getPoints().execute();
    }

    private void generateQuestion(){
        CoursePoint chosenCoursePoint = coursePoints.get(random.nextInt(coursePoints.size()));
        if (includeGapQuestions&&includeQAQuestions){
            if(random.nextBoolean()){
                generateGapQuestion(chosenCoursePoint);
            }else {
                generateQAQuestion(chosenCoursePoint);
            }
        }else if (includeGapQuestions){
            generateGapQuestion(chosenCoursePoint);
        }else{
            generateQAQuestion(chosenCoursePoint);
        }
        promptTextView.setText(prompt);

    }

    private void generateGapQuestion(CoursePoint chosenCoursePoint){
        String sentenceFormOfChosenCoursePoint = chosenCoursePoint.getSentence();
        String[] wordsFromSentenceFormOfChosenCoursePoint = sentenceFormOfChosenCoursePoint.split(" ");
        int hiddenWordIndex = random.nextInt(wordsFromSentenceFormOfChosenCoursePoint.length);
        String hiddenWord = wordsFromSentenceFormOfChosenCoursePoint[hiddenWordIndex];
        if (commonWordsChecker.checkIfCommonWord(hiddenWord)){
            generateGapQuestion(coursePoints.get(random.nextInt(coursePoints.size())));
        }else {
            StringBuilder promptStringBuilder = new StringBuilder();
            for (int wordIndex = 0; wordIndex < wordsFromSentenceFormOfChosenCoursePoint.length; wordIndex++) {
                if (wordIndex == hiddenWordIndex) {
                    for (int hiddenWordCharacterIndex = 0; hiddenWordCharacterIndex < hiddenWord.length(); hiddenWordCharacterIndex++) {
                        promptStringBuilder.append("_");
                    }
                } else {
                    promptStringBuilder.append(wordsFromSentenceFormOfChosenCoursePoint[wordIndex]);
                }
                promptStringBuilder.append(" ");
            }
            prompt = promptStringBuilder.toString();
            correctAnswer = hiddenWord;
        }
    }

    private void generateQAQuestion(CoursePoint chosenCoursePoint){
        prompt = chosenCoursePoint.getFlashcard_front();
        correctAnswer = chosenCoursePoint.getFlashcard_back();
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
            coursePoints = database.customDao().getCoursePointsForCourse(CourseID);

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();
            FloatingActionButton submitAnswerButton = findViewById(R.id.fab);
            submitAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder answerReviewAlertDialogBuilder = new AlertDialog.Builder(RevisionScreen.this);

                    double answerAccuracy = 100*StringDistance.getNormalisedSimilarity(userAnswerEditableTextView.getText().toString().toLowerCase(), correctAnswer.toLowerCase());

                    if(answerAccuracy>90){
                        answerReviewAlertDialogBuilder.setTitle("Well Done - "+ String.format(Locale.UK,"%.2f",answerAccuracy)  + "% Similar!");

                    } else if(answerAccuracy>50){
                        answerReviewAlertDialogBuilder.setTitle("Almost - "+ String.format(Locale.UK,"%.2f",answerAccuracy) + "% Similar");
                    }else{
                        answerReviewAlertDialogBuilder.setTitle("Wrong - Only "+ String.format(Locale.UK,"%.2f",answerAccuracy) + "% Similar");

                    }

                    TextView answerReviewTextView = new TextView(RevisionScreen.this);
                    String answerReviewText = R.string.the_answer_is+correctAnswer+"\".";
                    answerReviewTextView.setText(answerReviewText);

                    answerReviewAlertDialogBuilder.setView(answerReviewTextView);
                    answerReviewAlertDialogBuilder.setPositiveButton("Generate New Question", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            generateQuestion();
                        }
                    });
                    answerReviewAlertDialogBuilder.create().show();
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
