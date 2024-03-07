package com.ch.fishinglocation.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.location.Location;
import android.location.LocationManager;
import com.ch.fishinglocation.MainActivity;
import com.ch.fishinglocation.MyApplication;
import com.ch.fishinglocation.R;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 1000;
    private static final int REQUEST_LOCATION_PERMISSION = 1;
//    private static final int SPLASH_DISPLAY_LENGTH = 1000; // 延迟1秒
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 请求定位权限
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        } else {
            // 已经拥有权限，可以获取定位
            getLocation();
            continueToApp();
        }

    }

    private void getLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location globalLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            // TODO: 如果需要，可以将位置信息存储到全局变量或应用的数据存储中
            ((MyApplication) getApplication()).setGlobalLocation(globalLocation);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限被授予，获取定位
                getLocation();
            }
            // 继续进入应用，无论权限是否被授予
            continueToApp();
        }
    }

    private void continueToApp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish(); // 关闭欢迎页
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}

