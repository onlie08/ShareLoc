package com.ch.fishinglocation.ui.view;

import com.amap.api.maps.model.BitmapDescriptor;

public class MapElementStyleConfig {
    // Marker样式属性
    public static class MarkerStyle {
        public BitmapDescriptor icon;
        public float anchorU;
        public float anchorV;

        public MarkerStyle(BitmapDescriptor icon, float anchorU, float anchorV) {
            this.icon = icon;
            this.anchorU = anchorU;
            this.anchorV = anchorV;
        }
    }

    // Polygon样式属性
    public static class PolygonStyle {
        public int strokeColor;
        public int strokeWidth;
        public int fillColor;

        public PolygonStyle(int strokeColor, int strokeWidth, int fillColor) {
            this.strokeColor = strokeColor;
            this.strokeWidth = strokeWidth;
            this.fillColor = fillColor;
        }
    }

    // Polyline样式属性
    public static class PolylineStyle {
        public int color;
        public float width;
        public BitmapDescriptor texture;

        public PolylineStyle(int color, float width, BitmapDescriptor texture) {
            this.color = color;
            this.width = width;
            this.texture = texture;
        }
    }

    // ... 更多样式属性和构造方法 ...
}

