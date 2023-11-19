/**
 * Copyright 2019 Mek Global Limited.
 */
package com.kucoin.sdk.rest.adapter;

import com.kucoin.sdk.rest.impl.retrofit.AuthRetrofitAPIImpl;
import com.kucoin.sdk.rest.interfaces.LoanAPI;
import com.kucoin.sdk.rest.interfaces.retrofit.LoanAPIRetrofit;
import com.kucoin.sdk.rest.request.*;
import com.kucoin.sdk.rest.response.*;

import java.io.IOException;
import java.util.List;

/**
 * Created by ezreal on 2020/12/08.
 */
public class LoanAPIAdapter extends AuthRetrofitAPIImpl<LoanAPIRetrofit> implements LoanAPI {

    public LoanAPIAdapter(String baseUrl, String apiKey, String secret, String passPhrase, Integer apiKeyVersion) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
        this.secret = secret;
        this.passPhrase = passPhrase;
        this.apiKeyVersion = apiKeyVersion;
    }

    @Override
    public BorrowResponse borrow(BorrowRequest request) throws IOException {
        return executeSync(getAPIImpl().borrow(request));
    }

    @Override
    public BorrowQueryResponse queryBorrow(String orderId) throws IOException {
        return executeSync(getAPIImpl().queryBorrow(orderId));
    }

    @Override
    public Pagination<BorrowOutstandingResponse> queryBorrowOutstanding(BorrowRecordQueryRequest request) throws IOException {
        return executeSync(getAPIImpl().queryBorrowOutstanding(request.getCurrency(), request.getCurrentPage(), request.getPageSize()));
    }

    @Override
    public Pagination<BorrowRepaidResponse> queryBorrowRepaid(BorrowRecordQueryRequest request) throws IOException {
        return executeSync(getAPIImpl().queryBorrowRepaid(request.getCurrency(), request.getCurrentPage(), request.getPageSize()));
    }

    @Override
    public Void repayAll(RepayAllRequest request) throws IOException {
        return executeSync(getAPIImpl().repayAll(request));
    }

    @Override
    public Void repaySingle(RepaySingleRequest request) throws IOException {
        return executeSync(getAPIImpl().repaySingle(request));
    }

    @Override
    public LendResponse lend(LendRequest request) throws IOException {
        return executeSync(getAPIImpl().lend(request));
    }

    @Override
    public Void cancelLendOrder(String orderId) throws IOException {
        return executeSync(getAPIImpl().cancelLendOrder(orderId));
    }

    @Override
    public Void toggleAutoLend(ToggleAutoLendRequest request) throws IOException {
        return executeSync(getAPIImpl().toggleAutoLend(request));
    }

    @Override
    public Pagination<ActiveLendItem> queryActiveLend(String currency, Integer currentPage, Integer pageSize) throws IOException {
        return executeSync(getAPIImpl().queryActiveLend(currency, currentPage, pageSize));
    }

    @Override
    public Pagination<DoneLendItem> queryDoneLend(String currency, Integer currentPage, Integer pageSize) throws IOException {
        return executeSync(getAPIImpl().queryDoneLend(currency, currentPage, pageSize));

    }

    @Override
    public Pagination<UnsettledTradeItem> queryUnsettledTrade(String currency, Integer currentPage, Integer pageSize)
            throws IOException {
        return executeSync(getAPIImpl().queryUnsettledTrade(currency, currentPage, pageSize));
    }

    @Override
    public Pagination<SettledTradeItem> querySettledTrade(String currency, Integer currentPage, Integer pageSize)
            throws IOException {
        return executeSync(getAPIImpl().querySettledTrade(currency, currentPage, pageSize));
    }

    @Override
    public List<LendAssetsResponse> queryLendAssets(String currency) throws IOException {
        return executeSync(getAPIImpl().queryLendAssets(currency));
    }

    @Override
    public List<MarketItemResponse> queryMarket(String currency, Integer term) throws IOException {
        return executeSync(getAPIImpl().queryMarket(currency, term));
    }

    @Override
    public List<LastTradeResponse> queryLastTrade(String currency) throws IOException {
        return executeSync(getAPIImpl().queryLastTrade(currency));
    }
}
