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

public class CoursePointsScreenSentencesAdapter extends RecyclerView.Adapter<CoursePointsScreenSentencesAdapter.ViewHolder> {

    private List<CoursePoint> coursePoints;

    public CoursePointsScreenSentencesAdapter(List<CoursePoint> coursePoints){
        this.coursePoints = coursePoints;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_sentence_card,parent,false);//TODO: think of better name
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        String sentenceForm = coursePoints.get(position).getSentence();
        viewHolder.sentenceFormTextView.setText(sentenceForm);
        viewHolder.sentenceFormCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(sentenceForm));

    }

    @Override
    public int getItemCount() {
        return coursePoints.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView sentenceFormTextView;
        CardView sentenceFormCardView;

        private ViewHolder (View v){
            super(v);
            sentenceFormTextView = v.findViewById(R.id.sentenceSentence);
            sentenceFormCardView = v.findViewById(R.id.cardViewCoursePointsSentence);

        }
    }
}
