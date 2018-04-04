package com.example.hello.alarm;

import android.app.Activity;
import android.app.NotificationManager;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by hello on 4/4/18.
 */

public class CancelNotification extends Activity {

    public static final String notification_id = "notification_id";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("duy", "onCreate: Hello");
        NotificationManager notification = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notification.cancel(getIntent().getIntExtra(notification_id, -1));
        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately
    }

}
