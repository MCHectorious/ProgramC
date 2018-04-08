package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;


public class GetCoursePointsFromDatabase extends AsyncTask<Void, Void, List<CoursePoint>> {// gets the course points for the course from the database in a background thread

    private ProgressDialog progressDialog;// Stores the dialog which shows the user that a background task is running
    private int courseID;
    private WeakReference<Context> context;
    private AsyncTaskCompleteListener<List<CoursePoint>> onCompleteListener;

    public GetCoursePointsFromDatabase(Context context, int courseID, AsyncTaskCompleteListener<List<CoursePoint>> onCompleteListener) {
        this.context = new WeakReference<>(context);
        this.courseID = courseID;
        this.onCompleteListener = onCompleteListener;

    }

    @Override
    protected void onPreExecute() {//Shows the user that a long-running background task is running
        super.onPreExecute();
        progressDialog = new ProgressDialog(context.get());//Idealises the dialog
        progressDialog.setTitle(context.get().getString(R.string.loading_course_points));// Explains what this task is doing
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
        progressDialog.setIndeterminate(false);// The dialog shows an animation which doesn't represent how far through the task is
        progressDialog.show();//Shows the dialog on the screen
    }

    @Override
    protected List<CoursePoint> doInBackground(Void... voids) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            return database.databaseAccessObject().getCoursePointsForCourse(courseID);//In order to fulfil the implementation
        }catch (NullPointerException exception){
            AlertDialogHelper.showCannotAccessCoursePointsDialog(context.get());
        }finally {
            if(database != null){
                database.close();
            }
        }

        return null;

    }




    @Override
    protected void onPostExecute(List<CoursePoint> coursePoints) {
        super.onPostExecute(coursePoints);
        progressDialog.dismiss();//hides the alert to the user
        onCompleteListener.onAsyncTaskComplete(coursePoints);

    }
}


