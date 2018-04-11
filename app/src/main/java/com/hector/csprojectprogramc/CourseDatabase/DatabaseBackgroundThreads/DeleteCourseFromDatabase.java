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

public class DeleteCourseFromDatabase extends AsyncTask<Void,Void,Void> {

    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private Course course;//The course to be deleted
    private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what should occur after this task is complete

    public DeleteCourseFromDatabase(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener){//Initialises all the fields
        this.context = new WeakReference<>(context);
        this.course = course;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            database.databaseAccessObject().deleteCourse(course);//Deletes the course
        }catch (NullPointerException exception){//Will occur if the course or the database access object is null
            Toast.makeText(context.get(), R.string.unable_to_delete_course,Toast.LENGTH_LONG).show();//Shows the user that the program is unable to delete the course
            Log.w(context.get().getString(R.string.unable_to_delete_course),exception.getMessage());//Logs the issue so that it can be checked what is calling the issue
        }finally {
            if(database != null){//Makes sure the database has been initialised correctly
                database.close();//Closes the database to avoid leaks;
            }
        }

        return null;//To conform to the standard of its superclass
    }

    @Override
    protected void onPostExecute(Void result){//When the task is complete
        super.onPostExecute(result);//Do the general things that an async task does after doing the process

        onCompleteListener.onAsyncTaskComplete(result);//Lets the program continue with the next things it needs to do
    }
}
