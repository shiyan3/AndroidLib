package com.shiyan.androidlib.videorecord;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.shiyan.androidlib.R;


public class TestPackTimerActivity extends Activity implements View.OnClickListener{

    TextView tv;
    private PackTimer packTimer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_pack_timer);
        tv = (TextView) findViewById(R.id.tv_time);
        packTimer = new PackTimer(20000);
        packTimer.setTimerListener(new PackTimer.TimerListener() {
            @Override
            public void onReceive(String time) {
                tv.setText("计时中："+time);
            }

            @Override
            public void onFinish() {
                tv.setText("计时结束");
            }
        });
        findViewById(R.id.btn_start).setOnClickListener(this);
        findViewById(R.id.btn_stop).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start:
                packTimer.start();
                break;
            case R.id.btn_stop:
                packTimer.stop();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        packTimer.destory();
    }
}
