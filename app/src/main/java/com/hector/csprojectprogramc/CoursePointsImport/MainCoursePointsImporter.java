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

    private int numberOfExecutedProcesses;
    private static final int maximumNumberOfExecutedProcesses = 2;
    private WeakReference<Context> context;

    public MainCoursePointsImporter(Context context){
        numberOfExecutedProcesses = 0;
        this.context = new WeakReference<>(context);

    }

    public void getCoursePoints(Course course, AsyncTaskErrorListener errorListener){
        AfterGettingCoursePoints afterGettingCoursePoints = new AfterGettingCoursePoints();

        new GetCramCoursePoints().getCoursePoints(context.get(),course,context.get().getApplicationContext(), afterGettingCoursePoints, errorListener);
        new GetMemRiseCoursePoints().getCoursePoints(context.get(),course,context.get().getApplicationContext(), afterGettingCoursePoints, errorListener);


    }

    private class AfterGettingCoursePoints implements AsyncTaskCompleteListener<Void>{

        @Override
        public void onAsyncTaskComplete(Void result) {
            numberOfExecutedProcesses++;
            if (numberOfExecutedProcesses == maximumNumberOfExecutedProcesses){
                AlertDialog.Builder machineLearningWarningAlertDialogBuilder = new AlertDialog.Builder(context.get());// Initialises the alert dialog which will warn the user that some sentences may be machine generated
                String machineLearningWarningText =  context.get().getString(R.string.you_can_view_course_points)+ System.getProperty("line.separator")+ context.get().getString(R.string.machine_generated_sentences_warning)+ System.getProperty("line.separator")+context.get().getString(R.string.edit_tab_instructions);//The warning to the user and instruction as to how to resolve them
                machineLearningWarningAlertDialogBuilder.setMessage(machineLearningWarningText);
                machineLearningWarningAlertDialogBuilder.setCancelable(false).setPositiveButton(context.get().getString(R.string.okay), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {//Clicking on the button just closes the dialog
                        Intent toHomeScreen = new Intent(context.get(), HomeScreen.class);
                        context.get().startActivity(toHomeScreen);
                    }
                });
                machineLearningWarningAlertDialogBuilder.create().show();//Shows the warning on the screen

            }
        }
    }


}
