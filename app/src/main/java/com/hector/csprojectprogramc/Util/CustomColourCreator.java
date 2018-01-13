package com.hector.csprojectprogramc.Util;

import android.graphics.Color;
import android.util.Log;

/**
 * Created by Hector - New on 23/12/2017.
 */

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
        int color = Color.argb(255,249 - (red % 234),249 - (green % 233),249 - (blue % 226));
        //Log.i("string",""+red+green+blue);
        return color;
    }

}
