package com.example.hello.alarm;

import java.util.List;

public class User {
    public String name;
    public String user_id;
    public String photoUriString;
    public int point;
    public List<Alarm> alarms;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String photoUriString, int point, String user_id){
        this.name = name;
        this.photoUriString = photoUriString;
        this.point = point;
    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void addAlarm(Alarm alarm){
        this.alarms.add(alarm);
    }
}
