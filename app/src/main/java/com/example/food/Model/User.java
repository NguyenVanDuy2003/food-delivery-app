package com.example.food.Model;

import com.example.food.Enum.Role;

public class User {
    public String id;
    public String fullName;
    public String email;
    public String password;
    public String phoneNumber;
    public String address;
    public Role role;
    public String imageUser;

    public User(String address, String fullName, String phoneNumber, String id, String email) {
        this.address = address;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.id = id;
        this.email = email;
    }

    public User(String fullName, String id, String email, String phoneNumber, String address, String imageUser) {
        this.fullName = fullName;
        this.id = id;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.imageUser = imageUser;
    }


    public String getImageUser() {
        return imageUser;
    }

    public void setImageUser(String imageUser) {
        this.imageUser = imageUser;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public User(){

    }

    public User(String id, String fullName, String email, String password, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(String id, String fullName, String email, String password, String phoneNumber, String address, Role role) {
        this.id = id;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.role = role;
    }
}
