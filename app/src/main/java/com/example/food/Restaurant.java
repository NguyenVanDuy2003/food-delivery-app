package com.example.food;

import java.io.Serializable;
import java.util.List;

import com.example.food.Model.Food;

public class Restaurant implements Serializable {
    private String id;
    private String name;
    private int imageResource;
    private float rating;
    private String deliveryTime;
    private String phoneNumber;
    private String address;
    private String stk;
    private String imageUrl;
    private String description;
    private List<Food> menu;
    private String qrcodeUrl;

    // Default constructor
    public Restaurant() {
    }

    // Constructor with essential details
    public Restaurant(String name, int imageResource, float rating, String deliveryTime) {
        this.name = name;
        this.imageResource = imageResource;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
    }

    // Constructor with full details
    public Restaurant(String id, String name, float rating, String phoneNumber, String address, String stk, String description, List<Food> menu) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.stk = stk;
        this.description = description;
        this.menu = menu;
    }

    // Constructor for restaurants without a menu
    public Restaurant(String id, String name, float rating, String phoneNumber, String address, String stk, String description) {
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.stk = stk;
        this.description = description;
    }

    // Constructor for initializing with contact and address details
    public Restaurant(String name, String phoneNumber, String stk, String address) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.stk = stk;
        this.address = address;
    }

    // Constructor for initializing with address and description
    public Restaurant(String name, String phoneNumber, String address, String stk, String description) {
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.stk = stk;
        this.description = description;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getStk() {
        return stk;
    }

    public void setStk(String stk) {
        this.stk = stk;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Food> getMenu() {
        return menu;
    }

    public void setMenu(List<Food> menu) {
        this.menu = menu;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    @Override
    public String toString() {
        return "Restaurant{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", rating=" + rating +
                ", deliveryTime='" + deliveryTime + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", address='" + address + '\'' +
                ", stk='" + stk + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", description='" + description + '\'' +
                ", qrcodeUrl='" + qrcodeUrl + '\'' +
                ", menu=" + menu +
                '}';
    }
}
