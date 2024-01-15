package com.ch.fishinglocation.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.PixelCopy;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.ch.fishinglocation.R;
import com.ch.fishinglocation.bean.FishingSpot;
import com.ch.fishinglocation.ui.view.DrawingView;
import com.ch.fishinglocation.ui.view.MagnifierView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DataCollectionActivity extends AppCompatActivity {
    private String TAG = this.getClass().getSimpleName();
    private FishingSpot fishingSpot;

    private static final int REQUEST_CODE_LOCATION_SELECTION = 1;
    private EditText editTextName;
    private EditText editTextDescription;
    private Button buttonSubmit;
    private Button btnAddSpot;
    private Button btnAddParking;
    private Button btnAddPloygon;
    private Button btnAddWalking;
    private Button buttonToggleLayer;
    private DrawingView drawingView;
    private MapView mMapView;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private AMap aMap;
    private boolean isFirstLocate = true; // 用于标记是否是首次定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private int SPOT_MARKER = 1;
    private int PARKING_MARKER = 2;

    private List<Marker> spotMarkerList = new ArrayList<>();
    private List<Marker> parkingMarkerList = new ArrayList<>();

    private MagnifierView magnifierView;
    private ConstraintLayout rootView; // 用于放置放大镜的根布局


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        aMap = mMapView.getMap();
        mMapView.setDrawingCacheEnabled(true);
        mMapView.buildDrawingCache(true);
        initView();
        initData();
    }

    private void initView() {
        drawingView = findViewById(R.id.drawingView);
        editTextName = findViewById(R.id.editName);
        editTextDescription = findViewById(R.id.editDescription);
        btnAddSpot = findViewById(R.id.btnAddSpot);
        btnAddParking = findViewById(R.id.btnAddParking);
        btnAddPloygon = findViewById(R.id.btnAddPloygon);
        btnAddWalking = findViewById(R.id.btnAddWalking);
        buttonToggleLayer = findViewById(R.id.buttonToggleLayer);
        buttonToggleLayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleMapLayer();
            }
        });
        btnAddSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMarkerToMap(SPOT_MARKER);
            }
        });
        btnAddParking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                addMarkerToMap(PARKING_MARKER);
                if (drawingView.getVisibility() == View.GONE) {
                    // 启动绘图模式
                    drawingView.setVisibility(View.VISIBLE);
                    aMap.getUiSettings().setAllGesturesEnabled(false);
                    drawingView.setProjection(aMap.getProjection()); // 设置Projection对象
                } else {
                    // 结束绘图模式并获取点
                    drawingView.setVisibility(View.GONE);
                    aMap.getUiSettings().setAllGesturesEnabled(true);
                    List<LatLng> latLngs = drawingView.getPoints();
                    // 在地图上绘制多边形
                    if (latLngs != null && latLngs.size() > 1) {
                        aMap.addPolygon(new PolygonOptions()
                                .addAll(latLngs)
                                .fillColor(0x0D00FF00)
                                .strokeColor(0x3300FF00)
                                .strokeWidth(5));
                    }
                    drawingView.clear();
                }
            }
        });

        buttonSubmit = findViewById(R.id.buttonSubmit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Collect data and submit
                String name = editTextName.getText().toString();
                String description = editTextDescription.getText().toString();
                // Other data collected from the second activity should be added to the FishingSpot object
                // Then, you can upload the FishingSpot using FishingSpotUploader
            }
        });

        rootView = findViewById(R.id.constraint); // 假设你有一个 FrameLayout 作为根布局
        magnifierView = new MagnifierView(this, 200, 2.0f); // 创建放大镜视图
        ConstraintLayout.LayoutParams lp = new ConstraintLayout.LayoutParams(200, 200);
        rootView.addView(magnifierView, lp); // 添加放大镜视图
        magnifierView.setVisibility(View.VISIBLE); // 初始设置为隐藏

        aMap.setOnMapTouchListener(new AMap.OnMapTouchListener() {
            @Override
            public void onTouch(MotionEvent event) {
                // 在这里处理触摸事件，如显示放大镜
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
//                    showMagnifier(event);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    magnifierView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

//    private void captureMapScreen(final MagnifierView magnifierView, final MotionEvent event) {
//        final Bitmap bitmap = Bitmap.createBitmap(mMapView.getWidth(), mMapView.getHeight(), Bitmap.Config.ARGB_8888);
//        PixelCopy.request(mMapView, bitmap, new PixelCopy.OnPixelCopyFinishedListener() {
//            @Override
//            public void onPixelCopyFinished(int result) {
//                if (result == PixelCopy.SUCCESS) {
//                    // 在这里处理 Bitmap，例如显示在放大镜中
//                    showMagnifier(event, bitmap, magnifierView);
//                } else {
//                    // 处理失败情况
//                }
//            }
//        }, new Handler(Looper.getMainLooper()));
//    }

//    private void showMagnifier(MotionEvent event, Bitmap bitmap, MagnifierView magnifierView) {
//        int[] location = new int[2];
//        mMapView.getLocationOnScreen(location);
//        int x = (int) event.getRawX() - location[0];
//        int y = (int) event.getRawY() - location[1];
//        int magnifierDiameter = 100;
//        // 根据触摸位置计算要放大的区域，确保不超出 bitmap 的边界
//        int left = Math.max(0, x - magnifierDiameter / 2);
//        int top = Math.max(0, y - magnifierDiameter / 2);
//        int right = Math.min(bitmap.getWidth(), x + magnifierDiameter / 2);
//        int bottom = Math.min(bitmap.getHeight(), y + magnifierDiameter / 2);
//
//        // 创建一个放大的 Bitmap 区域
//        Bitmap magnifierContent = Bitmap.createBitmap(bitmap, left, top, right - left, bottom - top);
//
//        // 将放大的 Bitmap 设置到放大镜视图中
//        magnifierView.setBitmap(magnifierContent);
//        magnifierView.setX(event.getRawX() - magnifierView.getWidth() / 2);
//        magnifierView.setY(event.getRawY() - magnifierView.getHeight() * 1.5f);
//        magnifierView.setVisibility(View.VISIBLE);
//        magnifierView.invalidate();
//    }


//    private void showMagnifier(MotionEvent event) {
//        Log.d(TAG,"showMagnifier");
//        int[] location = new int[2];
//        mMapView.getLocationOnScreen(location); // 获取地图在屏幕上的位置
//
//        // 计算触摸点相对于地图的位置
//        int x = (int) event.getRawX() - location[0];
//        int y = (int) event.getRawY() - location[1];
//        Log.d(TAG,"showMagnifier x:"+x+"y:"+y);
//        // 截取地图的一部分作为放大镜的内容
//        Bitmap bitmap = mMapView.getDrawingCache();
//        Log.d(TAG,"showMagnifier:"+(bitmap == null));
//        Rect rect = new Rect(x - 100, y - 100, x + 100, y + 100);
//        Bitmap magnifierContent = Bitmap.createBitmap(bitmap, rect.left, rect.top, rect.width(), rect.height());
//
//        // 更新放大镜视图
//        magnifierView.setBitmap(magnifierContent);
//        magnifierView.setX(event.getRawX() - magnifierView.getWidth() / 2); // 设置放大镜的位置
//        magnifierView.setY(event.getRawY() - magnifierView.getHeight() * 1.5f); // 设置放大镜的位置
//        magnifierView.setVisibility(View.VISIBLE);
//        magnifierView.invalidate(); // 强制重新绘制放大镜
//    }

    private void addMarkerToMap(int spot_marker) {
        LatLng latLng = new LatLng(aMap.getCameraPosition().target.latitude,aMap.getCameraPosition().target.longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng));
        switch (spot_marker){
            case 1:
                spotMarkerList.add(marker);
                break;
            case 2:
                parkingMarkerList.add(marker);
                break;
        }
    }

    private void toggleMapLayer() {
        if (aMap != null) {
            if (aMap.getMapType() == AMap.MAP_TYPE_NORMAL) {
                // 切换到卫星影像图
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
            } else {
                // 切换回矢量图
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            }
        }
    }

    private void initData() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
            try {
                setupLocationStyle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy(); // 销毁定位客户端
        }
    }

    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            // 配置地图的定位参数
            MyLocationStyle myLocationStyle;
            myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
            myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
            aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
            aMap.setMyLocationEnabled(true); // 显示定位层并可触发定位
            aMap.getUiSettings().setMyLocationButtonEnabled(true); // 设置默认定位按钮是否显示
            aMap.moveCamera(CameraUpdateFactory.zoomTo(18)); // 设置缩放级别
            // 设置地图的点击事件
            aMap.setOnMapClickListener(latLng -> {
                // 点击地图其他地方隐藏钓点信息窗体
            });
            // 设置地图滑动监听
            aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {

                }

                @Override
                public void onCameraChangeFinish(CameraPosition cameraPosition) {
                    // 当地图停止滑动时，加载新区域的钓点
//                    loadFishingSpots(cameraPosition.target);
                }
            });
        }
    }


    // 设置定位参数和样式
    private void setupLocationStyle() throws Exception {

        // 初始化定位
        locationClient = new AMapLocationClient(this);
        locationOption = new AMapLocationClientOption();

        // 设置定位模式为高精度模式，GPS和网络同时使用
        locationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);

        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置是否返回地址信息（默认返回地址信息）
        locationOption.setNeedAddress(true);
        // 设置是否只定位一次
        locationOption.setOnceLocation(false);

        // 启动定位
        locationClient.startLocation();
    }


    // 实现定位监听
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation location) {
            Log.e(TAG, new Gson().toJson(location));
            if (location != null && location.getErrorCode() == 0) {
                Log.e(TAG, new Gson().toJson(location));
                if (isFirstLocate) {
                    // 首次定位成功，将地图移动到定位位置
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    isFirstLocate = false; // 更新首次定位标记
                    locationListener.onLocationChanged(location);// 显示系统小蓝点
                }
                // 后续定位更新可以在这里处理
            } else {
                // 定位失败处理
                String errorText = "定位失败," + location.getErrorCode() + ": " + location.getErrorInfo();
                Log.e(TAG, errorText);
            }
        }
    };
}
