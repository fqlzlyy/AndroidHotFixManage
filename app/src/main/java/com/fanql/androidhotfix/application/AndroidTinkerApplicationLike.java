package com.fanql.androidhotfix.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.fanql.commonlibrary.util.UIUtils;
import com.fanql.hotfixlibrary.tinker.TinkerFixPatchManager;
import com.fanql.netlibrary.RequestNetManager;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.loader.app.ApplicationLike;
import com.tencent.tinker.loader.shareutil.ShareConstants;

@DefaultLifeCycle(application = ".AndroidTinkerApplication",
    flags =  ShareConstants.TINKER_ENABLE_ALL,
    loadVerifyFlag = false
)
public class AndroidTinkerApplicationLike extends ApplicationLike {

    private static Context sContext;

    public static Context getContext() {
        return sContext;
    }

    public AndroidTinkerApplicationLike(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
            long applicationStartMillisTime,
            Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        // 初始化Tinker
        TinkerFixPatchManager.installTinker(this);
        sContext =  getApplication();

        // 完成网络请求组件的初始化
        RequestNetManager.init();
        // 上下文的赋值
        UIUtils.initContext(sContext);

        Log.i("qinglin.fan", "application tinker");
    }
}
