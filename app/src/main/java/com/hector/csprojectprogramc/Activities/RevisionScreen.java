package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.GetCoursePointsFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CommonWordsChecker;
import com.hector.csprojectprogramc.GeneralUtilities.StringDistance;

import java.util.List;
import java.util.Locale;
import java.util.Random;

public class RevisionScreen extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.revision_screen_layout);
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.revision);
        setSupportActionBar(toolbar);

        int courseID = -1;
        try{
            //noinspection ConstantConditions
             courseID = getIntent().getExtras().getInt(getString(R.string.course_id),0);
        }catch (NullPointerException exception){
            AlertDialogHelper.showCannotAccessIntentsDialog(RevisionScreen.this);
        }
        new GetCoursePointsFromDatabase(RevisionScreen.this,courseID,new AfterGettingCoursePointsAllowTheUserToRevise()).execute();

    }


    private class AfterGettingCoursePointsAllowTheUserToRevise implements AsyncTaskCompleteListener<List<CoursePoint>>{

        private boolean includeQuestionAnswerQuestions, includeFillInTheGapGapQuestions;// Booleans used to decide what type of question to ask
        private String prompt, correctAnswer; //The question to be showed to user and the actual answer to that question
        private Random random = new Random(); // Used to randomly choose which type of question to ask and to randomly choose which course point ot test on
        private TextView promptTextView;

        public void onAsyncTaskComplete(final List<CoursePoint> coursePoints){
            includeFillInTheGapGapQuestions = true;
            includeQuestionAnswerQuestions = true;
            final int coursePointsSize = coursePoints.size();

            FloatingActionButton submitAnswerButton = findViewById(R.id.fab);
            final TextView questionAnswerButtonText = findViewById(R.id.QuestionAnswerOption);
            final ImageView questionAnswerButtonIcon = findViewById(R.id.QuestionAnswerIcon);
            final TextView fillInTheGapButtonText = findViewById(R.id.FillInTheGapOption);
            final ImageView fillInTheGapButtonIcon = findViewById(R.id.FillInTheGapIcon);
            final EditText userAnswerEditableTextView =  findViewById(R.id.answerText); //Where the user inputs their answer
            promptTextView =  findViewById(R.id.questionText);

            submitAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String userInput = userAnswerEditableTextView.getText().toString().toLowerCase();
                    if(userInput.equals("")){
                        AlertDialog.Builder noUserInputAlertDialogBuilder = new AlertDialog.Builder(RevisionScreen.this);
                        noUserInputAlertDialogBuilder.setTitle(R.string.no_answer);
                        noUserInputAlertDialogBuilder.setMessage(R.string.please_reattempt_question);
                        noUserInputAlertDialogBuilder.setCancelable(false);
                        noUserInputAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());
                        noUserInputAlertDialogBuilder.create().show();
                    }else{
                        showReviewAlertDialog(userInput,userAnswerEditableTextView,coursePoints,coursePointsSize);
                    }
                }
            });

            View.OnClickListener questionAnswerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (includeFillInTheGapGapQuestions){
                        includeQuestionAnswerQuestions = !includeQuestionAnswerQuestions;
                        if (includeQuestionAnswerQuestions){
                            questionAnswerButtonText.setTextColor(getResources().getColor(R.color.colorAccent));
                            questionAnswerButtonIcon.setImageResource(R.drawable.question_answer_icon_selected);
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_enabled)+" "+getString(R.string.fill_in_the_gap_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();

                        }else{
                            questionAnswerButtonText.setTextColor(getResources().getColor(R.color.black));
                            questionAnswerButtonIcon.setImageResource(R.drawable.question_answer_icon_unselected);
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_disabled)+" "+getString(R.string.fill_in_the_gap_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();

                        }

                    }
                }
            };

            View.OnClickListener FillInTheGapOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (includeQuestionAnswerQuestions){
                        includeFillInTheGapGapQuestions = !includeFillInTheGapGapQuestions;
                        if(includeFillInTheGapGapQuestions){
                            fillInTheGapButtonText.setTextColor(getResources().getColor(R.color.colorAccent));
                            fillInTheGapButtonIcon.setImageResource(R.drawable.fill_in_the_gap_icon_selected);
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_enabled)+" "+getString(R.string.question_answer_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();
                        }else{
                            fillInTheGapButtonText.setTextColor(getResources().getColor(R.color.black));
                            fillInTheGapButtonIcon.setImageResource(R.drawable.fill_in_the_gap_icon_unselected);
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_disabled)+" "+getString(R.string.question_answer_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            };

            questionAnswerButtonIcon.setOnClickListener(questionAnswerOnClickListener);
            questionAnswerButtonText.setOnClickListener(questionAnswerOnClickListener);

            fillInTheGapButtonIcon.setOnClickListener(FillInTheGapOnClickListener);
            fillInTheGapButtonText.setOnClickListener(FillInTheGapOnClickListener);

            generateQuestion(coursePoints, coursePointsSize);

        }

        private void showReviewAlertDialog(String userInput, EditText userAnswerEditableTextView, final List<CoursePoint> coursePoints, final int coursePointsSize){
            AlertDialog.Builder answerReviewAlertDialogBuilder = new AlertDialog.Builder(RevisionScreen.this);

            double answerAccuracy = 100*StringDistance.getNormalisedSimilarity(userInput, correctAnswer.toLowerCase());

            userAnswerEditableTextView.setText("");

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

        private void generateQuestion(List<CoursePoint> coursePoints, int coursePointsSize){
            CoursePoint chosenCoursePoint = coursePoints.get(random.nextInt(coursePointsSize));// Gets a randomly chosen course point
            if (includeFillInTheGapGapQuestions && includeQuestionAnswerQuestions){
                if(random.nextBoolean()){//If both are available it chooses randomly
                    generateFillInTheGapQuestion(chosenCoursePoint);
                }else {
                    generateQuestionAnswerQuestion(chosenCoursePoint);
                }
            }else if (includeFillInTheGapGapQuestions){ //If only gap questions are available
                generateFillInTheGapQuestion(chosenCoursePoint);
            }else{
                generateQuestionAnswerQuestion(chosenCoursePoint);//If only QA questions are available
            }
            promptTextView.setText(prompt);//Shows the question on the screen

        }

        private void generateFillInTheGapQuestion(CoursePoint chosenCoursePoint){
            String sentenceFormOfChosenCoursePoint = chosenCoursePoint.getSentence();
            String[] wordsFromSentenceFormOfChosenCoursePoint = sentenceFormOfChosenCoursePoint.split("[\\p{Punct}\\s]+"); //Generates an array of each word in the sentence form of the course point
            int hiddenWordIndex = random.nextInt(wordsFromSentenceFormOfChosenCoursePoint.length);//Randomly decides the index of the word to be hidden
            String hiddenWord = wordsFromSentenceFormOfChosenCoursePoint[hiddenWordIndex];//The hidden word
            if (CommonWordsChecker.checkIfCommonWord(hiddenWord)){
                generateFillInTheGapQuestion(chosenCoursePoint); //If the word shouldn't be chosen then it generates a new question
            }else {

                int positionOfHiddenWord = sentenceFormOfChosenCoursePoint.indexOf(hiddenWord);
                StringBuilder promptStringBuilder = new StringBuilder();
                promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(0,positionOfHiddenWord));
                for (int hiddenWordCharacterIndex = 0; hiddenWordCharacterIndex < hiddenWord.length(); hiddenWordCharacterIndex++) {
                    promptStringBuilder.append("_");
                }
                promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(positionOfHiddenWord+hiddenWord.length(), sentenceFormOfChosenCoursePoint.length()));

                prompt = promptStringBuilder.toString();
                correctAnswer = hiddenWord;
            }
        }

        private void generateQuestionAnswerQuestion(CoursePoint chosenCoursePoint){
            prompt = chosenCoursePoint.getFlashcard_front();
            correctAnswer = chosenCoursePoint.getFlashcard_back();
        }


    }


}




