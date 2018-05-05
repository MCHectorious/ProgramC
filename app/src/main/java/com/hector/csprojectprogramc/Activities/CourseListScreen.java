package com.hector.csprojectprogramc.Activities;

import android.app.Activity;
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
        super.onCreate(savedInstanceState);//Allows general Android screen creation to occur
        setContentView(R.layout.course_list_screen); //Links the XML file that defines the layout of the screen

        String qualification = null;//Initialises the qualification of course
        try{
            qualification = getIntent().getStringExtra(getString(R.string.qualification)); //Gets the qualification of the courses to show from the previous screen
        }catch (NullPointerException exception){//Will occur if it cannot access the string extra from the previous screen for the qualification
            AlertDialogHelper.showCannotAccessIntentsDialog(CourseListScreen.this);//Shows the user that it can't access the information from the previous screen and allows the user to return to the Home Screen to try again
        }

        NetworkInfo networkInformation = null;//Initialises the object used to check the WiFi connection
        try{
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);//Gets the object which allows the program to access information about the device's connection
            //noinspection ConstantConditions //This is not inspected because it has been handled but the IDE doesn't realise and thus it is giving an inaccurate warning
            networkInformation = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);//Specifies that the object should check the WiFi connection (instead of mobile data for example)

        }catch (NullPointerException exception){//Can occur if the device doesn't support a WiFi connection
            AlertDialog.Builder noWiFiAvailableAlertDialogBuilder = new AlertDialog.Builder(CourseListScreen.this);//Initialises the builder which will help to show and Alert saying that it is unable to access the WiFi
            noWiFiAvailableAlertDialogBuilder.setTitle(R.string.no_wifi_available);//Sets the title of the alert to explain that the issue is that the app is unable to access the WiFi
            noWiFiAvailableAlertDialogBuilder.setMessage(R.string.no_wifi_available_instructions);//Explains what the user should do to try and resolve the issue
            noWiFiAvailableAlertDialogBuilder.setCancelable(false);//Prohibits the user from trying to continue to use the app as this may lead to further errors
            noWiFiAvailableAlertDialogBuilder.create().show();//Shows the alert
        }
        if (networkInformation != null){//Checks that that object has been successfully been initialised
            getCoursesIfConnectedToWifi(new GetAQACoursesAndTheirWebsites(CourseListScreen.this, qualification, new ShowCourseListWhenTaskComplete(qualification), new IfAnErrorOccursGettingCoursesReturnToHomeScreen()), networkInformation);//Checks the list of courses
        }


    }

    private void getCoursesIfConnectedToWifi(final GetAQACoursesAndTheirWebsites getCourses, final NetworkInfo networkInformation){//Checks if the device is connected to the WiFi and if it is, then it gets the courses
        if (networkInformation.isConnected()) {//Checks if the device is connected to the WiFi
            getCourses.execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        }else{
            AlertDialog.Builder noConnectionToWiFiAlertDialogBuilder = new AlertDialog.Builder(CourseListScreen.this);//Initialises the builder which will create the alert saying that the user needs to be connected to the WiFi
            noConnectionToWiFiAlertDialogBuilder.setTitle(R.string.you_need_to_be_connected_to_wifi);//Explains the issue to the user
            noConnectionToWiFiAlertDialogBuilder.setMessage(R.string.press_try_again);//Instructs the user how the issue may be resolved
            noConnectionToWiFiAlertDialogBuilder.setPositiveButton(R.string.try_again, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {//When the button is pressed, it will allow the user to try again
                    dialog.dismiss();//Hides the warning
                    getCoursesIfConnectedToWifi(getCourses, networkInformation);//Recursively calls itself to try again
                }
            });
            noConnectionToWiFiAlertDialogBuilder.create().show();//Shows the alert
        }
    }

    private class IfAnErrorOccursGettingCoursesReturnToHomeScreen implements AsyncTaskErrorListener{//Allows an object to be created to handle if an error occurs

        @Override
        public void onAsyncTaskError() {//Will run if a known error occurs
            Intent toHomeScreen = new Intent(CourseListScreen.this, HomeScreen.class);//Creates a link between this screen and the Home Screen
            startActivity(toHomeScreen);//Switches to the Home Screen

        }
    }

    private class ShowCourseListWhenTaskComplete implements AsyncTaskCompleteListener<HashMap<String,String>>{//Allows an object to be created which handles what happens after the courses have been found

        private String qualification;//The qualification of the courses

        ShowCourseListWhenTaskComplete(String qualification){//When an object of this type is instantiated it must have a qualification
            this.qualification = qualification;//Initialises the qualification of the courses
        }

        @Override
        public void onAsyncTaskComplete(HashMap<String, String> courseNamesAndWebsites) {//Runs once the courses and their websites have been collected
            final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView); //Initialises the recycler view which shows the courses
            recyclerViewForCourses.setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.setLayoutManager(new LinearLayoutManager(CourseListScreen.this));// Shows the courses in a vertical list

            ArrayList<String> courseNames = new ArrayList<>(courseNamesAndWebsites.keySet());//Converts the key set of course names into an array list so that they can be indexed
            Collections.sort(courseNames, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });//Puts the course names in alphabetical order (not case sensitive)

            ArrayList<String> courseWebsites = new ArrayList<>();//Initialises the array list which will contain the course websites
            for (String courseName: courseNames){
                courseWebsites.add(courseNamesAndWebsites.get(courseName));//Puts course websites in the same order as the course names so that the index of the array list match the course name and course website correctly
            }

            recyclerViewForCourses.setAdapter(new CourseListScreenCoursesAdapter(courseNames, courseWebsites, CourseListScreen.this,  qualification));//Define how the courses are handled

        }
    }


}
