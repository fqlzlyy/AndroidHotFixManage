package com.fanql.commonlibrary.ui;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import com.fanql.commonlibrary.ui.listener.DynamicPermissionListener;
import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends AppCompatActivity {

    private static final String TAG = BaseActivity.class.getSimpleName();
    protected SparseArray<DynamicPermissionListener> mListenerArray;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListenerArray = new SparseArray<>();
    }

    public void requestDynamicPermission(Integer requestCode, String[] permissions, DynamicPermissionListener permissionListener) {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            Log.i(TAG, "hasPermissions: API version < M, returning true by default");
            permissionListener.onSuccess();
            return;
        }

        if (mListenerArray.indexOfKey(requestCode) == -1) {
            mListenerArray.put(requestCode, permissionListener);
        }

        List<String> noPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            // 先判断哪些是否已经有权限了
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                noPermissionList.add(permission);
            }
        }

        if (noPermissionList.isEmpty()) {
            permissionListener.onSuccess();
            return;
        }
        int len = noPermissionList.size();
        ActivityCompat.requestPermissions(this, noPermissionList.toArray(new String[len]), requestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (mListenerArray.indexOfKey(requestCode) == -1) {
            // 没找到回调直接返回
            return;
        }
        DynamicPermissionListener listener = mListenerArray.get(requestCode);
        mListenerArray.remove(requestCode);
        if (grantResults.length > 0) {
            List<String> deniedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermissions.add(permission);
                }
            }
            // 只要有拒绝,认为全部失败
            if (deniedPermissions.isEmpty()) {
                listener.onSuccess();
            } else {
                listener.onFailure();
            }
        }
    }

}
