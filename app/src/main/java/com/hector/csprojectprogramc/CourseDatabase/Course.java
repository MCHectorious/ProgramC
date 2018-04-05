package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey(autoGenerate = true)
    private int course_ID;

    private String colloquial_name;
    private String official_name;
    private String website;


    private String examBoard;
    private String qualification;
    private String next_key_date;
    private String next_key_date_detail;

    public void setCourse_ID(int course_ID) {
        this.course_ID = course_ID;
    }

    public void setColloquial_name(String colloquial_name) {
        this.colloquial_name = colloquial_name;
    }

    public void setOfficial_name(String official_name) {
        this.official_name = official_name;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public void setExamBoard(String examBoard) {
        this.examBoard = examBoard;
    }

    public void setNext_key_date(String next_key_date) {
        this.next_key_date = next_key_date;
    }

    public void setNext_key_date_detail(String next_key_date_detail) {
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