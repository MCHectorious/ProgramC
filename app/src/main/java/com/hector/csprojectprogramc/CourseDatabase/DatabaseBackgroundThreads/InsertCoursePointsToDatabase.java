package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCoursePointsToDatabase extends AsyncTask<CoursePoint,Void, Void>{

    private WeakReference<Context> context;

    public InsertCoursePointsToDatabase(Context context){
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(CoursePoint... coursePoints) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            for(CoursePoint coursePoint: coursePoints){
                database.databaseAccessObject().insertCoursePoint(coursePoint);//inserts the course point via an SQL statement
            }
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

    }

}
