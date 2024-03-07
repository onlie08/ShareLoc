package com.ch.fishinglocation.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {

    private static final String APP_ID = "你的AppID";
    private static final String APP_SECRET = "你的AppSecret";
    private IWXAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 通过WXAPIFactory工厂获取IWXApi的实例
        api = WXAPIFactory.createWXAPI(this, APP_ID, true);

        // 将应用注册到微信
        api.registerApp(APP_ID);

        // 发起登录请求
        if (!api.isWXAppInstalled()) {
            // 检查手机上是否安装了微信
            Toast.makeText(this, "您还未安装微信客户端", Toast.LENGTH_SHORT).show();
            return;
        }
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo"; // 获取用户个人信息
        req.state = "wechat_sdk_demo_test"; // 自定义信息
        api.sendReq(req);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
        // 处理微信传过来的请求
    }

    @Override
    public void onResp(BaseResp resp) {
        // 登录回调
        switch (resp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                // 用户同意
                String code = ((SendAuth.Resp) resp).code;
                // 通过code获取access_token
                getAccessToken(code);
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                // 用户拒绝授权
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                // 用户取消
                break;
            default:
                // 发送返回
                break;
        }
    }

    private void getAccessToken(String code) {
        // 通过code换取网页授权access_token的URL
        String url = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="
                + APP_ID + "&secret=" + APP_SECRET + "&code=" + code + "&grant_type=authorization_code";

        // 发送网络请求获取access_token
        // 这里需要使用网络请求库，比如Volley或Retrofit
    }
}

