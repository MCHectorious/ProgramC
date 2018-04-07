package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public interface DatabaseAccessObject {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCourse(Course course);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCoursePoint(CoursePoint point);

    @Query("SELECT * FROM courses")
    List<Course> getAllCourses();


    @Query("SELECT * FROM course_points WHERE course_ID_foreign = :courseID")
    List<CoursePoint> getCoursePointsForCourse(int courseID);

    @Update
    void updateCoursePoint(CoursePoint coursePoint);


    @Delete
    void deleteCourse(Course course);

    @Delete
    void deleteCoursePoint(CoursePoint coursePoint);
}
