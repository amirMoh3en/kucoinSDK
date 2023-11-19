/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.websocket;

import com.kucoin.sdk.exception.KucoinApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by chenshiwei on 2019/1/19.
 */
public class PrintCallback<T> implements KucoinAPICallback<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PrintCallback.class);

    @Override
    public void onResponse(T response) throws KucoinApiException {
        LOGGER.debug("Got response: {}", response);
    }

}
