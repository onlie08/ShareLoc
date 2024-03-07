package com.ch.fishinglocation;

import android.app.Application;
import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.ch.fishinglocation.bean.User;
import com.tencent.bugly.crashreport.CrashReport;

import cn.leancloud.core.LeanCloud;

public class MyApplication extends Application {
    private static Context context;
    private String TAG = this.getClass().getSimpleName();
    public static User user;

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this;
        initLeancloud();
        CrashReport.initCrashReport(getApplicationContext(), "a8e59dd66f", BuildConfig.DEBUG);

        user = new User();
        user.setUsername("探钓武汉-海子");
        user.setPhone("13720282090");
        user.setVIP(true);
    }

    private void initLeancloud() {
        try {
            LeanCloud.initialize("pI5NbfmB4kh2AEir2i9WbIAf-gzGzoHsz", "WbbHTxNy4STZrGkqAz1b5nlV", "https://pi5nbfmb.lc-cn-n1-shared.com");
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

    private Location globalLocation;

    // 全局变量的Getter和Setter方法
    public Location getGlobalLocation() {
        return globalLocation;
    }

    public void setGlobalLocation(Location globalLocation) {
        this.globalLocation = globalLocation;
    }

}
