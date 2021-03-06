package com.hector.csprojectprogramc.CourseListImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.HashMap;

abstract class ExamBoardCourseListImporter extends AsyncTask<Void,Void,HashMap<String,String>> {

    private ProgressDialog progressDialog; //An dialog which shows the user that a long-running process is occurring
    AsyncTaskCompleteListener<HashMap<String,String>> onCompleteListener = null;//Handles what should occur after  the task is complete
    protected WeakReference<Context> context = null;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    protected String qualification = "";//The qualification of the course

    @Override
    protected void onPreExecute(){//Shows the user the dialog
        super.onPreExecute();//Runs the generic code which should run before the async task
        progressDialog = new ProgressDialog(context.get()); // Initialises the variable
        progressDialog.setTitle(context.get().getString(R.string.getting_latest_list)+" "+qualification+" "+context.get().getString(R.string.courses)); // Sets the title of the dialog shown to the user
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick)); //Sets the message of dialog to an instruction for the user to follow
        progressDialog.setIndeterminate(false); //Shows an animation while the dialog is displayed but this animation doesn't represent how far though the process is
        progressDialog.show();//Shows the dialog on the screen
    }

    @Override
    protected void onPostExecute(HashMap<String,String> courseNamesAndWebsites){
        super.onPostExecute(courseNamesAndWebsites);//Runs the generic code which should run after the async task
        progressDialog.dismiss();//Hides the dialog

        onCompleteListener.onAsyncTaskComplete(courseNamesAndWebsites);//Handles what should occur after the async task is complete
    }

}
