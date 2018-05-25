package com.example.hello.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * Created by hello on 4/4/18.
 */

public class AlarmNotification extends Activity {

    public static final String notification_id = "notification_id";
    long current_time;
    PendingIntent pending_intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_notification_layout);
        //Display current time and date
        TextView time_display = findViewById(R.id.time_display);
        TextView date_display = findViewById(R.id.date_display);
        Date currentDate = new Date();
        String currentTimeString = DateFormat.getTimeInstance(DateFormat.MEDIUM).format(currentDate);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(currentDate);
        time_display.setText(currentTimeString);
        date_display.setText(currentDateString);

        //Clear the notification on notification bar
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        final MediaPlayer mp = MediaPlayer.create(this, R.raw.apple_ring);
        final Vibrator vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vib.vibrate(500);
        mp.setLooping(true);
        mp.start();
        final Intent main_activity_intent = new Intent(this, MainActivity.class);

        Button turn_off_button = findViewById(R.id.turn_off);
        turn_off_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                vib.cancel();
                main_activity_intent.putExtra("type", "turn_off");
                startActivity(main_activity_intent);
            }
        });

        Button snooze_button = findViewById(R.id.snooze);
        snooze_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mp.stop();
                vib.cancel();
                main_activity_intent.putExtra("type", "snooze");
                //Set a temporary alarm after one minute
                long one_minute = 60000;
                AlarmManager alarmManager = (AlarmManager) AlarmNotification.this.getSystemService(Context.ALARM_SERVICE);
                final Intent alarm_intent = new Intent(v.getContext(), AlarmReceiver.class);
                pending_intent = PendingIntent.getBroadcast(v.getContext(), 0, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                current_time = Calendar.getInstance().getTimeInMillis();
                AlarmManager.AlarmClockInfo alarm_info = new AlarmManager.AlarmClockInfo(current_time + one_minute, pending_intent);
                alarmManager.setAlarmClock(alarm_info, pending_intent);
                startActivity(main_activity_intent);
            }
        });
    }
}
