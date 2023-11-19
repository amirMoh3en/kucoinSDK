/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kucoin.sdk.model.InstanceServer;
import lombok.Data;

import java.util.List;

/**
 * Created by chenshiwei on 2019/1/15.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebsocketTokenResponse {

    private List<InstanceServer> instanceServers;

    private String token;

}
