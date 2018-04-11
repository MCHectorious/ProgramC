package com.hector.csprojectprogramc.GeneralUtilities;

public class GeneralStringUtilities {

    public static String convertSpacesToPluses(String string){//Used to consert a string into a format sutiable for URls
        StringBuilder outputStringBuilder = new StringBuilder();//Initialise the output
        for (int i = 0; i < string.length(); i++) {
            outputStringBuilder.append(( string.charAt(i)==' ' )? "+":string.charAt(i));//Output the string unless the character is a space, when you should output a +
        }
        return outputStringBuilder.toString();
    }

    public static String convertOfficialCourseNameToColloquialCourseName(String text){
        String[] prefixesToRemove = {"AS and A-level","A-level ", "GCSE", "GCSE ","AS","AS "};
        String[] phrasesToRemove = {"New ", "New"};
        for (String prefix:prefixesToRemove){
            if(text.startsWith(prefix)){
                text = text.substring(prefix.length());//removes the prefix
            }
        }
        for (String phrase:phrasesToRemove){
            if(text.contains(phrase)){
                text = text.substring(0, text.indexOf(phrase)) + text.substring(text.indexOf(phrase)+phrase.length());//removes the phrase
            }
        }

        if(text.contains("(")){
            if(text.substring(text.lastIndexOf('(')+1,text.lastIndexOf(')')-1).matches("(Draft )*([0123456789])+")){//regex statement for any number, possible with the word Draft before
                try{
                    text = text.substring(0,text.lastIndexOf('(')-1).concat(text.substring(text.lastIndexOf(')')+1));
                }catch (IndexOutOfBoundsException e){//Will occur if the last character is ")"
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
