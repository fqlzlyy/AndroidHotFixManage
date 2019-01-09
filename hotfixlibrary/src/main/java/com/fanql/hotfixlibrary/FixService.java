package com.fanql.hotfixlibrary;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import com.fanql.commonlibrary.data.AppConstant;
import com.fanql.commonlibrary.data.ServerAddress;
import com.fanql.commonlibrary.util.AppUtils;
import com.fanql.commonlibrary.util.PreUtils;
import com.fanql.hotfixlibrary.patch.PatchInfoResponse;
import com.fanql.netlibrary.RequestNetManager;
import com.fanql.netlibrary.callback.CustomFileCallback;
import com.fanql.netlibrary.callback.CustomJsonCallback;
import com.fanql.netlibrary.exception.CustomOkHttpException;
import com.fanql.netlibrary.request.FileRequest;
import com.fanql.netlibrary.request.Request;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 专门用来进行热修复的服务 1. 检查是否有新的patch 2. 如果有就下载并加载新的patch,并进行加载,没有就停止当前服务
 *
 * @author fanql
 */
public class FixService extends Service {

    private static final String TAG = FixService.class.getSimpleName();

    private static final int UPDATE_PATCH_CHECK = 0x01;
    private static final int DOWN_LOAD_PATCH = 0x02;


    private static final String SUFFIX = ".apatch";

    private String mFixedPatchDir;
    private String mFixedPatchFile;

    private Handler mInnerHandler = new Handler(new Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_PATCH_CHECK:
                    updatePatchCheck();
                    break;
                case DOWN_LOAD_PATCH:
                    downloadPatch();
                    break;
            }
            return false;
        }
    });

    private void updatePatchCheck() {

        Request request = new Request();
        request.url = ServerAddress.S_AND_FIX.UPDATE_CHECK_URL;
        Map<String, String> params = new HashMap<>();
        // 大的版本号 -- 对应的就是我们的分支
        params.put("versionCode", AppUtils.getVersion(this));
        // 小的版本号(比如我们在同一个版本号上进行了多次修复)
        params.put("smallVersionCode", PreUtils.getInt(AppConstant.PRE_KEY_SMALL_VERSION_CODE, 0) + "");

        request.params = params;
        request.tag(ServerAddress.S_AND_FIX.DOWN_PATCH_URL);

        RequestNetManager.post(request, new CustomJsonCallback<PatchInfoResponse>() {
            @Override
            public void onSuccess(PatchInfoResponse responseObj) {
                String url = responseObj.data.downloadUrl;
                String smallVersionCode = responseObj.data.smallVersionCode;
                if (TextUtils.isEmpty(url)) {
                    stopSelf();
                    Log.i(TAG, "下载地址为空");
                } else {
                    if (!TextUtils.isEmpty(smallVersionCode)) {
                        // 判断小版本的code是否有更新
                        int smallVCode = Integer.parseInt(smallVersionCode);
                        if (smallVCode > PreUtils.getInt(AppConstant.PRE_KEY_SMALL_VERSION_CODE, 0)) {
                            mInnerHandler.sendEmptyMessage(DOWN_LOAD_PATCH);
                        } else {
                            stopSelf();
                        }
                    } else {
                        stopSelf();
                    }
                }
            }

            @Override
            public void onFailure(CustomOkHttpException exception) {
                String message = exception.message;
                Log.e(TAG, message);

            }
        });

    }

    private void downloadPatch() {

        Request request = new FileRequest();
        request.url = ServerAddress.S_AND_FIX.DOWN_PATCH_URL;
        String destFileName = String.valueOf(System.currentTimeMillis()).concat(SUFFIX);

        mFixedPatchFile = mFixedPatchDir.concat(destFileName);

        // 传入目标文件夹和文件名
        RequestNetManager.download(request, new CustomFileCallback(mFixedPatchDir, destFileName) {
            @Override
            public void onSuccess(File responseObj) {
                // 这个需要绝对路径
                AndFixPatchManager.getInstance().addPatch(mFixedPatchFile);
            }

            @Override
            public void onFailure(CustomOkHttpException exception) {
                Log.e(TAG, exception.message);
            }

            @Override
            public void inProgress(float progress, long total, int id) {
                super.inProgress(progress, total, id);
                Log.i(TAG, "progress = " + progress);
            }
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        checkFixedPatch();
    }

    private void checkFixedPatch() {

        mFixedPatchDir = getExternalCacheDir().getAbsolutePath() + "/apatch/";
        File file = new File(mFixedPatchDir);
        try {
            if (!file.exists()) {
                file.mkdir();
            }
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mInnerHandler.sendEmptyMessage(UPDATE_PATCH_CHECK);
        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
