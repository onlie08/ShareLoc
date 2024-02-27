package com.ch.fishinglocation.bean;

public class User {
    private String username;
    private String phone;
    private boolean isVIP;
    private String info;
    private Object authData;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isVIP() {
        return isVIP;
    }

    public void setVIP(boolean VIP) {
        isVIP = VIP;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Object getAuthData() {
        return authData;
    }

    public void setAuthData(Object authData) {
        this.authData = authData;
    }
}
