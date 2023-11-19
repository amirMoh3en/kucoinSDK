/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by zicong.lu on 2018/12/14.
 */
@AllArgsConstructor
@Getter
public enum APIConstants {

    API_BASE_URL("https://openapi-v2.kucoin.com/"),

    USER_API_KEY("KC-API-KEY"),
    USER_API_SECRET("KC-API-SECRET"),
    USER_API_PASSPHRASE("KC-API-PASSPHRASE"),

    API_HEADER_KEY("KC-API-KEY"),
    API_HEADER_SIGN("KC-API-SIGN"),
    API_HEADER_PASSPHRASE("KC-API-PASSPHRASE"),
    API_HEADER_TIMESTAMP("KC-API-TIMESTAMP"),
    API_HEADER_USER_AGENT("User-Agent"),
    API_HEADER_KEY_VERSION("KC-API-KEY-VERSION"),

    API_TICKER_TOPIC_PREFIX("/market/ticker:"),
    API_LEVEL2_TOPIC_PREFIX("/market/level2:"),
    API_DEPTH5_LEVEL2_TOPIC_PREFIX("/spotMarket/level2Depth5:"),
    API_DEPTH50_LEVEL2_TOPIC_PREFIX("/spotMarket/level2Depth50:"),
    API_MATCH_TOPIC_PREFIX("/market/match:"),
    
    API_LEVEL3_TOPIC_PREFIX("/market/level3:"),
    API_LEVEL3_V2_TOPIC_PREFIX("/spotMarket/level3:"),
    
    API_ACTIVATE_TOPIC_PREFIX("/market/level3:"),
    API_BALANCE_TOPIC_PREFIX("/account/balance"),
    API_ADVANCED_ORDER_TOPIC_PREFIX("/spotMarket/advancedOrders"),
    API_ORDER_TOPIC_PREFIX("/spotMarket/tradeOrders"),
    API_SNAPSHOT_PREFIX("/market/snapshot:");
    
    private final String value;
}
