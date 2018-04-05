package com.hector.csprojectprogramc.RecyclerViewAdapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.DeleteCourseFromDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;

import java.util.List;

public class HomeScreenCoursesRecyclerAdapter extends RecyclerView.Adapter<HomeScreenCoursesRecyclerAdapter.ViewHolder> {

    private List<Course> courses;
    private Context context;

    public HomeScreenCoursesRecyclerAdapter(List<Course> courses, Context context){
        this.courses = courses;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View card = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card,parent,false);
        return new ViewHolder(card, courses, context);

    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        viewHolder.courseNameTextView.setText(courses.get(position).getColloquial_name());
        viewHolder.courseNameTextView.setBackgroundColor(CustomColourCreator.generateCustomColourFromString(courses.get(position).getOfficial_name()));
        viewHolder.qualificationTextView.setText(courses.get(position).getQualification());
        viewHolder.examBoardTextView.setText(courses.get(position).getExamBoard());
        viewHolder.nextKeyDateTextView.setText(courses.get(position).getNext_key_date());
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameTextView, qualificationTextView, examBoardTextView,  nextKeyDateTextView;
        private ImageView deleteButton;
        private CardView courseCardView;
        public Context context;
        public Course course;

        ViewHolder(View cv, final List<Course> courses, final Context context){
            super(cv);
            this.context = context;

            courseCardView = cv.findViewById(R.id.homeScreenCardView);
            courseCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    course = courses.get(getAdapterPosition());
                    Intent intent = new Intent(context, CourseScreen.class);
                    intent.putExtra(context.getString(R.string.course_id),course.getCourse_ID() );
                    intent.putExtra(context.getString(R.string.official_name),course.getOfficial_name() );
                    intent.putExtra(context.getString(R.string.colloquial_name),course.getColloquial_name() );
                    intent.putExtra(context.getString(R.string.exam_board),course.getExamBoard() );
                    intent.putExtra(context.getString(R.string.qualification),course.getQualification() );
                    intent.putExtra(context.getString(R.string.key_date),course.getNext_key_date() );
                    intent.putExtra(context.getString(R.string.key_date_details), course.getNext_key_date_detail());
                    context.startActivity(intent);
                }
            });
            courseNameTextView =  cv.findViewById(R.id.courseName);
            qualificationTextView =  cv.findViewById(R.id.qualification);
            examBoardTextView =  cv.findViewById(R.id.examBoard);
            nextKeyDateTextView =  cv.findViewById(R.id.date);
            deleteButton =  cv.findViewById(R.id.deleteCourseButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder warningBuilder = new AlertDialog.Builder(context);
                    warningBuilder.setTitle(R.string.warning);
                    warningBuilder.setMessage(R.string.deleting_course_warning);
                    warningBuilder.setCancelable(true).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            course = courses.get(getAdapterPosition());
                            new DeleteCourseFromDatabase(context,course, new RefreshScreen()).execute();
                        }
                    });
                    warningBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    warningBuilder.create().show();

                }
            });
        }

        private class RefreshScreen implements AsyncTaskCompleteListener<Void>{

            @Override
            public void onAsyncTaskComplete(Void result) {
                Intent refreshHomeScreen = new Intent(context,HomeScreen.class);
                context.startActivity(refreshHomeScreen);
            }
        }

    }




}
