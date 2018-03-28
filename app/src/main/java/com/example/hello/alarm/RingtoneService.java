package com.example.hello.alarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by hello on 3/11/18.
 */

public class RingtoneService extends Service {
    MediaPlayer alarm_sound;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId){
        Log.e("On start command", "In the service start command");
        String yes_button = intent.getExtras().getString("extra");
        if (yes_button != null) {
            if (yes_button.equals("yes")) {
                Log.e("click ", "yes");
                alarm_sound = MediaPlayer.create(this, R.raw.apple_ring);
                startId = 1;
            } else if (yes_button.equals("no")) {
                startId = 0;
                Log.e("click ", "no");
            } else {
                Log.e("what ", "happend");
            }
        }
            if (alarm_sound != null) {
                if (!alarm_sound.isPlaying()){
                    Log.e("sound ", "not playing");
                }
                if (!alarm_sound.isPlaying() && startId == 1) {
                    alarm_sound.start();
                    Log.e("sound", "playing");
                    NotificationManager notification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
                    PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0, intent_main_activity, 0);
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        Notification notification_popup = new Notification.Builder(this)
                                .setContentTitle("The alarm is going off")
                                .setContentText("Turn off")
                                .setSmallIcon(R.drawable.alarmclock)
                                .setContentIntent(pending_intent_main_activity)
                                .setAutoCancel(false)
                                .build();
                        assert notification != null;
                        startForeground(1, notification_popup);
                    }
                } else if (alarm_sound.isPlaying() && startId == 0) {
                    alarm_sound.stop();
                } else if (alarm_sound.isPlaying() && startId == 1) {
                    Log.e("click yes", "with music");
                } else if (!alarm_sound.isPlaying() && startId == 0) {
                    Log.e("click no", "no music");
                }
            }
        return START_NOT_STICKY;
    }
    public void onDestroy(){
        Log.e("On destroy", "Exited");
        alarm_sound.stop();
        super.onDestroy();
    }
}
