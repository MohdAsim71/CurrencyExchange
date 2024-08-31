package com.sample.exchange.feature.currencyexchange.domain

import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import javax.inject.Inject

class ConvertedUseCase @Inject constructor() {

     fun calculateConvertedRates(
         rates: List<CurrencyRate>,
         enteredAmount: String?,
         selectedCurrency: String?
    ): List<CurrencyRate> {
         // Defaulting amount to 0.0 if entered amount is null or empty
         val amount = enteredAmount?.toDoubleOrNull() ?: 0.0
         // Defaulting selectedRate to 1.0 to avoid division by zero errors
         val selectedRate = rates.find { it.currency == selectedCurrency }?.rate ?: 1.0
         return if (amount > 0) {
             rates.map { rate ->
                 val convertedRate = rate.rate * (amount / selectedRate)
                 val trimmedRate = String.format("%.2f", convertedRate).toDouble()
                 CurrencyRate(rate.currency, trimmedRate)
             }
         } else {
             rates.map { rate ->
                 CurrencyRate(rate.currency, 0.0)
             }
         }
    }

}