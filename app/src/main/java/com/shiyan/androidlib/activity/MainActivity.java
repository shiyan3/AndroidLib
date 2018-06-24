package com.shiyan.androidlib.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.shiyan.androidlib.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void testCameraPermission(View view){
        startActivity(new Intent(this,TestCameraPermissionActivity.class));
    }
}
