package com.fanql.netlibrary;

import com.fanql.netlibrary.request.FileRequest;
import com.fanql.netlibrary.request.FileRequest.FileInput;
import com.fanql.netlibrary.request.Request;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.PostFormBuilder;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;

/**
 * 网络请求统一的管理类
 * @author fanql
 */
public class RequestNetManager {

    private static final int TIME_OUT = 30;
    private static OkHttpClient mOkHttpClient;
    private static Gson gson;

    public static void init() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

        builder.connectTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.readTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.writeTimeout(TIME_OUT, TimeUnit.SECONDS);
        builder.followRedirects(true);

        // 添加基于内存的cookie存储
        builder.cookieJar(new DefaultCookieStore());
        // 添加支持Https
        builder.hostnameVerifier(HttpsUtils.getUnsafeHostNameVerifier());
        builder.sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager);
        mOkHttpClient = builder.build();
        OkHttpUtils.initClient(mOkHttpClient);

    }

    public static OkHttpClient getOkHttpClient() {
        return mOkHttpClient;
    }

    // post 提交方式
    public static void post(Request request, Callback handleBack) {

        OkHttpUtils.post().url(request.url).headers(request.headers)
                .params(request.params)
                .build()
                .execute(handleBack);

    }


    // post 1个json
    public static void postJson(Request request, Callback handleBack) {

        OkHttpUtils.postString().url(request.url)
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .content(request.json)
                .build()
                .execute(handleBack);

    }

    // 直接上传一个文件
    public static void postFile(FileRequest request, Callback callback) {

        OkHttpUtils
                .postFile()
                .url(request.url)
                .file(request.file)
                .build()
                .execute(callback);

    }

    // 上传文件 + 提交参数
    public static void postUpload(FileRequest request, Callback callback) {

        PostFormBuilder builder = OkHttpUtils.post()
                .url(request.url)
                .params(request.params);

        List<FileInput> fileInputList = request.mFileInputList;
        if (fileInputList != null && !fileInputList.isEmpty()) {
            for (FileInput input : fileInputList) {
                builder.addFile(input.key, input.filename, input.file);
            }
        }
        builder.build().execute(callback);
    }


    // get 请求
    public static void get(Request request, Callback callback) {

        OkHttpUtils.get().params(request.params)
                .headers(request.headers)
                .url(request.url)
                .id(request.id)
                .build()
                .execute(callback);

    }


    // 下载文件
    public static void download(Request request, FileCallBack callback) {

        OkHttpUtils.get()
                .url(request.url)
                .build()
                .execute(callback);

    }

}
