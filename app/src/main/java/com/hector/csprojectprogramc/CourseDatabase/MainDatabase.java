package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.hector.csprojectprogramc.GeneralUtilities.AlertDialogHelper;
import com.hector.csprojectprogramc.R;

@Database(entities = {Course.class,CoursePoint.class},
        version = 8, exportSchema = false)

public abstract class MainDatabase extends RoomDatabase{
    public abstract DatabaseAccessObject databaseAccessObject();

    public static MainDatabase getDatabase(Context context){
        MainDatabase database = null;
        try{
            database = Room.databaseBuilder(context, MainDatabase.class, context.getString(R.string.database_location)).fallbackToDestructiveMigration().build();
        }catch (NullPointerException exception){
            AlertDialogHelper.showDatabaseNullWarningDialog(context);
        }
        return database;

    }

}
