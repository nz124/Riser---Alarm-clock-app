package com.example.hello.alarm;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Calendar;


/**
 * Created by hello on 4/4/18.
 */

public class SnoozeAlarm extends Activity {

    public static final String notification_id = "notification_id";
    long current_time;
    PendingIntent pending_intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.no_view);
        Log.e("snooze", "onCreate: Hello");
        NotificationManager notification = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        assert notification != null;
        notification.cancel(getIntent().getIntExtra(notification_id, -1));

        //Set a temporary alarm after one minute
        long one_minute = 60000;
        AlarmManager alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        final Intent alarm_intent = new Intent(this, alarm_receiver.class);
        pending_intent = PendingIntent.getBroadcast(this, 0, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        current_time = Calendar.getInstance().getTimeInMillis();
        AlarmManager.AlarmClockInfo alarm_info = new AlarmManager.AlarmClockInfo(current_time + one_minute, pending_intent);
        alarmManager.setAlarmClock(alarm_info, pending_intent);

        //Decrement point
       /* MainActivity.incrementPointAndSaveToDb(false, 100);*/
    }
}
