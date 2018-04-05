package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.Activities.CoursePointsScreen;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class UpdateCoursePointInDatabase extends AsyncTask<String,Void,Void> {

    private WeakReference<Context> context;
    private CoursePoint temporaryCoursePoint;

    private AsyncTaskCompleteListener<Void> listener;

    public UpdateCoursePointInDatabase(Context context, CoursePoint temporaryCoursePoint, AsyncTaskCompleteListener<Void> listener){
        this.context = new WeakReference<>(context);
        this.temporaryCoursePoint = temporaryCoursePoint;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(String... strings) {
        MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();
        database.customDao().updateCoursePoint(temporaryCoursePoint);
        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        listener.onAsyncTaskComplete(result);

        //Intent refreshCoursePointsScreen = new Intent(context.get(), CoursePointsScreen.class);
        //refreshCoursePointsScreen.putExtra( context.get().getString(R.string.course_id) , courseID);
       // refreshCoursePointsScreen.putExtra(context.get().getString(R.string.perspective), 2);

        //context.get().startActivity(refreshCoursePointsScreen);
    }

}
