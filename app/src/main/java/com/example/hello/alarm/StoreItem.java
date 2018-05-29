package com.example.hello.alarm;

public class StoreItem {
    public String imageUriString, title, description;
    public int price;

    public StoreItem(){
        //Empty constructor for database retrieval
    }

    public StoreItem(String imageUriString, String title, String description, int price){
        this.imageUriString = imageUriString;
        this.title = title;
        this.description = description;
    }

}
