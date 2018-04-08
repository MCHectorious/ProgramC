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

    private ProgressDialog progressDialog;
    private WeakReference<Context> context;
    private AsyncTaskCompleteListener<List<Course>> onCompleteListener;

    public GetAllCoursesFromDatabase(Context context, AsyncTaskCompleteListener<List<Course>> onCompleteListener){
        this.context = new WeakReference<>(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onPreExecute(){
        super.onPreExecute();
        progressDialog = new ProgressDialog(context.get());
        progressDialog.setTitle(context.get().getString(R.string.initialising_app));
        progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick));
        progressDialog.setIndeterminate(false);
        progressDialog.show();
    }

    @Override
    protected List<Course> doInBackground(Void... voids) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        try{
            return database.databaseAccessObject().getAllCourses();
        }catch (NullPointerException exception){
            AlertDialog.Builder cantAccessCoursesAlertDialogBuilder = new AlertDialog.Builder(context.get());
            cantAccessCoursesAlertDialogBuilder.setTitle(R.string.unable_to_load_courses);
            cantAccessCoursesAlertDialogBuilder.setMessage(R.string.press_button_to_go_to_exam_board_screen);
            cantAccessCoursesAlertDialogBuilder.setCancelable(false);
            cantAccessCoursesAlertDialogBuilder.setPositiveButton(context.get().getString(R.string.okay), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent toExamBoardScreen = new Intent(context.get(),ExamBoardScreen.class);
                    context.get().startActivity(toExamBoardScreen);
                }
            });
            cantAccessCoursesAlertDialogBuilder.create().show();
        }finally {
            if(database != null){
                database.close();
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(List<Course> courses){
        progressDialog.dismiss();

        onCompleteListener.onAsyncTaskComplete(courses);

    }
}
