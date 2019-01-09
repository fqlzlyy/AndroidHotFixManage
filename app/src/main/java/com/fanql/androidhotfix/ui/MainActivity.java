package com.fanql.androidhotfix.ui;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import com.fanql.androidhotfix.R;
import com.fanql.androidhotfix.constant.Constant;
import com.fanql.commonlibrary.ui.BaseActivity;
import com.fanql.commonlibrary.ui.listener.DynamicPermissionListener;
import com.fanql.commonlibrary.util.ToastUtils;

public class MainActivity extends BaseActivity {

    /**
     * apkpatch -f new.apk -t old.apk -o output1 -k lib.jks -p 123456 -a lib -e 123456
     */
    private void test() {

        int i = 1/1;
        Log.i("qinglin.fan", "修复");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        test();

        // 打电话权限申请
        findViewById(R.id.btn_call_phone).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                requestDynamicPermission(Constant.CALL_PHONE, new String[]{Manifest.permission.CALL_PHONE}, new DynamicPermissionListener() {

                    @Override
                    public void onSuccess() {
                        ToastUtils.showToast("电话权限获取成功");
                    }

                    @Override
                    public void onFailure() {
                        ToastUtils.showToast("电话权限被用户拒绝");
                    }
                });

            }
        });

        // SD卡权限申请
        findViewById(R.id.btn_access_sdcard).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                requestDynamicPermission(Constant.ACCESS_SDCARD, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        new DynamicPermissionListener() {

                            @Override
                            public void onSuccess() {
                                ToastUtils.showToast("SD卡权限获取成功");
                            }

                            @Override
                            public void onFailure() {
                                ToastUtils.showToast("SD卡权限被用户拒绝");
                            }
                        });
            }
        });

    }
}
