package com.example.hello.alarm;

import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;


public class AlarmListFragment extends Fragment {

    static AlarmManager alarmManager;
    TimePicker timePicker;
    Context context;
    PendingIntent pending_intent;
    ArrayList alarm_data;
    Integer current_point;
    static FirebaseDatabase database;
    static DatabaseReference myRef;
    String channelId;
    static Calendar calendar;
    static Intent snoozeIntent;
    static Intent cancelIntent;
    static PendingIntent turn_off_intent;
    static PendingIntent snooze_intent;
    static AlarmAdapter adapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Create array of all alarm to show on the list view
        alarm_data = new ArrayList<>();
        adapter = new AlarmAdapter(getContext(), R.layout.alarm_list_item, alarm_data);
    }



    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment*/
        View view = inflater.inflate(R.layout.alarm_list_layout, container, false);


        FloatingActionButton add_alarm_button = view.findViewById(R.id.add_new_alarm);
        add_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment newFragment = new TimePickerDialogFragment();
                newFragment.show(getActivity().getFragmentManager(), "timePickerFragment");
            }
        });

        //Set alarm list adapter
        final ListView alarm_list = view.findViewById(R.id.listView);
        alarm_list.setAdapter(adapter);

        return view;
    }

    public static class Alarm {
        String time_string;
        PendingIntent alarm_pending_intent;

        public Alarm() {
            super();
        }

        public Alarm(PendingIntent alarm_pending_intent, int hour, int minute) {
            super();
            this.alarm_pending_intent = alarm_pending_intent;
            String hour_string, minute_string;
            hour_string = String.valueOf(hour);
            minute_string = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            this.time_string = hour_string + " : " + minute_string;
        }

    }

    public class AlarmAdapter extends ArrayAdapter<Alarm> {

        AlarmAdapter(Context context, int listViewResource, ArrayList<Alarm> alarms) {
            super(context, R.layout.alarm_list_item, alarms);
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final Alarm alarm = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.alarm_list_item, parent, false);
            }
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            Switch switchButton = convertView.findViewById(R.id.switchButton);
            Button removeButton = convertView.findViewById(R.id.remove_button);


            switchButton.setChecked(true);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove the alarm from AlarmManager and from ListView
                    alarmManager.cancel(alarm != null ? alarm.alarm_pending_intent : null);
                    alarm_data.remove(position);
                    notifyDataSetChanged();
                }


            });
            assert alarm != null;
            tvTime.setText(alarm.time_string);
            return convertView;
        }
    }

    public static void AddAlarm(Context context, int hour, int minute){
        //Alarm Id
        int alarm_id = new Random().nextInt();

        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        calendar = Calendar.getInstance();
        snoozeIntent = new Intent(context, SnoozeAlarm.class);
        cancelIntent = new Intent(context, AlarmNotification.class);
        cancelIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP |Intent.FLAG_ACTIVITY_NEW_TASK);
        turn_off_intent = PendingIntent.getActivity(context, 1, cancelIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        snooze_intent = PendingIntent.getActivity(context, 2, snoozeIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Set calendar's time for the alarm
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);


        final Intent alarm_intent = new Intent(context, AlarmReceiver.class);
        alarm_intent.putExtra("alarm_id", alarm_id);
        PendingIntent pending_intent = PendingIntent.getBroadcast(context, alarm_id, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager.AlarmClockInfo alarm_info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pending_intent);
        alarmManager.setAlarmClock(alarm_info, pending_intent);

        //Add alarm to list view
        Alarm newAlarm = new Alarm(pending_intent, hour, minute);
        adapter.add(newAlarm);


        //Show a persistent notification on notification bar
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "Alarm Channel")
                .setSmallIcon(R.drawable.alarmclock)
                .setContentTitle(newAlarm.time_string)
                .setContentText("You have the next alarm at " + newAlarm.time_string)
                .setPriority(NotificationManagerCompat.IMPORTANCE_LOW)
                .addAction(R.drawable.alarmclock, "Turn Off", turn_off_intent)
                .addAction(R.drawable.alarmclock, "Add 10 minutes", snooze_intent);
        Notification mNotification = builder.build();
        mNotification.flags = Notification.FLAG_NO_CLEAR;
        NotificationManagerCompat notification= NotificationManagerCompat.from(context);
        notification.notify(alarm_id, mNotification);
    }
}

