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
    @Insert(onConflict = OnConflictStrategy.REPLACE)//If a course with this id already exist replace it with this new one
    void insertCourse(Course course);//Insert a course into a database

    @Insert(onConflict = OnConflictStrategy.REPLACE)//If a course point with this id already exist replace it with this new one
    void insertCoursePoint(CoursePoint point);//Insert a course into a database

    @Query("SELECT * FROM courses ORDER BY colloquial_name ASC")
    List<Course> getAllCourses();//Gets all the courses in the course table

    @Query("SELECT MAX(course_ID) FROM courses")
    int getMaxCourseID();//Gets the current maximum id of a course using the aggregate function max(...)

    @Query("SELECT * FROM course_points WHERE course_ID_foreign = :courseID")
    List<CoursePoint> getCoursePointsForCourse(Integer courseID);//Gets all the course points for a particular course

    @Update
    void updateCoursePoint(CoursePoint coursePoint);//updates the course point


    @Delete
    void deleteCourse(Course course);//removes course from the database

    @Delete
    void deleteCoursePoint(CoursePoint coursePoint);// removes the course point from database
}
