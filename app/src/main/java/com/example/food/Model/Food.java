package com.example.food.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Food implements Serializable {

    private String name;
    private double price;
    private int id;  // Firebase key id
    private int imageResource;
    private String ingredients;
    private boolean isAvailable;
    private List<String> toppings;  // List of toppings

    // Default constructor (required for Firebase)
    public Food() {
        this.toppings = new ArrayList<>();  // Initialize toppings as an empty list
    }

    // Constructor for initializing the fields
    public Food(String name, double price, int id, int imageResource, String ingredients, boolean isAvailable, List<String> toppings) {
        this.name = name;
        this.price = price;
        this.id = id;
        this.imageResource = imageResource;
        this.ingredients = ingredients;
        this.isAvailable = isAvailable;
        this.toppings = (toppings != null) ? toppings : new ArrayList<>();  // Ensure toppings is never null
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
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
