package com.hector.csprojectprogramc.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Hector - New on 03/02/2018.
 */

public class CommonWords {

    private ArrayList<String> words = new ArrayList<>();
    private int maxStringLength = 0;
    private int wordsSize;

    public CommonWords(){
        words.add("to");
        words.add("of");
        words.add("in");
        words.add("for");
        words.add("the");
        words.add("on");
        words.add("with");
        words.add("at");
        words.add("by");
        words.add("from");
        words.add("about");
        words.add("into");
        words.add("and");
        words.add("a");
        words.add("that");
        words.add("it");
        words.add("as");
        words.add("this");
        words.add("but");
        words.add("they");
        words.add("or");
        words.add("an");
        words.add("there");
        words.add("what");
        words.add("are");
        words.add("their");
        words.add("where");
        words.add("take");
        words.add("is");
        words.add("takes");
        words.add("make");
        words.add("do");
        words.add("so");
        words.add("can");
        words.add("carry");
        words.add("out");
        words.add("bring");
        words.add("such");
        words.add("these");
        words.add("part");
        words.add("if");
        words.add("then");
        words.add("also");
        words.add("due");
        words.add("be");
        words.add("had");
        words.add("when");
        words.add("does");
        words.add("have");
        words.add("-");
        words.add("how");
        words.add("may");
        words.add("thing");
        words.add("things");
        words.add("form");
        words.add("amount");
        words.add("cause");
        words.add("made");
        words.add("your");
        words.add("you");
        words.add("we");
        words.add("means");
        words.add("come");
        words.add("which");
        words.add("e.g.");
        words.add("type");
        words.add("why");
        words.add("?");
        words.add("its");
        words.add("because");




        Collections.sort(words);

        for (String string: words) {
            maxStringLength = Math.max(string.length(),maxStringLength);
        }
        wordsSize = words.size()-1;

    }

    public boolean isACommonWord(String input){
        String wordToTest = input.toLowerCase();

        if (wordToTest.length()<maxStringLength){
            int low = 0;
            int high = wordsSize;
            int mid;
            int compare;

            while (low <= high) {
                mid = (low + high) >> 1;


                compare = words.get(mid).compareTo(wordToTest);
                if (compare < 0) {
                    low = mid + 1;
                } else if (compare > 0) {
                    high = mid - 1;
                } else {
                    return true;
                }
            }
        }
        return false;
    }



}
