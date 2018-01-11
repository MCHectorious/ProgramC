package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector - New on 25/12/2017.
 */

public class CardCoursePointsAdapter extends RecyclerView.Adapter<CardCoursePointsAdapter.ViewHolder> {

    List<CoursePoints> dataset;

    public CardCoursePointsAdapter(List<CoursePoints> points, Context context){
        dataset = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_flashcard_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, dataset);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardSide.setText(dataset.get(position).getFlashcard_front());
        int colour = CustomColourCreator.getColourFromString(dataset.get(position).getSentence());
        holder.arrowRight.setBackgroundColor(colour);
        holder.arrowLeft.setBackgroundColor(colour);
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cardSide;
        ImageView arrowLeft, arrowRight;
        boolean showFront = true;

        public ViewHolder (View v, final List<CoursePoints> dataset){
            super(v);
            cardSide = v.findViewById(R.id.cardSide);
            arrowLeft = v.findViewById(R.id.leftPointer);
            arrowRight = v.findViewById(R.id.rightPointer);
            View.OnClickListener onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFront = !showFront;
                    cardSide.setText( (showFront)? dataset.get(getAdapterPosition()).getFlashcard_front() : dataset.get(getAdapterPosition()).getFlashcard_back()  );
                }
            };
            arrowLeft.setOnClickListener(onClickListener);
            arrowRight.setOnClickListener(onClickListener);

        }


    }


}