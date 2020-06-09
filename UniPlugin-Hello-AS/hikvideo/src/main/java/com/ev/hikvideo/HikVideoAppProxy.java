package com.ev.hikvideo;

import android.app.Application;
import android.content.res.AssetManager;

import com.hik.mcrsdk.MCRSDK;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hik.mcrsdk.talk.TalkClientSDK;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import io.dcloud.weex.AppHookProxy;

public class HikVideoAppProxy implements AppHookProxy {

    @Override
    public void onCreate(Application application) {
        System.out.println("==============================================初始化==============================================");
        MCRSDK.init();
        // 初始化RTSP
        RtspClient.initLib();
        MCRSDK.setPrint(1, null);
        // 初始化语音对讲
        TalkClientSDK.initLib();
        // SDK初始化
        VMSNetSDK.init(application);
    }


    /**
     * 释放demo.mp4到手机sd卡
     */
    private void releaseDemoVideo(Application app) {
        File demoVideo = new File(FileUtils.getVideoDirPath() + "/demo.mp4");
        if (demoVideo.exists())
            return;
        AssetManager assetManager = app.getAssets();
        try {
            InputStream inputStream = assetManager.open("demo.mp4");
            FileOutputStream outputStream = new FileOutputStream(demoVideo, false);
            byte[] buffer = new byte[1024];
            while (true) {
                int len = inputStream.read(buffer);
                if (len == -1) {
                    break;
                }
                outputStream.write(buffer, 0, len);
            }
            inputStream.close();
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
