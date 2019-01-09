package com.fanql.androidhotfix.application;

import android.app.Application;
import com.fanql.commonlibrary.util.UIUtils;
import com.fanql.hotfixlibrary.AndFixPatchManager;
import com.fanql.netlibrary.RequestNetManager;

public class AndroidApplication extends Application {

    public static AndroidApplication sInstance;


    public static AndroidApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sInstance = this;

        // 完成andFix的初始化
        AndFixPatchManager.getInstance().initPatch(this);
        // 完成网络请求组件的初始化
        RequestNetManager.init();
        // 上下文的赋值
        UIUtils.initContext(this);

    }

}
