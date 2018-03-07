package com.hector.csprojectprogramc.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import java.util.List;

public class CoursePointsScreenFlashcardAdapter extends RecyclerView.Adapter<CoursePointsScreenFlashcardAdapter.ViewHolder> {

    private List<CoursePoint> dataSet;

    public CoursePointsScreenFlashcardAdapter(List<CoursePoint> points){
        dataSet = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_flashcard_card,parent,false);
        return new ViewHolder(view, dataSet);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.cardSide.setText(dataSet.get(position).getFlashcard_front());
        holder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(dataSet.get(position).getSentence()));
    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView cardSide;
        CardView cardView;
        boolean showFront = true;

        private ViewHolder (View v, final List<CoursePoint> dataset){
            super(v);
            cardSide = v.findViewById(R.id.cardSide);
            cardView = v.findViewById(R.id.cardViewCoursePointsFlashcard);

            cardSide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFront = !showFront;
                    cardSide.setText( (showFront)? dataset.get(getAdapterPosition()).getFlashcard_front() : dataset.get(getAdapterPosition()).getFlashcard_back()  );
                }
            });


        }


    }


}
