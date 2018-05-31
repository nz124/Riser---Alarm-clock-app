package com.example.hello.alarm;

public class StoreItem {
    public String title, description;
    public int price, drawable;
    public boolean isOwned;

    public StoreItem(){
        //Empty constructor for database retrieval
    }

    public StoreItem(int drawable, String title, String description, int price, boolean isOwned){
        this.title = title;
        this.description = description;
        this.price = price;
        this.drawable = drawable;
        this.isOwned = isOwned;
    }

}
