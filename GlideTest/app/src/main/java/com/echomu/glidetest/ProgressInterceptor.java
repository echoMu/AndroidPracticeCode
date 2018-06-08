package com.echomu.glidetest;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * <pre>
 *     author : echoMu
 *     e-mail :
 *     time   : 2018/06/08
 *     desc   :
 *     version:
 * </pre>
 */
public class ProgressInterceptor implements Interceptor{

    static final Map<String,ProgressListener> LISTENER_MAP=new HashMap<>();

    public static void addListener(String url,ProgressListener listener) {
        LISTENER_MAP.put(url,listener);
    }

    public static void removeListener(String url) {
        LISTENER_MAP.remove(url);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request=chain.request();
        Response response=chain.proceed(request);
        String url=request.url().toString();
        ResponseBody responseBody=response.body();
        Response newResponse=response.newBuilder().body(new ProgressResponseBody(url,responseBody)).build();
        return response;
    }
}
