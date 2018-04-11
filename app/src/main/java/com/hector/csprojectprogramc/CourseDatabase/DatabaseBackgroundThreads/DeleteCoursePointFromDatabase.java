package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class DeleteCoursePointFromDatabase extends AsyncTask<Void,Void,Void> {

    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private CoursePoint coursePointToDelete;//The course point that is going to be deleted
    private AsyncTaskCompleteListener<Void> onCompleteListener;//Handles what occurs after the task is complete

    public DeleteCoursePointFromDatabase(Context context, CoursePoint coursePointToDelete, AsyncTaskCompleteListener<Void> listener){//Initialises all fields
        this.context = new WeakReference<>(context);
        this.coursePointToDelete = coursePointToDelete;
        this.onCompleteListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            database.databaseAccessObject().deleteCoursePoint(coursePointToDelete);//Deletes the course point
        }catch(NullPointerException exception){//Will occur if the course point or the database access object is null
            Toast.makeText(context.get(), R.string.unable_to_delete_course_point,Toast.LENGTH_LONG).show();//Shows the user that an issue has occurred
            Log.w(context.get().getString(R.string.unable_to_delete_course_point),exception.getMessage());//Logs the issue so that the cause can be determined
        }finally {
            if (database != null){//Checks the database has been initialised correctly
                database.close();//Closes the connection to the database to avoid leaks
            }
        }

        return null;//To conform to the standard of its superclass
    }

    @Override
    protected void onPostExecute(Void result){//When the task has finished
        super.onPostExecute(result);//Runs generic code that should occur after finishing the task

        onCompleteListener.onAsyncTaskComplete(result);//Runs the code that should occur after the task is complete
    }
}
