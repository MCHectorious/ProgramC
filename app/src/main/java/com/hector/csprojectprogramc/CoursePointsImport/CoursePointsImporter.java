package com.hector.csprojectprogramc.CoursePointsImport;

import android.content.Context;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;

public interface CoursePointsImporter {

    void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener, AsyncTaskErrorListener errorListener);

}
