package com.ch.fishinglocation.ui.view;

import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.Polyline;

public class MapElementStyleManager {

    // 应用Marker样式配置
    public static void applyMarkerStyle(Marker marker, MapElementStyleConfig.MarkerStyle styleConfig) {
        if (marker != null && styleConfig != null) {
            marker.setIcon(styleConfig.icon);
            marker.setAnchor(styleConfig.anchorU, styleConfig.anchorV);
        }
    }

    // 应用Polygon样式配置
    public static void applyPolygonStyle(Polygon polygon, MapElementStyleConfig.PolygonStyle styleConfig) {
        if (polygon != null && styleConfig != null) {
            polygon.setStrokeColor(styleConfig.strokeColor);
            polygon.setStrokeWidth(styleConfig.strokeWidth);
            polygon.setFillColor(styleConfig.fillColor);
        }
    }

    // 应用Polyline样式配置
    public static void applyPolylineStyle(Polyline polyline, MapElementStyleConfig.PolylineStyle styleConfig) {
        if (polyline != null && styleConfig != null) {
            polyline.setColor(styleConfig.color);
            polyline.setWidth(styleConfig.width);
            polyline.setCustomTexture(styleConfig.texture);
        }
    }

    // ... 更多样式应用方法 ...
}
