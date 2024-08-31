package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sample.exchange.R

@Composable
fun CurrencySelection(
    currencies: List<String>,
    selectedCurrency: String?,
    onCurrencySelected: (String) -> Unit
) {
    var selectedText by rememberSaveable {
        mutableStateOf(
            selectedCurrency ?: currencies.getOrNull(0) ?: ""
        )
    }
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 50.dp)
            .wrapContentWidth(align = Alignment.End)
    ) {
        var selectedIndex by rememberSaveable { mutableIntStateOf(0) }
        LargeDropdownMenu(
            modifier = Modifier
                .padding(start = 100.dp)
                .align(Alignment.TopEnd)
            ,
            label = stringResource(id = R.string.select_currency),
            items = currencies,
            selectedIndex = selectedIndex,
            onItemSelected = { index, _ ->
                selectedIndex = index
                selectedText = currencies[index]
                onCurrencySelected(currencies[index])
            },
        )
    }
}
