package com.sample.exchange.feature.currencyexchange.domain


import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import junit.framework.TestCase.assertEquals
import org.junit.Test

class ConvertedUseCaseTest {

    private val convertedUseCase = ConvertedUseCase()

    @Test
    fun `test calculateConvertedRates with non-null entered amount and selected currency`() {
        val rates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        val enteredAmount = "100"
        val selectedCurrency = "USD"

        val expectedRates = listOf(
            CurrencyRate("USD", 100.0),
            CurrencyRate("EUR", 85.0),
            CurrencyRate("GBP", 72.0)
        )

        val calculatedRates =
            convertedUseCase.calculateConvertedRates(rates, enteredAmount, selectedCurrency)

        assertEquals(expectedRates, calculatedRates)
    }

    @Test
    fun `test calculateConvertedRates with null entered amount`() {
        val rates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        val enteredAmount: String? = null
        val selectedCurrency = "USD"

        val expectedRates = listOf(
            CurrencyRate("USD", 0.0),
            CurrencyRate("EUR", 0.0),
            CurrencyRate("GBP", 0.0)
        )

        val calculatedRates =
            convertedUseCase.calculateConvertedRates(rates, enteredAmount, selectedCurrency)

        assertEquals(expectedRates, calculatedRates)
    }

    @Test
    fun `test calculateConvertedRates with empty entered amount`() {
        val rates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        val enteredAmount = ""
        val selectedCurrency = "USD"

        val expectedRates = listOf(
            CurrencyRate("USD", 0.0),
            CurrencyRate("EUR", 0.0),
            CurrencyRate("GBP", 0.0)
        )

        val calculatedRates =
            convertedUseCase.calculateConvertedRates(rates, enteredAmount, selectedCurrency)

        assertEquals(expectedRates, calculatedRates)
    }

    @Test
    fun `test calculateConvertedRates with zero entered amount`() {
        val rates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        val enteredAmount = "0"
        val selectedCurrency = "USD"

        val expectedRates = listOf(
            CurrencyRate("USD", 0.0),
            CurrencyRate("EUR", 0.0),
            CurrencyRate("GBP", 0.0)
        )

        val calculatedRates =
            convertedUseCase.calculateConvertedRates(rates, enteredAmount, selectedCurrency)

        assertEquals(expectedRates, calculatedRates)
    }
}