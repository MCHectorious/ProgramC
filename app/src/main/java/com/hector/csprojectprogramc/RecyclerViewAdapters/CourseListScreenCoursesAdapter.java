package com.hector.csprojectprogramc.RecyclerViewAdapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hector.csprojectprogramc.CourseImport.AQACourseImport;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;

import java.util.ArrayList;

public class CourseListScreenCoursesAdapter extends RecyclerView.Adapter<CourseListScreenCoursesAdapter.ViewHolder>{

    private Context context;//The screen the adapter is being used in (CourseListScreen).
    private String qualification;//The qualification of the courses to be shown
    private String[] courseNames, courseWebsites;//The list of the course names and their respective websites

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameTextView;//Where the name of the course is to be displayed
        private CardView courseCardView;//The card which contains the above Text View
        private ViewHolder(final View view){
            super(view);
            courseNameTextView = view.findViewById(R.id.courseNameInList);//Gets the text view from the XML code
            courseCardView = view.findViewById(R.id.courseListCard);//Gets the card from the XML code
            view.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    new AQACourseImport(context, courseNames[getAdapterPosition()],  qualification, courseWebsites[getAdapterPosition()], new IfAnErrorOccursHideView(view)).execute();//Inserts the course into the database and will also get the course points and insert them into the database
                }
            });

        }

    }

    public class IfAnErrorOccursHideView implements AsyncTaskErrorListener{

        private View view;//View that will be hidden

        IfAnErrorOccursHideView(View view){
            this.view = view;
        }

        @Override
        public void onAsyncTaskError() {
            view.setVisibility(View.GONE);//Hides the view
        }
    }

    public CourseListScreenCoursesAdapter(ArrayList<String> courseNames, ArrayList<String> courseWebsites, Context context, String qualification){
        this.courseNames = courseNames.toArray(new String[0]);
        this.courseWebsites = courseWebsites.toArray(new String[0]);
        this.context = context;
        this.qualification = qualification;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder( LayoutInflater.from(parent.getContext()).inflate(R.layout.course_list_card,parent,false) );//Gets the ViewHolder for the course
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.courseNameTextView.setText(courseNames[position]);//Shows the courseName
        holder.courseCardView.setCardBackgroundColor(CustomColourCreator.generateCustomColourFromString(courseNames[position]));//Bases the colour of the card on the course's official name
    }

    @Override
    public int getItemCount() {
        return courseNames.length;
    }














}