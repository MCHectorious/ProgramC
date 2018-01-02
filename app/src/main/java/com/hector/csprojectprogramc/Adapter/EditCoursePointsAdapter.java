package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.R;

import java.util.ArrayList;

/**
 * Created by Hector - New on 25/12/2017.
 */

public class EditCoursePointsAdapter extends RecyclerView.Adapter<EditCoursePointsAdapter.ViewHolder> {

    ArrayList<CoursePoints> dataset;

    public EditCoursePointsAdapter(ArrayList<CoursePoints> points, Context context){
        dataset = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_sentence_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardFront.setText(dataset.get(position).getFlashcard_front());
        holder.cardBack.setText(dataset.get(position).getFlashcard_back());
        holder.sentence.setText(dataset.get(position).getSentence());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cardFront, cardBack, sentence;

        public ViewHolder (CardView cv){
            super(cv);
            sentence = (TextView) cv.findViewById(R.id.sentences);
            cardFront = cv.findViewById(R.id.cardFront);
            cardBack = cv.findViewById(R.id.cardBack);
        }


    }


}
