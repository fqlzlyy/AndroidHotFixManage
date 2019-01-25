package com.fanql.androidhotfix.ui;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import com.fanql.androidhotfix.R;
import com.fanql.androidhotfix.constant.Constant;
import com.fanql.commonlibrary.ui.BaseActivity;
import com.fanql.commonlibrary.ui.listener.DynamicPermissionListener;
import com.fanql.hotfixlibrary.FixService;


public class SplashActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        requestDynamicPermission(Constant.ACCESS_SDCARD, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, new DynamicPermissionListener() {

            @Override
            public void onSuccess() {
                // 获取成功才去启动热更新检查服务
                Intent intent = new Intent(SplashActivity.this, FixService.class);
                // 设置是否使用tinker
                intent.putExtra("isUseTinker", false);
                startService(intent);


                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }, 5000);

            }

            @Override
            public void onFailure() {

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(SplashActivity.this, MainActivity.class));
                    }
                }, 5000);

            }
        });



    }
}
