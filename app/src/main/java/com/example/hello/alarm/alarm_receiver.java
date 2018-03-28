package com.example.hello.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Created by hello on 3/11/18.
 */

public class alarm_receiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Hey", "onReceive: Hello");
        String what_button = intent.getExtras().getString("extra");
        Intent service_intent = new Intent(context, RingtoneService.class);
        service_intent.putExtra("extra", what_button);
        context.startService(service_intent);

    }
}
