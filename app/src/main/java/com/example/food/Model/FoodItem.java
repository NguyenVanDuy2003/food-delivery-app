package com.example.food.Model;

public class FoodItem {
    private String title;
        private double rating;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    private String deliveryTime;
        private int imageResource;

        public FoodItem(String title, double rating, String deliveryTime, int imageResource) {
            this.title = title;
            this.rating = rating;
            this.deliveryTime = deliveryTime;
            this.imageResource = imageResource;
        }

        // Getter methods
        public String getTitle() { return title; }
        public double getRating() { return rating; }
        public String getDeliveryTime() { return deliveryTime; }
        public int getImageResource() { return imageResource; }
    }

