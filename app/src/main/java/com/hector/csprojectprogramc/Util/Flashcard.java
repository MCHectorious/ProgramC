package com.hector.csprojectprogramc.Util;

/**
 * Created by Hector - New on 24/12/2017.
 */

public class Flashcard {
    private final String front;
    private final String back;

    public Flashcard(String front, String back){
        this.front = front;
        this.back = back;
    }

    public String getFront() {
        return front;
    }

    public String getBack() {
        return back;
    }

}
