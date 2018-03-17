package com.hector.csprojectprogramc.Utilities;


public class CommonWordsChecker {

    private static String[] TwoCharWords = {"an","as","at","be","by","do","if","in","is","it","of","on","or","so","to","we"};
    private static String[] ThreeCharWords = {"and","are","but","can","due","for","had","how","its","may","out","the","why","you"};
    private static String[] FourCharWords = {"also","come","does","e.g.","form","from","have","into","made","make","part","such","take","that","then","they","this","type","what","when","with","your"};
    private static String[] FiveCharWords = {"about","bring","carry","cause","means","takes","their","there","these","thing","where","which"};
    private static String[] SixCharWords = {"amount","things"};
    private static String[] SevenCharWords = {"because"};

    public static boolean checkIfCommonWord(String input){
        String wordToTest = input.toLowerCase();

        switch (input.length()){
            case 1:
                return true;
            case 2:
                return isInArray(wordToTest,TwoCharWords);
            case 3:
                return isInArray(wordToTest,ThreeCharWords);
            case 4:
                return isInArray(wordToTest,FourCharWords);
            case 5:
                return isInArray(wordToTest,FiveCharWords);
            case 6:
                return isInArray(wordToTest,SixCharWords);
            case 7:
                return isInArray(wordToTest,SevenCharWords);
            default:
                return false;
        }

    }

    private static boolean isInArray(String string, String[] array){
        int low = 0;
        int high = array.length;
        int mid;
        int compare;

        while (low <= high) {
            mid = (low + high) >> 1;


            compare = array[mid].compareTo(string);
            if (compare < 0) {
                low = mid + 1;
            } else if (compare > 0) {
                high = mid - 1;
            } else {
                return true;
            }
        }
        return false;
    }


}
