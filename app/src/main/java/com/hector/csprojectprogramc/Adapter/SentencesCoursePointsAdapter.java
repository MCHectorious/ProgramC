package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import java.util.List;

public class SentencesCoursePointsAdapter extends RecyclerView.Adapter<SentencesCoursePointsAdapter.ViewHolder> {

    private List<CoursePoints> dataset;

    public SentencesCoursePointsAdapter(List<CoursePoints> points, Context context){
        dataset = points;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.course_points_sentence_card,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        String sentence = dataset.get(position).getSentence();
        holder.sentence.setText(sentence);
        holder.cardView.setCardBackgroundColor(CustomColourCreator.getColourFromString(sentence));

    }

    @Override
    public int getItemCount() {
        return dataset.size();
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
