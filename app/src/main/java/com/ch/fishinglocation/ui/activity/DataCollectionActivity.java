package com.ch.fishinglocation.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
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
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
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
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Polygon;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.fishinglocation.R;
import com.ch.fishinglocation.bean.FishingSpot;
import com.ch.fishinglocation.network.FishingSpotUploader;
import com.ch.fishinglocation.ui.view.DrawingView;
import com.ch.fishinglocation.ui.view.MagnifierView;
import com.ch.fishinglocation.ui.view.MapElementStyleConfig;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import cn.leancloud.LCObject;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

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
    private ImageButton btnLocate;
    private MapView mMapView;
    private AMapLocation lastLoc;
    private TextView tvTip;
    private TextView tvLocing;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private AMap aMap;
    private boolean isFirstLocate = true; // 用于标记是否是首次定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private int SPOT_MARKER = 1;
    private int PARKING_MARKER = 2;

    private List<Marker> spotMarkerList = new ArrayList<>();
    private List<Marker> parkingMarkerList = new ArrayList<>();
    private List<Polygon> ployGonList = new ArrayList<>();
    private List<Polyline> ployLineList = new ArrayList<>();

    private MagnifierView magnifierView;
    private ConstraintLayout rootView; // 用于放置放大镜的根布局
    private List<LatLng> walkingPathPoints = new ArrayList<>();
    private boolean isDrawingMode = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_collection);
        mMapView = findViewById(R.id.map_view);
        mMapView.onCreate(savedInstanceState);
        initView();
        initData();
    }

    private void initMapConfig() {
        aMap.setOnMarkerClickListener(marker -> {
            Log.d(TAG, "MarkerClick");
            String message = marker.getTitle().equals("钓位") ? "确定要删除这个钓位吗？" : "确定要删除这个停车位吗？";
            AlertDialog.Builder builder = new AlertDialog.Builder(DataCollectionActivity.this);
            builder.setMessage(message);
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 用户确认删除操作
                    deleteMarker(marker);
                }
            });
            builder.setNegativeButton("取消", null);
            AlertDialog dialog = builder.create();
            dialog.show();
            return true;
        });

        aMap.setOnPolylineClickListener(new AMap.OnPolylineClickListener() {
            @Override
            public void onPolylineClick(Polyline polyline) {
                Log.d(TAG, "PolylineClick");
                AlertDialog.Builder builder = new AlertDialog.Builder(DataCollectionActivity.this);
                builder.setMessage("确定要删除这个线路吗？");
                builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 用户确认删除操作
                        deletePolyline(polyline);
                    }
                });
                builder.setNegativeButton("取消", null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        aMap.setOnMapClickListener(latLng -> {
            boolean isPolygon = false;
            for (Polygon polygon : ployGonList) {
                if (polygon.contains(latLng)) {
                    isPolygon = true;
                }
            }
            if (!isPolygon) {
                return;
            }
            Log.d(TAG, "PolygonClick");
            AlertDialog.Builder builder = new AlertDialog.Builder(DataCollectionActivity.this);
            builder.setMessage("确定要删除这个钓点范围吗？");
            builder.setPositiveButton("删除", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // 用户确认删除操作
                    for (Polygon polygon : ployGonList) {
                        if (polygon.contains(latLng)) {
                            deletePolygon(polygon);
                            return;
                        }
                    }
                }
            });
            builder.setNegativeButton("取消", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    private void deletePolygon(Polygon polygon) {
        // 从地图上移除 Marker
        polygon.remove();
        ployGonList.remove(polygon);
    }

    private void deletePolyline(Polyline polyline) {
        // 从地图上移除 Marker
        polyline.remove();
        ployLineList.remove(polyline);
    }


    // 删除指定的钓点 Marker 以及相关的数据
    private void deleteMarker(Marker marker) {
        // 从地图上移除 Marker
        marker.remove();
        if (marker.getTitle().equals("钓位")) {
            spotMarkerList.remove(marker);
        } else if (marker.getTitle().equals("停车")) {
            parkingMarkerList.remove(marker);
        }
    }

    private void initView() {
        drawingView = findViewById(R.id.drawingView);
        btnLocate = findViewById(R.id.btn_locate);
        editTextName = findViewById(R.id.editName);
        editTextDescription = findViewById(R.id.editDescription);
        btnAddSpot = findViewById(R.id.btnAddSpot);
        btnAddParking = findViewById(R.id.btnAddParking);
        btnAddPloygon = findViewById(R.id.btnAddPloygon);
        btnAddWalking = findViewById(R.id.btnAddWalking);
        buttonToggleLayer = findViewById(R.id.buttonToggleLayer);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        tvTip = findViewById(R.id.tv_tip);
        tvLocing = findViewById(R.id.tv_locing);

        buttonToggleLayer.setOnClickListener(view -> toggleMapLayer());
        btnAddSpot.setOnClickListener(view -> addSpot());
        btnAddParking.setOnClickListener(view -> addParking());
        btnAddPloygon.setOnClickListener(view -> addPloygon());
        btnAddWalking.setOnClickListener(view -> addwalking());
        buttonSubmit.setOnClickListener(v -> uploadData());
        btnLocate.setOnClickListener(view -> {
            if (null != lastLoc) {
                LatLng latLng = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
            } else {
                ToastUtils.showShort("未获取到定位信息");
            }

        });
    }

    public boolean uploadData() {
        //todo 上传FishingSpot数据
        fishingSpot = new FishingSpot();
        fishingSpot.setName(editTextName.getText().toString().trim());
        fishingSpot.setDescription(editTextDescription.getText().toString().trim());
        if (!spotMarkerList.isEmpty()) {
            List<LatLng> latLngs = new ArrayList<>();
            for (Marker marker : spotMarkerList) {
                latLngs.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            }
            fishingSpot.setFirstSpot(new LatLng(spotMarkerList.get(0).getPosition().latitude, spotMarkerList.get(0).getPosition().longitude));
            fishingSpot.setSpots(latLngs);
        }
        if (!parkingMarkerList.isEmpty()) {
            List<LatLng> latLngs = new ArrayList<>();
            for (Marker marker : parkingMarkerList) {
                latLngs.add(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude));
            }
            fishingSpot.setParkingSpots(latLngs);
        }
        if (!ployLineList.isEmpty()) {
            List<List<LatLng>> latLngs = new ArrayList<>();
            for (Polyline polyline : ployLineList) {
                latLngs.add(polyline.getPoints());
            }
            fishingSpot.setWalkPaths(latLngs);
        }
        if (!ployGonList.isEmpty()) {
            List<List<LatLng>> latLngs = new ArrayList<>();
            for (Polygon polygon : ployGonList) {
                latLngs.add(polygon.getPoints());
            }
            fishingSpot.setRange(latLngs);
        }
//        fishingSpot.setUploadedBy("13720282090");
        Observer<LCObject> uploadObserver = new Observer<LCObject>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 可以在这里初始化一些资源，比如显示一个加载框
                System.out.println("开始上传钓点...");
            }

            @Override
            public void onNext(LCObject lcObject) {
                // 上传成功回调
                String objectId = lcObject.getObjectId();
                System.out.println("钓点上传成功，objectId: " + objectId);
            }

            @Override
            public void onError(Throwable e) {
                // 上传失败回调
                System.err.println("钓点上传失败: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                // 上传操作完成回调，无论成功或失败
                System.out.println("上传操作完成");
                finish();
            }
        };
// 调用上传方法
        FishingSpotUploader.uploadFishingSpot(fishingSpot, uploadObserver);
        return false;
    }

    public void addSpot() {
        spotMarkerList.add(addMarkerToMap(SPOT_MARKER));
    }

    public void addParking() {
        parkingMarkerList.add(addMarkerToMap(PARKING_MARKER));
    }

    public void addPloygon() {
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
                ployGonList.add(aMap.addPolygon(new PolygonOptions()
                        .addAll(latLngs)
                        .fillColor(0x0D00FF00)
                        .strokeColor(0x3300FF00)
                        .strokeWidth(5)));
            }
            drawingView.clear();
        }
    }

    // 开始绘制走路线路的方法
    public void startDrawingWalkingPath() {
        isDrawingMode = true;
        tvTip.setVisibility(View.VISIBLE);
        walkingPathPoints.clear(); // 清空之前的路径点
        drawingView.setVisibility(View.VISIBLE);
        aMap.getUiSettings().setAllGesturesEnabled(false);
        drawingView.setProjection(aMap.getProjection()); // 设置Projection对象
    }

    // 结束绘制走路线路的方法
    public void stopDrawingWalkingPath() {
        isDrawingMode = false;
        aMap.getUiSettings().setAllGesturesEnabled(true);
        drawingView.setVisibility(View.GONE);
        tvTip.setVisibility(View.GONE);
        // 在地图上绘制走路线路
        walkingPathPoints = drawingView.getPoints();
        if (!walkingPathPoints.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions();
            polylineOptions.addAll(walkingPathPoints)
                    .width(10) // 线宽
                    .color(Color.BLUE) // 线颜色
                    .geodesic(true);
            ployLineList.add(aMap.addPolyline(polylineOptions));
        }
        drawingView.clear();
    }

    public void addwalking() {
        //todo 绘制走路线路
        if (isDrawingMode) {
            stopDrawingWalkingPath();
        } else {
            startDrawingWalkingPath();
        }
    }

    private Marker addMarkerToMap(int spot_marker) {
        String title = spot_marker == 1 ? "钓位" : "停车";
        BitmapDescriptor icon = spot_marker == 1 ? BitmapDescriptorFactory.fromResource(R.drawable.map_spot) : BitmapDescriptorFactory.fromResource(R.drawable.map_paring);
        LatLng latLng = new LatLng(aMap.getCameraPosition().target.latitude, aMap.getCameraPosition().target.longitude);
        final Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(title).icon(icon));
        return marker;
    }

    /**
     * 切换影像图
     */
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
//        BitmapDescriptor texture = BitmapDescriptorFactory.defaultMarker();
//        BitmapDescriptor icon = BitmapDescriptorFactory.defaultMarker();
//        MapElementStyleConfig.MarkerStyle markerStyle = new MapElementStyleConfig.MarkerStyle(icon, 0.5f, 0.5f);
//        MapElementStyleConfig.PolygonStyle polygonStyle = new MapElementStyleConfig.PolygonStyle(Color.BLACK, 2, Color.RED);
//        MapElementStyleConfig.PolylineStyle polylineStyle = new MapElementStyleConfig.PolylineStyle(Color.BLUE, 5.0f, texture);
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
                initMapConfig();
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
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy(); // 销毁定位客户端
        }
    }

    private void setUpMap() {
        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
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

        // 设置地图的触摸监听
        aMap.setOnMapTouchListener(motionEvent -> {
            // 如果当前是绘制模式，捕获触摸点
            if (isDrawingMode && motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                LatLng latLng = aMap.getProjection().fromScreenLocation(
                        new Point((int) motionEvent.getX(), (int) motionEvent.getY())
                );
                walkingPathPoints.add(latLng); // 添加触摸点到路径列表
            }
        });
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
                lastLoc = location;
                if (isFirstLocate) {
                    // 首次定位成功，将地图移动到定位位置
                    tvLocing.setText("已定位");
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
