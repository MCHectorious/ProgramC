package com.hector.csprojectprogramc.MLModel;

import com.hector.csprojectprogramc.Util.Flashcard;

public class FlashcardToSentenceModel {

    public static String convertToSentence(String front, String back){
        return front+" "+back;//TODO: Implement actual model
    }

    public static String convertToSentence(Flashcard flashcard){
        return convertToSentence(flashcard.getFront(),flashcard.getBack());
    }

}
