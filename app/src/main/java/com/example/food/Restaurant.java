package com.example.food;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private String name;
    private int imageResource;
    private float rating;
    private String deliveryTime;

    public Restaurant(String name, int imageResource, float rating, String deliveryTime) {
        this.name = name;
        this.imageResource = imageResource;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
    }

    public String getName() { return name; }
    public int getImageResource() { return imageResource; }
    public float getRating() { return rating; }
    public String getDeliveryTime() { return deliveryTime; }
} 