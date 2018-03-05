package com.hector.csprojectprogramc.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import com.hector.csprojectprogramc.WebScraping.AQAScraper;


import java.util.ArrayList;



public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder>{

    private static ArrayList<String> courseNames, courseWebsites;
    private static Context context, appContext;
    private static String qualification;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public CardView cardView;
        public ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.courseNameInList);
            cardView = view.findViewById(R.id.courseListCard);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AQAScraper(courseWebsites.get(getAdapterPosition()),context,appContext, qualification,courseNames.get(getAdapterPosition()));
                }
            });
        }

    }

    public CourseListAdapter(ArrayList<String> cNames, ArrayList<String> cWebsites, Context context, Context appContext, String qualification){
        courseNames = cNames;
        courseWebsites = cWebsites;
        this.context = context;
        this.appContext = appContext;
        this.qualification = qualification;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.textView.setText(courseNames.get(position));
        holder.cardView.setCardBackgroundColor(CustomColourCreator.getColourFromString(courseNames.get(position)));
    }

    @Override
    public int getItemCount() {
        return courseNames.size();
    }

}