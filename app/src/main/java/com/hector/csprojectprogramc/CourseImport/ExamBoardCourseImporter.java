package com.hector.csprojectprogramc.CourseImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads.InsertCourseToDatabase;
import com.hector.csprojectprogramc.CoursePointsImport.MainCoursePointsImporter;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;

import java.lang.ref.WeakReference;

abstract class ExamBoardCourseImporter extends  AsyncTask<Void,Void,Course>{

    ProgressDialog progressDialog;
    protected WeakReference<Context> context;


    @Override
    protected void onPostExecute(Course course){
        progressDialog.dismiss();

        new InsertCourseToDatabase(context.get(),context.get().getApplicationContext(), new GettingCoursePoints(context,course) ).execute(course);

    }

    public class GettingCoursePoints implements AsyncTaskCompleteListener<Void> {

        private WeakReference<Context> context;
        private Course course;

        GettingCoursePoints(WeakReference<Context> context, Course course){
            this.context = context;
            this.course = course;
        }

        @Override
        public void onAsyncTaskComplete(Void result) {

            new MainCoursePointsImporter(context.get()).getCoursePoints(course);
        }
    }

}
