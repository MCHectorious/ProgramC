package com.hector.csprojectprogramc.WebScraping;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.hector.csprojectprogramc.Database.Course;
import com.hector.csprojectprogramc.Util.MultiThreading;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**
 * Created by Hector - New on 24/12/2017.
 */

public class AQAScraper {

    private String colloquialName, officialName, website, examBoard, qualification, nextKeyDate, nextKeyDateDetails;
    private Context context;
    private GetInformationFromOnline getInformation;

    public AQAScraper(String url, Context currentContext){
        context = currentContext;
        getInformation = new GetInformationFromOnline();
        getInformation.execute(url);
    }

    public AsyncTask getTask(){
        return getInformation;
    }

    public Course getCourse(){
        MultiThreading.waitUntilFinished(getInformation);
        Log.i("Got this far","Finished waiting");

        return new Course(colloquialName,
                officialName,website,examBoard,
                qualification,nextKeyDate,
                nextKeyDateDetails);
    }

    private class GetInformationFromOnline extends AsyncTask<String,Void,Void>{
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setTitle("Getting Additional Information From AQA");
            progressDialog.setMessage("Please wait");
            progressDialog.setIndeterminate(false);
            progressDialog.show();
            Log.i("Got this far","Finished AQA pre-execute");
        }

        @Override
        protected Void doInBackground(String... strings) {
            for (String string: strings) {
                website = string;
                try {
                    Document document = Jsoup.connect(string).timeout(100000).get();
                    Log.i("Got this far","connected to AQA website");
                    officialName = document.select("h1[class=mainTitle]").first().text();
                    Elements codesAndReferences = document.select("table[class=tableCodes]");
                    colloquialName = codesAndReferences.select("tr").get(1).select("td").text();
                    qualification = codesAndReferences.select("tr").get(0).select("td").text();
                    examBoard = "AQA";
                    Element keyDateSection = document.select("ul[class=listEvents]").select("li").first();
                    nextKeyDate = keyDateSection.select("span[class=timestamp]").text();
                    nextKeyDateDetails = keyDateSection.text().substring(nextKeyDate.length());
                    Log.i("Got this far", "Collected All Details");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Got this far","Finished AQA background");
            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            Log.i("Got this far","Started onPostExecute");

            progressDialog.dismiss();
        }
    }

}

