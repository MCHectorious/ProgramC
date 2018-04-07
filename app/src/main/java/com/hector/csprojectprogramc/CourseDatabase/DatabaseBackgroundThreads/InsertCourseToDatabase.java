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

    private AsyncTaskCompleteListener<Void> onCompleteListener;
    private WeakReference<Context> context;

    public InsertCourseToDatabase(Context context, AsyncTaskCompleteListener<Void> onCompleteListener){
        this.onCompleteListener = onCompleteListener;
        this.context = new WeakReference<>(context);
    }

    @Override
    protected Void doInBackground(Course... courses) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            for (Course course: courses) {
                database.databaseAccessObject().insertCourse(course);
            }
        }catch (NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_add_course_point,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_add_course_point),exception.getMessage());
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
        onCompleteListener.onAsyncTaskComplete(result);
    }
}
