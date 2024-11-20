package com.example.food.Model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class food implements Serializable {
    String nameFood;
    double price;
    int foodID;
    int image;
    int restaurantID;
    String description;
    boolean available;
    Date createdAt;


    public food(){

    }
    public food(String nameFood, Date createdAt, boolean available, double price, String description, int restaurantID, int foodID, int image) {
        this.nameFood = nameFood;
        this.createdAt = createdAt;
        this.available = available;
        this.price = price;
        this.description = description;
        this.restaurantID = restaurantID;
        this.foodID = foodID;
        this.image = image;
    }

    public String getNameFood() {
        return nameFood;
    }

    public void setNameFood(String nameFood) {
        this.nameFood = nameFood;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getFoodID() {
        return foodID;
    }

    public void setFoodID(int foodID) {
        this.foodID = foodID;
    }

    public int getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(int restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String desciption) {
        this.description = desciption;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("nameFood", nameFood);
        map.put("price", price);
        map.put("foodID", foodID);
        map.put("image", image);
        map.put("restaurantID", restaurantID);
        map.put("description", description);
        map.put("available", available);
        map.put("createdAt", createdAt);
        return map;
    }
}
