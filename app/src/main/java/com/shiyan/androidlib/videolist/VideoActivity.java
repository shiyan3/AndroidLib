package com.shiyan.androidlib.videolist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.VideoView;


import com.shiyan.androidlib.R;

import java.io.File;

public class VideoActivity extends Activity {

    private static final String TAG = "VideoActivity";
    private String path;
    private VideoView vv;
    private FrameLayout fl_play;
    private ImageView iv_icon, iv_preimg;
    private Button btn_back;
    private boolean isPause = false;
    private int currentPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        initView();
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        path = intent.getStringExtra("videopath");
        vv.setVideoPath(new File(path).getAbsolutePath());
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        currentPosition = vv.getCurrentPosition();
        pauseVideo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initView() {
        vv = (VideoView) findViewById(R.id.vv);
        fl_play = (FrameLayout) findViewById(R.id.fl_play);
        iv_icon = (ImageView) findViewById(R.id.iv_icon);
        iv_preimg = (ImageView) findViewById(R.id.iv_preimg);
        btn_back = (Button) findViewById(R.id.btn_back);
        fl_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (vv.isPlaying()) {
                    pauseVideo();
                } else {
                    startVideo();
                }
            }
        });
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseVideo();
                VideoActivity.this.finish();
            }
        });
        vv.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i(TAG, "video play end");
            }
        });
    }

    /**
     * 暂停
     */
    private void pauseVideo() {
        if (vv != null) {
            vv.pause();
            isPause = true;
        }
        iv_icon.setVisibility(View.VISIBLE);
        iv_preimg.setVisibility(View.VISIBLE);

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        mmr.setDataSource(path);
        Bitmap bitmap = mmr.getFrameAtTime(vv.getCurrentPosition());//获取第一帧图片
        iv_preimg.setImageBitmap(bitmap);
    }

    private void startVideo() {
        if (currentPosition > 0){
            vv.seekTo(currentPosition);
        }
        vv.start();
        iv_icon.setVisibility(View.GONE);
        iv_preimg.setVisibility(View.GONE);
    }
}
