package com.shiyan.androidlib.videorecord;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 封装一个简单调用的计时器
 */
public class PackTimer {
    private static final int FINISH = 1;
    private static final int UNFINISH = 0;

    private long limitTime = 0;
    private long passTime = 0;//累计计时时长
    private TimerListener listener;
    private Timer timer;

    /**
     * 初始化变量包含 总时长 时间单位默认精确到0.1秒
     */
    public PackTimer(long limitTime) {
        this.limitTime = limitTime;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (listener == null) {
                return;
            }
            switch (msg.what) {
                case UNFINISH:
                    listener.onReceive("" + passTime);
                    break;
                case FINISH:
                    clear();
                    listener.onFinish();
                    break;
            }

        }
    };

    public void start() {
        if (passTime < limitTime){
            init();
        }
    }

    public void stop() {
        clear();
    }

    public void reset() {
        clear();
        passTime = 0;
    }
    public void destory(){
        clear();
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        listener = null;
    }

    public void setTimerListener(TimerListener listener) {
        this.listener = listener;
    }

    public interface TimerListener {
        void onReceive(String time);
        void onFinish();
    }

    private void clear() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    private void init() {
        clear();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                passTime += 100;
                Log.i("test3","passtime="+passTime);
                if (passTime < limitTime) {
                    mHandler.sendEmptyMessage(UNFINISH);
                } else {
                    mHandler.sendEmptyMessage(FINISH);
                }
            }
        }, 0, 100);
    }
}
