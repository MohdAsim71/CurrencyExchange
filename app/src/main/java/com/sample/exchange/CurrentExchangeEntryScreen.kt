package com.sample.exchange

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sample.exchange.feature.currencyexchange.presentation.view.ExchangeRateScreen
import com.sample.exchange.feature.currencyexchange.presentation.viewmodel.ExchangeRateViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExchangeRateEntryScreenApp(viewModel: ExchangeRateViewModel) {

    val screenState = viewModel.calculateConvertedRates.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = stringResource(id = R.string.currency_exchange)) // Check if it should be "PayPay" or "PayPay"
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                ),
            )
        },
        content = { contentPadding ->
            ExchangeRateScreen(viewModel, modifier = Modifier.padding(contentPadding))
        }
    )
}
