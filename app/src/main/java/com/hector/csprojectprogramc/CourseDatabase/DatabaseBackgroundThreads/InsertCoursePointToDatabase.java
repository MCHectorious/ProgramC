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

public class InsertCoursePointToDatabase extends AsyncTask<String,Void,Void> { //Adds the course point to the database
    private WeakReference<Context> context;
    private int courseID;
    private AsyncTaskCompleteListener<Void> onCompleteListener;

    public InsertCoursePointToDatabase(int courseID, Context context, AsyncTaskCompleteListener<Void> onCompleteListener){
        this.courseID = courseID;
        this.context = new WeakReference<>(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            database.databaseAccessObject().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));//inserts the course point via an SQL statement
        }catch (NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_add_course_point,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_add_course_point),exception.getMessage());
        }finally {
            if (database != null){
                database.close();
            }
        }

        return null;//In order to override the method correctly
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        onCompleteListener.onAsyncTaskComplete(result);


    }
}
