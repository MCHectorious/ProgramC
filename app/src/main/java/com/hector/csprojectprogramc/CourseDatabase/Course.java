package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    private int course_ID;

    private String colloquial_name;
    private String official_name;
    private String website;
    private String examBoard;
    private String qualification;
    private String next_key_date;
    private String next_key_date_detail;

    public Course(int course_ID, String colloquial_name, String official_name, String website, String examBoard, String qualification, String next_key_date, String next_key_date_detail){
        this.course_ID = course_ID;
        this.colloquial_name = colloquial_name;
        this.official_name = official_name;
        this.website = website;
        this.examBoard = examBoard;
        this.qualification = qualification;
        this.next_key_date = next_key_date;
        this.next_key_date_detail = next_key_date_detail;
    }

    public int getCourse_ID() {
        return course_ID;
    }

    public String getColloquial_name() {
        return colloquial_name;
    }


    public String getOfficial_name() {
        return official_name;
    }

    public String getWebsite() {
        return website;
    }

    public String getExamBoard() {
        return examBoard;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getNext_key_date() {
        return next_key_date;
    }

    public String getNext_key_date_detail() {
        return next_key_date_detail;
    }

}
