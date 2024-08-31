package com.sample.exchange.feature.currencyexchange.presentation.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.sample.exchange.core.network.Result
import com.sample.exchange.feature.currencyexchange.data.ExchangeRateRepo
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import com.sample.exchange.feature.currencyexchange.domain.ConvertedUseCase
import com.sample.exchange.feature.currencyexchange.presentation.ExchangeUiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ExchangeRateViewModelTest {

    private lateinit var viewModel: ExchangeRateViewModel
    private lateinit var exchangeRateRepo: ExchangeRateRepo
    private lateinit var convertedUseCase: ConvertedUseCase

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        exchangeRateRepo = mockk()
        convertedUseCase = mockk()
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `test onCurrencySelected`() = runTest {
        // Set up your test scenario here
        val data =
            Result.Success(listOf(CurrencyRate("USD", 1.05),CurrencyRate("EUR", 2.3)))
        coEvery { exchangeRateRepo.fetchExchangeRates() } returns flowOf(data)
        viewModel = ExchangeRateViewModel(exchangeRateRepo, convertedUseCase)
        val initialUiState = ExchangeUiState.ExchangeData(
            data = emptyList(),
            currencyString = emptyList(),
            selectedCurrency = "USD",
            enteredAmount = "100",
            isLoading = false,
            errorMessage = null,
            isError = false
        )
        val updatedCurrency = "EUR"

        viewModel._state.value = initialUiState

        viewModel.onCurrencySelected(updatedCurrency)

        val newState = viewModel.uiState.first() as ExchangeUiState.ExchangeData
        assertEquals(updatedCurrency, newState.selectedCurrency)
        assertEquals(null, newState.enteredAmount)
    }

    @Test
    fun `test onAmountEntered`() = runTest {
        // Set up your test scenario here
        val data =
            Result.Success(listOf(CurrencyRate("USD", 1.05),CurrencyRate("EUR", 2.3)))
        coEvery { exchangeRateRepo.fetchExchangeRates() } returns flowOf(data)
        viewModel = ExchangeRateViewModel(exchangeRateRepo, convertedUseCase)
        val initialUiState = ExchangeUiState.ExchangeData(
            data = emptyList(),
            currencyString = emptyList(),
            selectedCurrency = "USD",
            enteredAmount = "100",
            isLoading = false,
            errorMessage = null,
            isError = false
        )
        val updatedAmount = "200"

        viewModel._state.value = initialUiState

        viewModel.onAmountEntered(updatedAmount)

        val newState = viewModel.uiState.first() as ExchangeUiState.ExchangeData
        assertEquals(initialUiState.selectedCurrency, newState.selectedCurrency)
        assertEquals(updatedAmount, newState.enteredAmount)
    }

    @Test
    fun `test fetchExchangeRateList`() = runTest {
        val mockApiException =
           Result.ApiException<List<CurrencyRate>>(Exception("Network error"))
        coEvery { exchangeRateRepo.fetchExchangeRates() } returns flowOf(mockApiException)


        val viewModel = ExchangeRateViewModel(exchangeRateRepo, convertedUseCase)

        // When
        viewModel.fetchExchangeRateList()

        // Then
        val currentState = viewModel.uiState.value
        assertTrue(currentState is ExchangeUiState.NoExchangeData)
        assertEquals(true, currentState.isLoading)
        assertEquals(null, currentState.errorMessage)
        assertEquals(false, currentState.isError)
    }


    @Test
    fun `test fetchExchangeRateList with API exception`() = runTest {

        // Mock the API error response
        val mockApiError = Result.ApiError<List<CurrencyRate>>(400, "Bad Request")
        coEvery { exchangeRateRepo.fetchExchangeRates() } returns flowOf(mockApiError)
        val viewModel = ExchangeRateViewModel(exchangeRateRepo, convertedUseCase)

        val job = launch {
            viewModel.uiState.collect { state ->
                if (!state.isLoading) {
                    assertFalse(state.isLoading)
                    assertEquals("Bad Request", state.errorMessage)
                    assertTrue(state.isError)
                    cancel() // Cancel the job to stop collecting
                }
            }
        }
        delay(1000) // Adjust this value as needed
        job.join()
    }

    @Test
    fun `test unCalculateConvertedRates`() = runTest {
        // Given
        val mockApiError = Result.ApiError<List<CurrencyRate>>(400, "Bad Request")
        coEvery { exchangeRateRepo.fetchExchangeRates() } returns flowOf(mockApiError)

        val viewModel = ExchangeRateViewModel(exchangeRateRepo, convertedUseCase)

        val state = ExchangeUiState.ExchangeData(
            data = listOf(CurrencyRate("USD", 1.0), CurrencyRate("EUR", 0.85)),
            isLoading = false,
            errorMessage = null,
            isError = false
        )

        // Mock the calculateConvertedRates function
        coEvery { convertedUseCase.calculateConvertedRates(any(), any(), any()) } returns listOf(
            CurrencyRate("EUR", 85.0)
        )

        // When
        viewModel.calculateConvertedRatesInternal(state)

        // Then
        delay(300) // Advance time to trigger debounce delay

        assertEquals(1, viewModel.calculateConvertedRates.value.size)
        assertEquals("EUR", viewModel.calculateConvertedRates.value[0].currency)
    }


    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

}
