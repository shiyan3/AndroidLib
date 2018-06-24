package com.shiyan.androidlib.activity;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;

import com.shiyan.androidlib.R;
import com.shiyan.androidlib.utils.CameraPermissionUtils;

import java.util.ArrayList;
import java.util.List;


public class TestCameraPermissionActivity extends Activity {
    private String [] permissions = new String[]{
        Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_camera_permission);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraPermissionUtils.checkPermission(this,permissions);
        }

    }

}
