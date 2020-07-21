package com.ev.hikvideo.activity;

import android.app.Activity;
import java.io.File;
import java.lang.ref.WeakReference;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ev.hikvideo.R;
import com.ev.hikvideo.util.UIUtils;
import com.ev.hikvideo.view.CustomSurfaceView;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.consts.SDKConstant.LiveSDKConstant;
import com.hikvision.sdk.consts.SDKConstant.PTZCommandConstant;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.FileUtils;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * 实时预览Activity
 * </p>
 *
 * @author zhangwei59 2017/3/10 14:30
 * @version V1.0.0
 */
public class LiveActivity extends BaseActivity implements View.OnClickListener, SurfaceHolder.Callback {

    /**
     * 获取监控点信息成功
     */
    private static final int GET_CAMERA_INFO_SUCCESS = 1;
    /**
     * 获取监控点信息失败
     */
    private static final int GET_CAMERA_INFO_FAILURE = 2;
    /**
     * 开启语音对讲失败
     */
    private static final int OPEN_TALK_FAILURE = 3;
    /**
     * 开启语音对讲成功
     */
    private static final int OPEN_TALK_SUCCESS = 4;
    /**
     * 关闭语音对讲
     */
    private static final int CLOSE_TALK_SUCCESS = 5;
    /**
     * 预览控件
     */
    private CustomSurfaceView mSurfaceView = null;
    /**
     * 录像按钮
     */
    private View mRecordBtn;
    private ImageView mIvRecord;
    private TextView mTvRecord;

    /**
     * 音频按钮
     */
    private View mAudioBtn;
    private ImageView mIvAudio;
    private TextView mTvAudio;
    /**
     * 语音对讲按钮
     */
    private View mTalkBtn;
    private ImageView mIvTalk;
    private TextView mTvTalk;

    /**
     * 码流
     */
    private View mClarityBtn;
    private ImageView mIvClarity;
    private TextView mTvClarity;
    private View subMenuClarity;
    private TextView mTvHigh;
    private TextView mTvMid;
    private TextView mTvLow;

    /**
     * 云台控制
     */
    private View mPaltformBtn;
    private ImageView mIvPaltform;
    private TextView mTvPaltform;
    /**
     * 云台控制菜单
     */
    private View subMenuPtz;

    /**
     * 控制移动
     */
    private boolean mIsShowMove;

    private View mVMove;
    private ImageView mIvMove;
    private TextView mTvMove;
    private View mVMoveMenu;
    private ImageView mIvUp, mIvLeft, mIvRight, mIvDown;

    /**
     * 焦距 光圈 变倍
     */
    private View mVFocal;
    private ImageView mIvFocal;
    private TextView mTvFocal;

    private View mVAperture;
    private ImageView mIvAperture;
    private TextView mTvAperture;

    private View mVChanging;
    private ImageView mIvChanging;
    private TextView mTvChanging;

    private View mVThirdMenu;
    private View mIvMenuPlus, mIvMenuDes;
    private TextView mTvOptText;

    /**
     * 码流类型
     */
    private int mStreamType = LiveSDKConstant.MAIN_HIGH_STREAM;
    /**
     * 音频是否开启
     */
    private boolean mIsAudioOpen;
    /**
     * 语音对讲是否开启
     */
    private boolean mIsTalkOpen;
    /**
     * 是否正在录像
     */
    private boolean mIsRecord;

    /**
     * 语音对讲是否开启
     */
    private boolean mIsShowClarity = false;

    /**
     * 监控点资源
     */
    // private SubResourceNodeBean mCamera = null;
    /**
     * 视图更新处理Handler
     */
    private Handler mHandler = null;
    /**
     * 对讲通道数目
     */
    private int talkChannels;
    /**
     * 临时选择对讲通道数目
     */
    private String channelNoTemp;
    /**
     * 最终选择对讲通道数目
     */
    private int channelNo;

    /**
     * 播放窗口1
     */
    private int PLAY_WINDOW_ONE = 1;

    // 播放的是摄像头
    private String deviceId = "";
    private JSONObject param = null;

    /**
     * 视图更新处理器
     */
    private class MyHandler extends Handler {

        WeakReference<LiveActivity> mActivityReference;

        MyHandler(LiveActivity activity) {
            mActivityReference = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            LiveActivity activity = mActivityReference.get();
            if (activity != null) {
                switch (msg.what) {
                    case GET_CAMERA_INFO_SUCCESS:
                        UIUtils.cancelProgressDialog();;
                        UIUtils.showToast(activity, R.string.rtsp_success);
                        break;
                    case GET_CAMERA_INFO_FAILURE:
                        UIUtils.cancelProgressDialog();;
                        UIUtils.showToast(activity, R.string.rtsp_fail);
                        break;
                    case OPEN_TALK_FAILURE:
                        activity.mIsTalkOpen = false;
                        UIUtils.showToast(activity, R.string.start_Talk_fail);
                        // activity.mTalkBtn.setText(R.string.start_Talk);

                        mIvTalk.setImageResource(R.drawable.icon_intercom);
                        mTvTalk.setTextColor(getResources().getColor(R.color.colorDark));
                        break;
                    case OPEN_TALK_SUCCESS:
                        activity.mIsTalkOpen = true;
                        UIUtils.showToast(activity, R.string.start_Talk_success);
                        // activity.mTalkBtn.setText(R.string.stop_Talk);

                        mIvTalk.setImageResource(R.drawable.icon_intercom_select);
                        mTvTalk.setTextColor(getResources().getColor(R.color.colorBlue));
                        break;
                    case CLOSE_TALK_SUCCESS:
                        activity.mIsTalkOpen = false;
                        UIUtils.showToast(activity, R.string.stop_Talk);
                        // activity.mTalkBtn.setText(R.string.start_Talk);

                        mIvTalk.setImageResource(R.drawable.icon_intercom_select);
                        mTvTalk.setTextColor(getResources().getColor(R.color.colorBlue));
                        break;
                    default:
                        break;
                }
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
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live);

        param = JSON.parseObject(getIntent().getStringExtra("dataJson"));
        deviceId = param.getString("device-id");

        initData();
        initView();

        initBackBtn();
        setTitle("实时现场");
        initSiteData();
    }

    // 更新APP
    private void initSiteData() {
        String color = param.getString("titlebar-color");
        if(color != null && !"".equals(color)) {
            this.findViewById(R.id.rl_header).setBackgroundColor(Color.parseColor(color));
        }

        TextView mTvSiteName = findViewById(R.id.tv_site_name);
        TextView mTvConstructName = findViewById(R.id.tv_construct_name);
        TextView mTvDeviceName = findViewById(R.id.tv_device_name);
        TextView mTvUnitName = findViewById(R.id.tv_unit_name);

        mTvSiteName.setText("项目名称：" + param.getString("project-name"));
        mTvConstructName.setText("施工单位：" + param.getString("construct-name"));
        mTvDeviceName.setText("设备名称：" + param.getString("device-name"));
        mTvUnitName.setText("分项名称：" + param.getString("unit-type-str"));
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // mCamera = (SubResourceNodeBean)
        // getIntent().getSerializableExtra(Constants.IntentKey.CAMERA);
        mHandler = new MyHandler(this);


    }

    /**
     * 初始化视图
     */
    private void initView() {
        mSurfaceView = (CustomSurfaceView) findViewById(R.id.surfaceView);
        mSurfaceView.getHolder().addCallback(this);

        // 抓拍按钮
        findViewById(R.id.live_capture).setOnClickListener(this);
        findViewById(R.id.btn_right).setOnClickListener(this);

        // 录像按钮
        mRecordBtn = findViewById(R.id.btn_recod_video);
        mIvRecord = (ImageView) findViewById(R.id.iv_record_video);
        mTvRecord = (TextView) findViewById(R.id.tv_record_video);
        mRecordBtn.setOnClickListener(this);

        // 音频按钮
        mAudioBtn = findViewById(R.id.btn_voice);
        mIvAudio = (ImageView) findViewById(R.id.iv_voice);
        mTvAudio = (TextView) findViewById(R.id.tv_voice);
        mAudioBtn.setOnClickListener(this);

        // 语音对讲按钮
        mTalkBtn = findViewById(R.id.btn_talk);
        mIvTalk = (ImageView) findViewById(R.id.iv_talk);
        mTvTalk = (TextView) findViewById(R.id.tv_talk);
        mTalkBtn.setOnClickListener(this);

        // 码流切换
        mClarityBtn = findViewById(R.id.btn_clarity);
        mIvClarity = (ImageView) findViewById(R.id.img_clarity);
        mTvClarity = (TextView) findViewById(R.id.tv_clarity);
        subMenuClarity = findViewById(R.id.ll_second_menu_clarity);
        mTvHigh = (TextView) findViewById(R.id.tv_hight);
        mTvMid = (TextView) findViewById(R.id.tv_mid);
        mTvLow = (TextView) findViewById(R.id.tv_low);
        mClarityBtn.setOnClickListener(this);
        mTvHigh.setOnClickListener(this);
        mTvMid.setOnClickListener(this);
        mTvLow.setOnClickListener(this);
        mStreamType = LiveSDKConstant.MAIN_HIGH_STREAM;

        // 云台控制UI
        mPaltformBtn = findViewById(R.id.btn_platform);
        mIvPaltform = (ImageView) findViewById(R.id.img_platform);
        mTvPaltform = (TextView) findViewById(R.id.tv_platform);
        mPaltformBtn.setOnClickListener(this);
        subMenuPtz = findViewById(R.id.ll_second_menu_platform);

        mVMove = findViewById(R.id.btn_move);
        mIvMove = (ImageView) findViewById(R.id.iv_move);
        mTvMove = (TextView) findViewById(R.id.tv_move);
        mVMoveMenu = findViewById(R.id.rl_ptz_arrow);
        mIvUp = (ImageView) findViewById(R.id.iv_up);
        mIvLeft = (ImageView) findViewById(R.id.iv_move_left);
        mIvRight = (ImageView) findViewById(R.id.iv_right);
        mIvDown = (ImageView) findViewById(R.id.iv_down);

        mVMove.setOnClickListener(this);

        ButtonListener b = new ButtonListener();
        mIvUp.setOnTouchListener(b);
        mIvLeft.setOnTouchListener(b);
        mIvRight.setOnTouchListener(b);
        mIvDown.setOnTouchListener(b);

        mVFocal = findViewById(R.id.btn_focal);
        mIvFocal = (ImageView) findViewById(R.id.iv_focal);
        mTvFocal = (TextView) findViewById(R.id.tv_focal);
        mVFocal.setOnClickListener(this);

        mVAperture = findViewById(R.id.btn_aperture);
        mIvAperture = (ImageView) findViewById(R.id.iv_aperture);
        mTvAperture = (TextView) findViewById(R.id.tv_aperture);
        mVAperture.setOnClickListener(this);

        mVChanging = findViewById(R.id.btn_changing);
        mIvChanging = (ImageView) findViewById(R.id.iv_changing);
        mTvChanging = (TextView) findViewById(R.id.tv_changing);
        mVChanging.setOnClickListener(this);

        mVThirdMenu = findViewById(R.id.ll_third_menu);
        mIvMenuPlus = findViewById(R.id.iv_opt_plus);
        mIvMenuDes = findViewById(R.id.iv_opt_des);
        mTvOptText = (TextView) findViewById(R.id.tv_opt_text);

        mIvMenuPlus.setOnTouchListener(b);
        mIvMenuDes.setOnTouchListener(b);

        // 调用开始预览
        // startPreview();
    }

    // 开始预览按钮点击操作
    public void startPreview() {
        UIUtils.showLoadingProgressDialog(this, R.string.loading_process_tip, false);
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                VMSNetSDK.getInstance().startLiveOpt(PLAY_WINDOW_ONE, deviceId, mSurfaceView, mStreamType, new OnVMSNetSDKBusiness() {
                    @Override
                    public void onFailure() {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_FAILURE);
                    }

                    @Override
                    public void onSuccess(Object obj) {
                        mHandler.sendEmptyMessage(GET_CAMERA_INFO_SUCCESS);
                    }
                });
                Looper.loop();
            }
        }.start();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (deviceId != null && !"".equals(deviceId)) {
            stopPreview();

            startPreview();
        }
    }

    // 停止预览按钮点击操作
    public void stopPreview() {
        boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(1);
        if (stopLiveResult) {
            UIUtils.showToast(this, R.string.live_stop_success);
        }
    }

    @Override
    protected void onActivityResult(int req, int res, Intent data) {
        if (req == 99 && res == 99) {
            deviceId = data.getStringExtra("sysCode");
        }
    }

    // 录像文件路径
    private String recordVideoPath = "";


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.live_capture) {// 抓拍按钮点击操作
            String finName = "Picture" + System.currentTimeMillis() + ".jpg";

            int opt = VMSNetSDK.getInstance().captureLiveOpt(PLAY_WINDOW_ONE, FileUtils.getPictureDirPath().getAbsolutePath(), finName);
            switch (opt) {
                case LiveSDKConstant.SD_CARD_UN_USABLE:
                    UIUtils.showToast(this, R.string.sd_card_fail);
                    break;
                case LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                    UIUtils.showToast(this, R.string.sd_card_not_enough);
                    break;
                case LiveSDKConstant.CAPTURE_FAILED:
                    UIUtils.showToast(this, R.string.capture_fail);
                    break;
                case LiveSDKConstant.CAPTURE_SUCCESS:
                    UIUtils.showToast(this, "抓图成功：" + FileUtils.getPictureDirPath().getAbsolutePath() + "/" + finName);
                    break;
            }
        } else if (id == R.id.btn_recod_video) {// 录像按钮点击操作
            if (!mIsRecord) {
                recordVideoPath = "Video" + System.currentTimeMillis() + ".mp4";

                int recordOpt = VMSNetSDK.getInstance().startLiveRecordOpt(PLAY_WINDOW_ONE, FileUtils.getVideoDirPath().getAbsolutePath(), recordVideoPath);
                switch (recordOpt) {
                    case LiveSDKConstant.SD_CARD_UN_USABLE:
                        UIUtils.showToast(this, R.string.sd_card_fail);
                        break;
                    case LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
                        UIUtils.showToast(this, R.string.sd_card_not_enough);
                        break;
                    case LiveSDKConstant.RECORD_FAILED:
                        mIsRecord = false;
                        UIUtils.showToast(this, R.string.start_record_fail);
                        break;
                    case LiveSDKConstant.RECORD_SUCCESS:
                        mIsRecord = true;
                        UIUtils.showToast(this, R.string.start_record_success);

                        mIvRecord.setImageResource(R.drawable.icon_recod_video_select);
                        mTvRecord.setTextColor(getResources().getColor(R.color.colorBlue));
                        break;
                }
            } else {
                VMSNetSDK.getInstance().stopLiveRecordOpt(PLAY_WINDOW_ONE);
                mIsRecord = false;
                UIUtils.showToast(this, "录像成功：" + FileUtils.getVideoDirPath().getAbsolutePath() + "/" + recordVideoPath);

                mIvRecord.setImageResource(R.drawable.icon_recod_video);
                mTvRecord.setTextColor(getResources().getColor(R.color.colorDark));
            }
        } else if (id == R.id.btn_voice) {// 音频按钮点击操作
            if (mIsAudioOpen) {
                boolean audioOpt = VMSNetSDK.getInstance().stopLiveAudioOpt(PLAY_WINDOW_ONE);
                if (audioOpt) {
                    mIsAudioOpen = false;
                    UIUtils.showToast(this, R.string.stop_Audio);

                    mIvAudio.setImageResource(R.drawable.icon_voice);
                    mTvAudio.setTextColor(getResources().getColor(R.color.colorDark));
                }
            } else {
                boolean ret = VMSNetSDK.getInstance().startLiveAudioOpt(PLAY_WINDOW_ONE);
                if (!ret) {
                    mIsAudioOpen = false;
                    UIUtils.showToast(LiveActivity.this, R.string.start_Audio_fail);

                    mIvAudio.setImageResource(R.drawable.icon_voice);
                    mTvAudio.setTextColor(getResources().getColor(R.color.colorDark));

                } else {
                    mIsAudioOpen = true;
                    // 开启音频成功，并不代表一定有声音，需要设备开启声音。
                    UIUtils.showToast(LiveActivity.this, R.string.start_Audio_success);

                    mIvAudio.setImageResource(R.drawable.icon_voice_select);
                    mTvAudio.setTextColor(getResources().getColor(R.color.colorBlue));
                }
            }
        } else if (id == R.id.btn_talk) {
            if (mIsTalkOpen) {
                VMSNetSDK.getInstance().closeLiveTalkOpt(PLAY_WINDOW_ONE);
                mHandler.sendEmptyMessage(CLOSE_TALK_SUCCESS);
            } else {
                talkChannels = VMSNetSDK.getInstance().getTalkChannelsOpt(PLAY_WINDOW_ONE);
                if (talkChannels <= 0) {
                    UIUtils.showToast(LiveActivity.this, R.string.no_Talk_channels);
                } else if (talkChannels > 1) {
                    showChannelSelectDialog();
                } else {
                    channelNo = 1;
                    startTalk();
                }
            }
        } else if (id == R.id.btn_clarity) { // 码流按钮显示
            optClarityLayout();
        } else if (id == R.id.tv_hight) { // 高清
            if (mStreamType != LiveSDKConstant.MAIN_HIGH_STREAM) {
                mStreamType = LiveSDKConstant.MAIN_HIGH_STREAM;
                // 先停止
                stopPreview();
                // 开始播放
                startPreview();
                // 初始化文字
                initStremText();
            }
        } else if (id == R.id.tv_mid) { // 标清
            if (mStreamType != LiveSDKConstant.SUB_STREAM) {
                mStreamType = LiveSDKConstant.SUB_STREAM;
                // 先停止
                stopPreview();
                // 开始播放
                startPreview();
                // 初始化文字
                initStremText();
            }
        } else if (id == R.id.tv_low) { // 流畅
            if (mStreamType != LiveSDKConstant.SUB_STANDARD_STREAM) {
                mStreamType = LiveSDKConstant.SUB_STANDARD_STREAM;
                // 先停止
                stopPreview();
                // 开始播放
                startPreview();
                // 初始化文字
                initStremText();
            }
        } else if (id == R.id.btn_platform) { // 显示或者影藏 云台控制按钮
            optControlLayout();
        } else if (id == R.id.btn_move) { // 移动
            optMoveMenu();
        } else if (id == R.id.btn_focal) { // 焦距
            if (optType != 1) {
                restChangeMenu();
            }

            optChangeMenu(1);
        } else if (id == R.id.btn_aperture) { // 光圈
            if (optType != 2) {
                restChangeMenu();
            }

            optChangeMenu(2);
        } else if (id == R.id.btn_changing) { // 变倍
            if (optType != 3) {
                restChangeMenu();
            }

            optChangeMenu(3);
        }
    }

    // 重置第二层按钮菜单
    private void restChangeMenu() {
        // 关闭移动菜单
        mIsShowMove = true;
        optMoveMenu();

        mVThirdMenu.setVisibility(View.GONE);
        isShowThirdMenu = false;

        mIvFocal.setImageResource(R.drawable.icon_focal);
        mTvFocal.setTextColor(getResources().getColor(R.color.colorDark));

        mIvAperture.setImageResource(R.drawable.icon_aperture);
        mTvAperture.setTextColor(getResources().getColor(R.color.colorDark));

        mIvChanging.setImageResource(R.drawable.icon_changing);
        mTvChanging.setTextColor(getResources().getColor(R.color.colorDark));
    }

    /**
     * 显示聚焦 光圈 变倍 点击出现的按钮按钮
     */
    private int optType = 1;
    private boolean isShowThirdMenu;

    private void optChangeMenu(int type) {
        this.optType = type;

        switch (type) {
            case 1: // 焦距
                mTvOptText.setText("焦距");

                if (isShowThirdMenu) {
                    isShowThirdMenu = false;
                    mVThirdMenu.setVisibility(View.GONE);

                    mIvFocal.setImageResource(R.drawable.icon_focal);
                    mTvFocal.setTextColor(getResources().getColor(R.color.colorDark));
                } else {
                    isShowThirdMenu = true;
                    mVThirdMenu.setVisibility(View.VISIBLE);

                    mIvFocal.setImageResource(R.drawable.icon_focal_d);
                    mTvFocal.setTextColor(getResources().getColor(R.color.colorBlue));
                }
                break;
            case 2: // 光圈
                mTvOptText.setText("光圈");

                if (isShowThirdMenu) {
                    isShowThirdMenu = false;
                    mVThirdMenu.setVisibility(View.GONE);

                    mIvAperture.setImageResource(R.drawable.icon_aperture);
                    mTvAperture.setTextColor(getResources().getColor(R.color.colorDark));
                } else {
                    isShowThirdMenu = true;
                    mVThirdMenu.setVisibility(View.VISIBLE);

                    mIvAperture.setImageResource(R.drawable.icon_aperture_d);
                    mTvAperture.setTextColor(getResources().getColor(R.color.colorBlue));
                }
                break;
            case 3: // 变倍
                mTvOptText.setText("变倍");

                if (isShowThirdMenu) {
                    isShowThirdMenu = false;
                    mVThirdMenu.setVisibility(View.GONE);

                    mIvChanging.setImageResource(R.drawable.icon_changing);
                    mTvChanging.setTextColor(getResources().getColor(R.color.colorDark));
                } else {
                    isShowThirdMenu = true;
                    mVThirdMenu.setVisibility(View.VISIBLE);

                    mIvChanging.setImageResource(R.drawable.icon_changing_d);
                    mTvChanging.setTextColor(getResources().getColor(R.color.colorBlue));
                }
                break;
        }
    }

    // 是否显示移动菜单
    public void optMoveMenu() {
        if (mIsShowMove) {
            mIsShowMove = false;
            mVMoveMenu.setVisibility(View.GONE);

            mIvMove.setImageResource(R.drawable.icon_move);
            mTvMove.setTextColor(getResources().getColor(R.color.colorDark));
        } else {
            mIsShowMove = true;

            mVMoveMenu.setVisibility(View.VISIBLE);

            mIvMove.setImageResource(R.drawable.icon_move_select);
            mTvMove.setTextColor(getResources().getColor(R.color.colorBlue));
        }
    }

    // 初始化码流文字
    private void initStremText() {
        if (mStreamType == LiveSDKConstant.MAIN_HIGH_STREAM) {
            mTvHigh.setTextColor(getResources().getColor(R.color.black));
            mTvMid.setTextColor(getResources().getColor(R.color.colorDark));
            mTvLow.setTextColor(getResources().getColor(R.color.colorDark));
        } else if (mStreamType == LiveSDKConstant.SUB_STREAM) {
            mTvMid.setTextColor(getResources().getColor(R.color.black));
            mTvHigh.setTextColor(getResources().getColor(R.color.colorDark));
            mTvLow.setTextColor(getResources().getColor(R.color.colorDark));
        } else if (mStreamType == LiveSDKConstant.SUB_STANDARD_STREAM) {
            mTvLow.setTextColor(getResources().getColor(R.color.black));
            mTvMid.setTextColor(getResources().getColor(R.color.colorDark));
            mTvHigh.setTextColor(getResources().getColor(R.color.colorDark));
        }
    }

    /***
     * 云台控制菜单显示按钮点击事件
     *
     * @param isShowPTZ
     *            是否显示云台控制菜单
     */
    private boolean isShowPTZ = false;

    private void optControlLayout() {
        if (isShowPTZ) {

            restChangeMenu();

            isShowPTZ = false;
            mIsShowClarity = false;

            subMenuPtz.setVisibility(View.GONE);
            subMenuClarity.setVisibility(View.GONE);

            mIvClarity.setImageResource(R.drawable.icon_clarity);
            mTvClarity.setTextColor(getResources().getColor(R.color.colorDark));

            mIvPaltform.setImageResource(R.drawable.icon_platform);
            mTvPaltform.setTextColor(getResources().getColor(R.color.colorDark));
        } else {
            isShowPTZ = true;
            mIsShowClarity = false;

            subMenuPtz.setVisibility(View.VISIBLE);
            subMenuClarity.setVisibility(View.GONE);

            mIvPaltform.setImageResource(R.drawable.icon_platform_selected);
            mTvPaltform.setTextColor(getResources().getColor(R.color.colorBlue));

            mIvClarity.setImageResource(R.drawable.icon_clarity);
            mTvClarity.setTextColor(getResources().getColor(R.color.colorDark));
        }
    }

    /***
     * 码流菜单是否显示
     *
     */
    private void optClarityLayout() {
        if (mIsShowClarity) {
            isShowPTZ = false;
            mIsShowClarity = false;

            restChangeMenu();
            subMenuPtz.setVisibility(View.GONE);
            subMenuClarity.setVisibility(View.GONE);

            mIvClarity.setImageResource(R.drawable.icon_clarity);
            mTvClarity.setTextColor(getResources().getColor(R.color.colorDark));

            mIvPaltform.setImageResource(R.drawable.icon_platform);
            mTvPaltform.setTextColor(getResources().getColor(R.color.colorDark));
        } else {
            isShowPTZ = false;
            mIsShowClarity = true;

            restChangeMenu();
            subMenuPtz.setVisibility(View.GONE);
            subMenuClarity.setVisibility(View.VISIBLE);

            mIvClarity.setImageResource(R.drawable.icon_clarity_select);
            mTvClarity.setTextColor(getResources().getColor(R.color.colorBlue));

            mIvPaltform.setImageResource(R.drawable.icon_platform);
            mTvPaltform.setTextColor(getResources().getColor(R.color.colorDark));

            initStremText();
        }
    }

    /**
     * 选择通道号开始语音对讲
     * @author lvlingdi 2016-5-18 上午10:29:36
     */
    private void showChannelSelectDialog() {
        // 创建对话框
        final AlertDialog mChannelSelectDialog = new AlertDialog.Builder(this).create();
        // 显示对话框
        mChannelSelectDialog.show();
        mChannelSelectDialog.setCanceledOnTouchOutside(false);
        final Window window = mChannelSelectDialog.getWindow();
        window.setContentView(R.layout.dialog_channle_select);
        RadioGroup channels = (RadioGroup) window.findViewById(R.id.rg_channels);

        for (int i = 1; i <= talkChannels; i++) {
            RadioButton rb = new RadioButton(window.getContext());
            rb.setTag(i);
            // 应ui设计要求，自定义RadioButton样式图片
            rb.setButtonDrawable(R.drawable.selector_radiobtn);
            String name = getResources().getString(R.string.analog_channel, i);
            rb.setText(name);
            rb.setPadding(0, 10, 10, 10);
            channels.addView(rb);
        }

        channels.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                RadioButton rb = (RadioButton) window.findViewById(radioButtonId);
                channelNoTemp = rb.getTag().toString();
            }
        });
        Button cancel_btn = (Button) window.findViewById(R.id.cancel_btn);
        cancel_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                mChannelSelectDialog.cancel();
            }
        });

        Button confirm_btn = (Button) window.findViewById(R.id.confirm_btn);
        confirm_btn.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                channelNo = Integer.valueOf(channelNoTemp);
                startTalk();
                mChannelSelectDialog.cancel();
            }
        });
    }

    /**
     * 开启语音播放
     */
    private void startTalk() {
        VMSNetSDK.getInstance().openLiveTalkOpt(PLAY_WINDOW_ONE, channelNo, new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(OPEN_TALK_FAILURE);
            }

            @Override
            public void onSuccess(Object obj) {
                mHandler.sendEmptyMessage(OPEN_TALK_SUCCESS);
            }
        });
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 页面销毁时停止预览
        boolean stopLiveResult = VMSNetSDK.getInstance().stopLiveOpt(1);
        if (stopLiveResult) {
            UIUtils.showToast(this, R.string.live_stop_success);
        }
    }

    // 处理云台的操作按钮
    private int mPtzCommand;
    class ButtonListener implements OnTouchListener {
        public boolean onTouch(View v, MotionEvent event) {

            int id = v.getId();
            if (id == R.id.iv_up) {
                mPtzCommand = PTZCommandConstant.CUSTOM_CMD_UP;
            } else if (id == R.id.iv_down) {
                mPtzCommand = PTZCommandConstant.CUSTOM_CMD_DOWN;
            } else if (id == R.id.iv_move_left) {
                mPtzCommand = PTZCommandConstant.CUSTOM_CMD_LEFT;
            } else if (id == R.id.iv_right) {
                mPtzCommand = PTZCommandConstant.CUSTOM_CMD_RIGHT;
            } else if (id == R.id.iv_opt_plus) { // 加
                if (optType == 1) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_FOCUS_NEAR;
                } else if (optType == 2) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_IRIS_UP;
                } else if (optType == 3) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_ZOOM_IN;
                }
            } else if (id == R.id.iv_opt_des) { // 减
                if (optType == 1) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_FOCUS_FAR;
                } else if (optType == 2) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_IRIS_DOWN;
                } else if (optType == 3) {
                    mPtzCommand = PTZCommandConstant.CUSTOM_CMD_ZOOM_OUT;
                }
            }

            switch (event.getAction()) {
                case MotionEvent.ACTION_UP:// 松开事件发生后执行代码的区域
                    // 停止云台操作
                    VMSNetSDK.getInstance().sendPTZCtrlCommand(PLAY_WINDOW_ONE, true, PTZCommandConstant.ACTION_STOP, mPtzCommand, 256, new OnVMSNetSDKBusiness() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(LiveActivity.this, R.string.ptz_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Object obj) {
                            Toast.makeText(LiveActivity.this, R.string.ptz_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
                case MotionEvent.ACTION_DOWN:// 按住事件发生后执行代码的区域
                    // 开始云台操作
                    VMSNetSDK.getInstance().sendPTZCtrlCommand(PLAY_WINDOW_ONE, true, PTZCommandConstant.ACTION_START, mPtzCommand, 256, new OnVMSNetSDKBusiness() {
                        @Override
                        public void onFailure() {
                            Toast.makeText(LiveActivity.this, R.string.ptz_fail, Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onSuccess(Object obj) {
                            Toast.makeText(LiveActivity.this, R.string.ptz_success, Toast.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            return true;

        }

    }
}
