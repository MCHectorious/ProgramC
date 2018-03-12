package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Utilities.CustomColourCreator;
import com.hector.csprojectprogramc.WebScraping.AQAScraper;


import java.util.ArrayList;



public class CourseListScreenCoursesAdapter extends RecyclerView.Adapter<CourseListScreenCoursesAdapter.ViewHolder>{

    private static ArrayList<String> courseNames, courseWebsites;
    private Context context, appContext;
    private String qualification;

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameTextView;
        private CardView courseCardView;
        private ViewHolder(View view){
            super(view);
            courseNameTextView = view.findViewById(R.id.courseNameInList);
            courseCardView = view.findViewById(R.id.courseListCard);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AQAScraper(courseWebsites.get(getAdapterPosition()),context,appContext, qualification,courseNames.get(getAdapterPosition()));
                }
            });
        }

    }

    public CourseListScreenCoursesAdapter(ArrayList<String> courseNames, ArrayList<String> courseWebsites, Context context, Context appContext, String qualification){
        this.courseNames = courseNames;
        this.courseWebsites = courseWebsites;
        this.context = context;
        this.appContext = appContext;
        this.qualification = qualification;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_card,parent,false) );
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.courseNameTextView.setText(courseNames.get(position));
        holder.courseCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(courseNames.get(position)));
    }

    @Override
    public int getItemCount() {
        return courseNames.size();
    }

}