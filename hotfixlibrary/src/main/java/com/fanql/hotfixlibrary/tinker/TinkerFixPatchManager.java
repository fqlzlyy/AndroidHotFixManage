package com.fanql.hotfixlibrary.tinker;

import android.content.Context;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.app.ApplicationLike;

/**
 * 对tinker热修复的封装
 */
public class TinkerFixPatchManager {

    private static boolean isInstalled = false;
    private static ApplicationLike mAppLike;

    /**
     * 完成Tinker的初始化
     */
    public static void installTinker(ApplicationLike applicationLike) {
        mAppLike = applicationLike;
        if (isInstalled) {
            return;
        }
        //完成tinker初始化
        TinkerInstaller.install(mAppLike);
        isInstalled = true;

        // add for tinker patch test
        System.out.print("tinker patch test");
    }

    //完成Patch文件的加载
    public static void loadPatch(String path) {
        if (Tinker.isTinkerInstalled()) {
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(), path);
        }
    }

    //通过ApplicationLike获取Context
    private static Context getApplicationContext() {
        if (mAppLike != null) {
            return mAppLike.getApplication().getApplicationContext();
        }
        return null;
    }
}
