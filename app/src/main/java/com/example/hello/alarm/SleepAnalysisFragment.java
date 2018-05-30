package com.example.hello.alarm;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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

public class SleepAnalysisFragment extends Fragment implements View.OnClickListener {
    LineData lineData;
    FirebaseUser currentUser;
    DatabaseReference myRef;
    List<BarEntry> entries;
    Button showWeekChartButton;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        //TO-DO: TURN THAT "MAY" INTO SOETHING ELSE
        myRef = FirebaseDatabase.getInstance().getReference().child(currentUser.getUid()).child("Sleep Data").child("May");

        Utils.init(getContext());
        entries = new ArrayList<BarEntry>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.sleep_analysis_fragment_layout, container, false);

        showWeekChartButton = rootView.findViewById(R.id.week_button);
        showWeekChartButton.setOnClickListener(this);


        // l.setExtra(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        // l.setCustom(ColorTemplate.VORDIPLOM_COLORS, new String[] { "abc",
        // "def", "ghj", "ikl", "mno" });
        //// add entries and styles to dataset
//                LineDataSet dataSet = new LineDataSet(entries, "Time");
//
//                dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//                dataSet.setCubicIntensity(0.2f);
//                dataSet.setDrawFilled(true);
//                dataSet.setDrawCircles(false);
//                dataSet.setLineWidth(1.8f);
//                dataSet.setCircleRadius(4f);
//                dataSet.setCircleColor(android.R.color.white);
//                dataSet.setHighLightColor(Color.rgb(244, 117, 117));
//                dataSet.setColor(Color.WHITE);
//                dataSet.setFillColor(Color.WHITE);
//                dataSet.setFillAlpha(100);
//                dataSet.setDrawHorizontalHighlightIndicator(false);
//
//                lineData = new LineData(dataSet);
//
//                LineChart chart = rootView.findViewById(R.id.sleep_chart);
//                chart.setData(lineData);
//                chart.getXAxis().setDrawGridLines(false);
//                chart.invalidate(); // refresh
//                Log.e("hey", "onDataChange: "+"what happens second" );
//            }
//
//
//        });

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.week_button) {
            Log.e("haha", "showWeekChart: " + "I clicked");
            showWeekChart(rootView);
        }
    }

    public void showWeekChart(final View rootView) {
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int date;
                int hour;
                int minute;
                int hourAndMinuteValue;
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    date = Integer.valueOf(data.getKey());
                    minute = Math.round(data.getValue(long.class) / (1000 * 60) % 60);
                    hour = Math.round(data.getValue(long.class) / (1000 * 60 * 60) % 24);
                    hourAndMinuteValue = Math.round(hour + minute / 60 * 100);

                    entries.add(new BarEntry(date, hourAndMinuteValue));
                    Log.e("Hour and Minute", "onDataChange: " + hourAndMinuteValue);
                }

                // programmatically create a LineChart and set size
                BarChart chart = new BarChart(getContext());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        1000
                );

                params.setMargins(0, 300, 0, 0);

                chart.setLayoutParams(params);

                // get parent layout in xml
                RelativeLayout rl = rootView.findViewById(R.id.root);
                rl.addView(chart); // add the programmatically created chart

                chart.setDrawBarShadow(false);
                chart.setDrawValueAboveBar(true);

                chart.getDescription().setEnabled(false);

                // if more than 60 entries are displayed in the chart, no values will be
                // drawn
                chart.setMaxVisibleValueCount(8);

                // scaling can now only be done on x- and y-axis separately
                chart.setPinchZoom(false);

                chart.setDrawGridBackground(false);
                // mChart.setDrawYLabels(false);

                IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

                XAxis xAxis = chart.getXAxis();
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f); // only intervals of 1 day
                xAxis.setLabelCount(7);
                xAxis.setValueFormatter(xAxisFormatter);

                IAxisValueFormatter custom = new MyAxisValueFormatter();

                YAxis leftAxis = chart.getAxisLeft();
                leftAxis.setLabelCount(8, false);
                leftAxis.setValueFormatter(custom);
                leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
                leftAxis.setSpaceTop(15f);
                leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                YAxis rightAxis = chart.getAxisRight();
                rightAxis.setDrawGridLines(false);
                rightAxis.setLabelCount(8, false);
                rightAxis.setValueFormatter(custom);
                rightAxis.setSpaceTop(15f);
                rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

                Legend l = chart.getLegend();
                l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
                l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
                l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                l.setDrawInside(false);
                l.setForm(Legend.LegendForm.SQUARE);
                l.setFormSize(9f);
                l.setTextSize(11f);
                l.setXEntrySpace(4f);

                BarDataSet dataSet = new BarDataSet(entries, "Sleeping Time");
                BarData data = new BarData(dataSet);
                chart.setData(data);
                chart.invalidate();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}
//chart.setDrawBarShadow(false);
//        chart.setDrawValueAboveBar(true);
//
//        chart.getDescription().setEnabled(false);
//
//        // if more than 60 entries are displayed in the chart, no values will be
//        // drawn
//        chart.setMaxVisibleValueCount(60);
//
//        // scaling can now only be done on x- and y-axis separately
//        chart.setPinchZoom(false);
//
//        chart.setDrawGridBackground(false);
//        // mChart.setDrawYLabels(false);
//
//        IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);
//
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setGranularity(1f); // only intervals of 1 day
//        xAxis.setLabelCount(7);
//        xAxis.setValueFormatter(xAxisFormatter);
//
//        IAxisValueFormatter custom = new MyAxisValueFormatter();
//
//        YAxis leftAxis = chart.getAxisLeft();
//        leftAxis.setLabelCount(8, false);
//        leftAxis.setValueFormatter(custom);
//        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
//        leftAxis.setSpaceTop(15f);
//        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//
//        YAxis rightAxis = chart.getAxisRight();
//        rightAxis.setDrawGridLines(false);
//        rightAxis.setLabelCount(8, false);
//        rightAxis.setValueFormatter(custom);
//        rightAxis.setSpaceTop(15f);
//        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
//
//        Legend l = chart.getLegend();
//        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
//        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
//        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
//        l.setDrawInside(false);
//        l.setForm(Legend.LegendForm.SQUARE);
//        l.setFormSize(9f);
//        l.setTextSize(11f);
//        l.setXEntrySpace(4f);
//
//        BarDataSet dataSet = new BarDataSet(entries, "Sleeping Time");
//        BarData data = new BarData(dataSet);
//        chart.setData(data);
//        chart.invalidate();