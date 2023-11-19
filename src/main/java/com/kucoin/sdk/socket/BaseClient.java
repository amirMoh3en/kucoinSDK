package com.kucoin.sdk.socket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.kucoin.sdk.KucoinObjectMapper;
import com.kucoin.sdk.websocket.event.KucoinEvent;
import org.java_websocket.client.WebSocketClient;

import java.io.IOException;
import java.util.UUID;

/**
 * Created By Alireza Dolatabadi
 * Date: 4/30/2023
 * Time: 7:55 PM
 */
public class BaseClient {
    protected JsonNode tree(String text) {
        try {
            return KucoinObjectMapper.INSTANCE.readTree(text);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialise message: " + text, e);
        }
    }

    protected <T> T deserialize(String text, TypeReference<T> typeReference) {
        try {
            return KucoinObjectMapper.INSTANCE.readValue(text, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("Failed to deserialise message: " + text, e);
        }
    }

    protected String serialize(Object o) {
        try {
            return KucoinObjectMapper.INSTANCE.writeValueAsString(o);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failure serializing object", e);
        }
    }


    protected String subscribe(String topic,
                               boolean response,
                               boolean privateChannel,
                               WebSocketClient webSocketClient) {
        String uuid = UUID.randomUUID().toString();
        KucoinEvent<Void> subscribe = new KucoinEvent<>();
        subscribe.setId(uuid);
        subscribe.setType("subscribe");
        subscribe.setTopic(topic);
        subscribe.setPrivateChannel(privateChannel);
        subscribe.setResponse(response);
        webSocketClient.send(serialize(subscribe));
        return null;
    }

    protected String unsubscribe(String topic,
                                 boolean privateChannel,
                                 boolean response,
                                 WebSocketClient webSocketClient) {
        String uuid = UUID.randomUUID().toString();
        KucoinEvent<Void> subscribe = new KucoinEvent<>();
        subscribe.setId(uuid);
        subscribe.setType("unsubscribe");
        subscribe.setTopic(topic);
        subscribe.setPrivateChannel(privateChannel);
        subscribe.setResponse(response);
        webSocketClient.send(serialize(subscribe));
        return null;
    }

    public String ping(String requestId, WebSocketClient webSocketClient) {
        KucoinEvent<Void> ping = new KucoinEvent<>();
        ping.setId(requestId);
        ping.setType("ping");
        webSocketClient.send(serialize(ping));
        return requestId;
    }
}
