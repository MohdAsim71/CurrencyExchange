package com.sample.exchange.core.network

import com.sample.exchange.feature.currencyexchange.data.modals.BitCoinResponse
import com.sample.exchange.feature.currencyexchange.data.modals.ExchangeRateResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {

    @GET("latest.json")
   suspend fun getExchangeRates(@Query("app_id") appId: String): Response<ExchangeRateResponse>

    @GET
    suspend fun getBitCoinsList(): Response<List<BitCoinResponse>>

}
