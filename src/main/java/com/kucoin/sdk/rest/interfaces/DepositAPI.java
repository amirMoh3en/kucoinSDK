/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.interfaces;

import com.kucoin.sdk.rest.response.DepositAddressResponse;
import com.kucoin.sdk.rest.response.DepositResponse;
import com.kucoin.sdk.rest.response.Pagination;

import java.io.IOException;

/**
 * Created by chenshiwei on 2019/1/9.
 */
public interface DepositAPI {

    /**
     * Create deposit address of currency for deposit. You can just create one deposit address.
     *
     * @param currency the code of the currency
     * @param chain    the chain name of currency
     * @return Details of a deposit address.
     */
    DepositAddressResponse createDepositAddress(String currency, String chain) throws IOException;

    /**
     * Get deposit address of currency for deposit.
     * If return data is null , you may need create a deposit address first.
     *
     * @param currency the code of the currency
     * @return Details of a deposit address.
     */
    DepositAddressResponse getDepositAddress(String currency, String chain) throws IOException;

    /**
     * Get deposit page list.
     *
     * @param currentPage
     * @param pageSize
     * @return A page of deposits.
     */
    Pagination<DepositResponse> getDepositPageList(int currentPage, int pageSize) throws IOException;

}
