package com.example.pruebamenu.Models;

public class userModel {
    String userID, userName, userPhone, userDir, userCity, userCP, cityID;
    boolean userStatus;

    public userModel() {
    }

    public userModel(String userID, String userName, String userPhone, String userDir, String userCity, String userCP, String cityID, boolean userStatus) {
        this.userID = userID;
        this.userName = userName;
        this.userPhone = userPhone;
        this.userDir = userDir;
        this.userCity = userCity;
        this.userCP = userCP;
        this.cityID = cityID;
        this.userStatus = userStatus;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserDir() {
        return userDir;
    }

    public void setUserDir(String userDir) {
        this.userDir = userDir;
    }

    public String getUserCity() {
        return userCity;
    }

    public void setUserCity(String userCity) {
        this.userCity = userCity;
    }

    public String getUserCP() {
        return userCP;
    }

    public void setUserCP(String userCP) {
        this.userCP = userCP;
    }

    public String getCityID() {
        return cityID;
    }

    public void setCityID(String cityID) {
        this.cityID = cityID;
    }

    public boolean isUserStatus() {
        return userStatus;
    }

    public void setUserStatus(boolean userStatus) {
        this.userStatus = userStatus;
    }
}
