package com.hector.csprojectprogramc.GeneralUtilities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.hector.csprojectprogramc.Activities.HomeScreen;
import com.hector.csprojectprogramc.R;

public class AlertDialogHelper {

    public static void showCannotAccessIntentsDialog(final Context context){
        AlertDialog.Builder cannotAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the builder for the alert dialog
        cannotAccessIntentsAlertDialogBuilder.setTitle(R.string.cant_access_intents);//Explains the issues
        cannotAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.press_button_to_return_to_home_screen));//Explains how the user can resolve the issue
        cannotAccessIntentsAlertDialogBuilder.setCancelable(false);//Prohibits the user from doing anything else to avoid any further issues
        cannotAccessIntentsAlertDialogBuilder.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toHomeScreen = new Intent(context,HomeScreen.class);//Creates a connection between the context and the home screen
                context.startActivity(toHomeScreen);//Starts the home screen
            }
        });
        cannotAccessIntentsAlertDialogBuilder.create().show();//Show alert dialog
    }

    public static void showDatabaseNullWarningDialog(final Context context){
        AlertDialog.Builder cantAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the builder for the alert dialog
        cantAccessIntentsAlertDialogBuilder.setTitle(R.string.cant_access_database);//Explains the issues
        cantAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.please_restart_app));//Explains how the user can resolve the issue
        cantAccessIntentsAlertDialogBuilder.setCancelable(false);//Prohibits the user from doing anything else to avoid any further issues
        cantAccessIntentsAlertDialogBuilder.create().show();//Show alert dialog
    }

    public static void showCannotAccessCoursePointsDialog(final Context context){
        AlertDialog.Builder cannotAccessIntentsAlertDialogBuilder = new AlertDialog.Builder(context);//Initialises the builder for the alert dialog
        cannotAccessIntentsAlertDialogBuilder.setTitle(R.string.unable_to_access_course_points);//Explains the issues
        cannotAccessIntentsAlertDialogBuilder.setMessage(context.getString(R.string.press_button_to_return_to_home_screen));//Explains how the user can resolve the issue
        cannotAccessIntentsAlertDialogBuilder.setCancelable(false);//Prohibits the user from doing anything else to avoid any further issues
        cannotAccessIntentsAlertDialogBuilder.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent toHomeScreen = new Intent(context,HomeScreen.class);//Creates a connection between the context and the home screen
                context.startActivity(toHomeScreen);//Starts the home screen
            }
        });
        cannotAccessIntentsAlertDialogBuilder.create().show();//Show alert dialog
    }

    public static DialogInterface.OnClickListener onClickDismissDialog(){
        return new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        };
    }


}
