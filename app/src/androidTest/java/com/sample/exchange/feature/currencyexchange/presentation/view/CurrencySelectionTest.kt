package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CurrencySelectionTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testCurrencySelection() {
        val currencies = listOf("USD", "EUR", "GBP", "ALL")
        val selectedCurrency = currencies[0]

        composeTestRule.setContent {
            CurrencySelection(
                currencies = currencies,
                selectedCurrency = selectedCurrency,
                onCurrencySelected = {}
            )
        }

        // Verify the initial state of the selection button
        composeTestRule.onNodeWithText(selectedCurrency).assertExists()

        composeTestRule.onNode(hasText(selectedCurrency)).performClick()

        // Verify that the dropdown menu is expanded
        composeTestRule.onNodeWithText("EUR").assertExists()
        composeTestRule.onNodeWithText("GBP").assertExists()
        composeTestRule.onNodeWithText("ALL").assertExists()

        // Click on a currency item in the dropdown
        composeTestRule.onNodeWithText("EUR").performClick()

        // Verify that the selected currency is updated
        composeTestRule.onNodeWithText("EUR").assertExists()

        composeTestRule.onNodeWithText("EUR").performClick()

        composeTestRule.onNodeWithText("ALL").assertExists()

        // Click on a currency item in the dropdown
        composeTestRule.onNodeWithText("ALL").performClick()

        // Verify that the selected currency is updated
        composeTestRule.onNodeWithText("ALL").assertExists()


    }
}
