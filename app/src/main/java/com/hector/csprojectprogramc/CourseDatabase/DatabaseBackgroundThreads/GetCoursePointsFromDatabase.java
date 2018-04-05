package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;


public class GetCoursePointsFromDatabase extends AsyncTask<Void, Void, List<CoursePoint>> {// gets the course points for the course from the database in a background thread //TODO: make static
    private ProgressDialog progressDialog;// Stores the dialog which shows the user that a background task is running
    private int courseID;
    private WeakReference<Context> context;
    private AsyncTaskCompleteListener<List<CoursePoint>> listener;

    public GetCoursePointsFromDatabase(Context context, int courseID, AsyncTaskCompleteListener<List<CoursePoint>> listener) {
        this.context = new WeakReference<>(context);
        this.courseID = courseID;
        this.listener = listener;

    }

    @Override
    protected void onPreExecute() {//Shows the user that a long-running background task is running
        super.onPreExecute();// TODO: research what this does
        progressDialog = new ProgressDialog(context.get());//Idealises the dialog
        progressDialog.setTitle(context.get().getString(R.string.loading_course_points));// Explains what this task is doing
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));// TODO: change and then explain
        progressDialog.setIndeterminate(false);// The dialog shows an animation which doesn't represent how far throught the task is //TODO: improve description
        progressDialog.show();//Shows the dialog on the screen
    }

    @Override
    protected List<CoursePoint> doInBackground(Void... voids) {
        MainDatabase database = null;
        try{
             database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();//Accesses the database
            return database.customDao().getCoursePointsForCourse(courseID);//In order to fulfil the implementation
        }catch (Exception exception){
            //TODO: handle appropriately
        }finally {
            if(database != null){
                database.close();
            }
        }

        return null;//TODO: handle appropriately

    }




    @Override
    protected void onPostExecute(List<CoursePoint> coursePoints) {
        progressDialog.dismiss();//hides the alert to the user
        listener.onAsyncTaskComplete(coursePoints);

    }
}


