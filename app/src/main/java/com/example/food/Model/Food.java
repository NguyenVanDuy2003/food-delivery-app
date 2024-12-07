package com.example.food.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Food implements Serializable {

    private String name;
    private double price;
    private String id;  // Firebase key id
    private String restaurantID;
    private String imageUrl;  // Changed from int imageResource to String imageUrl
    private String ingredients;
    private boolean isAvailable;
    private List<String> toppings;  // List of toppings

    // Default constructor (required for Firebase)
    public Food() {
        this.toppings = new ArrayList<>();  // Initialize toppings as an empty list
    }

    // Constructor for initializing the fields
    public Food(String name, double price, String id, String restaurantID, String imageUrl, String ingredients, boolean isAvailable, List<String> toppings) {
        this.name = name;
        this.price = price;
        this.id = id;
        this.restaurantID = restaurantID;
        this.imageUrl = imageUrl;  // Set image URL instead of resource
        this.ingredients = ingredients;
        this.isAvailable = isAvailable;
        this.toppings = (toppings != null) ? toppings : new ArrayList<>();  // Ensure toppings is never null
    }

    // Constructor without id (for creating new Food without an id initially)
    public Food(String name, double price, String imageUrl, String restaurantId, String ingredients, boolean isAvailable, List<String> toppings) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.restaurantID = restaurantId;
        this.ingredients = ingredients;
        this.isAvailable = isAvailable;
        this.toppings = (toppings != null) ? toppings : new ArrayList<>();
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantID() {
        return restaurantID;
    }

    public void setRestaurantID(String restaurantID) {
        this.restaurantID = restaurantID;
    }

    public String getImageUrl() {  // Changed getter for imageUrl
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {  // Changed setter for imageUrl
        this.imageUrl = imageUrl;
    }

    public String getIngredients() {
        return ingredients;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public List<String> getToppings() {
        return toppings;
    }

    public void setToppings(List<String> toppings) {
        this.toppings = (toppings != null) ? toppings : new ArrayList<>();  // Prevent null list
    }
}
