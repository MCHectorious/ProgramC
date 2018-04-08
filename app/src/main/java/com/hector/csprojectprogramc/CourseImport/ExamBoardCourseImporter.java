package com.hector.csprojectprogramc.CourseImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.Activities.ExamBoardScreen;
import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCourseToDatabase;
import com.hector.csprojectprogramc.CoursePointsImport.MainCoursePointsImporter;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;

import java.lang.ref.WeakReference;

abstract class ExamBoardCourseImporter extends  AsyncTask<Void,Void,Course>{

    ProgressDialog progressDialog;
    protected WeakReference<Context> context;

    @Override
    protected void onPostExecute(Course course){
        progressDialog.dismiss();

        new InsertCourseToDatabase(context.get(), new GettingCoursePoints(context,course) ).execute(course);

    }

    private class GettingCoursePoints implements AsyncTaskCompleteListener<Void> {

        private WeakReference<Context> context;
        private Course course;

        GettingCoursePoints(WeakReference<Context> context, Course course){
            this.context = context;
            this.course = course;
        }

        @Override
        public void onAsyncTaskComplete(Void result) {
            new MainCoursePointsImporter(context.get()).getCoursePoints(course, new IfAnErrorOccursGoToExamBoardScreen());
        }
    }

    private class IfAnErrorOccursGoToExamBoardScreen implements AsyncTaskErrorListener{

        @Override
        public void onAsyncTaskError() {
            Intent toExamBoardScreen = new Intent(context.get(), ExamBoardScreen.class);
            context.get().startActivity(toExamBoardScreen);
        }
    }


}
