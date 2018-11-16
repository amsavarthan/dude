package com.amsavarthan.dude.utils;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amsavarthan.dude.activities.FragmentContainer;

public class PowerGestureReceiver extends BroadcastReceiver {
    static int countPowerOff = 0;

    public PowerGestureReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.v("onReceive", "Power button is pressed.");

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            countPowerOff++;
        } else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if (countPowerOff == 3) {

                Intent i = new Intent(new Intent(context, FragmentContainer.class).putExtra("name","sos_"));
                context.startActivity(i);

            }
        }

    }
}