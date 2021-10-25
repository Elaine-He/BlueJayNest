package com.example.teamd_donationapp;

import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;
import java.util.ArrayList;

public class User implements Serializable {
    private String uid;
    private String email; // email will be the id for the dbase
    private String userName;
    private String profileText;
    private String profilePicture;
    private ArrayList<String> claimedItems;
    private int itemsDonated;
    private int itemsReceived;

    public User(){

    }
    public User(String uid, String email, String userName) {
        this.uid = uid;
        this.email = email;
        this.userName = userName;
        this.profileText = "";
        this.itemsDonated = 0;
        this.itemsReceived = 0;
        this.claimedItems = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return email.equals(user.email);
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }


    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String id) {
        this.uid = id;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getProfileText() {
        return profileText;
    }

    public void setProfileText(String profileText) {
        this.profileText = profileText;
    }

    public int getItemsDonated() {
        return itemsDonated;
    }

    public void setItemsDonated(int itemsDonated) {
        this.itemsDonated = itemsDonated;
    }

    public int getItemsReceived() {
        return itemsReceived;
    }

    public void setItemsReceived(int itemsReceived) {
        this.itemsReceived = itemsReceived;
    }
}