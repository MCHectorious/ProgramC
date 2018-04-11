package com.hector.csprojectprogramc.RecyclerViewAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hector.csprojectprogramc.Activities.CoursePointsScreen;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.DeleteCoursePointFromDatabase;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.UpdateCoursePointInDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;

import java.util.List;

public class CoursePointsScreenEditAdapter extends RecyclerView.Adapter<CoursePointsScreenEditAdapter.ViewHolder> {

    private List<CoursePoint> coursePoints;//The course points that need to be shown
    private Context context;//The screen the adapter is being called from (CoursePointsScreen)
    private CoursePoint coursePoint;//A temporary course point
    private int courseID;//the id of the course these course points belong to

    public CoursePointsScreenEditAdapter(List<CoursePoint> coursePoints, Context context, int courseID){
        this.coursePoints = coursePoints;
        this.context = context;
        this.courseID = courseID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View viewHolder =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_edit_card,parent,false);
        return new ViewHolder(viewHolder,context, coursePoints);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        coursePoint = coursePoints.get(position);//Course point represented by this card
        viewHolder.flashcardFormFrontTextView.setText(coursePoint.getFlashcard_front());//Displays the front of the flashcard form of the course point
        viewHolder.flashcardFormBackTextView.setText(coursePoint.getFlashcard_back());//Displays the back of the flashcard form of the course point
        viewHolder.sentenceFormTextView.setText(coursePoint.getSentence());//Displays the sentence form of the course point
        viewHolder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(coursePoint.getSentence()));//Bases the colour of the course point on the sentence form of the course point

        }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView flashcardFormFrontTextView, flashcardFormBackTextView, sentenceFormTextView;// Text view to show the front of th flashcard form, the back of the flashcard form and the sentence form of the course point
        CardView cardView;//The card which contains the about text views
        private ViewHolder (View v, final Context context, final List<CoursePoint> coursePoints){
            super(v);
            sentenceFormTextView = v.findViewById(R.id.sentenceEdit);//Initialises the text view from the XMl code
            flashcardFormFrontTextView = v.findViewById(R.id.cardFrontEdit);//Initialises the text view from the XMl code
            flashcardFormBackTextView = v.findViewById(R.id.cardBackEdit);//Initialises the text view from the XMl code
            cardView = v.findViewById(R.id.cardViewCoursePointsEdit);//Initialises the card view from the XMl code

            View.OnClickListener showEditCoursePointAlertDialog = new View.OnClickListener() {
                @Override
                public void onClick(View v) {//When a component of the course point is pressed
                    coursePoint = coursePoints.get(getAdapterPosition());//Gets the Course Point this represents

                    final AlertDialog.Builder editCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);//Initialise the builder which will create an alert allow the course point to be edited
                    editCoursePointAlertDialogBuilder.setTitle( context.getString(R.string.edit_course_points) );//Instructions for the user

                    LinearLayout layoutForAlertDialog = new LinearLayout(context);
                    layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL);
                    //Views will be shown in vertical list

                    final TextView flashcardFront = new TextView(context);//Initialises the text view
                    flashcardFront.setText(R.string.flashcard_front);//Explains to the user what this represents
                    layoutForAlertDialog.addView(flashcardFront);//Adds view to the vertical list of views

                    final EditText flashcardFrontEdit = new EditText(context);//Initialises the editable text view
                    flashcardFrontEdit.setText(coursePoint.getFlashcard_front());//Sets the text to current front of the flashcard form
                    layoutForAlertDialog.addView(flashcardFrontEdit);//Adds view to the vertical list of views

                    final TextView flashcardBack = new TextView(context);//Initialises the text view
                    flashcardBack.setText(R.string.flashcard_back);//Explains to the user what this represents
                    layoutForAlertDialog.addView(flashcardBack);//Adds view to the vertical list of views

                    final EditText flashcardBackEdit = new EditText(context);//Initialises the editable text view
                    flashcardBackEdit.setText(coursePoint.getFlashcard_back() );//Sets the text to current back of the flashcard form
                    layoutForAlertDialog.addView(flashcardBackEdit);//Adds view to the vertical list of views

                    final TextView sentence = new TextView(context);//Initialises the text view
                    sentence.setText(R.string.sentence);//Explains to the user what this represents
                    layoutForAlertDialog.addView(sentence);//Adds view to the vertical list of views

                    final EditText sentenceEdit = new EditText(context);//Initialises the editable text view
                    sentenceEdit.setText(coursePoint.getSentence() );//Sets the text to current sentence form
                    layoutForAlertDialog.addView(sentenceEdit);//Adds view to the vertical list of views

                    Button deleteButton = new Button(context);//Initialises the delete button
                    deleteButton.setText( context.getString(R.string.delete) );//Describes what this does for the user
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder warningBuilder = new AlertDialog.Builder(context);//Initialises a builder for an alert dialog warning the user about deleting the ourse point
                            warningBuilder.setTitle(R.string.warning);
                            warningBuilder.setMessage(R.string.deleting_course_point_warning);
                            warningBuilder.setCancelable(true).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    coursePoint = coursePoints.get(getAdapterPosition());//Gets the course point
                                    new DeleteCoursePointFromDatabase(context, coursePoint, new RefreshScreen()).execute();//deletes the course point
                                }
                            });
                            warningBuilder.setNegativeButton(R.string.cancel, AlertDialogHelper.onClickDismissDialog());//When the user presses this it returns them to the edit alert dialog
                            warningBuilder.create().show();//Shows warning

                        }
                    });
                    layoutForAlertDialog.addView(deleteButton);//Adds view to the vertical list of views
                    editCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);//Adds the list of views to the alert dialog
                    editCoursePointAlertDialogBuilder.setPositiveButton( context.getString(R.string.make_these_changes) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            coursePoint = coursePoints.get(getAdapterPosition());//Gets the course point

                            String flashcardFront = flashcardFrontEdit.getText().toString();//Gets the user's version of the front of the flashcard form
                            String flashcardBack = flashcardBackEdit.getText().toString();//Gets the user's version of the back of the flashcard form
                            String sentence = sentenceEdit.getText().toString();//Gets the user's version of the sentence form

                            if(flashcardFront.equals("")||flashcardBack.equals("")||sentence.equals("")){//Checks whether any of the components are blank
                                AlertDialog.Builder coursePointComponentBlankAlertDialogBuilder = new AlertDialog.Builder(context);
                                coursePointComponentBlankAlertDialogBuilder.setTitle(R.string.warning);
                                coursePointComponentBlankAlertDialogBuilder.setMessage(R.string.blank_components_warning);//Explains the issue to the user
                                coursePointComponentBlankAlertDialogBuilder.setCancelable(false);//Prohibits the user from continuing as it may cause further issues
                                coursePointComponentBlankAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());//Hides the dialog so that the user can make changes to their edits
                                coursePointComponentBlankAlertDialogBuilder.create().show();//Shows the art
                            }else{
                                coursePoint.setFlashcard_front(flashcardFront);//updates the course point's  value of the front of the flashcard form
                                coursePoint.setFlashcard_back(flashcardBack);//updates the course point's  value of the back of the flashcard form
                                coursePoint.setSentence(sentence);//updates the course point's  value of the sentence form
                                new UpdateCoursePointInDatabase(context, coursePoint, new RefreshScreen()).execute();//updates the value of the course point in the database
                            }
                        }
                    });

                    editCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel), AlertDialogHelper.onClickDismissDialog());//Hides the dialog if the user decide they don't want to make any changes
                    editCoursePointAlertDialogBuilder.create().show();//Shows the alert dialog
                }
            };

            sentenceFormTextView.setOnClickListener(showEditCoursePointAlertDialog);
            flashcardFormFrontTextView.setOnClickListener(showEditCoursePointAlertDialog);
            flashcardFormBackTextView.setOnClickListener(showEditCoursePointAlertDialog);
            //will allow user to edit the course point if any component of the course point is pressed

        }

    }

    private class RefreshScreen implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            Intent refreshCoursePointsScreen = new Intent(context, CoursePointsScreen.class);//Makes a connection between the context and the course points screen
            refreshCoursePointsScreen.putExtra( context.getString(R.string.course_id) , courseID);//Specifies that the course to be shown are for this course
            refreshCoursePointsScreen.putExtra(context.getString(R.string.perspective), 2);//Starts the screen on the edit perspective

            context.startActivity(refreshCoursePointsScreen);// refreshes the course points screen
        }
    }







}
