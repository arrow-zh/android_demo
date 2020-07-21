package com.ev.hikvideo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.ev.hikvideo.activity.LiveActivity;
import com.ev.hikvideo.activity.PlayBackActivity;
import com.ev.hikvideo.util.Constants;
import com.ev.hikvideo.util.TempDatas;
import com.ev.hikvideo.util.Tools;
import com.ev.hikvideo.util.UIUtils;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.HttpConstants;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;
import com.taobao.weex.annotation.JSMethod;
import com.taobao.weex.common.WXModule;

import java.lang.ref.WeakReference;

public class VideoWXModule extends WXModule {

    private Handler mHandler = null;  //发送消息的对象
    public static final int LOGIN_SUCCESS = 1;  //登录成功
    public static final int LOGIN_FAILED = 2;  //登录失败
    public static final int LOGOUT_SUCCESS = 3;  //退出成功
    public static final int LOGOUT_FAILED = 4; // 退出失败

    private class ViewHandler extends Handler {

        ViewHandler() {
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LOGIN_SUCCESS:
                    UIUtils.cancelProgressDialog();
                    break;
                case LOGIN_FAILED:
                    // 登录失败
                    UIUtils.cancelProgressDialog();
                    Tools.showToast(mWXSDKInstance.getContext(),"登陆平台失败，请退出以后重新登陆");
                    break;
                case LOGOUT_SUCCESS:
                    UIUtils.cancelProgressDialog();
                    // 退出成功
                    break;
                case LOGOUT_FAILED:
                    // 退出失败
                    UIUtils.cancelProgressDialog();
                    Tools.showToast(mWXSDKInstance.getContext(),"退出失败");
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 参数 {
     *      titlebar-color:'#333333', //状态栏颜色
     *      device-id: 'xxx',     //摄像头ID
     *      device-name: '',      //设备名称
     *      project-name: '',     //项目名称
     *      construct-name: '',   //施工单位名称
     *      unit-type-str: '',    //分项名称
     *      }
     * @param options
     */
    @JSMethod(uiThread = true)
    public void startLive(JSONObject options){
        if(!options.containsKey("titlebar-color") || !options.containsKey("device-id") || !options.containsKey("device-name") || !options.containsKey("project-name") ||
                !options.containsKey("construct-name") || !options.containsKey("unit-type-str")) {
            UIUtils.showToast(mWXSDKInstance.getContext(), "参数不完善，请完善调用参数");
            return ;
        }

        loginOpt(1, options);
    }

    /**
     * 参数 {
     *      titlebar-color:'#333333', //状态栏颜色
     *      device-id: 'xxx',     //摄像头ID
     *      project-name: '',     //项目名称
     *      construct-name: '',   //施工单位名称
     *      unit-type-str: '',    //分项名称
     *      device-name: '',      //设备名称
     *      bind-time: ''         //绑定时间
     *      unbind-time: ''       //解绑时间
     *      }
     * @param options
     */
    @JSMethod(uiThread = true)
    public void startPlayback(JSONObject options){
        if(!options.containsKey("titlebar-color") || !options.containsKey("device-id") || !options.containsKey("project-name") ||
                !options.containsKey("construct-name") || !options.containsKey("unit-type-str") || !options.containsKey("device-name") || !options.containsKey("bind-time")
                || !options.containsKey("unbind-time")) {
            UIUtils.showToast(mWXSDKInstance.getContext(), "参数不完善，请完善调用参数");
            return ;
        }

        loginOpt(2, options);
    }

    /**
     * 去直播页面
     * @param data
     */
    public void goToLive(JSONObject data){
        if (mWXSDKInstance != null && mWXSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mWXSDKInstance.getContext(), LiveActivity.class);
            intent.putExtra("dataJson", data.toJSONString());
            mWXSDKInstance.getContext().startActivity(intent);
        }
    }

    /**
     * 去回放页面
     * @param data
     */
    public void goToPlayBack(JSONObject data){
        if (mWXSDKInstance != null && mWXSDKInstance.getContext() instanceof Activity) {
            Intent intent = new Intent(mWXSDKInstance.getContext(), PlayBackActivity.class);
            intent.putExtra("dataJson", data.toJSONString());
            mWXSDKInstance.getContext().startActivity(intent);
        }
    }

    /***
     * 登录方法
     */
    private void loginOpt(final int liveOrPlayback, final JSONObject data) {
        mHandler = new ViewHandler();

        // 已经登陆过
        if (TempDatas.getIns().getLoginData() != null) {
            if(liveOrPlayback == 1) {
                goToLive(data);
            } else if(liveOrPlayback == 2) {
                goToPlayBack(data);
            }
            return;
        }

        final String url = Constants.PLATFORM_IP;
        String userName = Constants.USERNAME;
        String password = Constants.PASSSWD;
        String macAddress = Tools.getMacAddress(mWXSDKInstance.getContext());

        String loginAddress = HttpConstants.HTTPS + url;
        //showDialog();
        UIUtils.showLoadingProgressDialog((Activity) mWXSDKInstance.getContext(), R.string.loading_process_tip, false);
        VMSNetSDK.getInstance().Login(loginAddress, userName, password, macAddress, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(LOGIN_FAILED);
            }

            @SuppressLint("NewApi")
            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof LoginData) {
                    mHandler.sendEmptyMessage(LOGIN_SUCCESS);
                    // 存储登录数据
                    TempDatas.getIns().setLoginData((LoginData) obj);
                    TempDatas.getIns().setLoginAddr(url);

                    SharedPreferences sharedPreferences = mWXSDKInstance.getContext().getSharedPreferences(Constants.APP_DATA, Context.MODE_PRIVATE);

                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(Constants.USER_NAME, Constants.USERNAME);
                    editor.putString(Constants.PASSWORD, Constants.PASSSWD);
                    editor.putString(Constants.ADDRESS_NET, Constants.PLATFORM_IP);
                    editor.apply();
                    // 解析版本号
                    String appVersion = ((LoginData) obj).getVersion();
                    SDKUtil.analystVersionInfo(appVersion);

                    // 跳转到直播页面
                    if(liveOrPlayback == 1) {
                        goToLive(data);
                    } else if(liveOrPlayback == 2) {
                        goToPlayBack(data);
                    }
                }
            }
        });
    }
}
