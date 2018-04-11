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
    public abstract DatabaseAccessObject databaseAccessObject();//Object which allows me to access the database

    public static MainDatabase getDatabase(Context context){//gets the database
        MainDatabase database = null;//Initialises the database to an impossible value
        try{
            database = Room.databaseBuilder(context, MainDatabase.class, context.getString(R.string.database_location)).fallbackToDestructiveMigration().build();//Gets the database
        }catch (NullPointerException exception){//will occur if the built database is null
            AlertDialogHelper.showDatabaseNullWarningDialog(context);//Warns the user that the database can't accessed
        }
        return database;//return the database

    }

}
