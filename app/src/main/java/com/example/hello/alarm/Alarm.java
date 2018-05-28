package com.example.hello.alarm;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class Alarm {
    public boolean monday, tuesday, wednesday, thursday, friday, saturday, sunday, allday;
    public int id, hour, minute, date, month;

    public Alarm(){
    }

    public Alarm(int id, int hour, int minute, int date, int month){
        this.id = id;
        this.hour = hour;
        this.minute = minute;
        this.date = date;
        this.month = month;
    }

    @Exclude
    public Map<String, Object> toMapAlarm() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
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
}
