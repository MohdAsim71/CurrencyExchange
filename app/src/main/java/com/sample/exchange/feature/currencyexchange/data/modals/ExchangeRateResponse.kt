package com.sample.exchange.feature.currencyexchange.data.modals

data class ExchangeRateResponse(
    val disclaimer: String,
    val license: String,
    val timestamp: Long,
    val base: String,
    val rates: Map<String, Double>
)

data class CurrencyRate(
    val currency: String,
    val rate: Double
)




