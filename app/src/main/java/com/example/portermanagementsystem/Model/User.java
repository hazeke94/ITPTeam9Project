package com.example.portermanagementsystem.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String staffID;
    public String name;
    public String gender;
    public String role;
    public String email;
    public String contact;
    public String fcmtoken;
    public String status;

    public User() { }

    public User(String staffID, String name, String gender, String contact, String role, String fcmtoken, String status, String email) {
        this.staffID = staffID;
        this.role=role;
        this.name = name;
        this.contact = contact;
        this.gender = gender;
        this.fcmtoken = fcmtoken;
        this.status = status;
        this.email = email;
    }

    public String getName(){
        return this.name;
    }

    public String getGender(){
        return this.gender;
    }

    public String getStatus(){return this.status;}

    public void setName(String name) {
        this.name = name;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setStatus(String status){this.status = status;}

    public String getStaffID() {
        return staffID;
    }

    public void setStaffID(String staffID) {
        this.staffID = staffID;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getFCMToken() {
        return this.fcmtoken;
    }

    public void setFCMToken(String fcmtoken) {
        this.fcmtoken = fcmtoken;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getContact() {
        return this.contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }
}

