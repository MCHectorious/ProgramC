package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.CoursePoint;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;

import java.lang.ref.WeakReference;

public class InsertCoursePointsToDatabase extends AsyncTask<CoursePoint,Void, Void>{

    private WeakReference<Context> context;
    private AsyncTaskCompleteListener<Void> onCompleteListener;


    public InsertCoursePointsToDatabase(Context context, AsyncTaskCompleteListener<Void> onCompleteListener){
        this.context = new WeakReference<>(context);
        this.onCompleteListener = onCompleteListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //Looper.prepare();
    }

    @Override
    protected Void doInBackground(CoursePoint... coursePoints) {
        MainDatabase database =  MainDatabase.getDatabase(context.get());
        int courseID = -1;
        try {
            for (CoursePoint coursePoint : coursePoints) {


                //StringBuilder builder = new StringBuilder();
                //for (Course course : database.databaseAccessObject().getAllCourses()) {
                    //Log.e("course ID", Integer.toString(course.getCourse_ID()));
                    //builder.append(course.getCourse_ID()).append(" ");
                //}

                //Log.w("test", Integer.toString(database.databaseAccessObject().getAllCourses().size()));
                //builder.append(Integer.toString(coursePoint.getCourse_ID_foreign())).append("!");
                //courseID= coursePoint.getCourse_ID_foreign();
                //Toast.makeText(context.get(), builder.toString(), Toast.LENGTH_LONG).show();

                //Log.e("Course ID", Integer.toString(coursePoint.getCourse_ID_foreign()));
                database.databaseAccessObject().insertCoursePoint(coursePoint);//inserts the course point via an SQL statement
            }
        }catch(SQLiteConstraintException exception){
            StringBuilder builder = new StringBuilder();
            for (Course course : database.databaseAccessObject().getAllCourses()) {
                Log.e("course ID", Integer.toString(course.getCourse_ID()));
                builder.append(course.getCourse_ID()).append(" ");
            }

            //Log.w("test", Integer.toString(database.databaseAccessObject().getAllCourses().size()));
            builder.append(Integer.toString(courseID)).append("!");
            //Toast.makeText(context.get(), builder.toString(), Toast.LENGTH_LONG).show();
            Log.e("notes",builder.toString());
            Log.e("Course ID", Integer.toString(courseID));
        }catch (NullPointerException exception){
            Toast.makeText(context.get(), R.string.unable_to_add_course_point,Toast.LENGTH_LONG).show();
            Log.w(context.get().getString(R.string.unable_to_add_course_point),exception.getMessage());
        }finally {
            if (database != null){
                database.close();
            }
        }

        return null;//In order to override the method correctly
    }

    @Override
    protected void onPostExecute(Void result){
        super.onPostExecute(result);

        onCompleteListener.onAsyncTaskComplete(result);

    }

}
