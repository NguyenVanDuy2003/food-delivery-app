package com.example.food.Model;

import java.io.Serializable;
import java.util.Date;

public class Food implements Serializable {

    private String name;
    private double price;
    private int id;  // Firebase key id
    private int imageResource;
    private String ingredients;
    private boolean isAvailable;
//    private Date dateAdded;
    private int quantity;

    // Default constructor (required for Firebase)
    public Food() {
    }

    // Constructor for initializing the fields
    public Food(String name, double price, int id, int imageResource, String ingredients, boolean isAvailable, int quantity) {
        this.name = name;
        this.price = price;
        this.id = id;
        this.imageResource = imageResource;
        this.ingredients = ingredients;
        this.isAvailable = isAvailable;
//        this.dateAdded = dateAdded;
        this.quantity = quantity;
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

//    public Date getDateAdded() {
//        return dateAdded;
//    }
//
//    public void setDateAdded(Date dateAdded) {
//        this.dateAdded = dateAdded;
//    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
