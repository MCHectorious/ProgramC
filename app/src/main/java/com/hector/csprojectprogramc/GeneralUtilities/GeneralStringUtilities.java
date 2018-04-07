package com.hector.csprojectprogramc.GeneralUtilities;

public class GeneralStringUtilities {

    public static String convertSpacesToPluses(String string){
        StringBuilder outputStringBuilder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            outputStringBuilder.append(( string.charAt(i)==' ' )? "+":string.charAt(i));
        }
        return outputStringBuilder.toString();
    }

    public static String convertOfficialCourseNameToColloquialCourseName(String text){
        String[] prefixesToRemove = {"AS and A-level","A-level ", "GCSE", "GCSE ","AS","AS "};
        String[] phrasesToRemove = {"New ", "New"};
        for (String prefix:prefixesToRemove){
            if(text.startsWith(prefix)){
                text = text.substring(prefix.length());
            }
        }
        for (String phrase:phrasesToRemove){
            if(text.contains(phrase)){
                text = text.substring(0, text.indexOf(phrase)) + text.substring(text.indexOf(phrase)+phrase.length());
            }
        }

        if(text.contains("(")){
            if(text.substring(text.lastIndexOf('(')+1,text.lastIndexOf(')')-1).matches("(Draft )*([0123456789])+")){
                try{
                    text = text.substring(0,text.lastIndexOf('(')-1).concat(text.substring(text.lastIndexOf(')')+1));
                }catch (Exception e){
                    text = text.substring(0,text.lastIndexOf('(')-1);
                }

            }
        }
        return text;
    }

    public static int countOfCharacterInString(Character c, String s){
        int count = 0;
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i)==c){
                count++;
            }
        }
        return count;
    }

}
