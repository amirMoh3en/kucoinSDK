/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.model.enums;

import com.kucoin.sdk.constants.APIConstants;
import lombok.Getter;

/**
 * Created by chenshiwei on 2019/1/23.
 */
@Getter
public enum PrivateChannelEnum {

    @Deprecated
    ORDER(APIConstants.API_ACTIVATE_TOPIC_PREFIX.getValue()),

    ORDER_CHANGE(APIConstants.API_ORDER_TOPIC_PREFIX.getValue()),

    ACCOUNT(APIConstants.API_BALANCE_TOPIC_PREFIX.getValue()),

    ADVANCED_ORDER(APIConstants.API_ADVANCED_ORDER_TOPIC_PREFIX.getValue());

    private String topicPrefix;

    PrivateChannelEnum(String topicPrefix) {
        this.topicPrefix = topicPrefix;
    }
}
