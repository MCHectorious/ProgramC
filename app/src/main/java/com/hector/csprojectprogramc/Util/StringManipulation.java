package com.hector.csprojectprogramc.Util;

/**
 * Created by Hector - New on 23/12/2017.
 */

public class StringManipulation {

    public static String convertSpacesToPluses(String string){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < string.length(); i++) {
            builder.append(( string.charAt(i)==' ' )? "+":string.charAt(i));
        }
        return builder.toString();
    }

}
