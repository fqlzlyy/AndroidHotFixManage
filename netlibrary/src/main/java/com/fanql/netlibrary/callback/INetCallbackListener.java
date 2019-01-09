package com.fanql.netlibrary.callback;


import com.fanql.netlibrary.exception.CustomOkHttpException;

public interface INetCallbackListener<T> {

    public void onSuccess(T responseObj);
    public void onFailure(CustomOkHttpException exception);

}
