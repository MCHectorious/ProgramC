package com.hector.csprojectprogramc.MLModel;

import android.arch.persistence.room.Room;

import com.hector.csprojectprogramc.Database.MyDatabase;
import com.hector.csprojectprogramc.Util.Flashcard;

import java.util.ArrayList;

/**
 * Created by Hector - New on 26/12/2017.
 */

public class FlashcardToSentenceModel {

    public static String convertToSentence(String front, String back){
        return front+back;//TODO: Implement actual model
    }

    public static String convertToSentence(Flashcard flashcard){
        return convertToSentence(flashcard.getFront(),flashcard.getBack());
    }

}
