package com.fanql.netlibrary.constant;

public class NetConstant {

    // 通用错误，我们不知道具体错误时，返回的错误code
    public static final int NORMAL_ERROR = 0x00;

    // 网络异常(默认情况 比如连接失败，超时)
    public static final int NETWORK_ERROR = 0x01;
    // 解析json失败或者其他数据错误
    public static final int DATA_ERROR = 0x02;
    // 未知错误
    public static final int UNKNOWN_ERROR = 0x03;

    public static final String NETWORK_ERROR_STR = "网络错误";
    public static final String JSON_ERROR_STR = "服务器返回的数据格式有误";
    public static final String UNKNOWN_ERROR_STR = "未知错误";

    public static final int SUCCESS_CODE = 1000;


}
