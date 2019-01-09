package com.fanql.netlibrary.callback;

import com.zhy.http.okhttp.callback.Callback;

// 将 okhttputils 的 callback 再次进行包装
public class NetHandleBack<T> {

    private Callback mCallback;

    public NetHandleBack(Callback callback) {
        this.mCallback = callback;
    }

}
