package com.ch.fishinglocation.ad;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.bytedance.sdk.openadsdk.TTAdConfig;
import com.bytedance.sdk.openadsdk.TTAdConstant;
import com.bytedance.sdk.openadsdk.TTAdManager;
import com.bytedance.sdk.openadsdk.TTAdSdk;
import com.ch.fishinglocation.MainActivity;
import com.ch.fishinglocation.ad.liveoauth.TTInjectionAuthImpl;
import com.ch.fishinglocation.ad.liveoauth.TTLiveTokenHelper;


/**
 * 可以用一个单例来保存TTAdManager实例，在需要初始化sdk的时候调用
 */
public class TTAdManagerHolder {

    private static final String TAG = "TTAdManagerHolder";

    private static boolean sInit;
    private static boolean sStart;



    public static TTAdManager get() {

        return TTAdSdk.getAdManager();
    }

    public static void init(final Context context) {
        //初始化穿山甲SDK
        doInit(context);
    }

    //step1:接入网盟广告sdk的初始化操作，详情见接入文档和穿山甲平台说明
    private static void doInit(Context context) {
        if (sInit) {
            Toast.makeText(context, "您已经初始化过了", Toast.LENGTH_LONG).show();
            return;
        }
        //TTAdSdk.init(context, buildConfig(context));

        TTAdSdk.init(context, buildConfig(context));
        sInit = true;
        Toast.makeText(context, "初始化成功", Toast.LENGTH_LONG).show();
    }

    //开始展示广告
    public static void start(final Context context) {
        if (!sInit) {
            Toast.makeText(context, "还没初始化SDK，请先进行初始化", Toast.LENGTH_LONG).show();
            return;
        }
        if (sStart) {
            return;
        }

        TTAdSdk.start(new TTAdSdk.Callback() {
            @Override
            public void success() {
                if (!(context instanceof Activity)) {
                    return;
                }
                final Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);
                ((Activity) context).finish();
                Log.i(TAG, "success: " + TTAdSdk.isSdkReady());
            }

            @Override
            public void fail(int code, String msg) {
                Log.i(TAG, "fail:  code = " + code + " msg = " + msg);
            }
        });
        sStart = true;
    }


    private static TTAdConfig buildConfig(Context context) {

        return new TTAdConfig.Builder().appId("5001121").useTextureView(true) //使用TextureView控件播放视频,默认为SurfaceView,当有SurfaceView冲突的场景，可以使用TextureView
                .allowShowNotify(true) //是否允许sdk展示通知栏提示
                .debug(true) //测试阶段打开，可以通过日志排查问题，上线时去除该调用

                .directDownloadNetworkType(TTAdConstant.NETWORK_STATE_WIFI, TTAdConstant.NETWORK_STATE_3G) //允许直接下载的网络状态集合
                .supportMultiProcess(false)//是否支持多进程
                .injectionAuth(TTLiveTokenHelper.getInstance().useHostAuth() ? new TTInjectionAuthImpl() : null).build();
    }
}
