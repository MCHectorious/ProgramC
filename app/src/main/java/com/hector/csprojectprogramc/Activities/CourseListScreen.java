package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.CourseListImport.GetAQACoursesAndTheirWebsites;
import com.hector.csprojectprogramc.R;

public class CourseListScreen extends AppCompatActivity {
    private NetworkInfo wifi;


    @Override
    protected void onCreate(Bundle savedInstanceState) { //Runs when the screen starts
        super.onCreate(savedInstanceState); //TODO: research what this does
        setContentView(R.layout.course_list_screen); //Links the XML file that defines the layout of the screen
        String qualification = getIntent().getStringExtra("Qualification"); //Gets the qualification of the courses to show from the previous screen //TODO: Prepare for null
        String HTMLDividerClassForQualification = "";
        if(qualification.equals(getString(R.string.gcse))){
            HTMLDividerClassForQualification = "panelInner gcse-header"; //Sets the divider to the name of the section which includes the GCSE courses
        }
        if (qualification.equals(getString(R.string.as_and_a_level))){
            HTMLDividerClassForQualification = "panelInner as_and_a-level-header"; //Sets the divider to the name of the section which includes the A-Level courses
        }
        final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView); //Initialises the recycler view which shows the courses

        //new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, HTMLDividerClassForQualification,recyclerViewForCourses, getApplicationContext()).execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        try{
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            //noinspection ConstantConditions
            wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


            getCoursesIfConnectedToWifi(new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, HTMLDividerClassForQualification,recyclerViewForCourses, getApplicationContext()));

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


}
