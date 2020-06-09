package com.xtoucher.hexin;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;
import com.xtoucher.hexin.view.X5WebView;

public class MainActivity extends Activity {

    private X5WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webView = this.findViewById(R.id.full_web_view);
        webView.loadUrl("https://m.hxzhengquan.com/wapindex2.php");

    }

}
