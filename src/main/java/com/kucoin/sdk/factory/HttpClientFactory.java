/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.factory;

import com.kucoin.sdk.rest.interceptor.AuthenticationInterceptor;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by chenshiwei on 2019/1/18.
 */
public class HttpClientFactory {
    private static Dispatcher dispatcher = new Dispatcher();
    private static List<OkHttpClient> privateClient = new ArrayList<>();
    private static List<OkHttpClient> publicClient = new ArrayList<>();

    public static OkHttpClient getPublicClient() {
        OkHttpClient okHttpClient = buildHttpClient(null);
        publicClient.add(okHttpClient);
        return okHttpClient;
    }

    public static OkHttpClient getPrivateClient() {
        OkHttpClient okHttpClient = buildHttpClient(null);
        privateClient.add(okHttpClient);
        return okHttpClient;
    }

    public static OkHttpClient getAuthClient(String apiKey, String secret, String passPhrase, Integer apiKeyVersion) {
        return buildHttpClient(new AuthenticationInterceptor(apiKey, secret, passPhrase, apiKeyVersion));
    }

    private static OkHttpClient buildHttpClient(Interceptor interceptor) {
        dispatcher.setMaxRequestsPerHost(100);
        dispatcher.setMaxRequests(100);
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .pingInterval(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .connectionPool(
                        new ConnectionPool(
                                10 ,
                                5 ,
                                TimeUnit.SECONDS)
                )
                .dispatcher(dispatcher);
        if (interceptor != null) {
            builder.addInterceptor(interceptor);
        }
        return builder.build();
    }

    public static void stopClient() {
        privateClient.forEach(c -> {
            try {
                c.cache().close();
            } catch (Exception e) {
            }
            c.dispatcher().executorService().shutdown();
            c.connectionPool().evictAll();
        });
        publicClient.forEach(c -> {
            try {
                c.cache().close();
            } catch (Exception e) {
            }
            c.dispatcher().executorService().shutdown();
            c.connectionPool().evictAll();
        });
        dispatcher.executorService().shutdownNow();
        dispatcher.cancelAll();
        publicClient = new ArrayList<>();
        privateClient = new ArrayList<>();
        System.gc();
        dispatcher = new Dispatcher();
    }

}
