package com.hector.csprojectprogramc.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;
import java.util.List;

/**
 * Created by Hector - New on 24/12/2017.
 */

@Dao
public interface CustomDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Course course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoursePoint(CoursePoints point);

    @Query("SELECT * FROM courses")
    List<Course> getAllSavedCourses();


    @Query("SELECT * FROM course_points")
    List<CoursePoints> getAllSavedCoursePoints();

    //@Query("SELECT * FROM courses WHERE course_ID = :id")
    //Course getInformationFromCourse(int id);

    @Query("SELECT * FROM course_points WHERE course_ID_foreign = :id")
    List<CoursePoints> getPointsForCourse(int id);

    @Delete
    void deleteCourse(Course course);

    @Delete
    void deleteCoursePoint(CoursePoints points);
}
