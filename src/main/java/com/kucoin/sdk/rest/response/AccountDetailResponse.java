/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by tao.mao on 2018/11/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDetailResponse {

    private String id;

    private String currency;

    private BigDecimal amount;

    private BigDecimal fee;

    private BigDecimal balance;

    private String bizType;

    private String direction;

    private String createdAt;

    private String context;

}
