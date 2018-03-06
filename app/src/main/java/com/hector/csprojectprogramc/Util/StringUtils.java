package com.hector.csprojectprogramc.Util;

import android.util.Log;

public class StringUtils {

    public static String convertSpacesToPluses(String string){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            builder.append(( string.charAt(i)==' ' )? "+":string.charAt(i));
        }
        return builder.toString();
    }

    public static String convertOfficialToColloquial(String text){
        String[] prefixesToRemove = {"AS and A-level","A-level ", "GCSE", "GCSE ","AS","AS "};
        String[] phrasesToRemove = {"New ", "New"};
        for (String s:prefixesToRemove){
            if(text.startsWith(s)){
                text = text.substring(s.length());
            }
        }
        for (String s:phrasesToRemove){
            if(text.contains(s)){
                Log.i("Phrase to Remove", "True");
                text = text.substring(0, text.indexOf(s)) + text.substring(text.indexOf(s)+s.length());
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

}
