package com.shiyan.androidlib;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shiyan.androidlib.videolist.VideolistActivity;
import com.shiyan.androidlib.videorecord.RecordVideoActivity;
import com.shiyan.androidlib.videorecord.TestPackTimerActivity;


public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        findViewById(R.id.btn_to_recordvideo).setOnClickListener(this);
        findViewById(R.id.btn_to_packtimer).setOnClickListener(this);
        findViewById(R.id.btn_to_video).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_to_recordvideo:
                startActivity(new Intent(MainActivity.this, RecordVideoActivity.class));
                break;
            case R.id.btn_to_packtimer:
                startActivity(new Intent(MainActivity.this, TestPackTimerActivity.class));
                break;
            case R.id.btn_to_video:
                startActivity(new Intent(MainActivity.this, VideolistActivity.class));
                break;
        }
    }
}
