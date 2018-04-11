package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCourseToDatabase extends AsyncTask<Course,Void,Void> {

    private AsyncTaskCompleteListener<Void> onCompleteListener;//Handle what should occur when the task is complete
    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process

    public InsertCourseToDatabase(Context context, AsyncTaskCompleteListener<Void> onCompleteListener){//Initialises the fields
        this.onCompleteListener = onCompleteListener;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Course... courses) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            int i=1;//Initialises the amount that should be added to the maximum id to get a new valid id
            int maxCourseID = database.databaseAccessObject().getMaxCourseID();//Gets the maximum course id in the table
            for (Course course: courses) {
                course.setCourse_ID(maxCourseID+i);//Sets the course id to the smallest value not currently an id
                i++;//increments the value of the course id
                database.databaseAccessObject().insertCourse(course);//inserts the course
            }
        }catch (NullPointerException exception){//Will occur is courses is null or if the database access object is null
            Log.w(context.get().getString(R.string.unable_to_add_course_point),exception.getMessage());//Logs the issue so that the cause can be determined
        }finally {
            if (database != null){//Checks the database has been  initialised correctly
                database.close();//Closes the connection to the database to avoid leaks;
            }
        }

        return null;//To conform to the standard of the superclass
    }

    @Override
    protected void onPostExecute(Void result) {//When the task is complete
        super.onPostExecute(result);//Run the generic code that should be ran when the task is complete
        onCompleteListener.onAsyncTaskComplete(result);//Run the code that should occur when the task is complete
    }
}
