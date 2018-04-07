package com.hector.csprojectprogramc.GeneralUtilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.R;

public class CommonAlertDialogs {

    public static void showCannotAccessIntentsDialog(final Context context){
        AlertDialog.Builder cannotAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);
        cannotAccessIntentsAlertDialogBuilder.setTitle(R.string.cant_access_intents);
        cannotAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.press_button_to_return_to_home_screen));
        cannotAccessIntentsAlertDialogBuilder.setCancelable(false);
        cannotAccessIntentsAlertDialogBuilder.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toHomeScreen = new Intent(context,HomeScreen.class);
                context.startActivity(toHomeScreen);
            }
        });
        cannotAccessIntentsAlertDialogBuilder.create().show();
    }

    public static void showDatabaseNullWarningDialog(final Context context){
        AlertDialog.Builder cantAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);
        cantAccessIntentsAlertDialogBuilder.setTitle(R.string.cant_access_database);
        cantAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.please_restart_app));
        cantAccessIntentsAlertDialogBuilder.setCancelable(false);
        cantAccessIntentsAlertDialogBuilder.create().show();
    }

    public static void showCannotAccessCoursePointsDialog(final Context context){
        AlertDialog.Builder cannotAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);
        cannotAccessIntentsAlertDialogBuilder.setTitle(R.string.unable_to_access_course_points);
        cannotAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.press_button_to_return_to_home_screen));
        cannotAccessIntentsAlertDialogBuilder.setCancelable(false);
        cannotAccessIntentsAlertDialogBuilder.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toHomeScreen = new Intent(context,HomeScreen.class);
                context.startActivity(toHomeScreen);
            }
        });
        cannotAccessIntentsAlertDialogBuilder.create().show();
    }


}
