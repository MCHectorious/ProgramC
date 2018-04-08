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

    private WeakReference<Context> context;
    private Course course;
    private AsyncTaskCompleteListener<Void> onCompleteListener;

    public DeleteCourseFromDatabase(Context context, Course course, AsyncTaskCompleteListener<Void> onCompleteListener){
        this.context = new WeakReference<>(context);
        this.course = course;
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected Void doInBackground(Void... voids) {

        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            database.databaseAccessObject().deleteCourse(course);
        }catch (NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_delete_course,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_delete_course),exception.getMessage());
        }finally {
            if(database != null){
                database.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        onCompleteListener.onAsyncTaskComplete(result);
    }
}
