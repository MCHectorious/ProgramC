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
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import java.util.List;

public class CoursePointsScreenEditAdapter extends RecyclerView.Adapter<CoursePointsScreenEditAdapter.ViewHolder> {

    private List<CoursePoint> dataSet;
    private Context context;
    private CoursePoint tempPoint;

    public CoursePointsScreenEditAdapter(List<CoursePoint> points, Context context){
        dataSet = points;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_edit_card,parent,false);
        return new ViewHolder(view,context, dataSet);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cardFront.setText(dataSet.get(position).getFlashcard_front());
        holder.cardBack.setText(dataSet.get(position).getFlashcard_back());
        holder.sentence.setText(dataSet.get(position).getSentence());
        holder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(dataSet.get(position).getSentence()));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cardFront, cardBack, sentence;
        CardView cardView;
        private ViewHolder (View v, final Context context, final List<CoursePoint> dataset){
            super(v);
            sentence = v.findViewById(R.id.sentenceEdit);
            cardFront = v.findViewById(R.id.cardFrontEdit);
            cardBack = v.findViewById(R.id.cardBackEdit);
            cardView = v.findViewById(R.id.cardViewCoursePointsEdit);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Edit This Course Point As You Wish");
                    LinearLayout layout = new LinearLayout(context);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    final EditText cardFrontEdit = new EditText(context);
                    cardFrontEdit.setText(dataset.get(getAdapterPosition()).getFlashcard_front() );
                    layout.addView(cardFrontEdit);
                    final EditText cardBackEdit = new EditText(context);
                    cardBackEdit.setText(dataset.get(getAdapterPosition()).getFlashcard_back() );
                    layout.addView(cardBackEdit);
                    final EditText sentenceEdit = new EditText(context);
                    sentenceEdit.setText(dataset.get(getAdapterPosition()).getSentence() );
                    layout.addView(sentenceEdit);

                    Button button = new Button(context);
                    button.setText(R.string.delete);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tempPoint = dataset.get(getAdapterPosition());
                            new deleteCoursePointFromDatabase().execute();
                        }
                    });
                    layout.addView(button);
                    builder.setView(layout);
                    builder.setPositiveButton("Make These Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] textArray = {cardFrontEdit.getText().toString(),cardBackEdit.getText().toString(),sentenceEdit.getText().toString()};
                            tempPoint = dataset.get(getAdapterPosition());
                            new updateCoursePointInDatabase().execute(textArray);

                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    builder.create().show();
                }
            };
            sentence.setOnClickListener(onClickListener);
            cardFront.setOnClickListener(onClickListener);
            cardBack.setOnClickListener(onClickListener);
        }


    }

    private class updateCoursePointInDatabase extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            MainDatabase database = Room.databaseBuilder(context, MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(tempPoint);
            database.customDao().insertCoursePoint(new CoursePoint(tempPoint.getCourse_ID_foreign(),strings[0],strings[1],strings[2]));
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent intent = new Intent(context, CoursePointsScreen.class);
            intent.putExtra("course ID",tempPoint.getCourse_ID_foreign());
            context.startActivity(intent);
        }

    }

    private class deleteCoursePointFromDatabase extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MainDatabase database = Room.databaseBuilder(context, MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(tempPoint);
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Intent intent = new Intent(context, CoursePointsScreen.class);
            intent.putExtra("course ID",tempPoint.getCourse_ID_foreign());
            context.startActivity(intent);
        }
    }

}
