package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;

import java.lang.ref.WeakReference;

public class DeleteCoursePointFromDatabase extends AsyncTask<Void,Void,Void> {

    private WeakReference<Context> context;
    private CoursePoint temporaryCoursePoint;

    private AsyncTaskCompleteListener<Void> listener;

    public DeleteCoursePointFromDatabase(Context context, CoursePoint temporaryCoursePoint, AsyncTaskCompleteListener<Void> listener){
        this.context = new WeakReference<>(context);
        this.temporaryCoursePoint = temporaryCoursePoint;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDatabase database = null;
        try{
            database = Room.databaseBuilder(context.get(), MainDatabase.class, "my-db").build();
            database.customDao().deleteCoursePoint(temporaryCoursePoint);
        }catch(Exception exception){
            //TODO: handle appropriately
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

        listener.onAsyncTaskComplete(result);

    }
}
