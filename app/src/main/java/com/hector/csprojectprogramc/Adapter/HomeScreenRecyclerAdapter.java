package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hector.csprojectprogramc.Activities.CourseScreen;
import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hector - New on 25/12/2017.
 */

public class HomeScreenRecyclerAdapter extends RecyclerView.Adapter<HomeScreenRecyclerAdapter.ViewHolder> {

    private List<Course> dataset;
    Context context;

    public HomeScreenRecyclerAdapter(List<Course> courses, Context context){
        dataset = courses;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CardView cardView = (CardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(cardView, context);
        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.courseNameView.setText(dataset.get(position).getColloquial_name());
        holder.qualificationView.setText(dataset.get(position).getQualification());
        holder.examboardView.setText(dataset.get(position).getExamBoard());
        holder.dateView.setText(dataset.get(position).getNext_key_date());
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataset.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView courseNameView, qualificationView, examboardView,  dateView;
        public ImageView deleteButton;
        public Context context;


        public ViewHolder(CardView cv,Context context){
            super(cv);
            courseNameView =  cv.findViewById(R.id.courseName);
            qualificationView =  cv.findViewById(R.id.qualification);
            examboardView =  cv.findViewById(R.id.examBoard);
            dateView =  cv.findViewById(R.id.date);
            deleteButton =  cv.findViewById(R.id.options);//TODO: add deleting subjects

            this.context = context;
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, CourseScreen.class);
            intent.putExtra("course ID",1);//TODO:Fix
            context.startActivity(intent);
        }
    }




}
