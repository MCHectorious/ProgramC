package com.hector.csprojectprogramc.CoursePointsImport;

import android.content.Context;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;

public interface CoursePointsImporter {

    void getCoursePoints(Context context, Course course, Context appContext, AsyncTaskCompleteListener<Void> listener);

}
