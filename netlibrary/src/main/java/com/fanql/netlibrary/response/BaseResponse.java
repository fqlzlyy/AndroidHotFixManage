package com.fanql.netlibrary.response;

import java.io.Serializable;

public class BaseResponse implements Serializable {

    // 约定所有的服务端接口都有 code 字段, code 为1000 标识数据请求成功
    public int code;
    public String message;

}
