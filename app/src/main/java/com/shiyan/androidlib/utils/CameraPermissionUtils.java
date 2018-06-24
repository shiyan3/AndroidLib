package com.shiyan.androidlib.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

/**
 * created by shiyan on 2018/6/24
 *  工具类
 *  检查使用相机需求所需要的权限
 */
public class CameraPermissionUtils {

    /**
     *  权限等级：可用/询问/禁止
     * @return
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static boolean checkPermission(Context mContext,String [] permissions){
        List<String> requestPermissionList = new ArrayList<>();
        //找出所有未授权的权限
        for (String permission : permissions) {
            if (mContext.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                requestPermissionList.add(permission);
            }
        }
        if (requestPermissionList.isEmpty()) {
            //已经全部授权
            Log.i("test3","permission all granted");
        } else {
            //申请授权
            Log.i("test3","permission need request");
        }

        return false;
    }

    public static void requestPermission(){

    }
}
