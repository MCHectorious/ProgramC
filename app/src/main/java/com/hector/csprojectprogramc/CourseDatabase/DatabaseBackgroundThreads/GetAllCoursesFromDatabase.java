package com.hector.csprojectprogramc.CourseDatabase.DatabaseBackgroundThreads;

import android.app.ProgressDialog;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.hector.csprojectprogramc.CourseDatabase.Course;
import com.hector.csprojectprogramc.CourseDatabase.MainDatabase;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.RecyclerViewAdapters.HomeScreenCoursesRecyclerAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

public class GetAllCoursesFromDatabase  extends AsyncTask<Void,Void,List<Course>> {
    private ProgressDialog progressDialog;
    private WeakReference<Context> context;
    private AsyncTaskCompleteListener<List<Course>> listener;

    public GetAllCoursesFromDatabase(Context context, AsyncTaskCompleteListener<List<Course>> listener){
        this.context = new WeakReference<>(context);
        this.listener = listener;
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
        MainDatabase database = Room.databaseBuilder(context.get(), MainDatabase.class, context.get().getString(R.string.database_location)).build();
        return database.customDao().getAllCourses();

    }

    @Override
    protected void onPostExecute(List<Course> courses){
        progressDialog.dismiss();
        listener.onAsyncTaskComplete(courses);

    }
}
