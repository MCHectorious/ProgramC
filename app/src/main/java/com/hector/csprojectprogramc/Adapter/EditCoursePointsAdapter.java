package com.hector.csprojectprogramc.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.CoursePoints;
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

    public EditCoursePointsAdapter(List<CoursePoints> points, Context context){
        dataset = points;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_edit_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view,context);
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


    public static class ViewHolder extends RecyclerView.ViewHolder{
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

                    builder.setView(layout);

                    builder.setPositiveButton("Make These Changes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                }
            };
        }


    }


}
