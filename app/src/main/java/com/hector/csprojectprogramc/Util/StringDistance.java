package com.hector.csprojectprogramc.Util;

public class StringDistance {

    public static double getNormalisedSimilarity(String string1, String string2){
        int maxLength = Math.max(string1.length(),string2.length());
        if(maxLength==0){
            return 1.0;
        }
        return 1.0 - (getLevenshteinStringDistance(string1,string2)/maxLength);
    }

    private static double getLevenshteinStringDistance(String string1, String string2){
        if(string1.length()==string2.length()){
            if(string1.equals(string2)){
                return 0.0;
            }
        }

        if(string1.length() == 0){
            return string2.length();
        }
        if(string2.length() == 0){
            return string1.length();
        }

        int[] oldCost = new int[string2.length()+1];
        int[] newCost = new int[string2.length()+1];
        int[] tempCost;

        for (int i = string2.length();i>=0;i--){
            oldCost[i] = i;
        }

        int cost;
        for (int i = 0;i<string1.length();i++){
            newCost[i] = i+1;

            for (int j = 0; j<string2.length();j++){
                cost = (string1.charAt(i)==string2.charAt(j))? 0:1;
                newCost[j+1] = Math.min(newCost[j]+1,Math.min(oldCost[j+1]+1,oldCost[j] + cost));


            }
            tempCost = oldCost;
            oldCost = newCost;
            newCost = tempCost;
        }

        return oldCost[string2.length()];
    }

}
