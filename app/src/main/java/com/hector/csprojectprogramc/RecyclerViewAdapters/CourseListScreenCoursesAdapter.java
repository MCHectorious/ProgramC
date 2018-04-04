package com.hector.csprojectprogramc.RecyclerViewAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;
import com.hector.csprojectprogramc.CourseImport.AQAScraper;
import java.util.Collection;
import java.util.Set;


public class CourseListScreenCoursesAdapter extends RecyclerView.Adapter<CourseListScreenCoursesAdapter.ViewHolder>{

    private Context context, appContext;
    private String qualification;
    private String[] courseNames, courseWebsites;

    @SuppressWarnings("WeakerAccess")
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

                    new AQAScraper(courseWebsites[getAdapterPosition()],context,appContext, qualification,courseNames[getAdapterPosition()]);
                }
            });
        }

    }

    public CourseListScreenCoursesAdapter(Set<String> courseNames, Collection<String> courseWebsites, Context context, Context appContext, String qualification){
        this.courseNames = courseNames.toArray(new String[0]);
        this.courseWebsites = courseWebsites.toArray(new String[0]);
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
        holder.courseNameTextView.setText(courseNames[position]);
        holder.courseCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(courseNames[position]));
    }

    @Override
    public int getItemCount() {
        return courseNames.length;
    }














}