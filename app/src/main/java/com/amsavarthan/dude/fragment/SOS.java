package com.amsavarthan.dude.fragment;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.amsavarthan.dude.R;
import com.amsavarthan.dude.activities.MainActivity;
import com.amsavarthan.dude.utils.Utils;

import pl.bclogic.pulsator4droid.library.PulsatorLayout;


public class SOS extends Fragment {

    ImageView sos;
    View view;
    PulsatorLayout pulsator;
    private MediaPlayer mp;
    private NotificationManager mNotificationManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.frag_sos, container, false);
        return view;
    }

    public static SOS newInstance(boolean pulse)
    {
        Bundle args = new Bundle();
        args.putBoolean("pulsate", pulse);

        SOS fragment = new SOS();
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pulsator = view.findViewById(R.id.pulsator);
        sos=view.findViewById(R.id.button);
        mp = MediaPlayer.create(getActivity(),R.raw.siren);
        mp.setLooping(true);
        mNotificationManager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (getArguments() != null) {
            if (!pulsator.isStarted()) {
                mp.start();
                displayNotification();
                pulsator.start();
                Log.i("pulse","start");
            } else {
                mp.pause();
                mNotificationManager.cancel(0);
                pulsator.stop();
                Log.i("pulse","stop");
            }
        }

        sos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pulsator.isStarted()) {
                    mp.start();
                    displayNotification();
                    pulsator.start();
                    Log.i("pulse","start");
                } else {
                    mp.pause();
                    mNotificationManager.cancel(0);
                    pulsator.stop();
                    Log.i("pulse","stop");
                }
            }
        });

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            if (mp.isPlaying())
                mp.stop();
            mNotificationManager.cancel(0);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void displayNotification() {


        NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
        bigTextStyle.setBigContentTitle("Live Location is on");
        bigTextStyle.bigText("Location is being shared with your emergency contacts...");

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(view.getContext(), "notify_001");
        Intent ii = new Intent(view.getContext(), MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(view.getContext(), 0, ii, 0);

        //mBuilder.setContentIntent(pendingIntent);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("Live Location is on");
        mBuilder.setAutoCancel(false);
        mBuilder.setStyle(bigTextStyle);
        mBuilder.setPriority(Notification.PRIORITY_LOW);
        mBuilder.setOngoing(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("notify_001", "Location Updates",
                    NotificationManager.IMPORTANCE_LOW);
            mNotificationManager.createNotificationChannel(channel);
        }

        mNotificationManager.notify(0, mBuilder.build());

    }


}
