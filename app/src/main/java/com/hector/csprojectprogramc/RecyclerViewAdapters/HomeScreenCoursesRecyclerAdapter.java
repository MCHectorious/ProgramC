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
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.GeneralUtilities.CustomColourCreator;

import java.util.List;

public class HomeScreenCoursesRecyclerAdapter extends RecyclerView.Adapter<HomeScreenCoursesRecyclerAdapter.ViewHolder> {

    private List<Course> courses;//The list of courses to be shown
    private Context context;//The context from which this was called (Home Screen)

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
        viewHolder.courseNameTextView.setText(courses.get(position).getColloquial_name());//Shows the colloquial name of the course
        viewHolder.courseNameTextView.setBackgroundColor(CustomColourCreator.generateCustomColourFromString(courses.get(position).getOfficial_name()));//Bases the colour of the course on the official name of the course
        viewHolder.qualificationTextView.setText(courses.get(position).getQualification());//Shows the qualification of the course
        viewHolder.examBoardTextView.setText(courses.get(position).getExamBoard());//Shows the exam board of the course
        viewHolder.nextKeyDateTextView.setText(courses.get(position).getNext_key_date());//Shows the date of the next key event (if available)
    }

    @Override
    public int getItemCount() {
        return courses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        private TextView courseNameTextView, qualificationTextView, examBoardTextView,  nextKeyDateTextView;//The text views for the course name, qualification of the course, the exam board of the text view, the date of the next key event respectively
        private ImageView deleteButton;//The button to delete the course
        private CardView courseCardView;//The card which contains the above views
        public Context context;//The context from which this was called (Home Screen)
        public Course course;//The course this ViewHolder is representing

        ViewHolder(View cv, final List<Course> courses, final Context context){
            super(cv);
            this.context = context;

            courseCardView = cv.findViewById(R.id.homeScreenCardView);//Initialises the card view
            courseCardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    course = courses.get(getAdapterPosition());//Initialises the course
                    Intent toCourseScreen = new Intent(context, CourseScreen.class);//Creates a  connection between this context and the Course Screen
                    toCourseScreen.putExtra(context.getString(R.string.course_id),course.getCourse_ID() );//Provides the  id of the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.official_name),course.getOfficial_name() );//Provides the official name of the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.colloquial_name),course.getColloquial_name() );//Provides the colloquial name of the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.exam_board),course.getExamBoard() );//Provides the exam board of the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.qualification),course.getQualification() );//Provides the qualification of the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.key_date),course.getNext_key_date() );//Provides the date of the next key event for the course to the course screen
                    toCourseScreen.putExtra(context.getString(R.string.key_date_details), course.getNext_key_date_detail());//Provides the details of the next key event for the course to the course screen
                    context.startActivity(toCourseScreen);//Starts the course screen
                }
            });

            courseNameTextView =  cv.findViewById(R.id.courseName);//Initialises the text view from the XML code
            qualificationTextView =  cv.findViewById(R.id.qualification);//Initialises the text view from the XML code
            examBoardTextView =  cv.findViewById(R.id.examBoard);//Initialises the text view from the XML code
            nextKeyDateTextView =  cv.findViewById(R.id.date);//Initialises the text view from the XML code

            deleteButton =  cv.findViewById(R.id.deleteCourseButton);//Initialises the image view from the XML code
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder deleteWarningBuilder = new AlertDialog.Builder(context);
                    deleteWarningBuilder.setTitle(R.string.warning);
                    deleteWarningBuilder.setMessage(R.string.deleting_course_warning);//Explains what the issue is to the user
                    deleteWarningBuilder.setCancelable(true).setPositiveButton(R.string.okay, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            course = courses.get(getAdapterPosition());//Get the course this represents
                            new DeleteCourseFromDatabase(context,course, new RefreshScreen()).execute();//Deletes the course from the database and then refreshed the screen
                        }
                    });
                    deleteWarningBuilder.setNegativeButton(R.string.cancel, AlertDialogHelper.onClickDismissDialog());//Dismisses the dialog if the user decides to not delete the course
                    deleteWarningBuilder.create().show();//Show the alert dialog

                }
            });
        }

        private class RefreshScreen implements AsyncTaskCompleteListener<Void>{

            @Override
            public void onAsyncTaskComplete(Void result) {
                Intent refreshHomeScreen = new Intent(context,HomeScreen.class);//Creates a connection to the Home Screen
                context.startActivity(refreshHomeScreen);//Refreshes the home screen
            }

        }

    }




}
