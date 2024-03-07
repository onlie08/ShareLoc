package com.ch.fishinglocation.ui.home;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import io.reactivex.Observer;

import androidx.lifecycle.ViewModelProvider;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolygonOptions;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.blankj.utilcode.util.ToastUtils;
import com.ch.fishinglocation.MyApplication;
import com.ch.fishinglocation.bean.FishingSpot;
import com.ch.fishinglocation.databinding.FragmentHomeBinding;
import com.ch.fishinglocation.network.FishingSpotService;
import com.ch.fishinglocation.network.FishingSpotUploader;
import com.ch.fishinglocation.ui.activity.DataCollectionActivity;
import com.ch.fishinglocation.ui.activity.NaviActivity;
import com.google.gson.Gson;

import java.util.Arrays;
import java.util.List;

import cn.leancloud.LCObject;
import io.reactivex.disposables.Disposable;

public class HomeFragment extends Fragment {
    private String TAG = "HomeFragment";
    private FragmentHomeBinding binding;
    private MapView mMapView;
    private TextView tvLocing;
    private ImageButton btnLocate;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private AMap aMap;
    private AMapLocation lastLocation;
    private boolean isFirstLocate = true; // 用于标记是否是首次定位

    // 定位客户端
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mMapView = binding.mapView;
        mMapView.onCreate(savedInstanceState);
        binding.buttonToggleLayer.setOnClickListener(view -> toggleMapLayer());
        binding.btnUpload.setOnClickListener(view -> navigateTo(DataCollectionActivity.class));
        tvLocing = binding.tvLocing;
        btnLocate = binding.btnLocate;
        btnLocate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(getActivity(), NaviActivity.class);
                startActivity(intent);
//                if (null != lastLocation) {
//                    LatLng latLng = new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude());
//                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
//                } else {
//                    ToastUtils.showShort("未获取到定位信息");
//                }

            }
        });

        if (aMap == null) {
            aMap = mMapView.getMap();
            setUpMap();
            try {
                setupLocationStyle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return root;
    }

    private void navigateTo(Class<DataCollectionActivity> dataCollectionActivityClass) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), dataCollectionActivityClass);
        startActivity(intent);
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

    @Override
    public void onStart() {
        Log.d(TAG, "onStart");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        mMapView.onResume();

    }

    // 设置定位参数和样式
    private void setupLocationStyle() throws Exception {

        // 初始化定位
        locationClient = new AMapLocationClient(getActivity());
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
                lastLocation = location;
                if (isFirstLocate) {
                    // 首次定位成功，将地图移动到定位位置
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
                    isFirstLocate = false; // 更新首次定位标记
                    locationListener.onLocationChanged(location);// 显示系统小蓝点
                    tvLocing.setText("已定位");
                }
                // 后续定位更新可以在这里处理
            } else {
                // 定位失败处理
                String errorText = "定位失败," + location.getErrorCode() + ": " + location.getErrorInfo();
                Log.e(TAG, errorText);
            }
        }
    };

    private void setUpMap() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
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
            aMap.getUiSettings().setMyLocationButtonEnabled(false); // 设置默认定位按钮是否显示
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
                    loadFishingSpots(cameraPosition.target);
                }
            });
        }

        aMap.setOnMapLoadedListener(new AMap.OnMapLoadedListener() {
            @Override
            public void onMapLoaded() {
                aMap.showMapText(false);
            }
        });

        Location location = ((MyApplication) getActivity().getApplication()).getGlobalLocation();
        //获取全局位置
        if(null != location){
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,18));
        }
    }

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        super.onStop();
    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }
    @Override
    public void onDestroyView() {
        Log.d(TAG, "onDestroyView");
        super.onDestroyView();
        isFirstLocate = true;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        binding = null;
        mMapView.onDestroy();
        if (locationClient != null) {
            locationClient.onDestroy(); // 销毁定位客户端
        }
    }

    // 加载并显示钓点
    private void loadFishingSpots(LatLng center) {
        // 假设有个方法来从服务或数据库获取钓点，我们将其命名为 getFishingSpots

        // 假设用户当前位置
        LatLng currentUserLocation = center;
        // 使用50公里作为搜索半径
        double searchRadius = 50;

        // 创建Observer来处理从getFishingSpots返回的结果
        Observer<List<LCObject>> observer = new Observer<List<LCObject>>() {
            @Override
            public void onSubscribe(Disposable d) {
                // 在这里可以处理订阅事件，例如显示加载动画
            }

            @Override
            public void onNext(List<LCObject> lcObjects) {
                // 成功获取数据后，在这里将LCObject转换为FishingSpot实体
                List<FishingSpot> fishingSpots = FishingSpotService.convertToEntityList(lcObjects);
                // 更新UI或处理数据
                updateMapWithFishingSpots(fishingSpots);
            }

            @Override
            public void onError(Throwable e) {
                // 查询出错，处理错误，例如显示一个错误消息
            }

            @Override
            public void onComplete() {
                // 查询完成，可以在这里隐藏加载动画
            }
        };

        // 调用getFishingSpots方法
        FishingSpotService.getFishingSpots(currentUserLocation, searchRadius, observer);
    }

    private void updateMapWithFishingSpots(List<FishingSpot> fishingSpots) {
        if (aMap == null || fishingSpots == null) {
            // 地图尚未初始化或钓点列表为空
            return;
        }
        // 清除地图上现有的所有标记和图形
        aMap.clear();

        // 遍历钓点列表，为每个钓点添加Marker和Polygon
        for (FishingSpot spot : fishingSpots) {
            // 添加Marker
            for (int i = 0; i < spot.getSpots().size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(spot.getSpots().get(i));
                markerOptions.title(spot.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                aMap.addMarker(markerOptions);
            }

            for (int i = 0; i < spot.getParkingSpots().size(); i++) {
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(spot.getParkingSpots().get(i));
                markerOptions.title(spot.getName());
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                aMap.addMarker(markerOptions);
            }

            // 添加Polygon（如果钓点范围数据存在）
            if (spot.getRange() != null && !spot.getRange().isEmpty()) {
                for (int i = 0; i < spot.getRange().size(); i++) {
                    PolygonOptions polygonOptions = new PolygonOptions();
                    polygonOptions.addAll(spot.getRange().get(i));
                    polygonOptions.strokeWidth(5) // 边框宽度
                            .strokeColor(0xFF0000FF) // 边框颜色
                            .fillColor(0x220000FF); // 填充颜色
                    aMap.addPolygon(polygonOptions);
                }
            }

            if (spot.getWalkPaths() != null && !spot.getWalkPaths().isEmpty()) {
                for (int i = 0; i < spot.getWalkPaths().size(); i++) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    polylineOptions.addAll(spot.getWalkPaths().get(i));
                    polylineOptions.width(5) // 边框宽度
                            .color(0xFF0000FF); // 边框颜色
                    aMap.addPolyline(polylineOptions);
                }
            }
        }
    }

    // 当用户点击marker时实现导航功能
    private void setMarkerListener() {
        aMap.setOnMarkerClickListener(marker -> {
            // 这里实现点击marker后如何处理，例如弹出详情或导航
            startNavigation(marker.getPosition()); // 示例：启动导航
            return true;
        });
    }

    private void startNavigation(LatLng latLng) {
        // 使用高德地图或其他方式进行导航
    }
}