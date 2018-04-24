package com.example.hello.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Bundle;


import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SlidingPaneLayout;

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


public class MainActivity extends Fragment {

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



        /*//Get information from current user, if there is one.
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        String name = "", email = "", uID = null;
        Uri photoUrl;

        if (currentUser != null) {
            name = currentUser.getDisplayName();
            email = currentUser.getEmail();
            photoUrl = currentUser.getPhotoUrl();
            uID = currentUser.getUid();
        };

        //Access database
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference(uID).child("Point");


        String action_type = getIntent().getStringExtra("type");
        String notification = "";
        if (action_type != null) {
            if (action_type.equals("turn_off")) {
                incrementPointAndSaveToDb(true, 100);
                notification = "You gained 100 points";
            } else {
                incrementPointAndSaveToDb(false, 100);
                notification = "You lost 100 points";
            };
            Toast.makeText( this, notification,
                    Toast.LENGTH_LONG).show();
        }


*//*
        //Set information in the nav's header
        View header_view = navigationView.getHeaderView(0);
        final TextView nav_user = header_view.findViewById(R.id.user_name);*//*


        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                current_point = dataSnapshot.getValue(Integer.class);
                if (current_point != null) {
                    String point_display = String.valueOf(current_point);
              *//*      nav_user.setText(point_display);*//*
                    Log.e("", "onDataChange: "+current_point+"/"+point_display);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("", "Failed to read value.", error.toException());
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

    public void incrementPointAndSaveToDb(final boolean increment, final int point){
        myRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(final MutableData currentData) {
                if (currentData.getValue(Integer.class) == null) {
                    currentData.setValue(0);
                } else {
                    if (increment){
                         currentData.setValue(currentData.getValue(Integer.class) + point);
                    }
                    else{
                         currentData.setValue(currentData.getValue(Integer.class) - point);
                    }
                }

                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                if (databaseError != null) {
                    Log.d("Fail:","Firebase counter increment failed." + databaseError);
                } else {
                    Log.d("Success", "Increment successfully");
                }
            }
        });*/
    }
    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment*/
        View view = inflater.inflate(R.layout.activity_main, container, false);


        FloatingActionButton new_alarm_button = view.findViewById(R.id.add_new_alarm);
        new_alarm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "I clicked this button", Toast.LENGTH_LONG).show();
            }
        });

        //Handle navigation click events
        final DrawerLayout mDrawerLayout = view.findViewById(R.id.drawer_layout);

        NavigationView navigationView = view.findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        menuItem.setChecked(true);
                        // close drawer when item is tapped
                        mDrawerLayout.closeDrawers();

                        // Add code here to update the UI based on the item selected
                        // For example, swap UI fragments here

                        return true;
                    }
                });

        return view;
    }



}
