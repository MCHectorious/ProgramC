package com.hector.csprojectprogramc.Adapter;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import com.hector.csprojectprogramc.Database.CoursePoints;
import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Util.CustomColourCreator;
import java.util.List;

public class HomeScreenRecyclerAdapter extends RecyclerView.Adapter<HomeScreenRecyclerAdapter.ViewHolder> {

    private List<Course> dataset;
    private Context context;

    public HomeScreenRecyclerAdapter(List<Course> courses, Context context){
        dataset = courses;
        this.context = context;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_screen_card,parent,false);
        return new ViewHolder(view, dataset, context);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.courseNameView.setText(dataset.get(position).getColloquial_name());
        holder.courseNameView.setBackgroundColor(CustomColourCreator.getColourFromString(dataset.get(position).getOfficial_name()));
        holder.qualificationView.setText(dataset.get(position).getQualification());
        holder.examboardView.setText(dataset.get(position).getExamBoard());
        holder.dateView.setText(dataset.get(position).getNext_key_date());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameView, qualificationView, examboardView,  dateView;
        private ImageView deleteButton;
        private CardView cardView;
        public Context context;
        public Course course;


        public ViewHolder(View cv, final List<Course> dataset, final Context context){
            super(cv);
            this.context = context;

            cardView = cv.findViewById(R.id.homeScreenCardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    course = dataset.get(getAdapterPosition());
                    Intent intent = new Intent(context, CourseScreen.class);
                    intent.putExtra("Course ID",course.getCourse_ID() );
                    intent.putExtra("Official Name",course.getOfficial_name() );
                    intent.putExtra("Colloqial Name",course.getColloquial_name() );
                    intent.putExtra("Exam Board",course.getExamBoard() );
                    intent.putExtra("Qualification",course.getQualification() );
                    intent.putExtra("Key Date",course.getNext_key_date() );
                    intent.putExtra("Key Date Details", course.getNext_key_date_detail());
                    context.startActivity(intent);
                }
            });
            courseNameView =  cv.findViewById(R.id.courseName);
            qualificationView =  cv.findViewById(R.id.qualification);
            examboardView =  cv.findViewById(R.id.examBoard);
            dateView =  cv.findViewById(R.id.date);
            deleteButton =  cv.findViewById(R.id.deleteCourseButton);

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    course = dataset.get(getAdapterPosition());
                    new deleteCourse().execute();
                }
            });
        }

        private class deleteCourse extends AsyncTask<Void,Void,Void>{

            @Override
            protected Void doInBackground(Void... voids) {
                MyDatabase database = Room.databaseBuilder(context, MyDatabase.class, "my-db").build();
                database.customDao().deleteCourse(course);
                List<CoursePoints> points = database.customDao().getPointsForCourse(course.getCourse_ID());
                for(CoursePoints point:points){
                    database.customDao().deleteCoursePoint(point);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void result){
                Intent intent = new Intent(context,HomeScreen.class);
                context.startActivity(intent);
            }
        }

    }
}
