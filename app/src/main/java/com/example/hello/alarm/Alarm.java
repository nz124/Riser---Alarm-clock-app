package com.example.hello.alarm;

import android.util.Log;
import android.util.SparseArray;

import com.google.firebase.database.Exclude;

import java.time.Month;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Alarm {
    public int id, hour, durationInMillis, minute, date, month, year;
    SparseArray<String> month_dictionary = new SparseArray<>();

    public Alarm(){
    }

    public Alarm(int id, int durationInMillis, int hour, int minute, int date, int month, int year){
        this.id = id;
        this.hour = hour;
        this.durationInMillis = durationInMillis;
        this.minute = minute;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    @Exclude
    public Map<String, Object> toMapAlarm() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("durationInMillis", durationInMillis);
        result.put("year", year);
        result.put("hour", hour);
        result.put("minute", minute);
        result.put("date", date);
        result.put("month", month);
        return result;
    }

    @Exclude
    public String getTimeDisplay(){
        String minute = "00";
        if (this.minute < 10) {
            minute = "0" + this.minute;
        } else {
            minute = String.valueOf(this.minute);
        }
        return this.hour + ": " + minute;
    }

    @Exclude
    public String getDateDisplay(){

        month_dictionary.put(1, "Jan");
        month_dictionary.put(2, "Feb");
        month_dictionary.put(3, "Mar");
        month_dictionary.put(4, "Apr");
        month_dictionary.put(5, "May");
        month_dictionary.put(6, "Jun");
        month_dictionary.put(7, "Jul");
        month_dictionary.put(8, "Aug");
        month_dictionary.put(9, "Sep");
        month_dictionary.put(10, "Oct");
        month_dictionary.put(11, "Nov");
        month_dictionary.put(12, "Dec");

        return this.date + " " + month_dictionary.get(month + 1);
    }

    @Exclude
    public int getTimeInMillis(){
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(this.year, this.month, this.date, this.hour, this.minute);
        return (int) rightNow.getTimeInMillis();
    }

    @Exclude
    public int getDurationInMillis(){
        return this.durationInMillis;
    }

    @Exclude
    public void setId(int id){
        this.id = id;
    }

}
