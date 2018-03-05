package com.hector.csprojectprogramc.Util;

import android.graphics.Color;

public class CustomColourCreator {

    public static int getColourFromString(String string){
        int red = 137 ;
        int green = 251;
        int blue = 77;
        for (int i = 0; i < string.length(); i++) {
            int ascii = (int) string.charAt(i);
            red = red*41 + 5*ascii;
            green = green*103 + 3*ascii;
            blue = blue*23 + ascii;
        }
        return Color.argb(255,249 - (red % 234),249 - (green % 233),249 - (blue % 226));
    }

}
