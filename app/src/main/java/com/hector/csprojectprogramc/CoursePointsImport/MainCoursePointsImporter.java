package com.hector.csprojectprogramc.CoursePointsImport;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class MainCoursePointsImporter {

    private int numberOfExecutedProcesses;//The number of revision websites that have used to get course points
    private static final int maximumNumberOfExecutedProcesses = 2;//The maximum number of revision websites that have used to get course points
    private WeakReference<Context> context;//The screen this is being done on. It is a weak reference because the screen  could be closed during this process

    public MainCoursePointsImporter(Context context){
        numberOfExecutedProcesses = 0;
        this.context = new WeakReference<>(context);

    }

    public void getCoursePoints(Course course, AsyncTaskErrorListener errorListener){
        AfterGettingCoursePoints afterGettingCoursePoints = new AfterGettingCoursePoints();//Check whether all of the async tasks have been completed

        new GetCramCoursePoints().getCoursePoints(context.get(),course,context.get().getApplicationContext(), afterGettingCoursePoints, errorListener);//Gets course points from Cram
        new GetMemRiseCoursePoints().getCoursePoints(context.get(),course,context.get().getApplicationContext(), afterGettingCoursePoints, errorListener);//Gets course point from Memrise

    }

    private class AfterGettingCoursePoints implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            numberOfExecutedProcesses++;//Increases the number of async task that have completed
            if (numberOfExecutedProcesses == maximumNumberOfExecutedProcesses){//If all the async tasks have been complete
                AlertDialog.Builder machineLearningWarningAlertDialogBuilder = new AlertDialog.Builder(context.get());// Initialises the alert dialog which will warn the user that some sentences may be machine generated
                String machineLearningWarningText =  context.get().getString(R.string.you_can_view_course_points)+ System.getProperty("line.separator")+ context.get().getString(R.string.machine_generated_sentences_warning)+ System.getProperty("line.separator")+context.get().getString(R.string.edit_tab_instructions);//The warning to the user and instruction as to how to resolve them
                machineLearningWarningAlertDialogBuilder.setMessage(machineLearningWarningText);//Displays the warning
                machineLearningWarningAlertDialogBuilder.setCancelable(false).setPositiveButton(context.get().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {//Clicking on the button just closes the dialog
                        Intent toHomeScreen = new Intent(context.get(), HomeScreen.class);//Creates a connection between the context and the Home Screen
                        context.get().startActivity(toHomeScreen);//starts the homescreen
                    }
                });
                machineLearningWarningAlertDialogBuilder.create().show();//Shows the warning on the screen

            }
        }
    }


}
