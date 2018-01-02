package com.hector.csprojectprogramc.Database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.ArrayList;
import java.util.List;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

/**
 * Created by Hector - New on 24/12/2017.
 */

@Dao
public interface CustomDao {
    @Insert(onConflict = REPLACE)
    void insertCourse(Course course);

    @Insert(onConflict = REPLACE)
    void insertCoursePoint(CoursePoints point);

    @Update
    void updateCourse(Course course);

    @Delete
    void deleteCourse(Course course);

    @Query("SELECT * FROM courses")
    List<Course> getAllSavedCourses();

    @Query("SELECT * FROM courses WHERE course_ID = :id")
    Course getInformationFromCourse(int id);

    //@Query("SELECT * FROM course_points " +
    //        "INNER JOIN Course ON Course.course_ID = course_points.course_ID_foreign" +
    //        "WHERE .course_ID_foreign = :courseID")
    //ArrayList<CoursePoints> getCoursePointsForCourse(int courseID);

}
