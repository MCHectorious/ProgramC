package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Utilities.CommonWordsChecker;
import com.hector.csprojectprogramc.Utilities.StringDistance;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RevisionScreen extends AppCompatActivity {

    private boolean includeQAQuestions, includeGapQuestions;// Booleans used to decide what type of question to ask
    private Random random = new Random(); // Used to randomly choose which type of question to ask and to randomly choose which course point ot test on
    private List<CoursePoint> coursePoints;// The aspects of the course which can be tested on
    private String prompt, correctAnswer; //The question to be showed to user and the actual answer to that question
    private TextView promptTextView;
    private EditText userAnswerEditableTextView; //Where the user inputs their answer
    private int CourseID; // The id of the course the user is being test on

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //TODO: research what this does
        setContentView(R.layout.revision_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.revision);
        setSupportActionBar(toolbar);


        promptTextView =  findViewById(R.id.questionText);
        userAnswerEditableTextView =  findViewById(R.id.answerText);

        CourseID = getIntent().getExtras().getInt(getString(R.string.course_id),0);//TODO: change to just get int

        new getCoursePoints().execute();
    }

    private void generateQuestion(){
        CoursePoint chosenCoursePoint = coursePoints.get(random.nextInt(coursePoints.size()));// Gets a randomly chosen course point //TODO: cache coursePointsSize
        if (includeGapQuestions&&includeQAQuestions){
            if(random.nextBoolean()){//If both are available it chooses randomly
                generateGapQuestion(chosenCoursePoint);
            }else {
                generateQAQuestion(chosenCoursePoint);
            }
        }else if (includeGapQuestions){ //If only gap questions are available
            generateGapQuestion(chosenCoursePoint);
        }else{
            generateQAQuestion(chosenCoursePoint);//If only QA questions are available
        }
        promptTextView.setText(prompt);//Shows the question on the screen

    }

    private void generateGapQuestion(CoursePoint chosenCoursePoint){
        String sentenceFormOfChosenCoursePoint = chosenCoursePoint.getSentence();
        String[] wordsFromSentenceFormOfChosenCoursePoint = sentenceFormOfChosenCoursePoint.split("[\\p{Punct}\\s]+"); //Generates an array of each word in the sentence form of the course point
        int hiddenWordIndex = random.nextInt(wordsFromSentenceFormOfChosenCoursePoint.length);//Randomly decides the index of the word to be hidden
        String hiddenWord = wordsFromSentenceFormOfChosenCoursePoint[hiddenWordIndex];//The hidden word
        if (CommonWordsChecker.checkIfCommonWord(hiddenWord)){
            generateQuestion(); //If the word shouldn't be chosen then it generates a new question
        }else {

            int index = sentenceFormOfChosenCoursePoint.indexOf(hiddenWord);
            StringBuilder promptStringBuilder = new StringBuilder();
            promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(0,index));
            for (int hiddenWordCharacterIndex = 0; hiddenWordCharacterIndex < hiddenWord.length(); hiddenWordCharacterIndex++) {
                promptStringBuilder.append("_");
            }
            promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(index+hiddenWord.length(), sentenceFormOfChosenCoursePoint.length()));

            prompt = promptStringBuilder.toString();
            correctAnswer = hiddenWord;
        }
    }

    private void generateQAQuestion(CoursePoint chosenCoursePoint){
        prompt = chosenCoursePoint.getFlashcard_front();
        correctAnswer = chosenCoursePoint.getFlashcard_back();
    }

    private class getCoursePoints extends AsyncTask<Void,Void,Void>{
        private ProgressDialog progressDialog;//Shows the user that a long-running background task is running

        @Override
        protected void onPreExecute(){
            super.onPreExecute();//TODO: research what this does
            progressDialog = new ProgressDialog(RevisionScreen.this);
            progressDialog.setTitle(getString(R.string.loading_course_points));
            progressDialog.setMessage(getString(R.string.this_should_be_quick));
            progressDialog.setIndeterminate(false);
            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(RevisionScreen.this, MainDatabase.class, getString(R.string.database_location)).build();
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
                        answerReviewAlertDialogBuilder.setTitle(getString(R.string.well_done)+" - "+ String.format(Locale.UK,"%.2f",answerAccuracy)  + getString(R.string.percentage_similar)+"!");

                    } else if(answerAccuracy>50) {
                        answerReviewAlertDialogBuilder.setTitle(getString(R.string.almost) + " - " + String.format(Locale.UK, "%.2f", answerAccuracy) + getString(R.string.percentage_similar));
                    } else if(answerAccuracy==0){
                        answerReviewAlertDialogBuilder.setTitle(getString(R.string.completely_wrong));

                    }else{
                        answerReviewAlertDialogBuilder.setTitle(getString(R.string.wrong)+ String.format(Locale.UK,"%.2f",answerAccuracy) + getString(R.string.percentage_similar));

                    }

                    TextView answerReviewTextView = new TextView(RevisionScreen.this);

                    //String answerReviewText = getString(R.string.th e_answer_is)+correctAnswer;
                    SpannableStringBuilder answerReviewTextBuilder = new SpannableStringBuilder();
                    answerReviewTextBuilder.append(getString(R.string.the_answer_is)).append(correctAnswer);
                    answerReviewTextBuilder.setSpan(new StyleSpan(Typeface.BOLD),getString(R.string.the_answer_is).length(),answerReviewTextBuilder.length(),0);

                    answerReviewTextView.setText(answerReviewTextBuilder);

                    answerReviewAlertDialogBuilder.setView(answerReviewTextView);
                    answerReviewAlertDialogBuilder.setPositiveButton( getString(R.string.generate_new_question), new DialogInterface.OnClickListener() {
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
                        QuestionAnswerIcon.setImageResource((includeQAQuestions)? R.drawable.question_answer_icon_selected :R.drawable.question_answer_icon_unselected);
                    }
                }
            };

            View.OnClickListener GapOnClick = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (includeQAQuestions){
                        includeGapQuestions = !includeGapQuestions;
                        GapOption.setTextColor((includeGapQuestions)? getResources().getColor(R.color.colorAccent):getResources().getColor(R.color.black));
                        GapIcon.setImageResource((includeGapQuestions)? R.drawable.fill_in_the_gap_icon_selected :R.drawable.fill_in_the_gap_icon_unselected);

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
