package com.ch.fishinglocation;

import android.os.Bundle;
import android.os.StrictMode;

import com.amap.api.maps.MapsInitializer;
import com.amap.api.navi.NaviSetting;
import com.blankj.utilcode.constant.PermissionConstants;
import com.blankj.utilcode.util.PermissionUtils;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.ch.fishinglocation.ad.TTAdManagerHolder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.ch.fishinglocation.databinding.ActivityMainBinding;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        initView();
        initPermission();
        initAd();
        initMap();
        initOthers();
    }

    private void initOthers() {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
    }

    private void initView() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home,
//                R.id.navigation_dashboard,
                R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

    }

    private void initPermission() {
        PermissionUtils.permission(PermissionConstants.CAMERA, PermissionConstants.STORAGE, PermissionConstants.LOCATION)
                .rationale((activity, shouldRequest) -> shouldRequest.again(true))
                .callback(new PermissionUtils.FullCallback() {
                    @Override
                    public void onGranted(List<String> permissionsGranted) {
                        // 所请求的权限都被授予时调用
                    }

                    @Override
                    public void onDenied(List<String> permissionsDeniedForever, List<String> permissionsDenied) {
                        // 当权限被拒绝（分为永久拒绝和非永久拒绝）时调用
                        // permissionsDeniedForever 是用户选择了 "不再询问" 的权限列表
                        // permissionsDenied 是被拒绝但没有选择 "不再询问" 的权限列表
                    }
                })
                .request();
    }

    private void initAd() {
        TTAdManagerHolder.init(MainActivity.this.getApplicationContext());
        TTAdSdk.start(new TTAdSdk.Callback() {
            @Override
            public void success() {

            }

            @Override
            public void fail(int i, String s) {

            }
        });
    }

    private void initMap() {
//        NaviSetting.updatePrivacyShow(this, true, true);
//        NaviSetting.updatePrivacyAgree(this, true);
//        MapsInitializer.updatePrivacyShow(this,true,true);
//        MapsInitializer.updatePrivacyAgree(this,true);
    }

}