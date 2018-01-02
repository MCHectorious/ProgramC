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
        int blue = 73;
        for (int i = 0; i < string.length(); i++) {
            int ascii = (int) string.charAt(i);
            red = red*41 + 3*ascii;
            green = green*107 + 2*ascii;
            blue = blue*19 + ascii;
        }
        int color = Color.argb(200,red % 256,green % 256,blue % 256);
        //Log.i("string",""+red+green+blue);
        return color;
    }

}
