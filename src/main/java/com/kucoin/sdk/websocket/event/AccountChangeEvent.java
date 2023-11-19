/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.websocket.event;

import lombok.Data;

/**
 * Created by chenshiwei on 2019/1/23.
 */
@Data
public class AccountChangeEvent {

    private String total;

    private String available;

    private String availableChange;

    private String currency;

    private String hold;

    private String holdChange;

    private String relationEvent;

    private Object relationContext;

    private String relationEventId;

    private String time;

    private String accountId;

}
