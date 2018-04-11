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
    private int courseID;//The id of the course in the course database
    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private AsyncTaskCompleteListener<List<CoursePoint>> onCompleteListener;//Handle what should occur after the task is complete

    public GetCoursePointsFromDatabase(Context context, int courseID, AsyncTaskCompleteListener<List<CoursePoint>> onCompleteListener) {//Initialises the fields
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
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            return database.databaseAccessObject().getCoursePointsForCourse(courseID);//Gets the course points for this course
        }catch (NullPointerException exception){//Will occur if the database access object is null
            AlertDialogHelper.showCannotAccessCoursePointsDialog(context.get());//Warns the user
        }finally {
            if(database != null){//Checks whether the database has been initialised correctly
                database.close();//Closes the connection to the database to avoid leaks
            }
        }

        return null;//To show an error occurred

    }




    @Override
    protected void onPostExecute(List<CoursePoint> coursePoints) {//When the task is complete
        super.onPostExecute(coursePoints);//Runs the generic code for after the task is complete
        progressDialog.dismiss();//hides the alert to the user
        onCompleteListener.onAsyncTaskComplete(coursePoints);//Runs the appropriate code for after the task is complete

    }
}


