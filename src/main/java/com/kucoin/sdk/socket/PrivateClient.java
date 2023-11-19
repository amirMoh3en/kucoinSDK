package com.kucoin.sdk.socket;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.kucoin.sdk.constants.APIConstants;
import com.kucoin.sdk.model.enums.PrivateChannelEnum;
import com.kucoin.sdk.websocket.KucoinAPICallback;
import com.kucoin.sdk.websocket.PrintCallback;
import com.kucoin.sdk.websocket.event.*;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

/**
 * Created By Alireza Dolatabadi
 * Date: 4/30/2023
 * Time: 7:55 PM
 */
@Log4j2
@Setter
@Getter
public class PrivateClient extends BaseClient {
    private WebSocketClient webSocketClient;
    private boolean isConnected = false;
    private Long pingInterval;

    private KucoinAPICallback<KucoinEvent<OrderActivateEvent>> orderActivateCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<AccountChangeEvent>> accountChangeCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<OrderChangeEvent>> orderChangeCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<? extends AdvancedOrderEvent>> advancedOrderCallback = new PrintCallback<>();

    private Map<PrivateChannelEnum, Set<String>> usages = new HashMap<>();


    public PrivateClient(String url, Long pingInterval) throws URISyntaxException {
        if (this.webSocketClient != null) {
            return;
        }
        this.pingInterval = pingInterval;
        this.webSocketClient = new WebSocketClient(new URI(url)) {
            public void onOpen(ServerHandshake serverHandshake) {
                log.debug("connected");
                usages.forEach((k, v) -> {
                    switch (k) {
                        case ORDER_CHANGE:
                            onOrderChange(orderChangeCallback, String.valueOf(v));
                            break;
                        case ACCOUNT:
                            onAccountBalance(accountChangeCallback);
                            break;
                        case ADVANCED_ORDER:
                            onAdvancedOrder(advancedOrderCallback, String.valueOf(v));
                            break;
                    }
                });
                new Thread(() -> {
                    while (isConnected) {
                        try {
                            Thread.sleep(pingInterval);
                            ping(UUID.randomUUID().toString());
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }


            public void onMessage(String text) {
                isConnected = true;
                log.debug("Got message: {}", text);
                JsonNode jsonObject = tree(text);
                log.debug("Parsed message OK");

                String type = jsonObject.get("type").asText();
                if (!type.equals("message")) {
                    log.debug("Ignoring message type ({})", type);
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


            public void onClose(int i, String s, boolean b) {
                log.error("closed");
                isConnected = false;
                new Thread(() -> {
                    try {
                        Thread.sleep(10000);
                        webSocketClient.reconnectBlocking();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }


            public void onError(Exception e) {
                log.error("error");
                log.error(e.getMessage());
                isConnected = false;
//                new Thread(() -> {
//                    try {
//                        Thread.sleep(10000);
//                        webSocketClient.reconnectBlocking();
//                    } catch (InterruptedException ex) {
//                        ex.printStackTrace();
//                    }
//                }).start();

            }


            @Override
            public void reconnect() {
                super.reconnect();

            }
        };

        try {
            webSocketClient.connectBlocking();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    public String onAccountBalance(KucoinAPICallback<KucoinEvent<AccountChangeEvent>> callback) {
        if (callback != null) {
            this.setAccountChangeCallback(callback);
        }
        usages.put(PrivateChannelEnum.ACCOUNT, null);
        return subscribe(APIConstants.API_BALANCE_TOPIC_PREFIX.getValue(), true, true, webSocketClient);
    }

    public String onOrderChange(KucoinAPICallback<KucoinEvent<OrderChangeEvent>> callback, String... symbols) {
        if (callback != null) {
            this.setOrderChangeCallback(callback);
        }
        usages.put(PrivateChannelEnum.ORDER_CHANGE, new HashSet<>(Arrays.asList(symbols)));
        return subscribe(APIConstants.API_ORDER_TOPIC_PREFIX.getValue(), true, true, webSocketClient);
    }


    public String onAdvancedOrder(KucoinAPICallback<KucoinEvent<? extends AdvancedOrderEvent>> callback, String... symbols) {
        if (callback != null) {
            this.setAdvancedOrderCallback(callback);
        }
        usages.put(PrivateChannelEnum.ADVANCED_ORDER, new HashSet<>(Arrays.asList(symbols)));
        return subscribe(APIConstants.API_ADVANCED_ORDER_TOPIC_PREFIX.getValue(), true, true, webSocketClient);
    }


    public String ping(String requestId) {
        return super.ping(requestId, webSocketClient);
    }


    public String unsubscribe(PrivateChannelEnum channelEnum, String... symbols) {
        return super.unsubscribe(channelEnum.getTopicPrefix() + String.join(",", symbols), true, true, webSocketClient);
    }

}
