package com.hector.csprojectprogramc.GeneralUtilities;


public class CommonWordsChecker {

    private static final String[] TWO_CHARACTER_WORDS = {"an","as","at","be","by","do","if","in","is","it","of","on","or","so","to","we"};
    private static final String[] THREE_CHARACTER_WORDS = {"and","are","but","can","due","for","had","how","its","may","out","the","why","you"};
    private static final String[] FOUR_CHARACTER_WORDS = {"also","come","does","e.g.","form","from","have","into","made","make","part","such","take","that","then","they","this","type","what","when","with","your"};
    private static final String[] FIVE_CHARACTER_WORDS = {"about","bring","carry","cause","means","takes","their","there","these","thing","where","which"};
    private static final String[] SIX_CHARACTER_WORDS = {"amount","things"};
    private static final String[] SEVEN_CHARACTER_WORDS = {"because"};
    //Common words which should not be asked

    public static boolean checkIfCommonWord(String input){
        String wordToTest = input.toLowerCase();//because being a common word shouldn't be case

        switch (wordToTest.length()){
            case 1:
                return true;//Assumes no single character should be asked
            case 2:
                return isInArray(wordToTest, TWO_CHARACTER_WORDS);
            case 3:
                return isInArray(wordToTest, THREE_CHARACTER_WORDS);
            case 4:
                return isInArray(wordToTest, FOUR_CHARACTER_WORDS);
            case 5:
                return isInArray(wordToTest, FIVE_CHARACTER_WORDS);
            case 6:
                return isInArray(wordToTest, SIX_CHARACTER_WORDS);
            case 7:
                return isInArray(wordToTest, SEVEN_CHARACTER_WORDS);
            default:
                return false;//Assumes longer words should always be asked
        }

    }

    private static boolean isInArray(String string, String[] array){
        int low = 0;//the minimum index the string could be in
        int high = array.length;//the maximum index the string could be in
        int mid;//the pivot
        int compare;//the value of the comparison

        while (low <= high) {
            mid = (low + high) >> 1;//the floor of the midpoint between the two indexes
            compare = array[mid].compareTo(string);//how the strings compare
            if (compare < 0) {
                low = mid + 1;//Might be in upper halve of subset
            } else if (compare > 0) {
                high = mid - 1;//Might be in lower halve of subset
            } else {
                return true;//compare == 0, so strings are the same
            }
        }
        return false;//if it hasn't been found then it is not in the array
    }


}
