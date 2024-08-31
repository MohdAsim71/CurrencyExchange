package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.foundation.layout.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class ConvertedRatesListTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testConvertedRatesList() {
        // Prepare data
        val convertedRates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        // Launch the Compose UI
        composeTestRule.setContent {
            ConvertedRatesList(convertedRates)
        }

        // Verify the layout
        composeTestRule.onNodeWithText("USD:").assertExists()
        composeTestRule.onNodeWithText("1.00").assertExists()
        composeTestRule.onNodeWithText("EUR:").assertExists()
        composeTestRule.onNodeWithText("0.85").assertExists()
        composeTestRule.onNodeWithText("GBP:").assertExists()
        composeTestRule.onNodeWithText("0.72").assertExists()
    }
}