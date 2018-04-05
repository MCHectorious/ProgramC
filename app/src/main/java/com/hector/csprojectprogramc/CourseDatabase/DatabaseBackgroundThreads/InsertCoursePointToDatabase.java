package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCoursePointToDatabase extends AsyncTask<String,Void,Void> { //Adds the course point to the database
    private WeakReference<Context> context;
    private int courseID;
    private AsyncTaskCompleteListener<Void> listener;

    public InsertCoursePointToDatabase(int courseID, Context context, AsyncTaskCompleteListener<Void> listener){
        this.courseID = courseID;
        this.context = new WeakReference<>(context);
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        MainDatabase database = null;
        try{
            database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();//Accesses the database
            database.customDao().insertCoursePoint(new CoursePoint(courseID,strings[0],strings[1],strings[2]));//inserts the course point via an SQL statement
        }catch (Exception exception){
            //TODO: handle appropriately
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

        listener.onAsyncTaskComplete(result);


    }
}
