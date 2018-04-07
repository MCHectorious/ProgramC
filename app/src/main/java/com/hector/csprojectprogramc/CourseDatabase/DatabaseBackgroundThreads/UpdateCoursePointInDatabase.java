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

public class UpdateCoursePointInDatabase extends AsyncTask<String,Void,Void> {

    private WeakReference<Context> context;
    private CoursePoint chosenCoursePoint;

    private AsyncTaskCompleteListener<Void> listener;

    public UpdateCoursePointInDatabase(Context context, CoursePoint chosenCoursePoint, AsyncTaskCompleteListener<Void> listener){
        this.context = new WeakReference<>(context);
        this.chosenCoursePoint = chosenCoursePoint;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());

        try{
            database.databaseAccessObject().updateCoursePoint(chosenCoursePoint);
        }catch (NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_update_course_point,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_update_course_point),exception.getMessage());
        }finally {
            database.close();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        listener.onAsyncTaskComplete(result);
    }

}
