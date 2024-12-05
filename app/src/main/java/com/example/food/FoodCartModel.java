package com.example.food;

public class FoodCartModel {
    private String name;
    private String ingredient;
    private int quantity;
    private double price;
    private int imgId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(0, quantity);
    }

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = Math.max(0, price);
    }

    public FoodCartModel(String name, String ingredient, double price, int quantity, int imgId) {
        this.setName(name);
        this.setIngredient(ingredient);
        this.setPrice(price);
        this.setQuantity(quantity);
        this.setImgId(imgId);
    }
}
