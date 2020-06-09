package com.ev.hikvideo.util;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ev.hikvideo.R;


/**
 * UI工具类
 */
public class UIUtils {

    private static Dialog dialog;

    public static void showToast(Context c, int resId) {
        Toast.makeText(c, resId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(Context c, String desc) {
        Toast.makeText(c, desc, Toast.LENGTH_SHORT).show();
    }

    /**
     * 是否可取消
     */
    private static boolean isCancelable = false;

    public static void cancelProgressDialog() {
        if (dialog != null) {
            dialog.cancel();
            dialog = null;
        }
    }

    public static void showLoadingProgressDialog(Activity activity, int resId) {
        if (null != dialog && dialog.isShowing()) {
            return;
        }
        cancelProgressDialog();
        if (activity.isFinishing()) {
            return;
        }
        isCancelable = true;
        dialog = createLoadingDialog(activity, activity.getString(resId));
        dialog.show();
    }

    public static void showLoadingProgressDialog(Activity activity, String msg) {
        cancelProgressDialog();

        if (activity.isFinishing()) {
            return;
        }
        isCancelable = true;
        dialog = createLoadingDialog(activity, msg);
        dialog.show();
    }

    public static void showLoadingProgressDialog(Activity activity, int resId, boolean cancelable) {
        if (null != dialog && dialog.isShowing()) {
            return;
        }
        cancelProgressDialog();
        if (activity.isFinishing()) {
            return;
        }
        isCancelable = cancelable;
        dialog = createLoadingDialog(activity, activity.getString(resId));
        dialog.show();
    }

    public static void showLoadingProgressDialog(Activity activity, String msg, boolean cancelable) {
        if (null != dialog && dialog.isShowing()) {
            return;
        }
        cancelProgressDialog();
        if (activity.isFinishing()) {
            return;
        }
        isCancelable = cancelable;
        dialog = createLoadingDialog(activity, msg);
        dialog.show();
    }

    /**
     * 得到自定义的progressDialog
     */
    public static Dialog createLoadingDialog(Activity activity, String msg) {
        LayoutInflater inflater = LayoutInflater.from(activity);
        View v = inflater.inflate(R.layout.hikvi_progress_dialog, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.img);
        TextView tipTextView = (TextView) v.findViewById(R.id.tipTextView);// 提示文字
        // 加载动画
        Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(activity, R.anim.loading_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(hyperspaceJumpAnimation);
        tipTextView.setText(msg);// 设置加载信息

        Dialog loadingDialog = new Dialog(activity, R.style.loading_dialog);// 创建自定义样式dialog
        if (null != loadingDialog.getWindow()) {
            loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable());
        }
        loadingDialog.setCancelable(isCancelable);// 是否能用“返回键”取消
        loadingDialog.setCanceledOnTouchOutside(isCancelable); // 是否能点击外部取消
        loadingDialog.setContentView(layout, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));// 设置布局
        return loadingDialog;
    }
}
