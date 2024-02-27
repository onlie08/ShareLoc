package com.ch.fishinglocation.bean;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FishingSpot {
    private String id; // 钓点的唯一标识符
    private String name; // 钓点名称
    private String description; // 钓点描述
    private LatLng firstSpot; // 第一个钓点
    /**
     * type 1:黑坑 2:收费野钓 3:免费野钓
     */
    private int type;
    private List<LatLng> spots = new ArrayList<>(); // 钓点坐标集合
    private List<List<LatLng>> range = new ArrayList<>(); // 钓点范围，一系列的坐标点构成一个多边形
    private List<LatLng> parkingSpots = new ArrayList<>(); // 停车位置，可能有多个停车点
    private List<List<LatLng>> walkPaths = new ArrayList<>(); // 步行路径，每个路径是由多个坐标点构成的列表
    private String uploadedBy; // 上传钓点的用户的标识符

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LatLng getFirstSpot() {
        return firstSpot;
    }

    public void setFirstSpot(LatLng firstSpot) {
        this.firstSpot = firstSpot;
    }

    public List<LatLng> getSpots() {
        return spots;
    }

    public void setSpots(List<LatLng> spots) {
        this.spots = spots;
    }

    public List<List<LatLng>> getRange() {
        return range;
    }

    public void setRange(List<List<LatLng>> range) {
        this.range = range;
    }

    public List<LatLng> getParkingSpots() {
        return parkingSpots;
    }

    public void setParkingSpots(List<LatLng> parkingSpots) {
        this.parkingSpots = parkingSpots;
    }

    public List<List<LatLng>> getWalkPaths() {
        return walkPaths;
    }

    public void setWalkPaths(List<List<LatLng>> walkPaths) {
        this.walkPaths = walkPaths;
    }

    public String getUploadedBy() {
        return uploadedBy;
    }

    public void setUploadedBy(String uploadedBy) {
        this.uploadedBy = uploadedBy;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}

