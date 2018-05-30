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
    public String[] monthString = {
            "Jan", "Feb", "Mar", "Wed", "Thu", "Fri", "Sat", "Sun"
    };

    public Alarm(){
    }

    public Alarm(int id, int durationInMillis, int hour, int minute, int date, int month, int year){
        this.id = id;
        this.hour = hour;
        this.durationInMillis = durationInMillis;
        this.minute = minute;
        this.date = date;
        this.month = month + 1;
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

    public String getYear(){
        return Integer.toString(this.year);
    }

    public String getMonth(){
        return Integer.toString(this.month);
    }

    @Exclude
    public String getDate(){
        return Integer.toString(this.date);
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
