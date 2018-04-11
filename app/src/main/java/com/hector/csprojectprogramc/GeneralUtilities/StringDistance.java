package com.hector.csprojectprogramc.GeneralUtilities;

public class StringDistance {

    public static double getNormalisedSimilarity(String string1, String string2){
        int maxLength = Math.max(string1.length(),string2.length());
        if(maxLength==0){//If both strings are empty
            return 1.0;
        }
        return 1.0 - (getLevenshteinStringDistance(string1,string2)/maxLength);//Because the maximum value for the string distance is the maximum length of the strings
    }

    private static double getLevenshteinStringDistance(String string1, String string2){
        if(string1.length()==string2.length()){//Check to see if the following code can be avoid
            if(string1.equals(string2)){
                return 0.0;
            }
        }

        if(string1.length() == 0){
            return string2.length();//Because it will just be the cost of the insertions
        }
        if(string2.length() == 0){
            return string1.length();//Because it will just be the cost of the insertions
        }

        int[] oldCost = new int[string2.length()+1];
        int[] newCost = new int[string2.length()+1];
        int[] tempCost;

        for (int i = string2.length();i>=0;i--){//Initialising the array
            oldCost[i] = i;
        }

        int cost;
        for (int i = 0;i<string1.length();i++){
            newCost[i] = i+1;

            for (int j = 0; j<string2.length();j++){
                cost = (string1.charAt(i)==string2.charAt(j))? 0:1;//If the characters are the same the cost is 0, otherwise they are 1
                newCost[j+1] = Math.min(newCost[j]+1,Math.min(oldCost[j+1]+1,oldCost[j] + cost));


            }
            tempCost = oldCost;
            oldCost = newCost;
            newCost = tempCost;
        }

        return oldCost[string2.length()];//This is the total number of single character edit operations to get from one to the other
    }

}
