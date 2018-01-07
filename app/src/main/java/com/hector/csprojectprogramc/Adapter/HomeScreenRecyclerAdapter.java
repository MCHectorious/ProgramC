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
import com.hector.csprojectprogramc.Util.CustomColourCreator;

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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card,parent,false);
        ViewHolder viewHolder = new ViewHolder(view, dataset, context);

        return viewHolder;

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.courseNameView.setText(dataset.get(position).getColloquial_name());
        holder.courseNameView.setBackgroundColor(CustomColourCreator.getColourFromString(dataset.get(position).getOfficial_name()));
        holder.qualificationView.setText(dataset.get(position).getQualification());
        holder.examboardView.setText(dataset.get(position).getExamBoard());
        holder.dateView.setText(dataset.get(position).getNext_key_date());
        /*holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dataset.remove(holder.getAdapterPosition());
                notifyItemRemoved(holder.getAdapterPosition());
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView courseNameView, qualificationView, examboardView,  dateView;
        public ImageView deleteButton;
        public CardView cardView;
        public Context context;


        public ViewHolder(View cv, final List<Course> dataset, final Context context){
            super(cv);
            this.context = context;
            cardView = cv.findViewById(R.id.homeScreenCardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CourseScreen.class);
                    intent.putExtra("Course ID",dataset.get(getAdapterPosition()).getCourse_ID() );
                    intent.putExtra("Official Name",dataset.get(getAdapterPosition()).getOfficial_name() );
                    intent.putExtra("Colloqial Name",dataset.get(getAdapterPosition()).getColloquial_name() );
                    intent.putExtra("Exam Board",dataset.get(getAdapterPosition()).getExamBoard() );
                    intent.putExtra("Qualification",dataset.get(getAdapterPosition()).getQualification() );
                    intent.putExtra("Key Date",dataset.get(getAdapterPosition()).getNext_key_date() );
                    intent.putExtra("Key Date Details", dataset.get(getAdapterPosition()).getNext_key_date_detail());


                    context.startActivity(intent);
                }
            });
            courseNameView =  cv.findViewById(R.id.courseName);
            qualificationView =  cv.findViewById(R.id.qualification);
            examboardView =  cv.findViewById(R.id.examBoard);
            dateView =  cv.findViewById(R.id.date);
            deleteButton =  cv.findViewById(R.id.options);//TODO: add deleting subjects

            //this.context = context;
        }


    }




}
