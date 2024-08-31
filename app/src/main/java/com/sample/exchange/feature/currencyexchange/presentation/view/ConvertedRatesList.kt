package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate

@Composable
fun ConvertedRatesList(convertedRates: List<CurrencyRate>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        modifier = Modifier.padding(8.dp)
    ) {
        items(convertedRates.size) { index ->
            Box(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxSize()
                    .border(BorderStroke(1.dp, Color.Black)),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .heightIn(min = 80.dp)
                ) {
                    Text(
                        text = "${convertedRates[index].currency}:",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Text(
                        text = String.format("%.2f", convertedRates[index].rate),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}