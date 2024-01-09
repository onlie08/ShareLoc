package com.ch.fishinglocation.bean;

import com.amap.api.maps.model.LatLng;

import java.util.List;

public class FishingSpot {
    private String id; // 钓点的唯一标识符
    private String name; // 钓点名称
    private String description; // 钓点描述
    private LatLng location; // 钓点坐标
    private List<LatLng> range; // 钓点范围，一系列的坐标点构成一个多边形
    private List<LatLng> parkingSpots; // 停车位置，可能有多个停车点
    private List<List<LatLng>> walkPaths; // 步行路径，每个路径是由多个坐标点构成的列表
    private String uploadedBy; // 上传钓点的用户的标识符

    // 省略构造函数、getter和setter方法

    public FishingSpot() {
        // 默认构造函数
    }

    // 构造函数，参数初始化
    public FishingSpot(String id, String name, String description, LatLng location,
                       List<LatLng> range, List<LatLng> parkingSpots,
                       List<List<LatLng>> walkPaths, String uploadedBy) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.location = location;
        this.range = range;
        this.parkingSpots = parkingSpots;
        this.walkPaths = walkPaths;
        this.uploadedBy = uploadedBy;
    }

    // Getter和Setter方法
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

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public List<LatLng> getRange() {
        return range;
    }

    public void setRange(List<LatLng> range) {
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
}

