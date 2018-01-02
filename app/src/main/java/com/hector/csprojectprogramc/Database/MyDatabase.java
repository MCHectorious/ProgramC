package com.hector.csprojectprogramc.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Hector - New on 24/12/2017.
 */

@Database(entities = {Course.class,CoursePoints.class},
        version = 1, exportSchema = false)
public abstract class MyDatabase extends RoomDatabase{
    public abstract CustomDao customDao();
}
