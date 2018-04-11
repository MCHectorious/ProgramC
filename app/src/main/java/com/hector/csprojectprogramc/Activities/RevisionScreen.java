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
    protected void onCreate(Bundle savedInstanceState) {//When the screen is created
        super.onCreate(savedInstanceState);//Runs the code code for when a screen is created
        setContentView(R.layout.revision_screen_layout);//Links to an XML file which shows the layout of this screen
        Toolbar toolbar =  findViewById(R.id.toolbar);//Initialises the toolbar
        toolbar.setTitle(R.string.revision);//Sets the title of the screen to "Revision"
        setSupportActionBar(toolbar);//Displays custom toolbar

        int courseID = -1;//Initialises the id of the course to an impossible value
        try{
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
             courseID = getIntent().getExtras().getInt(getString(R.string.course_id));//Gets the id of the course from the previous screen
        }catch (NullPointerException exception){//Will occur the previous screen doesn't provide the course id
            AlertDialogHelper.showCannotAccessIntentsDialog(RevisionScreen.this);//Shows the user that the program is unable to access information from the previous screen and allows the user to return to the home screenn
        }
        new GetCoursePointsFromDatabase(RevisionScreen.this,courseID,new AfterGettingCoursePointsAllowTheUserToRevise()).execute();//Gets the course points from the database

    }


    private class AfterGettingCoursePointsAllowTheUserToRevise implements AsyncTaskCompleteListener<List<CoursePoint>>{//Allows objects to be created which handle what occurs after getting the courses points

        private boolean includeQuestionAnswerQuestions, includeFillInTheGapGapQuestions;// Booleans used to decide what type of question to ask
        private String prompt, correctAnswer; //The question to be showed to user and the actual answer to that question
        private Random random = new Random(); // Used to randomly choose which type of question to ask and to randomly choose which course point ot test on
        private TextView promptTextView;// Text view which states the question

        public void onAsyncTaskComplete(final List<CoursePoint> coursePoints){
            includeFillInTheGapGapQuestions = true;//Defaults to asking Fill-in-the-Gap questions
            includeQuestionAnswerQuestions = true; //Defaults to asking Question-Answer questions
            final int coursePointsSize = coursePoints.size();//Caches the number of course points to avoid having to calculate it multiple times

            FloatingActionButton submitAnswerButton = findViewById(R.id.fab);//Initialises the button that allows the user to submit their answer
            final TextView questionAnswerButtonText = findViewById(R.id.QuestionAnswerOption);//Initialises the text view which state that the button represents the QuestionAnswer format
            final ImageView questionAnswerButtonIcon = findViewById(R.id.QuestionAnswerIcon);//Initialises the image which shows whether Question-Answer questions are possible
            final TextView fillInTheGapButtonText = findViewById(R.id.FillInTheGapOption);//Initialises the text view which state that the button represents the Fill-in-the-Gap format
            final ImageView fillInTheGapButtonIcon = findViewById(R.id.FillInTheGapIcon);//Initialises the image which shows whether Fill-in-the-Gap questions are possible
            final EditText userAnswerEditableTextView =  findViewById(R.id.answerText); //Where the user inputs their answer
            promptTextView =  findViewById(R.id.questionText);//Initialises the text view which states the question

            submitAnswerButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {//When the user presses the button to submit their answer

                    String userInput = userAnswerEditableTextView.getText().toString().toLowerCase();//Gets the user input and sets it to lower case as the String Distance metric should not be case sensitive
                    if(userInput.equals("")){//Checks whether the user has actually given an answer
                        AlertDialog.Builder noUserInputAlertDialogBuilder = new AlertDialog.Builder(RevisionScreen.this);//Initialises the builder which will create an alert to say no answer has been received
                        noUserInputAlertDialogBuilder.setTitle(R.string.no_answer);//States the issue
                        noUserInputAlertDialogBuilder.setMessage(R.string.please_reattempt_question);//Explains how the issue can be resolved
                        noUserInputAlertDialogBuilder.setCancelable(false);//Prohibits the user from escaping the dialog so that no further issues can occur
                        noUserInputAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());//When the user presses the button, it hides the alert so that the user can try to answer
                        noUserInputAlertDialogBuilder.create().show();//Shows the alert
                    }else{
                        showReviewAlertDialog(userInput,userAnswerEditableTextView,coursePoints,coursePointsSize);//Shows a review of the users answer
                    }
                }
            });

            View.OnClickListener questionAnswerOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {//When the user presses the Question-Answer button
                    if (includeFillInTheGapGapQuestions){//To ensure that at least one format of question is possible
                        includeQuestionAnswerQuestions = !includeQuestionAnswerQuestions;//Flips the value of the boolean
                        if (includeQuestionAnswerQuestions){//Checks whether Question-Answer questions are possible
                            questionAnswerButtonText.setTextColor(getResources().getColor(R.color.colorAccent));//Shows the text in the accent colour to show it is enabled
                            questionAnswerButtonIcon.setImageResource(R.drawable.question_answer_icon_selected);//Shows the form of the Question-Answer icon that shows it is enabled
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_enabled)+" "+getString(R.string.question_answer_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();//Tells the user that they have enabled Question-Answer questions

                        }else{
                            questionAnswerButtonText.setTextColor(getResources().getColor(R.color.black));//Shows the text in the black to show it is disabled
                            questionAnswerButtonIcon.setImageResource(R.drawable.question_answer_icon_unselected);//Shows the form of the Question-Answer icon that shows it is disabled
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_disabled)+" "+getString(R.string.question_answer_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();//Tells the user that they have disabled Question-Answer questions

                        }

                    }
                }
            };

            View.OnClickListener fillInTheGapOnClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {//When the user presses the Question-Answer button
                    if (includeQuestionAnswerQuestions){//To ensure that at least one format of question is possible
                        includeFillInTheGapGapQuestions = !includeFillInTheGapGapQuestions;//Flips the value of the boolean
                        if(includeFillInTheGapGapQuestions){//Checks whether Fill-in-the-Gap questions are possible
                            fillInTheGapButtonText.setTextColor(getResources().getColor(R.color.colorAccent));//Shows the text in the accent colour to show it is enabled
                            fillInTheGapButtonIcon.setImageResource(R.drawable.fill_in_the_gap_icon_selected);//Shows the form of the Fill-in-the-Gap icon that shows it is enabled
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_enabled)+" "+getString(R.string.fill_in_the_gap_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();//Tells the user that they have enabled Fill-in-the-Gap questions
                        }else{
                            fillInTheGapButtonText.setTextColor(getResources().getColor(R.color.black));//Shows the text in the black to show it is disabled
                            fillInTheGapButtonIcon.setImageResource(R.drawable.fill_in_the_gap_icon_unselected);//Shows the form of the Fill-in-the-Gap icon that shows it is disabled
                            Toast.makeText(RevisionScreen.this, getString(R.string.you_have_disabled)+" "+getString(R.string.fill_in_the_gap_title)+" "+getString(R.string.questions), Toast.LENGTH_SHORT).show();//Tells the user that they have disabled Fill-in-the-Gap questions
                        }
                    }
                }
            };

            questionAnswerButtonIcon.setOnClickListener(questionAnswerOnClickListener);
            questionAnswerButtonText.setOnClickListener(questionAnswerOnClickListener);
            //Sets the behaviour of the components of the Question-Answer button when they are pressed

            fillInTheGapButtonIcon.setOnClickListener(fillInTheGapOnClickListener);
            fillInTheGapButtonText.setOnClickListener(fillInTheGapOnClickListener);
            //Sets the behaviour of the components of the Question-Answer button when they are pressed

            generateQuestion(coursePoints, coursePointsSize);//Generates the first question

        }

        private void showReviewAlertDialog(String userInput, EditText userAnswerEditableTextView, final List<CoursePoint> coursePoints, final int coursePointsSize){//Shows an Alert Dialog which reviews how correct the user's answer is
            AlertDialog.Builder answerReviewAlertDialogBuilder = new AlertDialog.Builder(RevisionScreen.this);//Initialises the builder which will create the alert displaying the review

            double answerAccuracy = 100*StringDistance.getNormalisedSimilarity(userInput, correctAnswer.toLowerCase());//Gets the percentage of how similar the user's answer is to the correct answer

            userAnswerEditableTextView.setText("");//Resets the user's answer ready for the next question

            if(answerAccuracy>90){//If the user's answer is very good
                answerReviewAlertDialogBuilder.setTitle(getString(R.string.well_done)+" - "+ String.format(Locale.UK,"%.2f",answerAccuracy)  + getString(R.string.percentage_similar)+"!");//Displays how correct the answer is

            } else if(answerAccuracy>50) {//If the user's answer is good
                answerReviewAlertDialogBuilder.setTitle(getString(R.string.almost) + " - " + String.format(Locale.UK, "%.2f", answerAccuracy) + getString(R.string.percentage_similar));//Displays how correct the answer is
            } else if(answerAccuracy==0){//If the user's answer is as wrong as possible
                answerReviewAlertDialogBuilder.setTitle(getString(R.string.completely_wrong));//Displays that the answer is completely wrong

            }else{
                answerReviewAlertDialogBuilder.setTitle(getString(R.string.wrong)+" "+ String.format(Locale.UK,"%.2f",answerAccuracy) + getString(R.string.percentage_similar));//Displays how correct the answer is

            }

            TextView correctAnswerTextView = new TextView(RevisionScreen.this);//Initialises the text view which will contain the correct answer

            SpannableStringBuilder correctAnswerTextBuilder = new SpannableStringBuilder();//Initialises the string builder which will store the correct answer
            correctAnswerTextBuilder.append(getString(R.string.the_answer_is)).append(" ").append(correctAnswer);//Shows the correct answer
            correctAnswerTextBuilder.setSpan(new StyleSpan(Typeface.BOLD),getString(R.string.the_answer_is).length(),correctAnswerTextBuilder.length(),0);//Sets the correct answer to be in bold

            correctAnswerTextView.setText(correctAnswerTextBuilder);//Adds the text to the text view

            answerReviewAlertDialogBuilder.setView(correctAnswerTextView);//Include the text view in the alert dialog
            answerReviewAlertDialogBuilder.setPositiveButton( getString(R.string.generate_new_question), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                    generateQuestion(coursePoints, coursePointsSize);//generates a new question
                }
            });
            answerReviewAlertDialogBuilder.create().show();//Show alert
        }

        private void generateQuestion(List<CoursePoint> coursePoints, int coursePointsSize){//Generates a question
            CoursePoint chosenCoursePoint = coursePoints.get(random.nextInt(coursePointsSize));// Gets a randomly chosen course point
            if (includeFillInTheGapGapQuestions && includeQuestionAnswerQuestions){//If both formats of question are available
                if(random.nextBoolean()){//If both are available it chooses randomly between the formats of question
                    generateFillInTheGapQuestion(chosenCoursePoint);//Generates a new Fill-in-the-Gap question
                }else {
                    generateQuestionAnswerQuestion(chosenCoursePoint);//Generates a new Question-Answer question
                }
            }else if (includeFillInTheGapGapQuestions){ //If only gap questions are available
                generateFillInTheGapQuestion(chosenCoursePoint);//Generates a new Fill-in-the-Gap question
            }else{//If only QA questions are available
                generateQuestionAnswerQuestion(chosenCoursePoint);//Generates a new Question-Answer format question
            }
            promptTextView.setText(prompt);//Sets the question text view to show the question

        }

        private void generateFillInTheGapQuestion(CoursePoint chosenCoursePoint){//Generates a new Fill-in-the-Gap question
            String sentenceFormOfChosenCoursePoint = chosenCoursePoint.getSentence();//Gets the sentence form of the course point
            String[] wordsFromSentenceFormOfChosenCoursePoint = sentenceFormOfChosenCoursePoint.split("[\\p{Punct}\\s]+"); //Generates an array of each word in the sentence form of the course point
            int hiddenWordIndex = random.nextInt(wordsFromSentenceFormOfChosenCoursePoint.length);//Randomly decides the index of the word to be hidden
            String hiddenWord = wordsFromSentenceFormOfChosenCoursePoint[hiddenWordIndex];//The hidden word
            if (CommonWordsChecker.checkIfCommonWord(hiddenWord)){//Checks whether the chosen word is so common that it shouldn't be asked as a  question
                generateFillInTheGapQuestion(chosenCoursePoint); //If the word shouldn't be chosen then it generates a new question
            }else {
                int positionOfHiddenWord = sentenceFormOfChosenCoursePoint.indexOf(hiddenWord);//Gets the position of the hidden word in the sentence form of the chosen course point
                StringBuilder promptStringBuilder = new StringBuilder();//Initialises the string builder which builds the answer
                if(positionOfHiddenWord>0){//Checks whether there is a part of the sentence form of the course point before the hidden word
                    promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(0,positionOfHiddenWord));//Starts the question with the part of the sentence form of the course point before the chosen word
                }
                for (int hiddenWordCharacterIndex = 0; hiddenWordCharacterIndex < hiddenWord.length(); hiddenWordCharacterIndex++) {
                    promptStringBuilder.append("_");//Appends the same number of underscores as the length of the hidden word
                }
                promptStringBuilder.append(sentenceFormOfChosenCoursePoint.substring(positionOfHiddenWord+hiddenWord.length(), sentenceFormOfChosenCoursePoint.length()));//Appends the part of the sentence form after the hidden word

                prompt = promptStringBuilder.toString();//Sets the question
                correctAnswer = hiddenWord;//Sets the correct answer
            }
        }

        private void generateQuestionAnswerQuestion(CoursePoint chosenCoursePoint){//Generates a Question-Answer question
            prompt = chosenCoursePoint.getFlashcard_front();//Sets the question to be the front of the flashcard form of the course point
            correctAnswer = chosenCoursePoint.getFlashcard_back();//Sets the correct answer to be the back of the flashcard form of the course point
        }


    }


}




