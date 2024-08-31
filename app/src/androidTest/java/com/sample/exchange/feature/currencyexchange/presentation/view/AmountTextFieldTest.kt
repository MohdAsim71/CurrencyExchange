package com.sample.exchange.feature.currencyexchange.presentation.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextClearance
import org.junit.Rule
import org.junit.Test

class AmountTextFieldTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAmountInput() {
        composeTestRule.setContent {
            var amount by remember { mutableStateOf("123") }
            AmountInput(amount = amount, onAmountEntered = {
                amount = it
            })
        }

        // Verify that the text field displays the correct initial text
        composeTestRule.onNodeWithText("123").assertExists()

        composeTestRule.onNodeWithText("123").performTextClearance()

        composeTestRule.onNodeWithText("123").assertDoesNotExist()

        // Verify that the text field is now empty
        composeTestRule.onNodeWithText("Enter amount").assertExists()

    }
}