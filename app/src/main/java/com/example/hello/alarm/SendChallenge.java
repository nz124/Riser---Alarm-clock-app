package com.example.hello.alarm;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.logging.Logger;

import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class SendChallenge extends Activity{
    DatabaseReference myRef;

    public static Intent createIntent(Context context, String user_id, String receiver_id ) {
        return new Intent(context, SendChallenge.class).putExtra("user_id", user_id)
                .putExtra("receiver_id", receiver_id);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge_layout);


        final String user_id = getIntent().getStringExtra("user_id");
        final String receiver_id = getIntent().getStringExtra("receiver_id");

        final Button pickPointButton = findViewById(R.id.pick_point_button);
        pickPointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myRef = FirebaseDatabase.getInstance().getReference();
                myRef.child(user_id).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int point = dataSnapshot.getValue(int.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                final Dialog d = new Dialog(SendChallenge.this);
                d.setContentView(R.layout.point_picker_dialog);
                Button challengeButton = d.findViewById(R.id.confirm_button);
                Button cancelButton = d.findViewById(R.id.cancel_button);
                final NumberPicker np = d.findViewById(R.id.number_picker);
                //Set maximum range based on user's current point
                myRef.child(user_id).child("point").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int userPoint = dataSnapshot.getValue(int.class);
                        np.setMinValue(0);   // min value 0
                        np.setMaxValue(userPoint); // max value == user's point
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                challengeButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        pickPointButton.setText(np.getValue() + " point");
                        d.dismiss();
                    }
                });
                cancelButton.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v) {
                        d.dismiss(); // dismiss the dialog
                    }
                });
                d.show();

            }
        });

        // TODO Auto-generated method stub
        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        final int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        final int month = mcurrentTime.get(Calendar.MONTH);
        final int year = mcurrentTime.get(Calendar.YEAR);

        final Button pickTimeButton = findViewById(R.id.pick_time_button);
        pickTimeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(SendChallenge.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        pickTimeButton.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        final Button pickDateButton = findViewById(R.id.pick_date_button);
        pickDateButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                DatePickerDialog mDatePicker;
                mDatePicker = new DatePickerDialog(SendChallenge.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        pickDateButton.setText(month + "/" + dayOfMonth);
                    }
                }, year, month, day);
                mDatePicker.setTitle("Select date");
                mDatePicker.show();
            }
        });

        final Button challengeButton = findViewById(R.id.challenge_button);
        challengeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Build an alert
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(SendChallenge.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SendChallenge.this);
                }
                builder.setTitle("Confirm")
                        .setMessage("You want to challenge " + receiver_id + " for " + pickPointButton.getText() + " to set an alarm at " + pickTimeButton.getText() + " on " + pickDateButton.getText()+ " and not snooze the alarm?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(SendChallenge.this, "You have challenged " + receiver_id, Toast.LENGTH_SHORT).show();
                                startActivity(MainActivity.createIntent(getApplicationContext(), null));
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(R.drawable.ic_challenge_icon)
                        .show();
            }
        });

    }
}
