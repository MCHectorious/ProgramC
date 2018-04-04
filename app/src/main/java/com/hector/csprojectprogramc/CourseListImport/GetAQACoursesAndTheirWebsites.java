package com.hector.csprojectprogramc.CourseListImport;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import java.util.HashMap;


public class GetAQACoursesAndTheirWebsites extends AsyncTask<Void,Void,HashMap<String,String>> { //Gets the list of courses from the AQA website
        private ProgressDialog progressDialog; //An dialog which shows the user that a long-running process is occurring
        private WeakReference<Context> context,  appContext;
        private String qualification, HTMLDivider;
        private WeakReference<RecyclerView> recyclerViewForCourses;


        public GetAQACoursesAndTheirWebsites(Context context, String qualification, String HTMLDivider, RecyclerView recyclerViewForCourses, Context appContext){
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
        protected HashMap<String, String> doInBackground(Void... voids) {
            HashMap<String,String> courseNameAndWebsite = new HashMap<>();
            try{
                Document document = Jsoup.connect("http://www.aqa.org.uk/qualifications").timeout(1000000).get();//Loads the aqa website
                Elements links = document.select("div[class="+ HTMLDivider +"]").select("a[href]");// Gets all the hyperlinks of the relevant courses
                for(Element element: links){ //Runs through each course
                    String courseName = element.text();
                    String courseWebsite = element.attr("href");
                    courseNameAndWebsite.put(courseName,courseWebsite);


                }
            }catch (IOException exception){//TODO: improve
                Log.e("Error","A IOException occurred");// Displays that an error has occurred
            }
            return courseNameAndWebsite;//So that it implements the method correctly
        }

        @Override
        protected void onPostExecute(HashMap<String,String> courseNamesAndWebsites){
            super.onPostExecute(courseNamesAndWebsites);
            progressDialog.dismiss();//Hides the

            recyclerViewForCourses.get().setHasFixedSize(true);// Improves the speed of using the recycler view
            recyclerViewForCourses.get().setLayoutManager(new LinearLayoutManager(context.get()));// Shows the courses in a vertical list
            recyclerViewForCourses.get().setAdapter(new CourseListScreenCoursesAdapter(courseNamesAndWebsites.keySet(), courseNamesAndWebsites.values(), context.get(), appContext.get(), qualification));//Define how the courses are handled
        }
    }