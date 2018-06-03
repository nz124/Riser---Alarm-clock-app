package com.example.hello.alarm;

import java.util.HashMap;
import java.util.List;

public class User {
    public String name;
    public String user_id;
    public String photoUriString;
    public int point;
    public List<Alarm> alarms;
    public HashMap<String, Integer> items;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String photoUriString, int point, String user_id){
        this.name = name;
        this.photoUriString = photoUriString;
        this.point = point;
        this.user_id = user_id;
        this.items = new HashMap<>();
    }

    public String getName() {
        return this.name;
    }

    public int getPoint() {
        return this.point;
    }

    public String getId(){
        return this.user_id;
    }

    public void addItem(String item, Integer value){
        this.items.put(item, value);
    }

    public Integer getItem(String itemName){
        if (this.items != null){
            return this.items.get(itemName);
        } else {
            return null;
        }

    }

    public List<Alarm> getAlarms() {
        return alarms;
    }

    public void addAlarm(Alarm alarm){
        this.alarms.add(alarm);
    }
}
