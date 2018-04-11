package com.hector.csprojectprogramc.RecyclerViewAdapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;
import java.util.List;

public class CoursePointsScreenFlashcardAdapter extends RecyclerView.Adapter<CoursePointsScreenFlashcardAdapter.ViewHolder> {

    private List<CoursePoint> coursePoints;//The list of course points to be shown

    public CoursePointsScreenFlashcardAdapter(List<CoursePoint> points){
        coursePoints = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_flashcard_card,parent,false);
        return new ViewHolder(card, coursePoints);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.flashcardFormSideTextView.setText(coursePoints.get(position).getFlashcard_front());//Defaults to the front of the flashcard form of the course point
        viewHolder.flashcardCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(coursePoints.get(position).getSentence()));//bases the colour of the course point on the sentence form
    }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView flashcardFormSideTextView;//Where either the front or the back of the flashcard form of the course point will be shown
        CardView flashcardCardView;// the card containing the above text view
        boolean showFront = true;//defaults to showing the front of the flashcard first

        private ViewHolder (View v, final List<CoursePoint> dataSet){
            super(v);
            flashcardFormSideTextView = v.findViewById(R.id.cardSide);//Initialises the text view
            flashcardCardView = v.findViewById(R.id.cardViewCoursePointsFlashcard);//Initialises the card view

            flashcardFormSideTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showFront = !showFront;//Flips the value of the boolean
                    flashcardFormSideTextView.setText( (showFront)? dataSet.get(getAdapterPosition()).getFlashcard_front() : dataSet.get(getAdapterPosition()).getFlashcard_back()  );//Shows the front or back ofthe flashcard repsectively
                }
            });


        }


    }


}
