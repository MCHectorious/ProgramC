package com.hector.csprojectprogramc.Activities;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;


import com.hector.csprojectprogramc.R;
import com.hector.csprojectprogramc.Adapter.CourseListScreenCoursesAdapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class CourseListScreen extends AppCompatActivity {
    private String qualification; //Stores whether to show GCSE or A-Level courses
    private String HTMLDividerClassForQualification; //Where to search for courses on the AQA website to find either GCSE or A-Level courses
    private ArrayList<String> courseNames = new ArrayList<>(); //Stores the names of all of the relevant courses
    private ArrayList<String> courseWebsites = new ArrayList<>(); //Stores the URLs of the websites for the courses

    @Override
    protected void onCreate(Bundle savedInstanceState) { //Runs when the screen starts
        super.onCreate(savedInstanceState); //TODO: research what this does
        setContentView(R.layout.activity_course_list_screen); //Links the XML file that defines the layout of the screen
        qualification = getIntent().getStringExtra("Qualification"); //Gets the qualification of the courses to show from the previous screen //TODO: Prepare for null
        if(qualification.equals("GCSE")){
            HTMLDividerClassForQualification = "panelInner gcse-header"; //Sets the divider to the name of the section which includes the GCSE courses
        }
        if (qualification.equals("AS and A-Level")){
            HTMLDividerClassForQualification = "panelInner as_and_a-level-header"; //Sets the divider to the name of the section which includes the A-Level courses
        }
        new getCoursesAndTheirWebsites().execute();//Gets the list of courses from the AQA website and then afterwards it displays them
    }

    private class getCoursesAndTheirWebsites extends AsyncTask<Void,Void,Void>{ //Gets the list of courses from the AQA website
        private ProgressDialog progressDialog; //An dialog which shows the user that a long-running process is occurring
        @Override
        protected void onPreExecute(){//Shows the user the dialog
            super.onPreExecute(); //TODO: research what this does
            progressDialog = new ProgressDialog(CourseListScreen.this); // Initialises the variable
            progressDialog.setTitle("Getting the Latest List of "+qualification+" courses"); // Sets the title of the dialog shown to the user
            progressDialog.setMessage("This should only take a moment"); //Sets the message of dialog to an instruction for the user to follow
            progressDialog.setIndeterminate(false); //Shows an animation while the dialog is displayed but this animation doesn't represent how far though the process is
            progressDialog.show();//Shows the dialog on the screen
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try{
                Document document = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(1000000).get();//Loads the aqa website
                Elements links = document.select("div[class="+ HTMLDividerClassForQualification +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
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
            final RecyclerView recyclerViewForCourses =  findViewById(R.id.courseListScreenRecyclerView); //Initialises the recycler view which shows the courses
            recyclerViewForCourses.setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.setLayoutManager(new LinearLayoutManager(CourseListScreen.this));// Shows the courses in a vertical list
            recyclerViewForCourses.setAdapter(new CourseListScreenCoursesAdapter(courseNames, courseWebsites, CourseListScreen.this, getApplicationContext(), qualification));//Define how the courses are handled //TODO: improve description
        }
    }
}
