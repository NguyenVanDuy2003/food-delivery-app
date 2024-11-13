package com.example.food.model;

public class Food {
    private String name;
    private double rating;
    private int reviewCount;
    private String restaurantName;
    private double price;
    private int imageResource;
    private boolean isFavorite;

    public Food(String name, double rating, int reviewCount, String restaurantName, 
               double price, int imageResource) {
        this.name = name;
        this.rating = rating;
        this.reviewCount = reviewCount;
        this.restaurantName = restaurantName;
        this.price = price;
        this.imageResource = imageResource;
        this.isFavorite = false;
    }

    // Getters
    public String getName() { return name; }
    public double getRating() { return rating; }
    public int getReviewCount() { return reviewCount; }
    public String getRestaurantName() { return restaurantName; }
    public double getPrice() { return price; }
    public int getImageResource() { return imageResource; }
    public boolean isFavorite() { return isFavorite; }

    // Setters
    public void setFavorite(boolean favorite) { isFavorite = favorite; }
} 