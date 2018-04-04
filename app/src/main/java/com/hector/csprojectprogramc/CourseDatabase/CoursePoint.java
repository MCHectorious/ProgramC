package com.hector.csprojectprogramc.CourseDatabase;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "course_points",
        foreignKeys = @ForeignKey(
                entity = Course.class,
                parentColumns = "course_ID",
                childColumns = "course_ID_foreign",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("course_ID_foreign")
        )
public class CoursePoint {
    @PrimaryKey(autoGenerate=true)
    private int point_ID;

    private int course_ID_foreign;

    public void setFlashcard_front(String flashcard_front) {
        this.flashcard_front = flashcard_front;
    }

    public void setFlashcard_back(String flashcard_back) {
        this.flashcard_back = flashcard_back;
    }

    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    private String flashcard_front;
    private String flashcard_back;
    private String sentence;

    public CoursePoint(int course_ID_foreign, String flashcard_front, String flashcard_back, String sentence){
        this.course_ID_foreign = course_ID_foreign;
        this.flashcard_front = flashcard_front;
        this.flashcard_back = flashcard_back;
        this.sentence = sentence;
    }


    public int getPoint_ID() {
        return point_ID;
    }

    public void setPoint_ID(int point_ID) {
        this.point_ID = point_ID;
    }

    public int getCourse_ID_foreign() {
        return course_ID_foreign;
    }

    public String getFlashcard_front() {
        return flashcard_front;
    }

    public String getFlashcard_back() {
        return flashcard_back;
    }

    public String getSentence() {
        return sentence;
    }

}
