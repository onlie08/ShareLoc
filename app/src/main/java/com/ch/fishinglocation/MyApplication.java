package com.ch.fishinglocation;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.tencent.bugly.crashreport.CrashReport;

import cn.leancloud.core.LeanCloud;

public class MyApplication extends Application {
    private static Context context;
    private String TAG = this.getClass().getSimpleName();

    public static Context getAppContext() {
        return MyApplication.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MyApplication.context = this;
        initLeancloud();
        CrashReport.initCrashReport(getApplicationContext(), "53144aab5a", BuildConfig.DEBUG);
    }

    private void initLeancloud() {
        try {
            LeanCloud.initialize("pI5NbfmB4kh2AEir2i9WbIAf-gzGzoHsz", "WbbHTxNy4STZrGkqAz1b5nlV", "https://pi5nbfmb.lc-cn-n1-shared.com");
        } catch (Exception e) {
            Log.e(TAG,e.getMessage());
        }
    }

}
