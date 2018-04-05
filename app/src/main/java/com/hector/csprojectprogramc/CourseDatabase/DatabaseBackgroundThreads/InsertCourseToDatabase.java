package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCourseToDatabase extends AsyncTask<Course,Void,Void> {

    private AsyncTaskCompleteListener<Void> listener;
    private WeakReference<Context> context,appContext;

    public InsertCourseToDatabase(Context context, Context appContext, AsyncTaskCompleteListener<Void> listener){
        this.listener = listener;
        this.context = new WeakReference<>(context);
        this.appContext = new WeakReference<>(appContext);
    }

    @Override
    protected Void doInBackground(Course... courses) {
        MainDatabase database = null;
        try{
            database = Room.databaseBuilder(appContext.get(),MainDatabase.class,context.get().getString(R.string.database_location)).build();
            for (Course course: courses) {
                database.customDao().insertCourse(course);
            }
        }catch (Exception exception){
            //TODO: handle appropriately
        }finally {
            if (database != null){
                database.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        listener.onAsyncTaskComplete(result);
    }
}
