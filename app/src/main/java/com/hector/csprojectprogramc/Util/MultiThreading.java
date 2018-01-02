package com.hector.csprojectprogramc.Util;

import android.os.AsyncTask;

/**
 * Created by Hector - New on 29/12/2017.
 */

public class MultiThreading {
    public static void waitUntilFinished(AsyncTask task){
        while (!task.getStatus().equals(AsyncTask.Status.FINISHED)){}
    }
}
