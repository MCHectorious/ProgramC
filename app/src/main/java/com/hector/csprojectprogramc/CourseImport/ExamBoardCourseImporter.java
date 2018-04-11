package com.hector.csprojectprogramc.CourseImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.Activities.ExamBoardScreen;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCourseToDatabase;
import com.hector.csprojectprogramc.CoursePointsImport.MainCoursePointsImporter;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;

import java.lang.ref.WeakReference;

abstract class ExamBoardCourseImporter extends  AsyncTask<Void,Void,Course>{

    ProgressDialog progressDialog;//Shows the user that a background task is occurring
    protected WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process

    @Override
    protected void onPostExecute(Course course){//When the task is complete
        progressDialog.dismiss();//Hides the dialog
        new InsertCourseToDatabase(context.get(), new GettingCoursePoints(context,course) ).execute(course);//Inserts the course into the database after it has been found

    }

    private class GettingCoursePoints implements AsyncTaskCompleteListener<Void> {//Allow object to be created to run after the course has been inserted to get course points for the course

        private WeakReference<Context> context;//The screen that this is being executed from. It is a weak reference because the screen may be closed during this process
        private Course course;//The course which the course points will belong to

        GettingCoursePoints(WeakReference<Context> context, Course course){//Initialises the fields
            this.context = context;
            this.course = course;
        }

        @Override
        public void onAsyncTaskComplete(Void result) {//Runs when the task is complete
            new MainCoursePointsImporter(context.get()).getCoursePoints(course, new IfAnErrorOccursGoToExamBoardScreen());//Gets the course points for the course
        }
    }

    private class IfAnErrorOccursGoToExamBoardScreen implements AsyncTaskErrorListener{//Allows objects to be created which handle if an error occurs

        @Override
        public void onAsyncTaskError() {
            Intent toExamBoardScreen = new Intent(context.get(), ExamBoardScreen.class);//Creates a connection between the context and the exam board screen
            context.get().startActivity(toExamBoardScreen);//Start the exam board screen
        }
    }


}
