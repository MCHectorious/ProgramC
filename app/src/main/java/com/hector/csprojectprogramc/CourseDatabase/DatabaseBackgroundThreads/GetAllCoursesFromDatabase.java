package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.Activities.ExamBoardScreen;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetAllCoursesFromDatabase  extends AsyncTask<Void,Void,List<Course>> {

    private ProgressDialog progressDialog;//Dialog that shows the user that a background task is occurring
    private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
    private AsyncTaskCompleteListener<List<Course>> onCompleteListener;//Handle what should occur after the task is complete

    public GetAllCoursesFromDatabase(Context context, AsyncTaskCompleteListener<List<Course>> onCompleteListener){//Initialises all the fields
        this.context = new WeakReference<>(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onPreExecute(){//Before the background task is started
        super.onPreExecute();//Do the generic code for what happens before the background task
        progressDialog = new ProgressDialog(context.get());//Initialises the progress dialog
        progressDialog.setTitle(context.get().getString(R.string.initialising_app));//Explains what is occurring
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));//Explains that this shouldn't take long
        progressDialog.setIndeterminate(false);//Show an icon that doesn't say how much progress has been made and how much is left
        progressDialog.show();//Show the dialog
    }

    @Override
    protected List<Course> doInBackground(Void... voids) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());//Gets the database
        try{
            return database.databaseAccessObject().getAllCourses();//Gets all the saved courses
        }catch (NullPointerException exception){//Will occur if the database access object is null
            AlertDialog.Builder cannotAccessCoursesAlertDialogBuilder = new AlertDialog.Builder(context.get());//Initialises the builder which will create an alert to say that the program is unable to access the database
            cannotAccessCoursesAlertDialogBuilder.setTitle(R.string.unable_to_load_courses);//Explains the issue
            cannotAccessCoursesAlertDialogBuilder.setMessage(R.string.press_button_to_go_to_exam_board_screen);//Explains how the issue may be resolved
            cannotAccessCoursesAlertDialogBuilder.setCancelable(false);//Prohibits the user from doing anything else to avoid any further issues
            cannotAccessCoursesAlertDialogBuilder.setPositiveButton(context.get().getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the user presses the button
                    Intent toExamBoardScreen = new Intent(context.get(),ExamBoardScreen.class);//Gets a connection between the current context and the exam boards screen
                    context.get().startActivity(toExamBoardScreen);//Start the exam board screen
                }
            });
            cannotAccessCoursesAlertDialogBuilder.create().show();//Show alert
        }finally {
            if(database != null){//Checks whether the database was initialised correctly
                database.close();//Closes the connection to the database to avoid leaks
            }
        }

        return null;//To conform to the standard of its superclass
    }

    @Override
    protected void onPostExecute(List<Course> courses){//When the task is complete
        progressDialog.dismiss();//Hide the dialog show that the task was running

        onCompleteListener.onAsyncTaskComplete(courses);//Run the appropriate code for after the  task is complete

    }
}
