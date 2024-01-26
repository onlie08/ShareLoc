package com.ch.fishinglocation.utils;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.Polyline;

import java.util.HashMap;
import java.util.Map;

public class MapElementManager {
    private AMap aMap;
    private final Map<String, Marker> markerMap;
    private final Map<String, Polygon> polygonMap;
    private final Map<String, Polyline> polylineMap;

    public MapElementManager(AMap aMap) {
        this.aMap = aMap;
        markerMap = new HashMap<>();
        polygonMap = new HashMap<>();
        polylineMap = new HashMap<>();
    }

    public void addMarker(String id, Marker marker) {
        removeMarker(id); // Remove any existing marker with the same id
        markerMap.put(id, marker);
    }

    public void removeMarker(String id) {
        Marker marker = markerMap.remove(id);
        if (marker != null) {
            marker.remove();
        }
    }

    public void addPolygon(String id, Polygon polygon) {
        removePolygon(id); // Remove any existing polygon with the same id
        polygonMap.put(id, polygon);
    }

    public void removePolygon(String id) {
        Polygon polygon = polygonMap.remove(id);
        if (polygon != null) {
            polygon.remove();
        }
    }

    public void addPolyline(String id, Polyline polyline) {
        removePolyline(id); // Remove any existing polyline with the same id
        polylineMap.put(id, polyline);
    }

    public void removePolyline(String id) {
        Polyline polyline = polylineMap.remove(id);
        if (polyline != null) {
            polyline.remove();
        }
    }

    public void clearAll() {
        // Remove and clear all markers
        for (Marker marker : markerMap.values()) {
            marker.remove();
        }
        markerMap.clear();

        // Remove and clear all polygons
        for (Polygon polygon : polygonMap.values()) {
            polygon.remove();
        }
        polygonMap.clear();

        // Remove and clear all polylines
        for (Polyline polyline : polylineMap.values()) {
            polyline.remove();
        }
        polylineMap.clear();
    }
}

