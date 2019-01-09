package com.fanql.hotfixlibrary;

import android.content.Context;
import android.util.Log;
import com.alipay.euler.andfix.patch.PatchManager;

/**
 * AndFix 管理 andFix 所有的 api
 * @author fanql
 * */
public class AndFixPatchManager {

    private static final String TAG = AndFixPatchManager.class.getSimpleName();

    private static AndFixPatchManager mAndFixPatchManager;
    private PatchManager mPatchManager;

    public static AndFixPatchManager getInstance() {
        if (mAndFixPatchManager == null) {
            synchronized (AndFixPatchManager.class) {
                if (mAndFixPatchManager == null) {
                    mAndFixPatchManager = new AndFixPatchManager();
                }
            }

        }
        return mAndFixPatchManager;
    }


    // 初始化andFix方法
    public void initPatch(Context context) {
        mPatchManager = new PatchManager(context);
        mPatchManager.init("versionName");
        // 根据github说明，该方法越早调用越好
        mPatchManager.loadPatch();
    }

    // 加载patch文件
    public void addPatch(String patch) {
        try {
             if (mPatchManager != null) {
                 mPatchManager.addPatch(patch);
             }
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "add patch 出现错误");
        }
    }
}
