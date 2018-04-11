package com.hector.csprojectprogramc.GeneralUtilities;

import android.graphics.Color;

public class CustomColourCreator {

    public static int generateCustomColourFromString(String string){
        //All numbers are prime to make sure than r,g,b are independent of each other
        //numbers chosen output personal preference of the resulting colours

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
