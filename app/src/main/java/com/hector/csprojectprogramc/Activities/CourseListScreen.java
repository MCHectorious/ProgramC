package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.hector.csprojectprogramc.CourseListImport.GetAQACoursesAndTheirWebsites;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskCompleteListener;
import com.hector.csprojectprogramc.GeneralUtilities.AsyncTaskErrorListener;
import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CourseListScreenCoursesAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class CourseListScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Runs when the screen starts
        super.onCreate(savedInstanceState);
        setContentView(R.layout.course_list_screen); //Links the XML file that defines the layout of the screen

        String qualification = null;
        try{
            qualification = getIntent().getStringExtra(getString(R.string.qualification)); //Gets the qualification of the courses to show from the previous screen
        }catch (NullPointerException exception){
            AlertDialogHelper.showCannotAccessIntentsDialog(CourseListScreen.this);
        }

        NetworkInfo networkInformation = null;
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            //noinspection ConstantConditions
            networkInformation = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        }catch (NullPointerException exception){
            AlertDialog.Builder noWiFiAvailableAlertDialogBuilder = new AlertDialog.Builder(CourseListScreen.this);
            noWiFiAvailableAlertDialogBuilder.setTitle(R.string.no_wifi_available);
            noWiFiAvailableAlertDialogBuilder.setMessage(R.string.no_wifi_available_instructions);
            noWiFiAvailableAlertDialogBuilder.setCancelable(false);
            noWiFiAvailableAlertDialogBuilder.create().show();
        }
        if (networkInformation != null){
            getCoursesIfConnectedToWifi(new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, new ShowCourseListWhenTaskComplete(qualification), new IfAnErrorOccursGettingCoursesReturnToHomeScreen()), networkInformation);
        }


    }

    private void getCoursesIfConnectedToWifi(final GetAQACoursesAndTheirWebsites getCourses, final NetworkInfo networkInformation){
        if (networkInformation.isConnected()) {
            getCourses.execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        }else{
            AlertDialog.Builder noConnectionToWiFiAlertDialogBuilder = new AlertDialog.Builder(CourseListScreen.this);
            noConnectionToWiFiAlertDialogBuilder.setTitle(R.string.you_need_to_be_connected_to_wifi);
            noConnectionToWiFiAlertDialogBuilder.setMessage(R.string.press_try_again);
            noConnectionToWiFiAlertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    getCoursesIfConnectedToWifi(getCourses, networkInformation);
                }
            });
            noConnectionToWiFiAlertDialogBuilder.create().show();
        }
    }

    private class IfAnErrorOccursGettingCoursesReturnToHomeScreen implements AsyncTaskErrorListener{

        @Override
        public void onAsyncTaskError() {
            Intent toHomeScreen = new Intent(CourseListScreen.this, HomeScreen.class);
            startActivity(toHomeScreen);
        }
    }


    private class ShowCourseListWhenTaskComplete implements AsyncTaskCompleteListener<HashMap<String,String>>{

        private String qualification;

        ShowCourseListWhenTaskComplete(String qualification){
            this.qualification = qualification;
        }


        @Override
        public void onAsyncTaskComplete(HashMap<String, String> courseNamesAndWebsites) {
            final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView); //Initialises the recycler view which shows the courses
            recyclerViewForCourses.setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.setLayoutManager(new LinearLayoutManager(CourseListScreen.this));// Shows the courses in a vertical list

            ArrayList<String> courseNames = new ArrayList<>(courseNamesAndWebsites.keySet());
            Collections.sort(courseNames, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });

            ArrayList<String> courseWebsites = new ArrayList<>();
            for (String courseName: courseNames){
                courseWebsites.add(courseNamesAndWebsites.get(courseName));
            }

            recyclerViewForCourses.setAdapter(new CourseListScreenCoursesAdapter(courseNames, courseWebsites, CourseListScreen.this,  qualification));//Define how the courses are handled

        }
    }


}
