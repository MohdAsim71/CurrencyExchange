package com.sample.exchange.feature.currencyexchange.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sample.exchange.core.network.Result
import com.sample.exchange.feature.currencyexchange.data.ExchangeRateRepo
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import com.sample.exchange.feature.currencyexchange.domain.ConvertedUseCase
import com.sample.exchange.feature.currencyexchange.presentation.ExchangeRateIntents
import com.sample.exchange.feature.currencyexchange.presentation.ExchangeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.annotations.VisibleForTesting
import javax.inject.Inject

@HiltViewModel
class ExchangeRateViewModel @Inject constructor(
    private val exchangeRateRepo: ExchangeRateRepo,
    private val convertedUseCase: ConvertedUseCase
) : ViewModel() {

    @VisibleForTesting
    val _state = MutableStateFlow<ExchangeUiState>(
        ExchangeUiState.NoExchangeData(isLoading = true, errorMessage = null, isError = false)
    )
    val uiState: StateFlow<ExchangeUiState> = _state.asStateFlow()

    @VisibleForTesting
    val _calculateConvertedRates = MutableStateFlow<List<CurrencyRate>>(emptyList())
    val calculateConvertedRates: StateFlow<List<CurrencyRate>> = _calculateConvertedRates

    @VisibleForTesting
    lateinit var calculateRateJob: Job

    init {
        performAction(ExchangeRateIntents.FetchExchangeRateList)
    }

    fun performAction(intents: ExchangeRateIntents) {
        when (intents) {
            is ExchangeRateIntents.FetchExchangeRateList -> {
                fetchExchangeRateList()
            }
        }
    }

    fun onCurrencySelected(currency: String) {
        updateState(currency = currency)
    }

    fun onAmountEntered(amount: String) {
        updateState(amount = amount)
    }

    @VisibleForTesting
    fun updateState(currency: String? = null, amount: String? = null) {
        _state.update { currentState ->
            when (currentState) {
                is ExchangeUiState.ExchangeData -> currentState.copy(
                    selectedCurrency = currency ?: currentState.selectedCurrency,
                    enteredAmount = amount ?: currentState.enteredAmount
                ).also {
                    calculateConvertedRates(it)
                }

                else -> currentState
            }
        }
    }

    @VisibleForTesting
    fun fetchExchangeRateList() {
        _state.update {
            ExchangeUiState.NoExchangeData(
                isLoading = true,
                errorMessage = null,
                isError = false
            )
        }
        viewModelScope.launch(Dispatchers.IO) {
            exchangeRateRepo.fetchExchangeRates().collect { res ->
                when (res) {
                    is Result.Success -> {
                        _state.update {
                            ExchangeUiState.ExchangeData(
                                data = res.data,
                                currencyString = res.data.map { it.currency },
                                selectedCurrency = res.data.firstOrNull()?.currency,
                                isLoading = false,
                                errorMessage = null,
                                isError = false
                            )
                        }
                    }

                    is Result.ApiError -> {
                        _state.update {
                            ExchangeUiState.NoExchangeData(
                                isLoading = false,
                                errorMessage = res.message ?: "Something went wrong",
                                isError = true
                            )
                        }
                    }

                    is Result.ApiException -> {
                        _state.update {
                            ExchangeUiState.NoExchangeData(
                                isLoading = false,
                                errorMessage = res.e.message ?: "Something went wrong",
                                isError = true
                            )
                        }
                    }
                }
            }
        }
    }

    @VisibleForTesting
    fun calculateConvertedRates(state: ExchangeUiState.ExchangeData) {
        if (::calculateRateJob.isInitialized && calculateRateJob.isActive) {
            calculateRateJob.cancel()
        }
        viewModelScope.launch {
            calculateRateJob = launch {
                // Call the suspending function to calculate converted rates
                calculateConvertedRatesInternal(state)
            }
        }
    }

    @VisibleForTesting
    suspend fun calculateConvertedRatesInternal(state: ExchangeUiState.ExchangeData) {
        // Debounce for 200 milliseconds
        delay(200)
        _calculateConvertedRates.update {
            convertedUseCase.calculateConvertedRates(
                state.data,
                state.enteredAmount,
                state.selectedCurrency
            )
        }
    }
}
