package com.fanql.commonlibrary.data;

public class ServerAddress {

    private static final String BASE_URL = "www.baidu.com";

    // and_fix 热修复用到的url
    public static final class S_AND_FIX {

        public static final String TEST_URL = BASE_URL + "/test";
        public static final String UPDATE_CHECK_URL = BASE_URL + "/update_check_url";
        public static final String DOWN_PATCH_URL =  BASE_URL + "/download";

    }



}
