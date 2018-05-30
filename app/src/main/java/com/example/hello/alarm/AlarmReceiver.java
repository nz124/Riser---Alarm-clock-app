package com.example.hello.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by hello on 3/11/18.
 */

public class AlarmReceiver extends BroadcastReceiver {
    Intent cancelIntent;
    Intent snoozeIntent;
    FirebaseUser currentUser;
    DatabaseReference myRef;

    String alarmTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        myRef = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid());

        cancelIntent = new Intent(context, AlarmNotification.class);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        snoozeIntent = new Intent(context, SnoozeAlarm.class);
        context.startActivity(cancelIntent);

        //Retrieve alarm's id and cancel reminding notification on status bar
        final int alarm_id = intent.getIntExtra("alarm_id", 0);
        MainActivity.clearNotification(context, alarm_id);
        myRef.child("Alarms").child(Integer.toString(alarm_id)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Alarm goingOffAlarm = dataSnapshot.getValue(Alarm.class);
                //Push alarm date and duration to sleep data
                myRef.child("Sleep Data").child(goingOffAlarm.getMonth()).child(Integer.toString(goingOffAlarm.date)).setValue(goingOffAlarm.getDurationInMillis());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}



