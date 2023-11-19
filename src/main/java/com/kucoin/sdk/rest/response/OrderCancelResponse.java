/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderCancelResponse {

    private Set<String> cancelledOrderIds = new HashSet<>();
}
