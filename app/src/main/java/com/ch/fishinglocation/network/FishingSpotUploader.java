package com.ch.fishinglocation.network;

import cn.leancloud.LCObject;
import cn.leancloud.types.LCGeoPoint;
import com.amap.api.maps.model.LatLng;
import com.ch.fishinglocation.bean.FishingSpot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;

public class FishingSpotUploader {

    // 上传钓点数据到服务器
    public static void uploadFishingSpot(FishingSpot spot, Observer<LCObject> observer) {
        // 创建LCObject实例，对应服务器上的FishingSpot类
        LCObject lcFishingSpot = new LCObject("FishingSpot");

        // 设置FishingSpot实体的属性
        lcFishingSpot.put("name", spot.getName());
        lcFishingSpot.put("description", spot.getDescription());
        lcFishingSpot.put("uploadedBy", spot.getUploadedBy());
        if(spot.getFirstSpot() != null ){
            LCGeoPoint lcGeoPoint = new LCGeoPoint();
            lcGeoPoint.setLatitude(spot.getFirstSpot().latitude);
            lcGeoPoint.setLongitude(spot.getFirstSpot().longitude);
            lcFishingSpot.put("firstSpot", lcGeoPoint);
        }
        // 设置钓点坐标
        List<LCGeoPoint> locationSpots = new ArrayList<>();
        for (LatLng latLng : spot.getSpots()) {
            locationSpots.add(new LCGeoPoint(latLng.latitude, latLng.longitude));
        }
        lcFishingSpot.put("spots", locationSpots);

        // 设置钓点范围
        List<List<LCGeoPoint>> lcRange = new ArrayList<>();
        for (List<LatLng> path : spot.getRange()) {
            List<LCGeoPoint> lcPath = new ArrayList<>();
            for (LatLng latLng : path) {
                lcPath.add(new LCGeoPoint(latLng.latitude, latLng.longitude));
            }
            lcRange.add(lcPath);
        }
        lcFishingSpot.put("range", lcRange);

        // 设置停车位置
        List<LCGeoPoint> lcParkingSpots = new ArrayList<>();
        for (LatLng latLng : spot.getParkingSpots()) {
            lcParkingSpots.add(new LCGeoPoint(latLng.latitude, latLng.longitude));
        }
        lcFishingSpot.put("parkingSpots", lcParkingSpots);

        // 设置步行路径
        List<List<LCGeoPoint>> lcWalkPaths = new ArrayList<>();
        for (List<LatLng> path : spot.getWalkPaths()) {
            List<LCGeoPoint> lcPath = new ArrayList<>();
            for (LatLng latLng : path) {
                lcPath.add(new LCGeoPoint(latLng.latitude, latLng.longitude));
            }
            lcWalkPaths.add(lcPath);
        }
        lcFishingSpot.put("walkPaths", lcWalkPaths);

        // 异步保存LCObject到LeanCloud
        lcFishingSpot.saveInBackground().subscribe(observer);
    }
}
