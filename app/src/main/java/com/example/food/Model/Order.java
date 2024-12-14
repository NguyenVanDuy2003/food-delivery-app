package com.example.food.Model;

public class Order {
    private String orderId;
    private String dishName;
    private int quantity;
    private double pricePerDish;
    private String orderDate;
    private String ordererName;
    private String paymentMethod;

    public Order(){
    }

    public Order(String orderId, String dishName, int quantity, double pricePerDish, String orderDate, String ordererName, String paymentMethod) {
        this.orderId = orderId;
        this.dishName = dishName;
        this.quantity = quantity;
        this.pricePerDish = pricePerDish;
        this.orderDate = orderDate;
        this.ordererName = ordererName;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPricePerDish() {
        return pricePerDish;
    }

    public void setPricePerDish(double pricePerDish) {
        this.pricePerDish = pricePerDish;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrdererName() {
        return ordererName;
    }

    public void setOrdererName(String ordererName) {
        this.ordererName = ordererName;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", dishName='" + dishName + '\'' +
                ", quantity=" + quantity +
                ", pricePerDish=" + pricePerDish +
                ", orderDate='" + orderDate + '\'' +
                ", ordererName='" + ordererName + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                '}';
    }
}
