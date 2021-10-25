package com.example.teamd_donationapp;


import java.lang.String;
import java.io.Serializable;

/**
 * Holds data for one item
 */
public class Item implements Serializable {
    private String name;
    private String description;
    private String location;
    private String category;
    private String mImageURL;
    private String status;
    private String postedBy;
    private String claimedBy;
    private String id; // database id
    private String accepted;
    public Item () {

    }
    public Item(String name, String description, String category, String location, String mImageURL, String postedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.category = category;
        this.status = "posted";
        this.mImageURL = mImageURL;
        this.postedBy = postedBy;
        this.claimedBy = null;
        this.accepted = "false";
    }

    public String getId() {
        return id;
    }

    public String isAccepted(){
        return accepted;
    }
    public void setAccepted() {
        this.accepted = "true";
    }
    public void setId(String id) {
        this.id = id;
    }

    public String getName() { return this.name; }
    public void setName(String name) { this.name = name; }

    public String getClaimedBy() { return this.claimedBy; }

    public void setClaimedBy(String s) { this.claimedBy = s; }

    public String getPostedBy() { return this.postedBy; }


    public void setPostedBy(String postedBy) {
        this.postedBy = postedBy;
    }
    public  String getImageURL() {
        return this.mImageURL;
    }

    public String getDescription() { return this.description; }

    public String getLocation() { return this.location; }

    public String getCategory() { return this.category; }

    public String getStatus() { return this.status; }

    public void setImageURL(String imURL) { this.mImageURL = imURL; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getmImageURL() {
        return mImageURL;
    }

    public void setmImageURL(String mImageURL) {
        this.mImageURL = mImageURL;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}

