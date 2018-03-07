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

public class CoursePointsScreenSentencesAdapter extends RecyclerView.Adapter<CoursePointsScreenSentencesAdapter.ViewHolder> {

    private List<CoursePoint> dataSet;

    public CoursePointsScreenSentencesAdapter(List<CoursePoint> points){
        dataSet = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_sentence_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String sentence = dataSet.get(position).getSentence();
        holder.sentence.setText(sentence);
        holder.cardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(sentence));

    }

    @Override
    public int getItemCount() {
        return dataSet.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView sentence;
        CardView cardView;

        private ViewHolder (View v){
            super(v);
            sentence = v.findViewById(R.id.sentenceSentence);
            cardView = v.findViewById(R.id.cardViewCoursePointsSentence);

        }
    }
}
