package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

@Database(entities = {Course.class,CoursePoint.class},
        version = 2, exportSchema = false)
public abstract class MainDatabase extends RoomDatabase{
    public abstract DatabaseAccessObject customDao();
}
