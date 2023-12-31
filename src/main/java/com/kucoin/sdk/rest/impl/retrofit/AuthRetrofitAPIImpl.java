/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.impl.retrofit;

import com.kucoin.sdk.factory.RetrofitFactory;

import java.lang.reflect.ParameterizedType;

/**
 * Created by chenshiwei on 2019/1/10.
 */
public class AuthRetrofitAPIImpl<T> extends AbstractRetrofitAPIImpl<T> {

    private volatile boolean inited;
    private T apiImpl;

    @Override
    public T getAPIImpl() {
        if (inited)
            return apiImpl;
        synchronized (getClass()) {
            if (inited)
              return apiImpl;
            @SuppressWarnings("unchecked")
            Class<T> tClass = (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass())
                    .getActualTypeArguments()[0];
            T t = RetrofitFactory.getAuthRetorfit(baseUrl, apiKey, secret, passPhrase, apiKeyVersion)
                    .create(tClass);
            apiImpl = t;
            return t;
        }
    }
}
