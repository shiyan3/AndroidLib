package com.shiyan.androidlib.videorecord;

/**
 * 权限检查工具类
 */
public class PermissionUtils {
    /**
     * 要检查的权限集合
     */
    private String[] permissions;

    public PermissionUtils(String[] permissions) {
        this.permissions = permissions;
    }

    /**
     *  is all permission granted
     * @return
     */
    public boolean isAllGranted(){
        //PackageManager.PERMISSION_GRANTED
        return false;
    }


}
