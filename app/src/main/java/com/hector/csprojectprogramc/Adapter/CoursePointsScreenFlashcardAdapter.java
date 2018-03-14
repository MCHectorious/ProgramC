package com.hector.csprojectprogramc.Adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.Database.CoursePoint;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Utilities.CustomColourCreator;
import java.util.List;

public class CoursePointsScreenFlashcardAdapter extends RecyclerView.Adapter<CoursePointsScreenFlashcardAdapter.ViewHolder> {

    private List<CoursePoint> coursePoints;

    public CoursePointsScreenFlashcardAdapter(List<CoursePoint> points){
        coursePoints = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_flashcard_card,parent,false);//TODO: think of better name
        return new ViewHolder(view, coursePoints);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.flashcardFormSideTextView.setText(coursePoints.get(position).getFlashcard_front());
        viewHolder.flashcardCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(coursePoints.get(position).getSentence()));
    }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView flashcardFormSideTextView;
        CardView flashcardCardView;
        boolean showFront = true;

        private ViewHolder (View v, final List<CoursePoint> dataSet){
            super(v);
            flashcardFormSideTextView = v.findViewById(R.id.cardSide);
            flashcardCardView = v.findViewById(R.id.cardViewCoursePointsFlashcard);

            flashcardFormSideTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFront = !showFront;
                    flashcardFormSideTextView.setText( (showFront)? dataSet.get(getAdapterPosition()).getFlashcard_front() : dataSet.get(getAdapterPosition()).getFlashcard_back()  );
                }
            });


        }


    }


}
