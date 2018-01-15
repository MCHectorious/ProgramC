package com.hector.csprojectprogramc.Adapter;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hector.csprojectprogramc.Activities.CourseListScreen;
import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import com.hector.csprojectprogramc.Util.MultiThreading;
import com.hector.csprojectprogramc.WebScraping.AQAScraper;
import com.hector.csprojectprogramc.WebScraping.CramScraper;
import com.hector.csprojectprogramc.WebScraping.MemRiseScraper;

import java.util.ArrayList;

/**
 * Created by Hector - New on 23/12/2017.
 */

public class CourseListAdapter extends RecyclerView.Adapter<CourseListAdapter.ViewHolder>{

    private static ArrayList<String> courseNames, courseWebsites;
    private static Context context, appContext;
    private static String qualification;

    public static class ViewHolder extends RecyclerView.ViewHolder{
        //public CardView cardView;
        public TextView textView;
        public CardView cardView;
        //private Course course;
        //private MyDatabase database;
        public ViewHolder(View view){
            super(view);
            textView = view.findViewById(R.id.courseNameInList);
            cardView = view.findViewById(R.id.courseListCard);
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    Log.i("Click", courseWebsites.get(getAdapterPosition()));
                    AQAScraper scraper = new AQAScraper(courseWebsites.get(getAdapterPosition()),context,appContext, qualification,courseNames.get(getAdapterPosition()));


                    //MemRiseScraper memRiseScraper = new MemRiseScraper();
                    //memRiseScraper.insertCoursePointsInDataBase(context, course, database);
                    //CramScraper cramScraper = new CramScraper();
                    //cramScraper.insertCoursePointsInDataBase(context, course, database);
                    //Toast toast = Toast.makeText(context,"Saved Course",Toast.LENGTH_LONG);
                    //toast.show();
                    //AsyncTask.Status finished = AsyncTask.Status.FINISHED;
                    //while (insertCourse.getStatus().equals(finished)){}
                    //MultiThreading.waitUntilFinished(insertCourse);
                    //new MemRiseScraper().insertCoursePointsInDataBase(context, course, database);
                    //new CramScraper().insertCoursePointsInDataBase(context, course, database);

                    //Log.i("Need to","Implement going back to home screen");

                    //Intent intent = new Intent(context, HomeScreen.class);
                    //new CourseListScreen().startActivity(intent);
                    //context.startActivity(intent);

                }
            });
        }

        /*private boolean hasNotFinished(AsyncTask.Status status){
            return !status.equals(AsyncTask.Status.FINISHED);
        }*/



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
        holder.cardView.setBackgroundColor(CustomColourCreator.getColourFromString(courseNames.get(position)));



        //try{
          //  holder.textView.performClick();

        //}catch (Exception e){
          //  Log.e("Error - "+position,e.getMessage());
        //}

    }

    @Override
    public int getItemCount() {
        return courseNames.size();
    }

}