package com.shiyan.androidlib.videolist;

import android.app.Activity;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.shiyan.androidlib.R;

import java.io.IOException;

public class Video2Activity extends Activity implements View.OnClickListener{

    private String filePath="";
    private SurfaceView sv_video;
    private MediaPlayer player;
    private ImageView iv_preview;
    RelativeLayout rl_root;
    private MediaMetadataRetriever mmr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video2);
        initView();
        initData();
    }

    private void initView() {
        sv_video = (SurfaceView) findViewById(R.id.sv_video);
        iv_preview = (ImageView) findViewById(R.id.iv_preview);
        rl_root = (RelativeLayout) findViewById(R.id.rl_root);
        findViewById(R.id.flayout_play).setOnClickListener(this);
        player = new MediaPlayer();
        sv_video.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                player.setDisplay(holder);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    private void initData(){
        filePath = getIntent().getStringExtra("videopath");
        mmr = new MediaMetadataRetriever();
        mmr.setDataSource(filePath);
        try {
            player.setDataSource(filePath);
            player.prepare();
            player.setLooping(false);
            player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    //根据视频宽高设置surfaceview的宽高
                    rl_root.post(new Runnable() {
                        @Override
                        public void run() {
                            // 首先取得video的宽和高
                            int vWidth = player.getVideoWidth();
                            int vHeight = player.getVideoHeight();
                            // 父容器的宽高
                            int lw = rl_root.getWidth();
                            int lh = rl_root.getHeight();

                            /**
                             * surfaceview显示策略 完整的显示视频内容
                             * 1，视频宽大于高：宽等于屏幕宽度；高视频宽高比例计算获得
                             * 2，视频高大于宽：<1>
                             */

                            if (vWidth > lw || vHeight > lh) {
                                // 如果video的宽或者高超出了当前屏幕的大小，则要进行缩放
                                float wRatio = (float) vWidth / (float) lw;
                                float hRatio = (float) vHeight / (float) lh;

                                // 选择大的一个进行缩放
                                float ratio = Math.max(wRatio, hRatio);
                                vWidth = (int) Math.ceil((float) vWidth / ratio);
                                vHeight = (int) Math.ceil((float) vHeight / ratio);

                                // 设置surfaceView的布局参数
                                RelativeLayout.LayoutParams lp= new RelativeLayout.LayoutParams(vWidth, vHeight);
                                sv_video.setLayoutParams(lp);
                            }
                        }
                    });

                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.flayout_play:
                if (player.isPlaying()){
                    stopVideo();
                }else{
                    startVideo();
                }
                break;
        }
    }

    private void startVideo(){
        player.start();
        iv_preview.setVisibility(View.GONE);
    }
    private void stopVideo(){
        player.pause();
        //设置封面图
        Bitmap bitmap = mmr.getFrameAtTime(player.getCurrentPosition()*1000, MediaMetadataRetriever.OPTION_CLOSEST);
        iv_preview.setImageBitmap(bitmap);
        iv_preview.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        player.stop();
        player.release();
    }
}

