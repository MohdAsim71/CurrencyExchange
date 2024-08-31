package com.sample.exchange.feature.currencyexchange.network

import com.sample.exchange.core.network.ApiService
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private lateinit var mockWebServer: MockWebServer
private lateinit var apiService: ApiService

class ApiServiceTest {

    @Before
    fun setUp() {
        mockWebServer = MockWebServer()
        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `test fetchExchangeRates`() = runTest {
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(
                "{\n" +
                        "  \"disclaimer\": \"Sample disclaimer\",\n" +
                        "  \"license\": \"Sample license\",\n" +
                        "  \"timestamp\": 1623485734,\n" +
                        "  \"base\": \"USD\",\n" +
                        "  \"rates\": {\n" +
                        "    \"USD\": 1.0,\n" +
                        "    \"EUR\": 0.85,\n" +
                        "    \"GBP\": 0.72\n" +
                        "  }\n" +
                        "}"
            )
        mockWebServer.enqueue(mockResponse)

        val response = apiService.getExchangeRates("apiid")
        assertEquals("Sample disclaimer", response.body()?.disclaimer)
        assertEquals("Sample license", response.body()?.license)
        assertEquals(1623485734L, response.body()?.timestamp)
        assertEquals("USD", response.body()?.base)
        assertEquals(1.0, response.body()?.rates?.get("USD"))
        assertEquals(0.85, response.body()?.rates?.get("EUR"))
        assertEquals(0.72, response.body()?.rates?.get("GBP"))
    }
}
