package com.hector.csprojectprogramc.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.RecyclerViewAdapters.CourseListScreenCoursesAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CourseListScreen extends AppCompatActivity {
    //private String qualification; //Stores whether to show GCSE or A-Level courses
    //private String HTMLDividerClassForQualification; //Where to search for courses on the AQA website to find either GCSE or A-Level courses
    //private ArrayList<String> courseNames = new ArrayList<>(); //Stores the names of all of the relevant courses
    //private ArrayList<String> courseWebsites = new ArrayList<>(); //Stores the URLs of the websites for the courses
    private NetworkInfo wifi;
    //private String qualification; //Stores whether to show GCSE or A-Level courses
    //private String HTMLDividerClassForQualification; //Where to search for courses on the AQA website to find either GCSE or A-Level courses
    //private ArrayList<String> courseNames = new ArrayList<>(); //Stores the names of all of the relevant courses
    //private ArrayList<String> courseWebsites = new ArrayList<>(); //Stores the URLs of the websites for the courses

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

        //new getCoursesAndTheirWebsites(CourseListScreen.this, qualification, HTMLDividerClassForQualification,recyclerViewForCourses, getApplicationContext()).execute();//Gets the list of courses from the AQA website and then afterwards it displays them
        try{
            ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

            //noinspection ConstantConditions
            wifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);


            getCoursesIfConnectedToWifi(new getCoursesAndTheirWebsites(CourseListScreen.this, qualification, HTMLDividerClassForQualification,recyclerViewForCourses, getApplicationContext()));

        }catch (NullPointerException exception){
            Log.w("Warning","Null Pointer");
            //TODO: handle appropriately
        }

    }

    private void getCoursesIfConnectedToWifi(final getCoursesAndTheirWebsites getCourses){
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

    private static class getCoursesAndTheirWebsites extends AsyncTask<Void,Void,Void>{ //Gets the list of courses from the AQA website
        private ProgressDialog progressDialog; //An dialog which shows the user that a long-running process is occurring
        private WeakReference<Context> context,  appContext;
        private String qualification, HTMLDivider;
        private ArrayList<String> courseNames = new ArrayList<>();
        private ArrayList<String> courseWebsites = new ArrayList<>();
        private WeakReference<RecyclerView> recyclerViewForCourses;


        private getCoursesAndTheirWebsites(Context context, String qualification, String HTMLDivider, RecyclerView recyclerViewForCourses, Context appContext){
            this.context = new WeakReference<>(context) ;
            this.qualification = qualification;
            this.HTMLDivider = HTMLDivider;
            this.recyclerViewForCourses = new WeakReference<>(recyclerViewForCourses);
            this.appContext = new WeakReference<>(appContext);
        }


        @Override
        protected void onPreExecute(){//Shows the user the dialog
            super.onPreExecute(); //TODO: research what this does
            progressDialog = new ProgressDialog(context.get()); // Initialises the variable
            progressDialog.setTitle(context.get().getString(R.string.getting_latest_list)+" "+qualification+" "+context.get().getString(R.string.courses)); // Sets the title of the dialog shown to the user
            progressDialog.setMessage(context.get().getString(R.string.this_should_be_quick)); //Sets the message of dialog to an instruction for the user to follow
            progressDialog.setIndeterminate(false); //Shows an animation while the dialog is displayed but this animation doesn't represent how far though the process is
            progressDialog.show();//Shows the dialog on the screen
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document document = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(1000000).get();//Loads the aqa website
                Elements links = document.select("div[class="+ HTMLDivider +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
                for(Element element: links){ //Runs through each course
                    courseNames.add(element.text());// Adds the course name
                    courseWebsites.add(element.attr("href"));// Adds the url of the course website
                }
            }catch (IOException exception){//TODO: improve
                Log.e("Error","A IOException occurred");// Displays that an error has occurred
            }
            return null;//So that it implements the method correctly
        }

        @Override
        protected void onPostExecute(Void result){
            progressDialog.dismiss();//Hides the dialog
            recyclerViewForCourses.get().setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.get().setLayoutManager(new LinearLayoutManager(context.get()));// Shows the courses in a vertical list
            recyclerViewForCourses.get().setAdapter(new CourseListScreenCoursesAdapter(courseNames, courseWebsites, context.get(), appContext.get(), qualification));//Define how the courses are handled
        }
    }
}
