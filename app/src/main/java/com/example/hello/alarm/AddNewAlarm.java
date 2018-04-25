package com.example.hello.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

import static android.content.Context.ALARM_SERVICE;


public class AddNewAlarm extends Fragment {

    AlarmManager alarmManager;
    TimePicker timePicker;
    Context context;
    Button setOnButton;
    PendingIntent pending_intent;
    ArrayList alarm_data;
    Integer current_point;
    static FirebaseDatabase database;
    static DatabaseReference myRef;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);

    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment*/
        View view = inflater.inflate(R.layout.add_new_alarm, container, false);


        timePicker = view.findViewById(R.id.timePicker);
        //Create array of all alarm to show on the list view
        alarm_data = new ArrayList<>();
        final AlarmAdapter adapter = new AlarmAdapter(getContext(), R.layout.listitem, alarm_data);
        final ListView alarm_list = view.findViewById(R.id.listView);
        alarm_list.setAdapter(adapter);
        final Calendar calendar = Calendar.getInstance();
        final Intent alarm_intent = new Intent(getContext(), alarm_receiver.class);



        setOnButton = view.findViewById(R.id.setOn);
        setOnButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Date current_date = new Date();
                calendar.set(Calendar.DATE, current_date.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                Log.e("Time", "onClick: " + calendar.getTime());
                if (calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                    Log.e("Time", "onClick: " + calendar.getTime());
                }
                ;

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                int alarm_id = new Random().nextInt();


                alarm_intent.putExtra("alarm_id", alarm_id);
                pending_intent = PendingIntent.getBroadcast(getContext(), alarm_id, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager.AlarmClockInfo alarm_info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pending_intent);
                alarmManager.setAlarmClock(alarm_info, pending_intent);


                //Add alarm to list view
                alarm newAlarm = new alarm(pending_intent, hour, minute);
                adapter.add(newAlarm);
            }
        });
        return view;
    }

    public class alarm {
        String time_string;
        PendingIntent alarm_pending_intent;

        public alarm() {
            super();
        }

        public alarm(PendingIntent alarm_pending_intent, int hour, int minute) {
            super();
            this.alarm_pending_intent = alarm_pending_intent;
            String hour_string, minute_string;
            hour_string = String.valueOf(hour);
            minute_string = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            this.time_string = hour_string + " : " + minute_string;
            Log.e("alarm", "alarm: " + this.time_string);
        }

    }

    public class AlarmAdapter extends ArrayAdapter<alarm> {
        AlarmAdapter(Context context, int listViewResource, ArrayList<alarm> alarms) {
            super(context, R.layout.listitem, alarms);
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final alarm alarm = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
            }
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            Switch switchButton = convertView.findViewById(R.id.switchButton);
            Button removeButton = convertView.findViewById(R.id.remove_button);
            switchButton.setChecked(true);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Remove the alarm from AlarmManager and from ListView]'
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
}

