package com.hector.csprojectprogramc.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Course.class,CoursePoints.class},
        version = 2, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase{
    public abstract CustomDao customDao();
}
