package com.example.hello.alarm;

public class User {
    public String name;
    public String photoUriString;
    public int point;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String photoUriString, int point){
        this.name = name;
        this.photoUriString = photoUriString;
        this.point = point;
    }

}
