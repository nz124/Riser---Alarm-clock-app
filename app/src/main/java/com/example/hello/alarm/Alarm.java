package com.example.hello.alarm;

import com.google.firebase.database.Exclude;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Alarm {
    public int id, hour, minute, date, month, year;

    public Alarm(){
    }

    public Alarm(int id, int hour, int minute, int date, int month, int year){
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.date = date;
        this.month = month;
        this.year = year;
    }

    @Exclude
    public Map<String, Object> toMapAlarm() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
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
    public long getTimeInMillis(){
        Calendar rightNow = Calendar.getInstance();
        rightNow.set(this.year, this.month, this.date, this.hour, this.minute);
        return rightNow.getTimeInMillis();
    }
}
