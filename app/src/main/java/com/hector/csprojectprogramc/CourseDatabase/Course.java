package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "courses")
public class Course {
    @PrimaryKey
    private int course_ID;//id of course in the database. I handle setting it to a unique value

    private String colloquial_name;//The shortened name for the course
    private String official_name;//The official name for the course
    private String website;//The official website for the course
    private String examBoard;//The exam board for the course
    private String qualification;//The qualification of the course
    private String next_key_date;//The date of the next key event (if available)
    private String next_key_date_detail;//The details of the next key event (if available)

    public Course(String colloquial_name, String official_name, String website, String examBoard, String qualification,
                  String next_key_date, String next_key_date_detail){
        this.course_ID = -1;//Initialises to an impossible value to make sure I update it to a correct value
        this.colloquial_name =colloquial_name;
        this.official_name = official_name;
        this.website = website;
        this.examBoard = examBoard;
        this.qualification = qualification;
        this.next_key_date = next_key_date;
        this.next_key_date_detail = next_key_date_detail;
    }

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
