package com.hector.csprojectprogramc.GeneralUtilities;

public interface AsyncTaskCompleteListener<T> {//Parameterised with the generic object T so that it can handle the results of the async task

   void onAsyncTaskComplete(T result);//runs after an async task has completed

}
