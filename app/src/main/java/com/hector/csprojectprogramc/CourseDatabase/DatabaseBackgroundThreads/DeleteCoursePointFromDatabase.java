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

    private WeakReference<Context> context;
    private CoursePoint temporaryCoursePoint;

    private AsyncTaskCompleteListener<Void> onCompleteListener;

    public DeleteCoursePointFromDatabase(Context context, CoursePoint temporaryCoursePoint, AsyncTaskCompleteListener<Void> listener){
        this.context = new WeakReference<>(context);
        this.temporaryCoursePoint = temporaryCoursePoint;
        this.onCompleteListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            database.databaseAccessObject().deleteCoursePoint(temporaryCoursePoint);
        }catch(NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_delete_course_point,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_delete_course_point),exception.getMessage());
        }finally {
            if (database != null){
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
