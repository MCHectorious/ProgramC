package com.hector.csprojectprogramc.Adapter;

import android.app.AlertDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hector.csprojectprogramc.Activities.CoursePointsScreen;
import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector - New on 25/12/2017.
 */

public class EditCoursePointsAdapter extends RecyclerView.Adapter<EditCoursePointsAdapter.ViewHolder> {

    List<CoursePoints> dataset;
    Context context;
    CoursePoints tempPoint;

    public EditCoursePointsAdapter(List<CoursePoints> points, Context context){
        dataset = points;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_edit_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,context,dataset);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.cardFront.setText(dataset.get(position).getFlashcard_front());
        holder.cardBack.setText(dataset.get(position).getFlashcard_back());
        holder.sentence.setText(dataset.get(position).getSentence());
        int colour = CustomColourCreator.getColourFromString(dataset.get(position).getSentence());
        holder.border1.setBackgroundColor(colour);
        holder.border2.setBackgroundColor(colour);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView cardFront, cardBack, sentence;
        TextView border1, border2;
        public ViewHolder (View v, final Context context, final List<CoursePoints> dataset){
            super(v);
            sentence = v.findViewById(R.id.sentenceEdit);
            cardFront = v.findViewById(R.id.cardFrontEdit);
            cardBack = v.findViewById(R.id.cardBackEdit);
            border1 = v.findViewById(R.id.border1);
            border2 = v.findViewById(R.id.border2);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i("Got this far","Clicked on Edit");
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
                    button.setText("Delete");
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tempPoint = dataset.get(getAdapterPosition());
                            new deleteCoursePoint().execute();
                        }
                    });
                    layout.addView(button);

                    builder.setView(layout);

                    builder.setPositiveButton("Make These Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] textArray = {cardFrontEdit.getText().toString(),cardBackEdit.getText().toString(),sentenceEdit.getText().toString()};
                            tempPoint = dataset.get(getAdapterPosition());
                            new updateCoursePoint().execute(textArray);

                        }
                    });
                    Log.i("Line","112");
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    Log.i("Line","119");
                    builder.create().show();
                    Log.i("Line","121");
                }
            };
            sentence.setOnClickListener(onClickListener);
            cardFront.setOnClickListener(onClickListener);
            cardBack.setOnClickListener(onClickListener);
            Log.i("Line","127");
        }


    }

    private class updateCoursePoint extends AsyncTask<String,Void,Void>{

        @Override
        protected Void doInBackground(String... strings) {
            Log.i("Line","137");
            MyDatabase database = Room.databaseBuilder(context, MyDatabase.class, "my-db").build();
            Log.i("Line","139");
            //database.customDao().updateCoursePoint(new CoursePoints(tempPoint.getCourse_ID_foreign(),strings[0],strings[1],strings[2]));
            //Log.i("Line","141");
            //dataset = database.customDao().getPointsForCourse(tempPoint.getCourse_ID_foreign());

            database.customDao().deleteCoursePoint(tempPoint);
            database.customDao().insertCoursePoint(new CoursePoints(tempPoint.getCourse_ID_foreign(),strings[0],strings[1],strings[2]));
            Log.i("Line","146");
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i("Line","149");
            Intent intent = new Intent(context, CoursePointsScreen.class);
            intent.putExtra("course ID",tempPoint.getCourse_ID_foreign());
            Log.i("Line","152");
            context.startActivity(intent);
        }

    }

    private class deleteCoursePoint extends AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            MyDatabase database = Room.databaseBuilder(context, MyDatabase.class, "my-db").build();
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
