package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetCoursePointsFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CommonWordsChecker;
import com.hector.csprojectprogramc.GeneralUtilities.StringDistance;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RevisionScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); //TODO: research what this does
        setContentView(R.layout.revision_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.revision);
        setSupportActionBar(toolbar);




        try{

            //noinspection ConstantConditions
             int CourseID = getIntent().getExtras().getInt(getString(R.string.course_id),0);//TODO: change to just get int

            new GetCoursePointsFromDatabase(RevisionScreen.this,CourseID,new AfterGettingCoursePoints()).execute();
        }catch (NullPointerException exception){
            //TODO: handle appropriately
        }
    }


    private class AfterGettingCoursePoints implements AsyncTaskCompleteListener<List<CoursePoint>>{

        private boolean includeQAQuestions, includeGapQuestions;// Booleans used to decide what type of question to ask
        private String prompt, correctAnswer; //The question to be showed to user and the actual answer to that question
        private Random random = new Random(); // Used to randomly choose which type of question to ask and to randomly choose which course point ot test on
        private TextView promptTextView;

        public void onAsyncTaskComplete(final List<CoursePoint> coursePoints){
            final int coursePointsSize = coursePoints.size();

            FloatingActionButton submitAnswerButton = findViewById(R.id.fab);
            final TextView QuestionAnswerOption = findViewById(R.id.QuestionAnswerOption);
            final ImageView QuestionAnswerIcon = findViewById(R.id.QuestionAnswerIcon);
            final TextView GapOption = findViewById(R.id.FillInTheGapOption);
            final ImageView GapIcon = findViewById(R.id.FillInTheGapIcon);
            final EditText userAnswerEditableTextView =  findViewById(R.id.answerText); //Where the user inputs their answer
            promptTextView =  findViewById(R.id.questionText);



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
                        answerReviewAlertDialogBuilder.setTitle(getString(R.string.wrong)+" "+ String.format(Locale.UK,"%.2f",answerAccuracy) + getString(R.string.percentage_similar));

                    }

                    TextView answerReviewTextView = new TextView(RevisionScreen.this);

                    //String answerReviewText = getString(R.string.th e_answer_is)+correctAnswer;
                    SpannableStringBuilder answerReviewTextBuilder = new SpannableStringBuilder();
                    answerReviewTextBuilder.append(getString(R.string.the_answer_is)).append(" ").append(correctAnswer);
                    answerReviewTextBuilder.setSpan(new StyleSpan(Typeface.BOLD),getString(R.string.the_answer_is).length(),answerReviewTextBuilder.length(),0);

                    answerReviewTextView.setText(answerReviewTextBuilder);

                    answerReviewAlertDialogBuilder.setView(answerReviewTextView);
                    answerReviewAlertDialogBuilder.setPositiveButton( getString(R.string.generate_new_question), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            generateQuestion(coursePoints, coursePointsSize);
                        }
                    });
                    answerReviewAlertDialogBuilder.create().show();
                }
            });
            includeGapQuestions = true;
            includeQAQuestions = true;



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

            generateQuestion(coursePoints, coursePointsSize);

        }

        private void generateQuestion(List<CoursePoint> coursePoints, int coursePointsSize){
            CoursePoint chosenCoursePoint = coursePoints.get(random.nextInt(coursePointsSize));// Gets a randomly chosen course point
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
                generateGapQuestion(chosenCoursePoint); //If the word shouldn't be chosen then it generates a new question
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


    }


}




