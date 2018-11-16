package com.amsavarthan.dude.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import okhttp3.MediaType;

public class Utils {

    // WolframAlpha API

    public static final String WOLFRAM_APP_ID = "2THKVH-GR58KTPHAU";
    public static final String WOLFRAM_BASE_URL = "http://api.wolframalpha.com/v2/query?";
    public static final String WOLFRAM_POD_STATE_BASE_URL = "&podstate=";

    public static final String PACKAGE_NAME = "com.amsavarthan.dude";
    public static final String DATA_PATH = Environment.getExternalStorageDirectory().toString() + "/DUDE/";
    public static final String tess_lang = "eng";
    public static final String RADIUS="radius";

    private static int screenWidth = 0;
    private static int screenHeight = 0;

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{permission}, 0);
        }
    }

    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    public static String locationUserId(int count){

        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();

    }

    public static boolean isMyServiceRunning(Class<?> serviceClass,Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("isMyServiceRunning?", true+"");
                return true;
            }
        }
        Log.i ("isMyServiceRunning?", false+"");
        return false;
    }


    public static String calculateMD5(String wikiImageFileName) {
        MessageDigest md = null;
        StringBuffer stringBuffer = null;

        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        md.update(wikiImageFileName.getBytes());

        byte byteData[] = md.digest();

        //convert the byte to hex format
        stringBuffer = new StringBuffer();

        for (int i = 0; i < byteData.length; i++) {
            stringBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return stringBuffer.toString();
    }

    // Show multiline snackbar with message and action

    public static void showMultiLineSnackBar(CoordinatorLayout coordinatorLayout, String contentMessage, String actionMessage, View.OnClickListener mOnClickListener) {

        Snackbar snackbar = Snackbar
                .make(coordinatorLayout, contentMessage, Snackbar.LENGTH_INDEFINITE)
                .setAction(actionMessage, mOnClickListener);
        snackbar.setActionTextColor(Color.YELLOW);

        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);

        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setMaxLines(4);

        snackbar.show();
    }

    // Check network connectivity

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int getScreenHeight(Context c) {
        if (screenHeight == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenHeight = size.y;
        }

        return screenHeight;
    }

    public static boolean locationServicesStatusCheck(final Context context) {
        final LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return true;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Enable GPS")
                .setMessage("This function needs your GPS, do you want to enable it now?")
                .setIcon(android.R.drawable.ic_menu_mylocation)
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();


        return false;
    }

    public static int getScreenWidth(Context c) {
        if (screenWidth == 0) {
            WindowManager wm = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            screenWidth = size.x;
        }

        return screenWidth;
    }

}
