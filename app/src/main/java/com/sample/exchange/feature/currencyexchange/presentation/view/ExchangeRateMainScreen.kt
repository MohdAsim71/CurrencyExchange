package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.exchange.R
import com.sample.exchange.feature.currencyexchange.presentation.ExchangeRateIntents
import com.sample.exchange.feature.currencyexchange.presentation.ExchangeUiState
import com.sample.exchange.feature.currencyexchange.presentation.viewmodel.ExchangeRateViewModel
import com.sample.exchange.ui.common.ErrorScreen
import com.sample.exchange.ui.common.LoadingScreen

@Composable
fun ExchangeRateScreen(viewModel: ExchangeRateViewModel, modifier: Modifier) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val convertedRates = viewModel.calculateConvertedRates.collectAsStateWithLifecycle()

    Column(modifier = modifier.padding(16.dp)) {
        when (val state = uiState) {
            // when there is no data
            is ExchangeUiState.NoExchangeData -> {
                if (state.isLoading) {
                    LoadingScreen()
                } else if (state.isError) {
                    ErrorScreen(errorMessage = state.errorMessage) {
                        viewModel.performAction(ExchangeRateIntents.FetchExchangeRateList)
                    }
                }
            }
            is ExchangeUiState.ExchangeData -> {
                AmountInput(
                    amount = state.enteredAmount,
                    onAmountEntered = viewModel::onAmountEntered
                )
                Spacer(modifier = Modifier.height(16.dp))
                CurrencySelection(
                    currencies = state.currencyString,
                    selectedCurrency = state.selectedCurrency,
                    onCurrencySelected = viewModel::onCurrencySelected
                )
                Spacer(modifier = Modifier.height(16.dp))
                ConvertedRatesList(convertedRates.value)
            }
        }
    }
}

@Composable
fun AmountInput(amount: String?, onAmountEntered: (String) -> Unit) {
    OutlinedTextField(
        value = amount ?: "",
        onValueChange = onAmountEntered,
        label = { Text(stringResource(id = R.string.enter_amount)) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(40.dp, 10.dp, 0.dp),
        textStyle = TextStyle(textAlign = TextAlign.End),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

