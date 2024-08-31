package com.sample.exchange.feature.currencyexchange.presentation


sealed class ExchangeRateIntents {
    data object FetchExchangeRateList : ExchangeRateIntents()
}
