package com.ev.hikvideo.activity;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ev.hikvideo.R;
import com.ev.hikvideo.util.ToolUtils;
import com.ev.hikvideo.util.UIUtils;
import com.ev.hikvideo.view.CustomSurfaceView;
import com.ev.hikvideo.view.ObservableScrollView;
import com.ev.hikvideo.view.TimeScrollView;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.SDKConstant;
import com.hikvision.sdk.consts.SDKConstant.PlayBackSDKConstant;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.CustomRect;
import com.hikvision.sdk.net.bean.PlaybackSpeed;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.FileUtils;
import com.hikvision.sdk.utils.SDKUtil;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

/**
 * <p>
 * 远程回放Activity
 * </p>
 *
 * @author zhangwei59 2017/3/10 14:32
 * @version V1.0.0
 */
public class PlayBackActivity extends BaseActivity implements OnClickListener, SurfaceHolder.Callback {

	/**
	 * 获取监控点信息成功
	 */
	private static final int CAMERA_INFO_SUCCESS = 1;
	/**
	 * 获取监控点信息失败
	 */
	private static final int CAMERA_INFO_FAILURE = 2;
	/**
	 * 查找录像片段成功
	 */
	private static final int QUERY_SUCCESS = 3;
	/**
	 * 查找录像片段失败
	 */
	private static final int QUERY_FAILURE = 4;
	/**
	 * 开启回放成功
	 */
	public static final int START_SUCCESS = 5;
	/**
	 * 开启回放失败
	 */
	public static final int START_FAILURE = 6;
	/**
	 * 回放结束
	 */
	public static final int STOP_PLAY = 7;
	/**
	 * 进度条最大值
	 */
	private static final int PROGRESS_MAX_VALUE = 100;

	/**
	 * 播放视图控件
	 */
	private CustomSurfaceView mSurfaceView;
	/**
	 * 播放或者暂停按钮
	 */
	private ImageView mIvPlayOrPause;
	/**
	 * 录像按钮
	 */
	private ImageView mIvRecord;
	/**
	 * 音频按钮
	 */
	private ImageView mIvSound;
	/**
	 * 电子放大控件
	 */
	private ImageView mIvZoom;

	/**
	 * 是否暂停标志
	 */
	private boolean mIsPause;
	/**
	 * 音频是否开启
	 */
	private boolean mIsAudioOpen;
	/**
	 * 是否正在录像
	 */
	private boolean mIsRecord;
	/**
	 * 监控点详情
	 */
	private CameraInfo mCameraInfo;
	/**
	 * 存储介质
	 */
	private int mStorageType;
	/**
	 * 录像唯一标识Guid
	 */
	private String mGuid;
	/**
	 * 录像详情
	 */
	private RecordInfo mRecordInfo;
	/**
	 * 初始开始时间
	 */
	private Calendar mFirstStartTime;
	/**
	 * 开始时间
	 */
	private Calendar mStartTime;
	/**
	 * 结束时间
	 */
	private Calendar mEndTime;
	/**
	 * 录像片段
	 */
	private RecordSegment mRecordSegment;
	/**
	 * 定时器
	 */
	private Timer mUpdateTimer = null;
	/**
	 * 定时器执行的任务
	 */
	private TimerTask mUpdateTimerTask = null;
	/**
	 * 创建消息对象
	 */
	private Handler mMessageHandler;

	/**
	 * 播放窗口1
	 */
	private int PLAY_WINDOW_ONE = 1;

	private String deviceId = ""; // 摄像头码
	private JSONObject param = null; //传递数据

	/**
	 * 播放速度
	 */
	private ImageView mIvSpeed;
	private ImageView mIvSpeed14;
	private ImageView mIvSpeed12;
	private ImageView mIvSpeed1;
	private ImageView mIvSpeed2;
	private ImageView mIvSpeed4;
	private View mVSpeed;

	/**
	 * 播放时间
	 */
	private TimeScrollView mSvTime;

	/***
	 * UI处理Handler
	 */
	@SuppressLint("HandlerLeak")
	private class MyHandler extends Handler {
		WeakReference<PlayBackActivity> mActivityReference;

		MyHandler(PlayBackActivity activity) {
			mActivityReference = new WeakReference<>(activity);
		}

		public void handleMessage(Message msg) {
			PlayBackActivity activity = mActivityReference.get();
			if (activity != null) {
				switch (msg.what) {
					case CAMERA_INFO_SUCCESS:
						UIUtils.cancelProgressDialog();
						// 解析监控点录像信息
						int[] mRecordPos = SDKUtil.processStorageType(activity.mCameraInfo);
						String[] mGuids = SDKUtil.processGuid(activity.mCameraInfo);
						// 默认选取第一种存储类型进行查询 默认第一种存储介质
						if (null != mRecordPos && 0 < mRecordPos.length) {
							activity.mStorageType = mRecordPos[0];
						}
						if (null != mGuids && 0 < mGuids.length) {
							activity.mGuid = mGuids[0];
						}
						if (null != mRecordPos && 0 < mRecordPos.length) {
							activity.queryRecordSegment();
						} else {
							UIUtils.showToast(activity, "录像文件查询失败");
						}
						break;
					case CAMERA_INFO_FAILURE:
						UIUtils.cancelProgressDialog();
						UIUtils.showToast(activity, R.string.loading_camera_info_failure);
						activity.finish();
						break;
					case QUERY_SUCCESS:
						// 录像片段查询成功
						UIUtils.cancelProgressDialog();
						UIUtils.showToast(activity, "录像文件查询成功");
						initTimeView();
						// 播放视频
						play();
						break;
					case QUERY_FAILURE:
						UIUtils.cancelProgressDialog();
						UIUtils.showToast(activity, "录像文件查询失败");
						break;
					case START_SUCCESS:
						UIUtils.cancelProgressDialog();
						mIvPlayOrPause.setImageResource(R.drawable.icon_pause);
						UIUtils.showToast(activity, R.string.rtsp_success);
						PlayBackActivity.this.startUpdateTimer();
						break;
					case START_FAILURE:
						UIUtils.cancelProgressDialog();
						UIUtils.showToast(activity, R.string.rtsp_fail);
						break;
					case STOP_PLAY:
						UIUtils.showToast(PlayBackActivity.this, R.string.play_back_finish);
						mIvPlayOrPause.setImageResource(R.drawable.icon_play);
						break;
					case PlayBackSDKConstant.MSG_REMOTELIST_UI_UPDATE:
						// 更新播放进度条
						activity.updateRemotePlayUI();
						break;

				}
			}
		}
	}

	/**
	 * 参数 {
	 *      titlebar-color:'#333333', //状态栏颜色
	 *      device-id: 'xxx',     //摄像头ID
	 *      city-name: '',        //地市
	 *      project-name: '',     //项目名称
	 *      construct-name: '',   //施工单位名称
	 *      unit-type-str: '',    //分项名称
	 *      device-name: '',      //设备名称
	 *      bind-time: ''         //绑定时间 2019-01-12 12:12:12
	 *      unbind-time: ''       //解绑时间
	 *      }
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.play_back);

		param = JSON.parseObject(getIntent().getStringExtra("dataJson"));
		deviceId = param.getString("device-id");

		initView();
		initData(param.getString("bind-time").split(" ")[0]);
		initBackBtn();
		setTitle("历史回放");

		initSiteData();
	}

	// 初始化时间组件
	public void initTimeView() {
		List<RecordSegment> records = mRecordInfo.getSegmentList();

		List<Map<String, String>> times = new ArrayList<Map<String, String>>();
		for (RecordSegment recordSegment : records) {
			String beginTime = recordSegment.getBeginTime();
			String endTime = recordSegment.getEndTime();

			Map<String, String> map = new HashMap<String, String>();
			map.put("startDate", beginTime);
			map.put("endDate", endTime);
			times.add(map);
		}
		mSvTime.initFillTime(times);
	}

	/**
	 * 初始化视图
	 */
	private void initView() {
		mSurfaceView = (CustomSurfaceView) findViewById(R.id.playbackSurfaceView);
		mSurfaceView.getHolder().addCallback(this);

		setRightImg(R.drawable.btn_right_time);

		// 开始按钮
		mIvPlayOrPause = (ImageView) findViewById(R.id.iv_play);
		mIvPlayOrPause.setOnClickListener(this);
		// 抓拍按钮
		findViewById(R.id.iv_snap).setOnClickListener(this);

		mIvRecord = (ImageView) findViewById(R.id.iv_record_video);
		mIvRecord.setOnClickListener(this);

		mIvSound = (ImageView) findViewById(R.id.iv_voice);
		mIvSound.setOnClickListener(this);

		mIvZoom = (ImageView) findViewById(R.id.iv_max);
		mIvZoom.setOnClickListener(this);

		mIvSpeed = (ImageView) findViewById(R.id.iv_speed);
		mIvSpeed.setOnClickListener(this);

		mIvSpeed14 = (ImageView) findViewById(R.id.iv_speed_1_4);
		mIvSpeed14.setOnClickListener(this);

		mIvSpeed12 = (ImageView) findViewById(R.id.iv_speed_1_2);
		mIvSpeed12.setOnClickListener(this);

		mIvSpeed1 = (ImageView) findViewById(R.id.iv_speed_1);
		mIvSpeed1.setOnClickListener(this);

		mIvSpeed2 = (ImageView) findViewById(R.id.iv_speed_2);
		mIvSpeed2.setOnClickListener(this);

		mIvSpeed4 = (ImageView) findViewById(R.id.iv_speed_4);
		mIvSpeed4.setOnClickListener(this);

		// 筛选
		findViewById(R.id.btn_right).setOnClickListener(this);

		mSvTime = (TimeScrollView) findViewById(R.id.sv_select_time);
		mVSpeed = findViewById(R.id.ll_change_speed);

		mSvTime.setOnScrollListener(new ObservableScrollView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(ObservableScrollView view, int scrollState) {
			}

			@Override
			public void onScroll(ObservableScrollView view, boolean isTouchScroll, int x, int y, int oldx, int oldy) {
			}

			@Override
			public void onTouchScrollDonw(int x, int y) {
				stopUpdateTimer();
			}

			@Override
			public void onTouchScrollUp(int x, int y) {
				VMSNetSDK.getInstance().stopPlayBackOpt(PLAY_WINDOW_ONE);
				int defaultDis = (int) (ToolUtils.getScreenWith(PlayBackActivity.this) / 2);
				int distance = ToolUtils.px2dip(PlayBackActivity.this, x) + defaultDis - 30;

				// 将distance 转换成时间
				int hour = distance / 60;
				int min = distance % 60;
				Log.i("HOUR_AND_MIN", hour + ":" + min);

				mStartTime.set(mStartTime.get(Calendar.YEAR), mStartTime.get(Calendar.MONTH), mStartTime.get(Calendar.DAY_OF_MONTH), hour, min, 0);
				VMSNetSDK.getInstance().startPlayBackOpt(PLAY_WINDOW_ONE, mSurfaceView, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime, new OnVMSNetSDKBusiness() {
					@Override
					public void onFailure() {
						mMessageHandler.sendEmptyMessage(START_FAILURE);
					}

					@Override
					public void onSuccess(Object obj) {
						mMessageHandler.sendEmptyMessage(START_SUCCESS);
					}

					@Override
					public void onStatusCallback(int status) {
						// 录像片段回放结束
						if (status == RtspClient.RTSPCLIENT_MSG_PLAYBACK_FINISH) {
							mMessageHandler.post(new Runnable() {
								@Override
								public void run() {
									UIUtils.showToast(PlayBackActivity.this, R.string.play_back_finish);
								}
							});
						}
					}
				});
			}
		});

	}

	/**
	 * 初始化数据
	 */
	private void initData(String date) {
		mMessageHandler = new MyHandler(this);
		// 监控点
		int year;
		int month;
		int day;
		if (date == null) {
			Calendar calendar = Calendar.getInstance();
			year = calendar.get(Calendar.YEAR);
			month = calendar.get(Calendar.MONTH);
			day = calendar.get(Calendar.DAY_OF_MONTH);
		} else {
			String[] yearMonthDay = date.split("-");
			year = Integer.parseInt(yearMonthDay[0]);
			month = Integer.parseInt(yearMonthDay[1]) - 1;
			day = Integer.parseInt(yearMonthDay[2]);
		}

		mFirstStartTime = Calendar.getInstance();
		mStartTime = Calendar.getInstance();
		mEndTime = Calendar.getInstance();
		mFirstStartTime.set(year, month, day, 0, 0, 0);
		mStartTime.set(year, month, day, 0, 0, 0);
		mEndTime.set(year, month, day, 23, 59, 59);
		getCameraInfo();
	}

	/**
	 * 获取监控点详细信息
	 */
	private void getCameraInfo() {
		UIUtils.showLoadingProgressDialog(this, R.string.loading_process_tip, false);
		VMSNetSDK.getInstance().getPlayBackCameraInfo(PLAY_WINDOW_ONE, deviceId, new OnVMSNetSDKBusiness() {
			@Override
			public void onFailure() {
				mMessageHandler.sendEmptyMessage(CAMERA_INFO_FAILURE);
			}

			@Override
			public void onSuccess(Object obj) {
				if (obj instanceof CameraInfo) {
					mCameraInfo = (CameraInfo) obj;
					mMessageHandler.sendEmptyMessage(CAMERA_INFO_SUCCESS);
				}
			}
		});
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		VMSNetSDK.getInstance().resumePlayBackOpt(PLAY_WINDOW_ONE);
		VMSNetSDK.getInstance().setVideoWindowOpt(PLAY_WINDOW_ONE, holder);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		VMSNetSDK.getInstance().pausePlayBackOpt(PLAY_WINDOW_ONE);
		VMSNetSDK.getInstance().setVideoWindowOpt(PLAY_WINDOW_ONE, null);
	}

	// 开始回放
	public void play() {
		if (mRecordInfo == null)
			return;
		UIUtils.showLoadingProgressDialog(this, R.string.loading_process_tip, false);
		VMSNetSDK.getInstance().startPlayBackOpt(PLAY_WINDOW_ONE, mSurfaceView, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime, new OnVMSNetSDKBusiness() {
			@Override
			public void onFailure() {
				mMessageHandler.sendEmptyMessage(START_FAILURE);
			}

			@Override
			public void onSuccess(Object obj) {
				mMessageHandler.sendEmptyMessage(START_SUCCESS);
			}

			@Override
			public void onStatusCallback(int status) {
				// 录像片段回放结束
				if (status == RtspClient.RTSPCLIENT_MSG_PLAYBACK_FINISH) {
					mMessageHandler.sendEmptyMessage(STOP_PLAY);
				}
			}
		});
	}

	/**
	 * 	 *      titlebar-color:'#333333', //状态栏颜色
	 * 	 *      device-id: 'xxx',     //摄像头ID
	 * 	 *      city-name: '',        //地市
	 * 	 *      project-name: '',     //项目名称
	 * 	 *      construct-name: '',   //施工单位名称
	 * 	 *      unit-type-str: '',    //分项名称
	 * 	 *      device-name: '',      //设备名称
	 * 	 *      bind-time: ''         //绑定时间
	 * 	 *      unbind-time: ''       //解绑时间
	 */
	private void initSiteData() {
		String color = param.getString("titlebar-color");
		if(color != null && !"".equals(color)) {
			this.findViewById(R.id.rl_header).setBackgroundColor(Color.parseColor(color));
		}

		TextView mTvSiteName = findViewById(R.id.tv_site_name);
		TextView mTvConstructName = findViewById(R.id.tv_construct_name);
		TextView mTvDeviceName = findViewById(R.id.tv_device_name);
		TextView mTvUnitName = findViewById(R.id.tv_unit_name);
		TextView mTvStartTime = findViewById(R.id.tv_start_time);
		TextView mTvEndTime = findViewById(R.id.tv_end_time);

		mTvSiteName.setText("项目名称：" + param.getString("project-name"));
		mTvConstructName.setText("施工单位：" + param.getString("construct-name"));
		mTvDeviceName.setText("设备名称：" + param.getString("device-name"));
		mTvUnitName.setText("分项名称：" + param.getString("unit-type-str"));
		mTvStartTime.setText("绑定时间：" + param.getString("bind-time"));
		mTvEndTime.setText("解绑时间：" + param.getString("unbind-time"));
	}

	// 停止播放
	public void stopPlay() {
		// 停止回放按钮点击操作
		boolean stopPlayBackOpt = VMSNetSDK.getInstance().stopPlayBackOpt(PLAY_WINDOW_ONE);
		if (stopPlayBackOpt) {
			stopUpdateTimer();
			UIUtils.showToast(this, R.string.live_stop_success);
		}
	}


	private boolean isZoom = false;
	private boolean isSpeed = false;
	private int speed = 3; // 1- 1/4倍速 2-1/2倍速 3-1倍速 4-2倍速 5-4倍速;

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.iv_play) {
			if (!mIsPause) {
				boolean pausePlayBackOpt = VMSNetSDK.getInstance().pausePlayBackOpt(PLAY_WINDOW_ONE);
				if (pausePlayBackOpt) {
					UIUtils.showToast(this, "暂停成功");
					mIvPlayOrPause.setImageResource(R.drawable.icon_play);
					mIsPause = true;
				} else {
					UIUtils.showToast(this, "暂停失败");
					mIvPlayOrPause.setImageResource(R.drawable.icon_pause);
					mIsPause = false;
				}
			} else {
				boolean resumePlayBackOpt = VMSNetSDK.getInstance().resumePlayBackOpt(PLAY_WINDOW_ONE);
				if (resumePlayBackOpt) {
					UIUtils.showToast(this, "恢复播放成功");
					mIvPlayOrPause.setImageResource(R.drawable.icon_pause);
					mIsPause = false;
				} else {
					UIUtils.showToast(this, "恢复播放失败");
					mIvPlayOrPause.setImageResource(R.drawable.icon_play);
					mIsPause = true;
				}
			}
		} else if (id == R.id.iv_snap) {// 抓拍按钮点击操作
			String path = "Picture" + System.currentTimeMillis() + ".jpg";
			int opt = VMSNetSDK.getInstance().capturePlaybackOpt(PLAY_WINDOW_ONE, FileUtils.getPictureDirPath().getAbsolutePath(), path);
			switch (opt) {
				case PlayBackSDKConstant.SD_CARD_UN_USABLE:
					UIUtils.showToast(this, R.string.sd_card_fail);
					break;
				case PlayBackSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
					UIUtils.showToast(this, R.string.sd_card_not_enough);
					break;
				case PlayBackSDKConstant.CAPTURE_FAILED:
					UIUtils.showToast(this, R.string.capture_fail);
					break;
				case PlayBackSDKConstant.CAPTURE_SUCCESS:
					UIUtils.showToast(this, "抓图成功:" + FileUtils.getPictureDirPath().getAbsolutePath() + path);
					break;
			}
		} else if (id == R.id.iv_record_video) {// 录像按钮点击操作
			if (!mIsRecord) {
				String path2 = "Video" + System.currentTimeMillis() + ".mp4";
				int recordOpt = VMSNetSDK.getInstance().startPlayBackRecordOpt(PLAY_WINDOW_ONE, FileUtils.getVideoDirPath().getAbsolutePath(), path2);
				switch (recordOpt) {
					case SDKConstant.LiveSDKConstant.SD_CARD_UN_USABLE:
						UIUtils.showToast(this, R.string.sd_card_fail);
						break;
					case SDKConstant.LiveSDKConstant.SD_CARD_SIZE_NOT_ENOUGH:
						UIUtils.showToast(this, R.string.sd_card_not_enough);
						break;
					case PlayBackSDKConstant.RECORD_FAILED:
						mIsRecord = false;
						UIUtils.showToast(this, R.string.start_record_fail);
						break;
					case PlayBackSDKConstant.RECORD_SUCCESS:
						mIsRecord = true;
						UIUtils.showToast(this, "录像成功:" + FileUtils.getPictureDirPath().getAbsolutePath() + path2);
						mIvRecord.setImageResource(R.drawable.icon_recod_video_select);
						break;
				}
			} else {
				VMSNetSDK.getInstance().stopPlayBackRecordOpt(PLAY_WINDOW_ONE);
				mIsRecord = false;
				UIUtils.showToast(PlayBackActivity.this, R.string.stop_record_success);
				mIvRecord.setImageResource(R.drawable.icon_recod_video);
			}
		} else if (id == R.id.iv_voice) {// 音频按钮点击操作
			if (mIsAudioOpen) {
				VMSNetSDK.getInstance().stopPlayBackAudioOpt(PLAY_WINDOW_ONE);
				mIsAudioOpen = false;
				UIUtils.showToast(PlayBackActivity.this, "关闭音频");

				mIvSound.setImageResource(R.drawable.icon_voice);
			} else {
				boolean retAudio = VMSNetSDK.getInstance().startPlayBackAudioOpt(PLAY_WINDOW_ONE);
				if (!retAudio) {
					mIsAudioOpen = false;
					UIUtils.showToast(PlayBackActivity.this, "开启音频失败");
				} else {
					mIsAudioOpen = true;
					// 开启音频成功，并不代表一定有声音，需要设备开启声音。
					UIUtils.showToast(PlayBackActivity.this, "开启音频成功");
					mIvSound.setImageResource(R.drawable.icon_voice_select);
				}
			}
		} else if (id == R.id.iv_max) {// 电子放大选中操作
			if (isZoom) {
				mSurfaceView.setOnZoomListener(null);
				VMSNetSDK.getInstance().zoomPlayBackOpt(PLAY_WINDOW_ONE, false, null, null);
				mIvZoom.setImageResource(R.drawable.icon_max);
				isZoom = false;
			} else {
				isZoom = true;
				mSurfaceView.setOnZoomListener(new CustomSurfaceView.OnZoomListener() {
					@Override
					public void onZoomChange(CustomRect original, CustomRect current) {
						VMSNetSDK.getInstance().zoomPlayBackOpt(PLAY_WINDOW_ONE, true, original, current);
					}
				});
				mIvZoom.setImageResource(R.drawable.icon_max_select);
			}
		} else if (id == R.id.iv_speed) { // 点击速度
			// 关闭速度
			if (isSpeed) {
				isSpeed = false;
				mIvSpeed.setImageResource(R.drawable.icon_1x);
				mVSpeed.setVisibility(View.GONE);

				setNoneSpeedView();
				switch (speed) {
					case 1:
						mIvSpeed14.setImageResource(R.drawable.icon_speed_1_4_select);
						break;
					case 2:
						mIvSpeed12.setImageResource(R.drawable.icon_speed_1_2_select);
						break;
					case 3:
						mIvSpeed1.setImageResource(R.drawable.icon_speed_1_select);
						break;
					case 4:
						mIvSpeed2.setImageResource(R.drawable.icon_speed_2_select);
						break;
					case 5:
						mIvSpeed4.setImageResource(R.drawable.icon_speed_4_select);
						break;
				}
			} else {
				isSpeed = true;
				mIvSpeed.setImageResource(R.drawable.icon_1x_select);
				mVSpeed.setVisibility(View.VISIBLE);
			}
		} else if (id == R.id.iv_speed_1_4) { // 1/4倍速
			speed = 1;
			setNoneSpeedView();
			mIvSpeed14.setImageResource(R.drawable.icon_speed_1_4_select);
			if (!mIsPause) {
				VMSNetSDK.getInstance().setPlaybackSpeed(PLAY_WINDOW_ONE, PlaybackSpeed.QUARTER);
			} else {
				Toast.makeText(this, "非播放状态不能调节倍速！", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.iv_speed_1_2) { // 1/2倍速
			speed = 2;
			setNoneSpeedView();
			mIvSpeed12.setImageResource(R.drawable.icon_speed_1_2_select);
			if (!mIsPause) {
				VMSNetSDK.getInstance().setPlaybackSpeed(PLAY_WINDOW_ONE, PlaybackSpeed.HALF);
			} else {
				Toast.makeText(this, "非播放状态不能调节倍速！", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.iv_speed_1) { // 1倍速
			speed = 3;
			setNoneSpeedView();
			mIvSpeed1.setImageResource(R.drawable.icon_speed_1_select);
			if (!mIsPause) {
				VMSNetSDK.getInstance().setPlaybackSpeed(PLAY_WINDOW_ONE, PlaybackSpeed.NORMAL);
			} else {
				Toast.makeText(this, "非播放状态不能调节倍速！", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.iv_speed_2) { // 2倍速
			speed = 4;
			setNoneSpeedView();
			mIvSpeed2.setImageResource(R.drawable.icon_speed_2_select);
			if (!mIsPause) {
				VMSNetSDK.getInstance().setPlaybackSpeed(PLAY_WINDOW_ONE, PlaybackSpeed.DOUBLE);
			} else {
				Toast.makeText(this, "非播放状态不能调节倍速！", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.iv_speed_4) { // 4倍速
			speed = 5;
			setNoneSpeedView();
			mIvSpeed4.setImageResource(R.drawable.icon_speed_4_select);
			if (!mIsPause) {
				VMSNetSDK.getInstance().setPlaybackSpeed(PLAY_WINDOW_ONE, PlaybackSpeed.FOUR);
			} else {
				Toast.makeText(this, "非播放状态不能调节倍速！", Toast.LENGTH_SHORT).show();
			}
		} else if (id == R.id.btn_right) { // 筛选
			Calendar calendar=Calendar.getInstance();
			new DatePickerDialog( this, new DatePickerDialog.OnDateSetListener() {
				@Override
				public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
					stopPlay();
					initData(year + "-" + (month +1) +"-"+dayOfMonth);
				}
			},calendar.get(Calendar.YEAR) ,calendar.get(Calendar.MONTH) ,calendar.get(Calendar.DAY_OF_MONTH)).show();
		}
	}

	private void setNoneSpeedView() {
		mIvSpeed14.setImageResource(R.drawable.icon_speed_1_4);
		mIvSpeed12.setImageResource(R.drawable.icon_speed_1_2);
		mIvSpeed1.setImageResource(R.drawable.icon_speed_1);
		mIvSpeed2.setImageResource(R.drawable.icon_speed_2);
		mIvSpeed4.setImageResource(R.drawable.icon_speed_4);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		stopPlay();
	}

	/**
	 * 查找录像片段
	 */
	private void queryRecordSegment() {
		if (null == mCameraInfo) {
			return;
		}
		UIUtils.showLoadingProgressDialog(this, R.string.loading_process_tip, false);
		VMSNetSDK.getInstance().queryRecordSegment(PLAY_WINDOW_ONE, mCameraInfo, mStartTime, mEndTime, mStorageType, mGuid, new OnVMSNetSDKBusiness() {
			@Override
			public void onFailure() {
				mMessageHandler.sendEmptyMessage(QUERY_FAILURE);
			}

			@Override
			public void onSuccess(Object obj) {
				if (obj instanceof RecordInfo) {
					mRecordInfo = ((RecordInfo) obj);
					if (null != mRecordInfo.getSegmentList() && 0 < mRecordInfo.getSegmentList().size()) {
						mRecordSegment = mRecordInfo.getSegmentList().get(0);
						// 级联设备的时候使用录像片段中的时间
						if (SDKConstant.CascadeFlag.CASCADE == mCameraInfo.getCascadeFlag()) {
							mEndTime = SDKUtil.convertTimeString(mRecordSegment.getEndTime());
							mStartTime = SDKUtil.convertTimeString(mRecordSegment.getBeginTime());
							mFirstStartTime = mStartTime;
						}
						mMessageHandler.sendEmptyMessage(QUERY_SUCCESS);
					} else {
						mMessageHandler.sendEmptyMessage(QUERY_FAILURE);
					}
				}
			}
		});

	}

	/***
	 * 更新播放库UI
	 */
	private void updateRemotePlayUI() {
		// 获取播放进度
		long osd = VMSNetSDK.getInstance().getOSDTimeOpt(PLAY_WINDOW_ONE);
		if (osd != -1) {
			handlePlayProgress(osd);
		}
	}

	/***
	 * 更新播放进度
	 *
	 * @param osd
	 *            播放进度
	 */
	private void handlePlayProgress(long osd) {
		// 更新时间组件
		mSvTime.setDate(ToolUtils.formatDate("yyyy-MM-dd HH:mm:ss", new Date(osd)));
	}

	/**
	 * 启动定时器
	 */
	private void startUpdateTimer() {
		stopUpdateTimer();
		// 开始录像计时
		mUpdateTimer = new Timer();
		mUpdateTimerTask = new TimerTask() {
			@Override
			public void run() {
				mMessageHandler.sendEmptyMessage(PlayBackSDKConstant.MSG_REMOTELIST_UI_UPDATE);
			}
		};
		// 延时1000ms后执行，1000ms执行一次
		mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
	}

	/**
	 * 停止定时器
	 */
	private void stopUpdateTimer() {
		if (mUpdateTimer != null) {
			mUpdateTimer.cancel();
			mUpdateTimer = null;
		}

		if (mUpdateTimerTask != null) {
			mUpdateTimerTask.cancel();
			mUpdateTimerTask = null;
		}
	}
}
