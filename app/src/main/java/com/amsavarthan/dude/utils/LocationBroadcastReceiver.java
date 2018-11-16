package com.amsavarthan.dude.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LocationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("Service", "Service Stops!!!!");
        context.startService(new Intent(context, GoogleService.class).putExtra("notification",true));
    }
}
