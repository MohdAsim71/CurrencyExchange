package com.sample.exchange.feature.currencyexchange.presentation

import androidx.compose.runtime.Stable
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate

sealed interface ExchangeUiState {
    val isLoading: Boolean
    val errorMessage: String?
    val isError: Boolean

    data class NoExchangeData(
        override val isLoading: Boolean,
        override val errorMessage: String?,
        override val isError: Boolean
    ) : ExchangeUiState

    @Stable
    data class ExchangeData(
        val data: List<CurrencyRate> = emptyList(),
        val currencyString: List<String> = emptyList(),
        val selectedCurrency: String? = null,
        val enteredAmount: String? = null,
        override val isLoading: Boolean,
        override val errorMessage: String?,
        override val isError: Boolean
    ) : ExchangeUiState
}