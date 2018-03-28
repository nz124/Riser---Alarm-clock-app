package com.example.hello.alarm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by hello on 3/11/18.
 */

public class RingtoneService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    public void onStart(Intent intent, int startId){
        super.onStart(intent, startId);
        Log.e("On start command", "In the service start command");
        String yes_button = intent.getExtras().getString("extra");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Alarm Notification";
            String description ="This is used to ring the alarm";
            int importance = NotificationManagerCompat.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("default", name, importance);
            channel.setDescription(description);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(RingtoneService.NOTIFICATION_SERVICE);
            assert notificationManager!= null;
            notificationManager.createNotificationChannel(channel);
        }
        if (yes_button != null) {
            if (yes_button.equals("yes")) {
                Log.e("click ", "yes");
                startId = 1;
            } else if (yes_button.equals("no")) {
                startId = 0;
                Log.e("click ", "no");
            } else {
                Log.e("what ", "happend");
            }
        }


        if (startId == 1) {
            Log.e("sound", "playing");

            Intent intent_main_activity = new Intent(this.getApplicationContext(), MainActivity.class);
            PendingIntent pending_intent_main_activity = PendingIntent.getActivity(this, 0, intent_main_activity, 0);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "default")
                    .setSmallIcon(R.drawable.alarmclock)
                    .setContentTitle("The alarm is going off")
                    .setContentText("Turn off")
                    .setContentIntent(pending_intent_main_activity)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setSound(Uri.parse("android.resource://"+ getApplicationContext().getPackageName()+"/"+R.raw.apple_ring))
                    .setAutoCancel(true);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
            notificationManager.notify(0, notification.build());
        }
    }
    public void onDestroy(){
        Log.e("On destroy", "Exited");
        super.onDestroy();
    }
}
