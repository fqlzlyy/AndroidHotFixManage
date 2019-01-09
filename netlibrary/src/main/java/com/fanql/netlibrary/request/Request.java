package com.fanql.netlibrary.request;

import java.util.LinkedHashMap;
import java.util.Map;

public class Request {
    public String url;
    public Object tag;
    public Map<String, String> headers;
    public Map<String, String> params;
    public int id;
    public String json;

    public Request id(int id) {
        this.id = id;
        return this;
    }

    public Request url(String url) {
        this.url = url;
        return this;
    }


    public Request tag(Object tag) {
        this.tag = tag;
        return this;
    }

    public Request headers(Map<String, String> headers) {
        this.headers = headers;
        return this;
    }

    public Request addHeader(String key, String val) {
        if (this.headers == null) {
            headers = new LinkedHashMap<>();
        }
        headers.put(key, val);
        return (Request) this;
    }

}
