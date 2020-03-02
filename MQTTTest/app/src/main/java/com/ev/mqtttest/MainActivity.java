package com.ev.mqtttest;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.ev.mqtttest.service.MyMqttService;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MyMqttService.startService(this); //开启服务
    }
}
