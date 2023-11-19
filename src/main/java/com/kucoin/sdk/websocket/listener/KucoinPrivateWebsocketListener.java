/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.websocket.listener;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.kucoin.sdk.KucoinObjectMapper;
import com.kucoin.sdk.constants.APIConstants;
import com.kucoin.sdk.impl.KucoinPrivateWSClientImpl;
import com.kucoin.sdk.websocket.KucoinAPICallback;
import com.kucoin.sdk.websocket.PrintCallback;
import com.kucoin.sdk.websocket.event.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Created by chenshiwei on 2019/1/19.
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class KucoinPrivateWebsocketListener extends WebSocketListener {

    private KucoinPrivateWSClientImpl privateWSClient;
    private Set<String> usages = new HashSet<>();
    private WebSocket webSocket;
    private static final Logger LOGGER = LoggerFactory.getLogger(KucoinPrivateWebsocketListener.class);

    private KucoinAPICallback<KucoinEvent<OrderActivateEvent>> orderActivateCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<AccountChangeEvent>> accountChangeCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<OrderChangeEvent>> orderChangeCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<? extends AdvancedOrderEvent>> advancedOrderCallback = new PrintCallback<>();

    public WebSocket getWebSocket() {
        return webSocket;
    }

    public void setWebSocket(WebSocket webSocket) {
        this.webSocket = webSocket;
    }

    public void setPrivateWSClient(KucoinPrivateWSClientImpl privateWSClient) {
        this.privateWSClient = privateWSClient;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        LOGGER.debug("web socket open");
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        LOGGER.debug("Got message: {}", text);
        JsonNode jsonObject = tree(text);
        LOGGER.debug("Parsed message: {}", text);

        String type = jsonObject.get("type").asText();
        if (!type.equals("message")) {
            LOGGER.debug("Ignoring message type ({})", type);
            return;
        }

        String topic = jsonObject.get("topic").asText();
        if (topic.contains(APIConstants.API_ACTIVATE_TOPIC_PREFIX.getValue())) {
            KucoinEvent<OrderActivateEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<OrderActivateEvent>>() {
            });
            orderActivateCallback.onResponse(kucoinEvent);
        } else if (topic.contains(APIConstants.API_BALANCE_TOPIC_PREFIX.getValue())) {
            KucoinEvent<AccountChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<AccountChangeEvent>>() {
            });
            accountChangeCallback.onResponse(kucoinEvent);
        } else if (topic.contains(APIConstants.API_ORDER_TOPIC_PREFIX.getValue())) {
            KucoinEvent<OrderChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<OrderChangeEvent>>() {
            });
            orderChangeCallback.onResponse(kucoinEvent);
        } else if (topic.contains(APIConstants.API_ADVANCED_ORDER_TOPIC_PREFIX.getValue())) {
            String subject = jsonObject.get("subject").asText();
            KucoinEvent<? extends AdvancedOrderEvent> kucoinEvent = null;
            if (Objects.equals(subject, "stopOrder")) {
                kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<StopOrderEvent>>() {
                });
            }
            if (kucoinEvent != null) {
                advancedOrderCallback.onResponse(kucoinEvent);
            }
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        LOGGER.error("Error on private socket", t);
//        webSocket.close(1000, null);
//        close();
//        try {
//            this.webSocket = privateWSClient.connect();
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    private JsonNode tree(String text) {
        try {
            return KucoinObjectMapper.INSTANCE.readTree(text);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialise message: " + text, e);
        }
    }

    public void close() {
        if (webSocket != null) {
            webSocket.close(1000, "Connection closed");
        }
    }
    private <T> T deserialize(String text, TypeReference<T> typeReference) {
        try {
            return KucoinObjectMapper.INSTANCE.readValue(text, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialise message: " + text, e);
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        super.onClosing(webSocket, code, reason);
        System.out.println("on closing private");
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        super.onClosed(webSocket, code, reason);
        System.out.println("on closed private");
    }
}
