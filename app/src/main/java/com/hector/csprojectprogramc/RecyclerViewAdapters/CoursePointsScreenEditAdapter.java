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

    private List<CoursePoint> coursePoints;
    private Context context;
    private CoursePoint coursePoint;
    private int courseID;

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
        coursePoint = coursePoints.get(position);
        viewHolder.flashcardFormFrontTextView.setText(coursePoint.getFlashcard_front());
        viewHolder.flashcardFormBackTextView.setText(coursePoint.getFlashcard_back());
        viewHolder.sentenceFormTextView.setText(coursePoint.getSentence());
        viewHolder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(coursePoint.getSentence()));

        }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView flashcardFormFrontTextView, flashcardFormBackTextView, sentenceFormTextView;
        CardView cardView;
        private ViewHolder (View v, final Context context, final List<CoursePoint> coursePoints){
            super(v);
            sentenceFormTextView = v.findViewById(R.id.sentenceEdit);
            flashcardFormFrontTextView = v.findViewById(R.id.cardFrontEdit);
            flashcardFormBackTextView = v.findViewById(R.id.cardBackEdit);
            cardView = v.findViewById(R.id.cardViewCoursePointsEdit);

            View.OnClickListener showEditCoursePointAlertDialog = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    coursePoint = coursePoints.get(getAdapterPosition());

                    final AlertDialog.Builder editCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);
                    editCoursePointAlertDialogBuilder.setTitle( context.getString(R.string.edit_course_points) );
                    LinearLayout layoutForAlertDialog = new LinearLayout(context);
                    layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL);

                    final TextView flashcardFront = new TextView(context);
                    flashcardFront.setText(R.string.flashcard_front);
                    layoutForAlertDialog.addView(flashcardFront);

                    final EditText flashcardFrontEdit = new EditText(context);
                    flashcardFrontEdit.setText(coursePoint.getFlashcard_front());
                    layoutForAlertDialog.addView(flashcardFrontEdit);

                    final TextView flashcardBack = new TextView(context);
                    flashcardBack.setText(R.string.flashcard_back);
                    layoutForAlertDialog.addView(flashcardBack);

                    final EditText flashcardBackEdit = new EditText(context);
                    flashcardBackEdit.setText(coursePoint.getFlashcard_back() );
                    layoutForAlertDialog.addView(flashcardBackEdit);

                    final TextView sentence = new TextView(context);
                    sentence.setText(R.string.sentence);
                    layoutForAlertDialog.addView(sentence);

                    final EditText sentenceEdit = new EditText(context);
                    sentenceEdit.setText(coursePoint.getSentence() );
                    layoutForAlertDialog.addView(sentenceEdit);

                    Button deleteButton = new Button(context);
                    deleteButton.setText( context.getString(R.string.delete) );
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder warningBuilder = new AlertDialog.Builder(context);
                            warningBuilder.setTitle(R.string.warning);
                            warningBuilder.setMessage(R.string.deleting_course_point_warning);
                            warningBuilder.setCancelable(true).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    coursePoint = coursePoints.get(getAdapterPosition());
                                    new DeleteCoursePointFromDatabase(context, coursePoint, new RefreshScreen()).execute();
                                }
                            });
                            warningBuilder.setNegativeButton(R.string.cancel, AlertDialogHelper.onClickDismissDialog());
                            warningBuilder.create().show();

                        }
                    });
                    layoutForAlertDialog.addView(deleteButton);
                    editCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);
                    editCoursePointAlertDialogBuilder.setPositiveButton( context.getString(R.string.make_these_changes) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            coursePoint = coursePoints.get(getAdapterPosition());

                            String flashcardFront = flashcardFrontEdit.getText().toString();
                            String flashcardBack = flashcardBackEdit.getText().toString();
                            String sentence = sentenceEdit.getText().toString();

                            if(flashcardFront.equals("")||flashcardBack.equals("")||sentence.equals("")){
                                AlertDialog.Builder coursePointComponentBlankAlertDialogBuilder = new AlertDialog.Builder(context);
                                coursePointComponentBlankAlertDialogBuilder.setTitle(R.string.warning);
                                coursePointComponentBlankAlertDialogBuilder.setMessage(R.string.blank_components_warning);
                                coursePointComponentBlankAlertDialogBuilder.setCancelable(false);
                                coursePointComponentBlankAlertDialogBuilder.setPositiveButton(R.string.reattempt, AlertDialogHelper.onClickDismissDialog());
                                coursePointComponentBlankAlertDialogBuilder.create().show();
                            }else{
                                coursePoint.setFlashcard_front(flashcardFront);
                                coursePoint.setFlashcard_back(flashcardBack);
                                coursePoint.setSentence(sentence);
                                new UpdateCoursePointInDatabase(context, coursePoint, new RefreshScreen()).execute();
                            }
                        }
                    });

                    editCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel), AlertDialogHelper.onClickDismissDialog());
                    editCoursePointAlertDialogBuilder.create().show();
                }
            };

            sentenceFormTextView.setOnClickListener(showEditCoursePointAlertDialog);
            flashcardFormFrontTextView.setOnClickListener(showEditCoursePointAlertDialog);
            flashcardFormBackTextView.setOnClickListener(showEditCoursePointAlertDialog);
        }




    }

    private class RefreshScreen implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            Intent refreshCoursePointsScreen = new Intent(context, CoursePointsScreen.class);
            refreshCoursePointsScreen.putExtra( context.getString(R.string.course_id) , courseID);
            refreshCoursePointsScreen.putExtra(context.getString(R.string.perspective), 2);

            context.startActivity(refreshCoursePointsScreen);
        }
    }







}
