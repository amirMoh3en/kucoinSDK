/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.interfaces;

import com.kucoin.sdk.rest.request.WithdrawApplyRequest;
import com.kucoin.sdk.rest.response.Pagination;
import com.kucoin.sdk.rest.response.WithdrawApplyResponse;
import com.kucoin.sdk.rest.response.WithdrawQuotaResponse;
import com.kucoin.sdk.rest.response.WithdrawResponse;

import java.io.IOException;

/**
 * Created by chenshiwei on 2019/1/9.
 */
public interface WithdrawalAPI {

    /**
     * get withdrawal quotas
     *
     * @param currency currency. e.g. BTC
     * @return The withdrawal quotas.
     */
    WithdrawQuotaResponse getWithdrawQuotas(String currency, String chain) throws IOException;

    /**
     * apply withdraw
     *
     * @param request
     * @return A response containing the withdrawal id.
     */
    WithdrawApplyResponse applyWithdraw(WithdrawApplyRequest request) throws IOException;

    /**
     * Only withdrawals in processing status could be cancelled.
     *
     * @param withdrawalId unique identity for withdrawal order
     */
    void cancelWithdraw(String withdrawalId) throws IOException;

    /**
     * check withdrawals list
     *
     * @param currentPage
     * @param pageSize
     * @return A page of withdrawals.
     */
    Pagination<WithdrawResponse> getWithdrawList(int currentPage, int pageSize) throws IOException;

}
