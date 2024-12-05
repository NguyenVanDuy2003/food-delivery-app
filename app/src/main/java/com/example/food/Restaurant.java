package com.example.food;

import java.io.Serializable;

public class Restaurant implements Serializable {
    private  String id;
    private String name;
    private int imageResource;
    private float rating;
    private String deliveryTime;
    private  String phone_number;
    private String address;
    private  String stk;
    private String qrcode;
    private String imageUrl;
    private String Mota;

    private String qrcodeUrl;

    public String getQrcodeUrl() {
        return qrcodeUrl;
    }

    public void setQrcodeUrl(String qrcodeUrl) {
        this.qrcodeUrl = qrcodeUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public  Restaurant(){

    }
    public Restaurant(String name, int imageResource, float rating, String deliveryTime) {
        this.name = name;
        this.imageResource = imageResource;
        this.rating = rating;
        this.deliveryTime = deliveryTime;
    }

    public Restaurant(String name, String phone_number, String stk, String address) {
        this.name = name;
        this.phone_number = phone_number;
        this.stk = stk;
        this.address = address;
    }

    public Restaurant(String name, String phone_number, String address, String stk, String mota) {
        this.name = name;
        this.phone_number = phone_number;
        this.address = address;
        this.stk = stk;
        Mota = mota;
    }

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

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
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

    public String getQrcode() {
        return qrcode;
    }

    public void setQrcode(String qrcode) {
        this.qrcode = qrcode;
    }

    public String getMota() {
        return Mota;
    }

    public void setMota(String mota) {
        Mota = mota;
    }
}
