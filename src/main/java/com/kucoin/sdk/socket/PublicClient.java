package com.kucoin.sdk.socket;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Iterables;
import com.kucoin.sdk.constants.APIConstants;
import com.kucoin.sdk.model.enums.PublicChannelEnum;
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
import java.util.stream.Collectors;

/**
 * Created By Alireza Dolatabadi
 * Date: 4/30/2023
 * Time: 4:14 PM
 */
@Log4j2
@Setter
@Getter
public class PublicClient extends BaseClient {

    private WebSocketClient webSocketClient;
    private Long pingInterval;
    private boolean isConnected = false;
    private KucoinAPICallback<KucoinEvent<TickerChangeEvent>> tickerCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<Level2ChangeEvent>> level2Callback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<Level2Event>> level2Depth5Callback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<Level2Event>> level2Depth50Callback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<MatchExcutionChangeEvent>> matchDataCallback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<Level3ChangeEvent>> level3Callback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<Level3Event>> level3V2Callback = new PrintCallback<>();
    private KucoinAPICallback<KucoinEvent<SnapshotEvent>> snapshotCallback = new PrintCallback<>();
    private Map<PublicChannelEnum, Set<String>> usages = new HashMap<>();

    public PublicClient(String url, Long pingInterval) throws URISyntaxException {
        if (this.webSocketClient != null) {
            return;
        }
        this.pingInterval =  pingInterval;
        this.webSocketClient = new WebSocketClient(new URI(url)) {
            public void onOpen(ServerHandshake serverHandshake) {
                log.debug("connected");
                usages.forEach((k, v) -> {
                    switch (k) {

                        case TICKER:
                            onTicker(tickerCallback, String.valueOf(v));
                            break;
                        case LEVEL2:
                            onLevel2Data(level2Callback, String.valueOf(v));
                            break;
                        case LEVEL2_DEPTH5:
                            onLevel2Data(5, level2Depth5Callback, String.valueOf(v));
                            break;
                        case LEVEL2_DEPTH50:
                            onLevel2Data(50, level2Depth50Callback, String.valueOf(v));
                            break;
                        case MATCH:
                            onMatchExecutionData(matchDataCallback, String.valueOf(v));
                            break;
                        case LEVEL3:
                            onLevel3Data(level3Callback, String.valueOf(v));
                            break;
                        case LEVEL3_V2:
                            onLevel3Data_V2(level3V2Callback, String.valueOf(v));
                            break;
                        case SNAPSHOT:
                            onSnapshot(snapshotCallback, v);
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
                if (topic.contains(APIConstants.API_TICKER_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<TickerChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<TickerChangeEvent>>() {
                    });
                    tickerCallback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_LEVEL2_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<Level2ChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<Level2ChangeEvent>>() {
                    });
                    level2Callback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_MATCH_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<MatchExcutionChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<MatchExcutionChangeEvent>>() {
                    });
                    matchDataCallback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_LEVEL3_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<Level3ChangeEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<Level3ChangeEvent>>() {
                    });
                    level3Callback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_SNAPSHOT_PREFIX.getValue())) {
                    KucoinEvent<SnapshotEvent> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<SnapshotEvent>>() {
                    });
                    snapshotCallback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_LEVEL3_V2_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<Level3Event> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<Level3Event>>() {
                    });
                    level3V2Callback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_DEPTH5_LEVEL2_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<Level2Event> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<Level2Event>>() {
                    });
                    level2Depth5Callback.onResponse(kucoinEvent);
                } else if (topic.contains(APIConstants.API_DEPTH50_LEVEL2_TOPIC_PREFIX.getValue())) {
                    KucoinEvent<Level2Event> kucoinEvent = deserialize(text, new TypeReference<KucoinEvent<Level2Event>>() {
                    });
                    level2Depth50Callback.onResponse(kucoinEvent);
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

    public synchronized WebSocketClient getWebSocketClient() {
        if (webSocketClient == null) {
            throw new RuntimeException("NO_SOCKET");
        }
        return webSocketClient;
    }


    public String onTicker(KucoinAPICallback<KucoinEvent<TickerChangeEvent>> callback, String... symbols) {
        if (callback != null) {
            this.setTickerCallback(callback);
        }
        usages.put(PublicChannelEnum.TICKER, new HashSet<>(Arrays.asList(symbols)));
        String topic = APIConstants.API_TICKER_TOPIC_PREFIX.getValue() + Arrays.stream(symbols).collect(Collectors.joining(","));
        return subscribe(topic, true, false, webSocketClient);
    }


    public String onLevel2Data(KucoinAPICallback<KucoinEvent<Level2ChangeEvent>> callback, String... symbols) {
        if (callback != null) {
            this.setLevel2Callback(callback);
        }
        usages.put(PublicChannelEnum.LEVEL2, new HashSet<>(Arrays.asList(symbols)));
        String topic = APIConstants.API_LEVEL2_TOPIC_PREFIX.getValue() + Arrays.stream(symbols).collect(Collectors.joining(","));
        return subscribe(topic, true, false, webSocketClient);
    }


    public String onLevel2Data(int depth, KucoinAPICallback<KucoinEvent<Level2Event>> callback, String... symbols) {
        String topic = null;
        String market = Arrays.stream(symbols).collect(Collectors.joining(","));
        if (depth == 5) {
            if (callback != null) {
                this.setLevel2Depth5Callback(callback);
            }
            topic = APIConstants.API_DEPTH5_LEVEL2_TOPIC_PREFIX.getValue() + market;
        } else if (depth == 50) {
            if (callback != null) {
                this.setLevel2Depth50Callback(callback);
            }
            topic = APIConstants.API_DEPTH50_LEVEL2_TOPIC_PREFIX.getValue() + market;
        }
        if (topic == null) {
            return null;
        }
        usages.put(PublicChannelEnum.LEVEL2_DEPTH50, new HashSet<>(Arrays.asList(symbols)));

        return subscribe(topic, true, false, webSocketClient);
    }


    public String onMatchExecutionData(KucoinAPICallback<KucoinEvent<MatchExcutionChangeEvent>> callback, String... symbols) {
        String market = Arrays.stream(symbols).collect(Collectors.joining(","));
        if (callback != null) {
            this.setMatchDataCallback(callback);
        }
        usages.put(PublicChannelEnum.MATCH, new HashSet<>(Arrays.asList(symbols)));
        String topic = APIConstants.API_MATCH_TOPIC_PREFIX.getValue() + market;
        return subscribe(topic, true, false, webSocketClient);
    }


    public String onLevel3Data_V2(KucoinAPICallback<KucoinEvent<Level3Event>> callback, String... symbols) {
        if (callback != null) {
            this.setLevel3V2Callback(callback);
        }
        usages.put(PublicChannelEnum.LEVEL3_V2, new HashSet<>(Arrays.asList(symbols)));
        String topic = APIConstants.API_LEVEL3_V2_TOPIC_PREFIX.getValue() + Arrays.stream(symbols).collect(Collectors.joining(","));
        return subscribe(topic, true, false, webSocketClient);
    }


    @Deprecated
    public String onLevel3Data(KucoinAPICallback<KucoinEvent<Level3ChangeEvent>> callback, String... symbols) {
        if (callback != null) {
            this.setLevel3Callback(callback);
        }
        usages.put(PublicChannelEnum.LEVEL3, new HashSet<>(Arrays.asList(symbols)));
        String topic = APIConstants.API_LEVEL3_TOPIC_PREFIX.getValue() +
                String.join(",", symbols);
        return subscribe(topic, true, false, webSocketClient);
    }

    public String ping(String requestId) {
        return super.ping(requestId, webSocketClient);
    }

    public String unsubscribe(PublicChannelEnum channelEnum, String... symbols) {
        usages.remove(channelEnum);
        return unsubscribe(channelEnum.getTopicPrefix() +
                        String.join(",", symbols),
                false, true, webSocketClient);
    }

    public void onSnapshot(
            KucoinAPICallback<KucoinEvent<SnapshotEvent>> callback,
            Set<String> marketsString
    ) {
        if (usages.get(PublicChannelEnum.SNAPSHOT) != null) {
            usages.get(PublicChannelEnum.SNAPSHOT).addAll(marketsString);
        } else {
            usages.put(PublicChannelEnum.SNAPSHOT, marketsString);
        }
        if (callback != null) {
            this.setSnapshotCallback(callback);
        }
        Iterable<List<String>> smallerLists = Iterables.partition(marketsString, 100);
        smallerLists.forEach(strings -> {
            String symbols = "";
            for (String market : strings) {
                symbols = symbols.concat(market + ",");
            }
            symbols = symbols.substring(0, symbols.length() - 1);
            String topic = APIConstants.API_SNAPSHOT_PREFIX.getValue() + symbols;
            subscribe(topic, true, false, webSocketClient);
        });
    }


}
