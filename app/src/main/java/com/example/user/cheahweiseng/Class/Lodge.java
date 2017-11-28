package com.example.user.cheahweiseng.Class;


import java.util.Date;

/**
 * Created by USER on 2017/10/24.
 */

public class Lodge {
    private String lodgeID;
    private String lodgeTitle;
    private String lodgeStatus;
    private String lodgeDescription;
    private String lodgeLocation;
    private String lodgePrice;
    private String lodgeImage;
    private Date addedDate;
    private String lodgeProviderID;
    private String Longitude;
    private String Latitude;

    public Lodge(String lodgeID, String lodgeTitle, String lodgeStatus, String lodgeDescription, String lodgeLocation, String lodgePrice, String lodgeImage, Date addedDate, String lodgeProviderID, String longitude, String latitude) {
        this.lodgeID = lodgeID;
        this.lodgeTitle = lodgeTitle;
        this.lodgeStatus = lodgeStatus;
        this.lodgeDescription = lodgeDescription;
        this.lodgeLocation = lodgeLocation;
        this.lodgePrice = lodgePrice;
        this.lodgeImage = lodgeImage;
        this.addedDate = addedDate;
        this.lodgeProviderID = lodgeProviderID;
        Longitude = longitude;
        Latitude = latitude;
    }

    public Lodge(String lodgeID, String lodgeTitle, String lodgeStatus, String lodgeDescription, String lodgeLocation, String lodgePrice, String lodgeImage, Date addedDate, String lodgeProviderID) {
        this.lodgeID = lodgeID;
        this.lodgeTitle = lodgeTitle;
        this.lodgeStatus = lodgeStatus;
        this.lodgeDescription = lodgeDescription;
        this.lodgeLocation = lodgeLocation;
        this.lodgePrice = lodgePrice;
        this.lodgeImage = lodgeImage;
        this.addedDate = addedDate;
        this.lodgeProviderID = lodgeProviderID;
    }

    public Lodge(String lodgeID, String lodgeTitle, String lodgeDescription, String lodgePrice, String lodgeImage, String lodgeProviderID) {
        this.lodgeID = lodgeID;
        this.lodgeTitle = lodgeTitle;
        this.lodgeDescription = lodgeDescription;
        this.lodgePrice = lodgePrice;
        this.lodgeImage = lodgeImage;
        this.lodgeProviderID = lodgeProviderID;
    }

    public String getLodgeID() {
        return lodgeID;
    }

    public void setLodgeID(String lodgeID) {
        this.lodgeID = lodgeID;
    }

    public String getLodgeTitle() {
        return lodgeTitle;
    }

    public void setLodgeTitle(String lodgeTitle) {
        this.lodgeTitle = lodgeTitle;
    }

    public String getLodgeStatus() {
        return lodgeStatus;
    }

    public void setLodgeStatus(String lodgeStatus) {
        this.lodgeStatus = lodgeStatus;
    }

    public String getLodgeDescription() {
        return lodgeDescription;
    }

    public void setLodgeDescription(String lodgeDescription) {
        this.lodgeDescription = lodgeDescription;
    }

    public String getLodgeLocation() {
        return lodgeLocation;
    }

    public void setLodgeLocation(String lodgeLocation) {
        this.lodgeLocation = lodgeLocation;
    }

    public String getLodgePrice() {
        return lodgePrice;
    }

    public void setLodgePrice(String lodgePrice) {
        this.lodgePrice = lodgePrice;
    }

    public String getLodgeImage() {
        return lodgeImage;
    }

    public void setLodgeImage(String lodgeImage) {
        this.lodgeImage = lodgeImage;
    }

    public Date getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(Date addedDate) {
        addedDate = addedDate;
    }

    public String getLodgeProviderID() {
        return lodgeProviderID;
    }

    public void setLodgeProviderID(String lodgeProviderID) {
        this.lodgeProviderID = lodgeProviderID;
    }

    public String getLongitude() {
        return Longitude;
    }

    public void setLongitude(String longitude) {
        Longitude = longitude;
    }

    public String getLatitude() {
        return Latitude;
    }

    public void setLatitude(String latitude) {
        Latitude = latitude;
    }
}
