package com.fanql.netlibrary.callback;

import com.fanql.netlibrary.constant.NetConstant;
import com.fanql.netlibrary.exception.CustomOkHttpException;
import com.zhy.http.okhttp.callback.FileCallBack;
import java.io.File;
import okhttp3.Call;

public abstract class CustomFileCallback extends FileCallBack implements INetCallbackListener<File> {

    public CustomFileCallback(String destFileDir, String destFileName) {
        super(destFileDir, destFileName);
    }

    @Override
    public void onError(Call call, Exception e, int id) {
        String message = e.getMessage();
        if (message != null) {
            onFailure(new CustomOkHttpException(NetConstant.NORMAL_ERROR, message, e));
        } else {
            onFailure(new CustomOkHttpException(NetConstant.NETWORK_ERROR, NetConstant.NETWORK_ERROR_STR, e));
        }
    }

    @Override
    public void onResponse(File response, int id) {
        onSuccess(response);
    }
}
