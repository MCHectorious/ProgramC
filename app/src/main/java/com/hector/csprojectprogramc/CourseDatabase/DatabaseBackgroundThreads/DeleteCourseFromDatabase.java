package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;

import java.lang.ref.WeakReference;
import java.util.List;

public class DeleteCourseFromDatabase extends AsyncTask<Void,Void,Void> {

    private WeakReference<Context> context;
    private Course course;
    private AsyncTaskCompleteListener<Void> listener;

    public DeleteCourseFromDatabase(Context context, Course course, AsyncTaskCompleteListener<Void> listener){
        this.context = new WeakReference<>(context);
        this.course = course;
        this.listener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, "my-db").build();
        database.customDao().deleteCourse(course);
        List<CoursePoint> coursePoints = database.customDao().getCoursePointsForCourse(course.getCourse_ID());
        for(CoursePoint point:coursePoints){
            database.customDao().deleteCoursePoint(point);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        listener.onAsyncTaskComplete(result);



    }
}
