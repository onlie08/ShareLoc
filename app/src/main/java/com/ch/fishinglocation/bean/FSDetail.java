package com.ch.fishinglocation.bean;

import com.amap.api.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class FSDetail {
    private String detailId; // 钓点的唯一标识符
    private String spotId; // 钓点名称
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
}
