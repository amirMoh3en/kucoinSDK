/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.model.enums;

import com.kucoin.sdk.constants.APIConstants;
import lombok.Getter;

/**
 * Created by chenshiwei on 2019/1/19.
 */
@Getter
public enum PublicChannelEnum {

    TICKER(APIConstants.API_TICKER_TOPIC_PREFIX.getValue()),

    LEVEL2(APIConstants.API_LEVEL2_TOPIC_PREFIX.getValue()),

    LEVEL2_DEPTH5(APIConstants.API_DEPTH5_LEVEL2_TOPIC_PREFIX.getValue()),

    LEVEL2_DEPTH50(APIConstants.API_DEPTH50_LEVEL2_TOPIC_PREFIX.getValue()),

    MATCH(APIConstants.API_MATCH_TOPIC_PREFIX.getValue()),

    @Deprecated
    LEVEL3(APIConstants.API_LEVEL3_TOPIC_PREFIX.getValue()),

    LEVEL3_V2(APIConstants.API_LEVEL3_V2_TOPIC_PREFIX.getValue()),

    SNAPSHOT(APIConstants.API_SNAPSHOT_PREFIX.getValue());


    private String topicPrefix;

    PublicChannelEnum(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
}
