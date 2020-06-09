package com.ev.hikvideo.view;

import java.util.Calendar;
import java.util.List;
import java.util.Map;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ev.hikvideo.R;
import com.ev.hikvideo.util.ToolUtils;

public class TimeScrollView extends LinearLayout {

	ObservableScrollView hsv;
	TextView mTvCurrentTime;
	LinearLayout mLyoutTime;
	LinearLayout mLyoutTimeFill;

	Context context;

	public TimeScrollView(Context context) {
		super(context);
		init(context);
	}

	public TimeScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	@SuppressLint("NewApi")
	public TimeScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	private void init(Context context) {
		this.context = context;

		View v = View.inflate(context, R.layout.time_select, null);

		hsv = (ObservableScrollView) v.findViewById(R.id.hsv_main);
		mTvCurrentTime = (TextView) v.findViewById(R.id.tv_current_time);
		mLyoutTime = (LinearLayout) v.findViewById(R.id.ll_time);
		mLyoutTimeFill = (LinearLayout) v.findViewById(R.id.ll_time_fill);

		for (int i = 0; i < 25; i++) {
			View vTime = View.inflate(context, R.layout.item_time, null);
			TextView mTvTime = (TextView) vTime.findViewById(R.id.tv_time);
			String hour = i + "";
			if (i < 10) {
				hour = "0" + i;
			}
			mTvTime.setText(hour + ":00");
			mLyoutTime.addView(vTime);
		}
		this.addView(v);
	}
	
	
	public void setOnScrollListener(ObservableScrollView.OnScrollListener onScrollListener) {
		if(null != hsv)
			hsv.setOnScrollListener(onScrollListener);
	}


	public void initFillTime(List<Map<String, String>> timeList) {
		mLyoutTimeFill.removeAllViews();

		View vInitView = new View(context);
		LayoutParams p = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		p.width = ToolUtils.dip2px(context, 30);
		p.height = ToolUtils.dip2px(context, 15);
		vInitView.setLayoutParams(p);
		mLyoutTimeFill.addView(vInitView);

		int desDistance = 0;

		for (Map<String, String> map : timeList) {
			String startDate = map.get("startDate");
			String endDate = map.get("endDate");

			int startHour = Integer.parseInt(ToolUtils.formatDate("HH", ToolUtils.parseDate(startDate.split("T")[1], "HH:mm:ss.000")));
			int startMin = Integer.parseInt(ToolUtils.formatDate("mm", ToolUtils.parseDate(startDate.split("T")[1], "HH:mm:ss.000")));
			int startDistance = startHour * 60 + startMin;

			View vStart = new View(context);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.width = ToolUtils.dip2px(context, (startDistance - desDistance));
			params.height = ToolUtils.dip2px(context, 15);
			vStart.setLayoutParams(params);
			mLyoutTimeFill.addView(vStart);

			int endHour = Integer.parseInt(ToolUtils.formatDate("HH", ToolUtils.parseDate(endDate.split("T")[1], "HH:mm:ss.000")));
			int endMin = Integer.parseInt(ToolUtils.formatDate("mm", ToolUtils.parseDate(endDate.split("T")[1], "HH:mm:ss.000")));
			int endDistance = endHour * 60 + endMin;

			View vEnd = new View(context);
			LayoutParams params2 = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params2.width = ToolUtils.dip2px(context, (endDistance - startDistance));
			params2.height = ToolUtils.dip2px(context, 15);
			vEnd.setLayoutParams(params2);
			vEnd.setBackgroundColor(getResources().getColor(R.color.colorBlue));
			mLyoutTimeFill.addView(vEnd);

			desDistance = endDistance;
		}
	}
	

	public void setDate(String date) {
		mTvCurrentTime.setText(date);

		int defaultDis = (int) (ToolUtils.getScreenWith(context) / 2) - 30;
		int currentHour = Integer.parseInt(ToolUtils.formatDate("HH", ToolUtils.parseDate(date, null)));
		int currentMin = Integer.parseInt(ToolUtils.formatDate("mm", ToolUtils.parseDate(date, null)));
		int currentDis = currentHour * 60 + currentMin;

		if (currentDis - defaultDis > 0) {
			hsv.smoothScrollTo(ToolUtils.dip2px(context, currentDis - defaultDis), 0);
		}
	}

}
