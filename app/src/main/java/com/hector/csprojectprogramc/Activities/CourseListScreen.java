package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.CourseListImport.GetAQACoursesAndTheirWebsites;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CourseListScreenCoursesAdapter;

import java.util.HashMap;

public class CourseListScreen extends AppCompatActivity {
    private NetworkInfo wifi;
    private String qualification;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Runs when the screen starts
        super.onCreate(savedInstanceState); //TODO: research what this does
        setContentView(R.layout.course_list_screen); //Links the XML file that defines the layout of the screen
        qualification = getIntent().getStringExtra("Qualification"); //Gets the qualification of the courses to show from the previous screen //TODO: Prepare for null

        //new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, HTMLDividerClassForQualification,recyclerViewForCourses, getApplicationContext()).execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        try{
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            //noinspection ConstantConditions
            wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


            getCoursesIfConnectedToWifi(new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, new AfterGettingCourses()));

        }catch (NullPointerException exception){
            Log.w("Warning","Null Pointer");
            //TODO: handle appropriately
        }

    }

    private void getCoursesIfConnectedToWifi(final GetAQACoursesAndTheirWebsites getCourses){
        if (wifi.isConnected()) {
            getCourses.execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        }else{
            AlertDialog.Builder noWiFiAlertDialogBuilder = new AlertDialog.Builder(CourseListScreen.this);
            noWiFiAlertDialogBuilder.setTitle(R.string.no_wifi_available);
            noWiFiAlertDialogBuilder.setMessage(R.string.no_wifi_message);
            noWiFiAlertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getCoursesIfConnectedToWifi(getCourses);
                }
            });
            noWiFiAlertDialogBuilder.create().show();
        }
    }

    private  class AfterGettingCourses implements AsyncTaskCompleteListener<HashMap<String,String>>{



        @Override
        public void onAsyncTaskComplete(HashMap<String, String> courseNamesAndWebsites) {
            final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView); //Initialises the recycler view which shows the courses


            recyclerViewForCourses.setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.setLayoutManager(new LinearLayoutManager(CourseListScreen.this));// Shows the courses in a vertical list
            recyclerViewForCourses.setAdapter(new CourseListScreenCoursesAdapter(courseNamesAndWebsites.keySet(), courseNamesAndWebsites.values(), CourseListScreen.this,  qualification));//Define how the courses are handled

        }
    }


}
