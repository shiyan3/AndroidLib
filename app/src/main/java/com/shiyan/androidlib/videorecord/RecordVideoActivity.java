package com.shiyan.androidlib.videorecord;

import android.app.Activity;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.Toast;


import com.shiyan.androidlib.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RecordVideoActivity extends Activity {

    private static final String TAG = "RecordVideoActivity";
    private Chronometer chronometer;
    private Button btn_record;
    private Button btn_choose_camera;
    private SurfaceView sv;
    /**
     * 录制未开始状态
     */
    private static final int RECORD_STOP = 0;
    /**
     * 录制进行中状态
     */
    private static final int RECORD_RUNNING = 1;
    /**
     * 录制暂停状态
     */
    private static final int RECORD_PAUSE = 2;
    private int recordState= RECORD_STOP;
    private long stopTime = 0;
    private static final long RECORD_LIMIT_TIME = 20 * 1000;
    private SurfaceHolder mSurfaceHolder;
    private File mVecordFile;// 存储文件
    private Camera mCamera;
    private MediaRecorder mediaRecorder;
    private int cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_video);
        initView();
    }

    /**
     * 初始化view
     */
    private void initView(){
        chronometer = (Chronometer) findViewById(R.id.chronometer_view);
        btn_record = (Button) findViewById(R.id.btn_record);
        btn_choose_camera = (Button) findViewById(R.id.choose_canmer);
        sv = (SurfaceView) findViewById(R.id.sv_record);
        //配置SurfaceHodler
        mSurfaceHolder = sv.getHolder();
        // 设置Surface不需要维护自己的缓冲区
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        // 设置分辨率
        mSurfaceHolder.setFixedSize(320, 280);
        // 设置该组件不会让屏幕自动关闭
        mSurfaceHolder.setKeepScreenOn(true);
        mSurfaceHolder.addCallback(mCallBack); //相机创建回调接口
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chr) {
                //录制时间超过限制时间 停止录制
                if (SystemClock.elapsedRealtime() - chr.getBase() > RECORD_LIMIT_TIME) {
                    btn_record.setEnabled(false);
                    stopRecord();
                }
            }
        });
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (recordState){
                    case RECORD_RUNNING:
                        pauseRecord();
                        break;
                    case RECORD_STOP:
                    case RECORD_PAUSE:
                        startRecord();
                        break;
                }
            }
        });

        btn_choose_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (cameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
                    cameraID = Camera.CameraInfo.CAMERA_FACING_FRONT;
                }else{
                    cameraID = Camera.CameraInfo.CAMERA_FACING_BACK;
                }
                initCamera();
            }
        });
    }



    /**
     * 开始录制、计时
     */
    private void startRecord(){
        if(recordState == RECORD_STOP){
            boolean creakOk = createRecordDir();
            if (!creakOk) {
                return;
            }
            initCamera();
            mCamera.unlock();
            setConfigRecord();
            try {
                mediaRecorder.prepare();
                mediaRecorder.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else {
      //      mediaRecorder.resume();
        }
        recordState = RECORD_RUNNING;
        if (stopTime == 0){
            chronometer.setBase(SystemClock.elapsedRealtime());//计时器清零
        }else{
            chronometer.setBase(chronometer.getBase() + (SystemClock.elapsedRealtime() - stopTime));
        }
        int hour = (int) ((SystemClock.elapsedRealtime() - chronometer.getBase()) / 1000 / 60);
        chronometer.setFormat("0"+ String.valueOf(hour)+":%s");
        chronometer.start();
    }

    /**
     * 暂停录制、计时
     */
    private void pauseRecord(){
        mediaRecorder.setOnErrorListener(null);
        mediaRecorder.setPreviewDisplay(null);
     //   mediaRecorder.pause();
        stopTime = SystemClock.elapsedRealtime();
        recordState = RECORD_PAUSE;
        chronometer.stop();
    }

    /**
     * 停止录制、计时
     */
    private void stopRecord(){
        mediaRecorder.setOnErrorListener(null);
        mediaRecorder.setPreviewDisplay(null);
        //停止录制
        mediaRecorder.stop();
        mediaRecorder.reset();
        //释放资源
        mediaRecorder.release();
        mediaRecorder = null;
        stopTime = SystemClock.elapsedRealtime();
        recordState = RECORD_STOP;
        chronometer.stop();
    }

    /**
     * 初始化摄像头
     */
    private void initCamera() {
        Log.i(TAG," call initCamera()");
        if (mCamera != null) {
            stopCamera();
        }
        mCamera = Camera.open(cameraID);
        if (mCamera == null) {
            Toast.makeText(this, "未能获取到相机！", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            mCamera.setPreviewDisplay(mSurfaceHolder);
            //配置CameraParams
            setCameraParams();
            //启动相机预览
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d("RecordVideoActivity", "Error starting camera preview: " + e.getMessage());
        }
    }
    private void setCameraParams() {
        if (mCamera != null) {
            Camera.Parameters params = mCamera.getParameters();
            if (this.getResources().getConfiguration().orientation != Configuration.ORIENTATION_LANDSCAPE) {
                params.set("orientation", "portrait");
                mCamera.setDisplayOrientation(90);
            } else {
                params.set("orientation", "landscape");
                mCamera.setDisplayOrientation(0);
            }
            //设置聚焦模式 前置摄像头不支持
            if(cameraID == Camera.CameraInfo.CAMERA_FACING_BACK){
                params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
            }
            //缩短Recording启动时间
           params.setRecordingHint(true);
            //是否支持影像稳定能力，支持则开启
//            if (params.isVideoStabilizationSupported()){
//                params.setVideoStabilization(true);
//            }
            mCamera.setParameters(params);
        }
    }
    /**
     * 释放摄像头资源
     */
    private void stopCamera() {
        if (mCamera != null) {
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }

    /**
     * 创建视频文件保存路径
     */
    private boolean createRecordDir() {
        //todo SD不存在时 处理逻辑
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            return false;
        }
        File sampleDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), "SOHURecord");
        if (!sampleDir.exists()) {
            sampleDir.mkdirs();
        }
        if(mVecordFile == null || ! mVecordFile.exists()){
            String recordName = "SOHU_VIDEO_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".mp4";
            mVecordFile = new File(sampleDir, recordName);
        }
        return true;
    }

    /**
     * 配置MediaRecorder()
     */
    private void setConfigRecord() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.reset();
        mediaRecorder.setCamera(mCamera);
        mediaRecorder.setOnErrorListener(onErrorListener);

        //使用SurfaceView预览
        mediaRecorder.setPreviewDisplay(mSurfaceHolder.getSurface());

        //1.设置采集声音
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //设置采集图像
        mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
        //2.设置视频，音频的输出格式 mp4
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        //3.设置音频的编码格式
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //设置图像的编码格式
        mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
        //设置立体声
//        mediaRecorder.setAudioChannels(2);
        //设置最大录像时间 单位：毫秒
//        mediaRecorder.setMaxDuration(60 * 1000);
        //设置最大录制的大小 单位，字节
//        mediaRecorder.setMaxFileSize(1024 * 1024);
        //音频一秒钟包含多少数据位
        CamcorderProfile mProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_480P);
        mediaRecorder.setAudioEncodingBitRate(44100);
        if (mProfile.videoBitRate > 2 * 1024 * 1024){
            mediaRecorder.setVideoEncodingBitRate(2 * 1024 * 1024);
        } else{
            mediaRecorder.setVideoEncodingBitRate(1024 * 1024);
        }
        mediaRecorder.setVideoFrameRate(mProfile.videoFrameRate);

        //设置选择角度，顺时针方向，因为默认是逆向90度的，这样图像就是正常显示了,这里设置的是观看保存后的视频的角度
        mediaRecorder.setOrientationHint(90);
        //设置录像的分辨率
        mediaRecorder.setVideoSize(352, 288);

        mediaRecorder.setOutputFile(mVecordFile.getAbsolutePath());
    }

    private SurfaceHolder.Callback mCallBack = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            initCamera();
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
            if (mSurfaceHolder.getSurface() == null) {
                return;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            stopCamera();
        }
    };
    private MediaRecorder.OnErrorListener onErrorListener = new MediaRecorder.OnErrorListener() {
        @Override
        public void onError(MediaRecorder mediaRecorder, int what, int extra) {
            try {
                if (mediaRecorder != null) {
                    mediaRecorder.reset();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    /**
     * 检查权限
     */
    private void checkPermission(){

    }

    /**
     * 权限不足时 动态申请
     */
    private void requextPermission(){

    }
}
