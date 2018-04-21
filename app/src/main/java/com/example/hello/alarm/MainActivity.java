package com.example.hello.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;

import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    AlarmManager alarmManager;
    TimePicker timePicker;
    Context context;
    Button setOnButton;
    PendingIntent pending_intent;
    ArrayList alarm_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.context = this;

        //Side nav drawer
        final DrawerLayout drawer_layout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);


        //Get information from current user, if there is one.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String name = "", email = "";
        Uri photoUrl;

        if (currentUser != null) {
            name = currentUser.getDisplayName();
            email = currentUser.getEmail();
            photoUrl = currentUser.getPhotoUrl();
        };
        //Set information in the nav's header
        View header_view = navigationView.getHeaderView(0);
        TextView nav_user = header_view.findViewById(R.id.user_name);

        nav_user.setText(email);


        //On click listener for items in the nav drawer
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // set item as selected to persist highlight
                item.setChecked(true);
                // close drawer when item is tapped
                drawer_layout.closeDrawers();
                return true;
            }
        });




        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        timePicker = findViewById(R.id.timePicker);
        //Create array of all alarm to show on the list view
        alarm_data = new ArrayList<>();
        final alarm_adapter adapter = new alarm_adapter(this, R.layout.listitem, alarm_data);
        final ListView alarm_list = findViewById(R.id.listView);
        alarm_list.setAdapter(adapter);
        final Calendar calendar = Calendar.getInstance();
        final Intent alarm_intent = new Intent(this.context, alarm_receiver.class);



        setOnButton = findViewById(R.id.setOn);
        setOnButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                Date current_date = new Date();
                calendar.set(Calendar.DATE, current_date.getDate());
                calendar.set(Calendar.HOUR_OF_DAY, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                Log.e("Time", "onClick: "+ calendar.getTime() );
                if(calendar.before(Calendar.getInstance())) {
                    calendar.add(Calendar.DATE, 1);
                    Log.e("Time", "onClick: "+ calendar.getTime() );
                };

                int hour = timePicker.getHour();
                int minute = timePicker.getMinute();

                int alarm_id = new Random().nextInt();



                alarm_intent.putExtra("alarm_id", alarm_id);
                pending_intent = PendingIntent.getBroadcast(MainActivity.this, alarm_id, alarm_intent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager.AlarmClockInfo alarm_info = new AlarmManager.AlarmClockInfo(calendar.getTimeInMillis(), pending_intent);
                alarmManager.setAlarmClock(alarm_info, pending_intent);


                //Add alarm to list view
                alarm newAlarm = new alarm(pending_intent, hour, minute);
                adapter.add(newAlarm);


            }
        });
    };

    public class alarm{
        String time_string;
        PendingIntent alarm_pending_intent;
        public alarm(){
            super();
        }
        public alarm(PendingIntent alarm_pending_intent, int hour, int minute){
            super();
            this.alarm_pending_intent= alarm_pending_intent;
            String hour_string, minute_string;
            hour_string = String.valueOf(hour);
            minute_string = minute < 10 ? "0" + String.valueOf(minute) : String.valueOf(minute);
            this.time_string = hour_string + " : " + minute_string;
            Log.e("alarm", "alarm: "+this.time_string);
        }

    }

    public class alarm_adapter extends ArrayAdapter<alarm> {
        alarm_adapter(Context context, int listViewResource, ArrayList<alarm> alarms) {
            super(context, R.layout.listitem, alarms);
        }


        @NonNull
        @Override
        public View getView(final int position, View convertView, ViewGroup parent){
            final alarm alarm = getItem(position);
            if (convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.listitem, parent, false);
            }
            TextView tvTime = convertView.findViewById(R.id.tvTime);
            Switch switchButton = convertView.findViewById(R.id.switchButton);
            Button removeButton = convertView.findViewById(R.id.remove_button);
            switchButton.setChecked(true);
            removeButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    //Remove the alarm from AlarmManager and from ListView]
                    Log.e("hey", "onClick: "+alarm.alarm_pending_intent );
                    alarmManager.cancel(alarm.alarm_pending_intent);
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
