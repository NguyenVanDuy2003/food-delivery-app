package com.example.food.model;

public class User {
    private static int idCounter = 0; // Biến static để theo dõi id cuối cùng
    private int id;
    private String fullName;
    private String email;
    private String password;

    public User(){}

    // Constructor tự động gán id
    public User(String fullName, String email, String password) {
        this.id = ++idCounter;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
