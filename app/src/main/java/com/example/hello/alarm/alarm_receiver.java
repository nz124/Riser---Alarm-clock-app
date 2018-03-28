package com.example.hello.alarm;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioTrack;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

/**
 * Created by hello on 3/11/18.
 */

public class alarm_receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        showNotification(context);
    }

    private void showNotification(Context context){
        String channelId = "alarm_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Create audio attribute instance to set notification's sound
            AudioAttributes aSound_attribute = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build();
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            CharSequence name = "Alarm Notification";
            String description ="This channel is for alarm notification";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
            channel.setDescription(description);
            channel.enableVibration(true);
            channel.setSound(Uri.parse("android.resource://"+ context.getPackageName()+"/"+R.raw.apple_ring), aSound_attribute);
            // Register the channel with the system
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(MainActivity.NOTIFICATION_SERVICE);
            assert notificationManager!= null;
            notificationManager.createNotificationChannel(channel);
        }
        Intent intent_main_activity = new Intent(context, MainActivity.class);
        PendingIntent pending_intent_main_activity = PendingIntent.getActivity(context, 0, intent_main_activity, 0);
        PendingIntent turn_off_intent = PendingIntent.getActivity(context, 1, intent_main_activity, 0);
        PendingIntent snooze_intent = PendingIntent.getActivity(context, 2, intent_main_activity, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId)
                .setSmallIcon(R.drawable.alarmclock)
                .setContentTitle("The alarm is going off")
                .setContentText("Turn off")
                .setContentIntent(pending_intent_main_activity)
                .addAction(R.drawable.alarmclock, "Turn Off", turn_off_intent)
                .addAction(R.drawable.alarmclock, "Snooze", snooze_intent);
        builder.setSound(Uri.parse("android.resource://"+ context.getPackageName()+"/"+R.raw.apple_ring));
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, builder.build());
    }
}
