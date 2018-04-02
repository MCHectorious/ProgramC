package com.hector.csprojectprogramc.Adapter;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.Database.MainDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Utilities.CustomColourCreator;

import java.lang.ref.WeakReference;
import java.util.List;

public class CoursePointsScreenEditAdapter extends RecyclerView.Adapter<CoursePointsScreenEditAdapter.ViewHolder> {

    private List<CoursePoint> coursePoints;
    private Context context;
    private CoursePoint temporaryCoursePoint;
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
        temporaryCoursePoint = coursePoints.get(position);
        viewHolder.flashcardFormFrontTextView.setText(temporaryCoursePoint.getFlashcard_front());
        viewHolder.flashcardFormBackTextView.setText(temporaryCoursePoint.getFlashcard_back());
        viewHolder.sentenceFormTextView.setText(temporaryCoursePoint.getSentence());
        viewHolder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(temporaryCoursePoint.getSentence()));

        }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }

    @SuppressWarnings("WeakerAccess")
    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView flashcardFormFrontTextView, flashcardFormBackTextView, sentenceFormTextView;
        CardView cardView;
        private ViewHolder (View v, final Context context, final List<CoursePoint> coursePoints){
            super(v);
            sentenceFormTextView = v.findViewById(R.id.sentenceEdit);
            flashcardFormFrontTextView = v.findViewById(R.id.cardFrontEdit);
            flashcardFormBackTextView = v.findViewById(R.id.cardBackEdit);
            cardView = v.findViewById(R.id.cardViewCoursePointsEdit);
            View.OnClickListener showCoursePointAlertDialog = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder editCoursePointAlertDialogBuilder = new AlertDialog.Builder(context);
                    editCoursePointAlertDialogBuilder.setTitle( context.getString(R.string.edit_course_points) );
                    LinearLayout layoutForAlertDialog = new LinearLayout(context);
                    layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL);
                    final TextView cardFront = new TextView(context);
                    cardFront.setText(R.string.flashcard_front);
                    layoutForAlertDialog.addView(cardFront);
                    final EditText cardFrontEdit = new EditText(context);
                    temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                    cardFrontEdit.setText(temporaryCoursePoint.getFlashcard_front());
                    layoutForAlertDialog.addView(cardFrontEdit);
                    final TextView cardBack = new TextView(context);
                    cardBack.setText(R.string.flashcard_back);
                    layoutForAlertDialog.addView(cardBack);
                    final EditText cardBackEdit = new EditText(context);
                    cardBackEdit.setText(temporaryCoursePoint.getFlashcard_back() );
                    layoutForAlertDialog.addView(cardBackEdit);
                    final TextView sentence = new TextView(context);
                    sentence.setText(R.string.sentence);
                    layoutForAlertDialog.addView(sentence);
                    final EditText sentenceEdit = new EditText(context);
                    sentenceEdit.setText(temporaryCoursePoint.getSentence() );
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
                                    temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                                    new deleteCoursePointFromDatabase(context, temporaryCoursePoint, courseID).execute();
                                }
                            });
                            warningBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            warningBuilder.create().show();

                        }
                    });
                    layoutForAlertDialog.addView(deleteButton);
                    editCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);
                    editCoursePointAlertDialogBuilder.setPositiveButton( context.getString(R.string.make_these_changes) , new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //String[] coursePointComponentsArray = {cardFrontEdit.getText().toString(),cardBackEdit.getText().toString(),sentenceEdit.getText().toString()};
                            temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                            temporaryCoursePoint.setFlashcard_front(cardFrontEdit.getText().toString());
                            temporaryCoursePoint.setFlashcard_back(cardBackEdit.getText().toString());
                            temporaryCoursePoint.setSentence(sentenceEdit.getText().toString());
                            new updateCoursePointInDatabase(context, temporaryCoursePoint, courseID).execute();

                        }
                    });
                    editCoursePointAlertDialogBuilder.setNegativeButton( context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    editCoursePointAlertDialogBuilder.create().show();
                }
            };
            sentenceFormTextView.setOnClickListener(showCoursePointAlertDialog);
            flashcardFormFrontTextView.setOnClickListener(showCoursePointAlertDialog);
            flashcardFormBackTextView.setOnClickListener(showCoursePointAlertDialog);
        }




    }

    private static class updateCoursePointInDatabase extends AsyncTask<String,Void,Void>{

        private WeakReference<Context> context;
        private CoursePoint temporaryCoursePoint;
        private int courseID;

        private updateCoursePointInDatabase(Context context, CoursePoint temporaryCoursePoint, int courseID){
            this.context = new WeakReference<>(context);
            this.temporaryCoursePoint = temporaryCoursePoint;
            this.courseID = courseID;
        }

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();
            //database.customDao().deleteCoursePoint(temporaryCoursePoint);
            //database.customDao().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));
            database.customDao().updateCoursePoint(temporaryCoursePoint);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent refreshCoursePointsScreen = new Intent(context.get(), CoursePointsScreen.class);
            refreshCoursePointsScreen.putExtra( context.get().getString(R.string.course_id) , courseID);
            refreshCoursePointsScreen.putExtra(context.get().getString(R.string.perspective), 2);

            context.get().startActivity(refreshCoursePointsScreen);
        }

    }

    private static class deleteCoursePointFromDatabase extends AsyncTask<Void,Void,Void>{

        private WeakReference<Context> context;
        private CoursePoint temporaryCoursePoint;
        private int courseID;

        private deleteCoursePointFromDatabase(Context context, CoursePoint temporaryCoursePoint, int courseID){
            this.context = new WeakReference<>(context);
            this.temporaryCoursePoint = temporaryCoursePoint;
            this.courseID = courseID;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(temporaryCoursePoint);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent intent = new Intent(context.get(), CoursePointsScreen.class);
            intent.putExtra( context.get().getString(R.string.course_id) , courseID);
            context.get().startActivity(intent);
        }
    }



}
