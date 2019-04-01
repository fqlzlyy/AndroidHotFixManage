package com.fanql.netlibrary.callback;

import com.fanql.netlibrary.constant.NetConstant;
import com.fanql.netlibrary.exception.CustomOkHttpException;
import com.fanql.netlibrary.response.BaseResponse;
import com.google.gson.Gson;
import com.zhy.http.okhttp.callback.Callback;
import java.lang.reflect.ParameterizedType;
import okhttp3.Call;
import okhttp3.Response;

public abstract class CustomJsonCallback<T extends BaseResponse> extends Callback<T> implements INetCallbackListener<T> {

    @Override
    public T parseNetworkResponse(Response response, int id) throws Exception {
        String string = response.body().string();
        Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return new Gson().fromJson(string, entityClass);

    }

    @Override
    public void onError(Call call, Exception e, int id) {
        String message = e.getMessage();
        // message 优先级最高，如果message空，则我们自己构造message
        if (message != null) {
            onFailure(new CustomOkHttpException(NetConstant.NORMAL_ERROR, message, e));
        } else {
            onFailure(new CustomOkHttpException(NetConstant.NETWORK_ERROR, NetConstant.NETWORK_ERROR_STR, e));
        }
    }

    @Override
    public void onResponse(T response, int id) {
        // 如果返回的code不是1000 则数据异常
        if (response.code != NetConstant.SUCCESS_CODE) {
            String message = response.message;
            if (message != null) {
                onFailure(new CustomOkHttpException(NetConstant.NORMAL_ERROR, message, null));
            } else {
                onFailure(new CustomOkHttpException(NetConstant.DATA_ERROR, NetConstant.JSON_ERROR_STR, null));
            }
        } else {
            onSuccess(response);
        }
    }
}
