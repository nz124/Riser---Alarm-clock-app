package com.example.hello.alarm;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.Utils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SleepAnalysisFragment extends Fragment {
    LineData lineData;
    FirebaseUser currentUser;
    DatabaseReference myRef;
    List<Entry> entries;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //TO-DO: TURN THAT "MAY" INTO SOETHING ELSE
        myRef = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()).child("Sleep Data").child("May");

        Utils.init(getContext());
        entries = new ArrayList<Entry>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.sleep_analysis_fragment_layout, container, false);

        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int date;
                int hour;
                int minute;
                int hourAndMinuteValue;
                for (DataSnapshot data : dataSnapshot.getChildren()){
                    date = Integer.valueOf(data.getKey());
                    minute = Math.round(data.getValue(long.class) / (1000*60) % 60);
                    hour   = Math.round(data.getValue(long.class) / (1000*60*60) % 24);
                    hourAndMinuteValue = Math.round(hour +  minute/60*100);

                    entries.add(new Entry(date, hourAndMinuteValue));
                    Log.e("Hour and Minute", "onDataChange: "+ hourAndMinuteValue );
                }
                //// add entries and styles to dataset
                LineDataSet dataSet = new LineDataSet(entries, "Time");

                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
                dataSet.setCubicIntensity(0.2f);
                dataSet.setDrawFilled(true);
                dataSet.setDrawCircles(false);
                dataSet.setLineWidth(1.8f);
                dataSet.setCircleRadius(4f);
                dataSet.setCircleColor(android.R.color.white);
                dataSet.setHighLightColor(Color.rgb(244, 117, 117));
                dataSet.setColor(Color.WHITE);
                dataSet.setFillColor(Color.WHITE);
                dataSet.setFillAlpha(100);
                dataSet.setDrawHorizontalHighlightIndicator(false);

                lineData = new LineData(dataSet);

                LineChart chart = rootView.findViewById(R.id.sleep_chart);
                chart.setData(lineData);
                chart.getXAxis().setDrawGridLines(false);
                chart.invalidate(); // refresh
                Log.e("hey", "onDataChange: "+"what happens second" );
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return rootView;
    }
}
