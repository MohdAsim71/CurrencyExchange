package com.sample.exchange.feature.currencyexchange.data

import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import com.sample.exchange.core.network.Result
import kotlinx.coroutines.flow.Flow

interface ExchangeRateRepo {
    suspend fun fetchExchangeRates(): Flow<Result<List<CurrencyRate>>>
}
