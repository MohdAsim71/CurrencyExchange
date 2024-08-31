package com.sample.exchange.feature.currencyexchange.data


import com.google.gson.Gson
import com.sample.exchange.core.db.LocalStoragePref
import com.sample.exchange.core.db.SharedPreferencesKeys.EXCHANGE_RATES_KEY
import com.sample.exchange.core.db.SharedPreferencesKeys.RATES_LAST_FETCH_TIME
import com.sample.exchange.core.network.ApiService
import com.sample.exchange.core.network.Result
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import com.sample.exchange.feature.currencyexchange.data.modals.ExchangeRateResponse
import com.sample.exchange.utils.EXCHANGE_API_KEY
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.HttpException
import retrofit2.Response

@ExperimentalCoroutinesApi
class ExchangeRateRepoImpTest {

    private lateinit var localStoragePref: LocalStoragePref
    private lateinit var apiService: ApiService
    private lateinit var exchangeRateRepoImp: ExchangeRateRepoImp

    @Before
    fun setup() {
        localStoragePref = mockk(relaxed = true)
        apiService = mockk()
        exchangeRateRepoImp = ExchangeRateRepoImp(localStoragePref, apiService, Gson())
    }

    @Test
    fun `test fetchExchangeRates from SharedPreferences`() = runTest {
        // Given
        val localStoragePref: LocalStoragePref = mockk(relaxed = true)
        val apiService: ApiService = mockk()
        val gson = Gson()

        val cachedRates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        coEvery { localStoragePref.getLong(any(), any()) } returns System.currentTimeMillis()
        coEvery { localStoragePref.getString(any(), any()) } returns gson.toJson(cachedRates)

        val exchangeRateRepoImp = ExchangeRateRepoImp(localStoragePref, apiService, gson)

        // When
        val result = exchangeRateRepoImp.fetchExchangeRates().first()

        // Then
        assertEquals(Result.Success(cachedRates), result)
        coEvery { localStoragePref.getLong(any(), any()) }
        coEvery { localStoragePref.getString(any(), any()) }
    }

    @Test
    fun `test saveRatesToSharedPreferences`() = runTest {
        // Given
        val rates = listOf(
            CurrencyRate("USD", 1.0),
            CurrencyRate("EUR", 0.85),
            CurrencyRate("GBP", 0.72)
        )
        val ratesJson =
            "[{\"currency\":\"USD\",\"rate\":1.0},{\"currency\":\"EUR\",\"rate\":0.85},{\"currency\":\"GBP\",\"rate\":0.72}]"
        every { localStoragePref.putString(any(), any()) } just Runs
        every { localStoragePref.putLong(any(), any()) } just Runs

        // When
        exchangeRateRepoImp.saveRatesToSharedPreferences(rates)

        // Then
        verify(exactly = 1) { localStoragePref.putString(EXCHANGE_RATES_KEY, ratesJson) }
        verify(exactly = 1) { localStoragePref.putLong(RATES_LAST_FETCH_TIME, any()) }
    }

    @Test
    fun `test fetchCurrencyRateFromApi API error`() = runTest {
        // Given
        val localStoragePref: LocalStoragePref = mockk(relaxed = true)
        val apiService: ApiService = mockk()
        val gson = Gson()

        val exchangeRateRepoImp = ExchangeRateRepoImp(localStoragePref, apiService, gson)

        val apiErrorCode = 404 // Assuming this is the error code you expect
        val errorMessage = "Not found" // Example error message
        val errorBody = ResponseBody.create(null, errorMessage)
        val response = Response.error<ExchangeRateResponse>(apiErrorCode, errorBody)
        coEvery { apiService.getExchangeRates(any()) } throws HttpException(response)

        // When
        val result = exchangeRateRepoImp.fetchCurrencyRateFromApi()

        // Then
        assertTrue(result is Result.ApiError)
        val apiError = result as Result.ApiError
        assertEquals(apiErrorCode, apiError.code)
    }

    @Test
    fun `test fetchCurrencyRateFromApi Api Success`() = runTest {
        val localStoragePref: LocalStoragePref = mockk(relaxed = true)
        val apiService: ApiService = mockk()
        val gson = Gson()

        val exchangeRateRepoImp = ExchangeRateRepoImp(localStoragePref, apiService, gson)

        val exchangeRateResponse =
            ExchangeRateResponse("disclaimer", "license", 0L, "", mutableMapOf("USD" to 1.05))
        val response = Response.success(exchangeRateResponse)
        coEvery { apiService.getExchangeRates(EXCHANGE_API_KEY) } returns response
        val result = exchangeRateRepoImp.fetchCurrencyRateFromApi() as Result.Success
        assertEquals(result.data.size, 1)
        assertEquals(result.data[0].currency, "USD")

        val exchangeRateResponse1 =
            ExchangeRateResponse("disclaimer", "license", 0L, "", mutableMapOf("USD" to 1.05, "INR" to 83.50))

    }

    @Test
    fun `test fetchCurrencyRateFromApi Api Success with more data`() = runTest {
        val localStoragePref: LocalStoragePref = mockk(relaxed = true)
        val apiService: ApiService = mockk()
        val gson = Gson()

        val exchangeRateRepoImp = ExchangeRateRepoImp(localStoragePref, apiService, gson)

        val exchangeRateResponse =
            ExchangeRateResponse("disclaimer", "license", 0L, "", mutableMapOf("USD" to 1.05, "INR" to 83.50))
        val response = Response.success(exchangeRateResponse)
        coEvery { apiService.getExchangeRates(EXCHANGE_API_KEY) } returns response
        val result = exchangeRateRepoImp.fetchCurrencyRateFromApi() as Result.Success
        assertEquals(result.data.size, 2)
    }

    @Test
    fun `test fetchCurrencyRateFromApiWithRetry`() = runTest {
        // Given
        val exchangeRateResponse =
            ExchangeRateResponse("disclaimer", "license", 0L, "", mapOf("USD" to 1.05, "INR" to 83.50))
        val response = Response.success(exchangeRateResponse)
        coEvery { apiService.getExchangeRates(EXCHANGE_API_KEY) } returns response

        // When
        val flow: Flow<Result<List<CurrencyRate>>> = exchangeRateRepoImp.fetchCurrencyRateFromApiWithRetry()

        // Then
        val result = flow.first()
        assertTrue(result is Result.Success) // Ensure that the result is success
        val successResult = result as Result.Success
        assertEquals(2, successResult.data.size) // Ensure that the response contains the expected number of currency rates
        // You can add more assertions here if needed
    }
}

