package com.example.hello.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by hello on 3/11/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Intent cancelIntent;
    Intent snoozeIntent;

    @Override
    public void onReceive(Context context, Intent intent) {
        cancelIntent = new Intent(context, AlarmNotification.class);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        snoozeIntent = new Intent(context, SnoozeAlarm.class);
        context.startActivity(cancelIntent);
    }
}



