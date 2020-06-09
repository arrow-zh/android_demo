package com.ev.hikvideo.activity;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.ev.hikvideo.R;

public class BaseActivity extends Activity {

    public void initBackBtn() {
        Button mBtnBack = (Button) this.findViewById(R.id.btn_back);
        mBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BaseActivity.this.finish();
            }
        });
    }

    public void setTitle(String title) {
        TextView mTvTitle = (TextView) this.findViewById(R.id.tv_title);
        mTvTitle.setText(title);
    }

    public void setTitle(int resId) {
        TextView mTvTitle = (TextView) this.findViewById(R.id.tv_title);
        mTvTitle.setText(resId);
    }

    public void setRightImg(int resouceId) {
        this.findViewById(R.id.fl_right).setVisibility(View.VISIBLE);
        ImageView mIvImg = (ImageView) this.findViewById(R.id.iv_img);
        mIvImg.setImageResource(resouceId);
    }

    public void setRightText(String resouceId) {
        this.findViewById(R.id.fl_right).setVisibility(View.VISIBLE);
        Button mIvImg = (Button) this.findViewById(R.id.btn_right);
        mIvImg.setText(resouceId);
    }
}
