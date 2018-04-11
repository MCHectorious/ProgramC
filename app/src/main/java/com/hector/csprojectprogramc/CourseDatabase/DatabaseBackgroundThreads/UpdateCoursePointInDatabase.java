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

public class UpdateCoursePointInDatabase extends AsyncTask<Void,Void,Void> {

    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private CoursePoint coursePointToBeUpdated;//The course that is to be updated
    private AsyncTaskCompleteListener<Void> onCompleteListener;//Handle what should occur after the task is complete

    public UpdateCoursePointInDatabase(Context context, CoursePoint coursePointToBeUpdated, AsyncTaskCompleteListener<Void> onCompleteListener){//Initialises the fields
        this.context = new WeakReference<>(context);
        this.coursePointToBeUpdated = coursePointToBeUpdated;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(Void... strings) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            database.databaseAccessObject().updateCoursePoint(coursePointToBeUpdated);//Updates the course point
        }catch (NullPointerException exception){//Will occur if the database access object is null
            Toast.makeText(context.get(), R.string.unable_to_update_course_point,Toast.LENGTH_LONG).show();//Tells the user that an issue has occurred
            Log.w(context.get().getString(R.string.unable_to_update_course_point),exception.getMessage());//Logs the issue so that the cause can be determined
        }finally {
            if(database!=null){//Checks that the database has been initialised correctly
                database.close();//Closes the connection to the database to avoid leaks

            }
        }

        return null;//To conform to the standard of it superclass
    }

    @Override
    protected void onPostExecute(Void result){//When the task is complete
        super.onPostExecute(result);//Run the generic code that is ran when the task is complete

        onCompleteListener.onAsyncTaskComplete(result);//Run the appropriate code for when the task is complete
    }

}
