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
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_edit_card,parent,false);//TODO: think of appropriate name
        return new ViewHolder(view,context, coursePoints);
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
                    editCoursePointAlertDialogBuilder.setTitle("Edit This Course Point As You Wish");
                    LinearLayout layoutForAlertDialog = new LinearLayout(context);
                    layoutForAlertDialog.setOrientation(LinearLayout.VERTICAL);
                    final EditText cardFrontEdit = new EditText(context);
                    temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                    cardFrontEdit.setText(temporaryCoursePoint.getFlashcard_front());
                    layoutForAlertDialog.addView(cardFrontEdit);
                    final EditText cardBackEdit = new EditText(context);
                    cardBackEdit.setText(temporaryCoursePoint.getFlashcard_back() );
                    layoutForAlertDialog.addView(cardBackEdit);
                    final EditText sentenceEdit = new EditText(context);
                    sentenceEdit.setText(temporaryCoursePoint.getSentence() );
                    layoutForAlertDialog.addView(sentenceEdit);

                    Button deleteButton = new Button(context);
                    deleteButton.setText(R.string.delete);
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                            new deleteCoursePointFromDatabase().execute();
                        }
                    });
                    layoutForAlertDialog.addView(deleteButton);
                    editCoursePointAlertDialogBuilder.setView(layoutForAlertDialog);
                    editCoursePointAlertDialogBuilder.setPositiveButton("Make These Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] coursePointComponentsArray = {cardFrontEdit.getText().toString(),cardBackEdit.getText().toString(),sentenceEdit.getText().toString()};
                            temporaryCoursePoint = coursePoints.get(getAdapterPosition());
                            new updateCoursePointInDatabase().execute(coursePointComponentsArray);

                        }
                    });
                    editCoursePointAlertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
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

    private class updateCoursePointInDatabase extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(context, MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(temporaryCoursePoint);
            database.customDao().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent refreshCoursePointsScreen = new Intent(context, CoursePointsScreen.class);
            refreshCoursePointsScreen.putExtra("course ID", courseID);
            context.startActivity(refreshCoursePointsScreen);
        }

    }

    private class deleteCoursePointFromDatabase extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(context, MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(temporaryCoursePoint);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent intent = new Intent(context, CoursePointsScreen.class);
            intent.putExtra("course ID", courseID);
            context.startActivity(intent);
        }
    }

}
