package com.ch.fishinglocation.network;

import cn.leancloud.LCObject;
import cn.leancloud.LCQuery;
import cn.leancloud.types.LCGeoPoint;

import com.amap.api.maps.model.LatLng;
import com.ch.fishinglocation.bean.FishingSpot;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observer;

public class FishingSpotService {

    // 根据中心点坐标和半径查询附近的钓点
    public static void getFishingSpots(LatLng center, double radius, Observer<List<LCObject>> observer) {
        LCGeoPoint centerPoint = new LCGeoPoint(center.latitude, center.longitude);
        LCQuery<LCObject> query = new LCQuery<>("FishingSpot");
        query.whereWithinKilometers("location", centerPoint, radius);
        query.findInBackground().subscribe(observer);
    }

    // 转换函数，从LCObject到FishingSpot实体
    public static List<FishingSpot> convertToEntityList(List<LCObject> lcObjects) {
        List<FishingSpot> fishingSpots = new ArrayList<>();
        for (LCObject lcObject : lcObjects) {
            FishingSpot spot = new FishingSpot();
            spot.setId(lcObject.getObjectId());
            spot.setName(lcObject.getString("name"));
            spot.setDescription(lcObject.getString("description"));

            LCGeoPoint lcGeoPoint = lcObject.getLCGeoPoint("location");
            spot.setLocation(new LatLng(lcGeoPoint.getLatitude(), lcGeoPoint.getLongitude()));
            // 转换range字段
            List<LatLng> range = new ArrayList<>();
            List<Object> lcRangeList = lcObject.getList("range");
            if (lcRangeList != null) {
                for (Object obj : lcRangeList) {
                    if (obj instanceof LCGeoPoint) {
                        LCGeoPoint geoPoint = (LCGeoPoint) obj;
                        range.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                    }
                }
            }
            spot.setRange(range);

            // 转换parkingSpots字段
            List<LatLng> parkingSpots = new ArrayList<>();
            List<Object> lcParkingSpotsList = lcObject.getList("parkingSpots");
            if (lcParkingSpotsList != null) {
                for (Object obj : lcParkingSpotsList) {
                    if (obj instanceof LCGeoPoint) {
                        LCGeoPoint geoPoint = (LCGeoPoint) obj;
                        parkingSpots.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                    }
                }
            }
            spot.setParkingSpots(parkingSpots);

            // 转换walkPaths字段
            List<List<LatLng>> walkPaths = new ArrayList<>();
            List<Object> lcWalkPathsList = lcObject.getList("walkPaths");
            if (lcWalkPathsList != null) {
                for (Object pathObj : lcWalkPathsList) {
                    if (pathObj instanceof List<?>) {
                        List<?> pathList = (List<?>) pathObj;
                        List<LatLng> path = new ArrayList<>();
                        for (Object pointObj : pathList) {
                            if (pointObj instanceof LCGeoPoint) {
                                LCGeoPoint geoPoint = (LCGeoPoint) pointObj;
                                path.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
                            }
                        }
                        if (!path.isEmpty()) {
                            walkPaths.add(path);
                        }
                    }
                }
            }
            spot.setWalkPaths(walkPaths);

            fishingSpots.add(spot);
        }
        return fishingSpots;
    }
}
