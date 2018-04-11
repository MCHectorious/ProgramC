package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCoursePointsToDatabase extends AsyncTask<CoursePoint,Void, Void>{

    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private AsyncTaskCompleteListener<Void> onCompleteListener;//Handle what should occur after the task is complete


    public InsertCoursePointsToDatabase(Context context, AsyncTaskCompleteListener<Void> onCompleteListener){//Initialises the fields
        this.context = new WeakReference<>(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(CoursePoint... coursePoints) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        int courseID = -1;//Initialises the id of the course to an impossible value
        try {
            for (CoursePoint coursePoint : coursePoints) {
                courseID = coursePoint.getCourse_ID_foreign();//Gets the course ID for the course point for error handling purposes
                database.databaseAccessObject().insertCoursePoint(coursePoint);//inserts the course point via an SQL statement
            }
        }catch(SQLiteConstraintException exception){//Will occur if the foreign key doesn't match any value in the parent column (in this case if the foreign course ID doesn't reference an actual course)
            Log.e("Course ID", Integer.toString(courseID));//Logs what it thinks the foreign key should be to aid in discovering the issue
        }catch (NullPointerException exception){//Will occur if the database access object is null or the course points are null
            Toast.makeText(context.get(), R.string.unable_to_add_course_point,Toast.LENGTH_LONG).show();//Tells the user that an issue has occurred
            Log.w(context.get().getString(R.string.unable_to_add_course_point),exception.getMessage());//Logs the issue so that the cause can be determined
        }finally {
            if (database != null){//Checks the database has been initialised correctly
                database.close();//Closes the connection to the database to avoid leaks
            }
        }

        return null;//In order to override the method correctly
    }

    @Override
    protected void onPostExecute(Void result){//When the task is complete
        super.onPostExecute(result);//Runs the generic code for what should happen when the task is complete

        onCompleteListener.onAsyncTaskComplete(result);//Runs the code that should when the task is complete

    }

}
