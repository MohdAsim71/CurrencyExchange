package com.sample.exchange.feature.currencyexchange.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sample.exchange.core.db.LocalStoragePref
import com.sample.exchange.core.db.SharedPreferencesKeys.EXCHANGE_RATES_KEY
import com.sample.exchange.core.db.SharedPreferencesKeys.RATES_LAST_FETCH_TIME
import com.sample.exchange.core.network.ApiService
import com.sample.exchange.core.network.Result
import com.sample.exchange.core.network.handleApi
import com.sample.exchange.feature.currencyexchange.data.modals.CurrencyRate
import com.sample.exchange.utils.EXCHANGE_API_KEY
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import org.jetbrains.annotations.VisibleForTesting
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val MAX_RETRY_COUNT = 2
private const val REFETCH_EXCHANGE_RATE_DURATION_IN_MINUTE = 30L

class ExchangeRateRepoImp @Inject constructor(
    private val localStoragePref: LocalStoragePref,
    private val apiService: ApiService,
    private val gson: Gson
) : ExchangeRateRepo {

    override suspend fun fetchExchangeRates(): Flow<Result<List<CurrencyRate>>> {
        return flow {
            // Fetch exchange rates from SharedPreferences
            val cachedRates = fetchExchangeRatesFromSharedPreferences()

            // Check if rates are stale (last fetch time > 30 minutes)
            val lastFetchTime = localStoragePref.getLong(RATES_LAST_FETCH_TIME, 0L)
            val isRatesStale =
                System.currentTimeMillis() - lastFetchTime > TimeUnit.MINUTES.toMillis(
                    REFETCH_EXCHANGE_RATE_DURATION_IN_MINUTE
                )

            // If rates are not stale and exist in SharedPreferences, emit them
            if (!isRatesStale && cachedRates.isNotEmpty()) {
                emit(Result.Success(cachedRates))
            } else {
                // Fetch rates from API with retry
                emitAll(fetchCurrencyRateFromApiWithRetry())
            }
        }
    }

    @VisibleForTesting fun fetchExchangeRatesFromSharedPreferences(): List<CurrencyRate> {
        val ratesJson = localStoragePref.getString(EXCHANGE_RATES_KEY, "")
        return if (ratesJson.isNotEmpty()) {
            val type = object : TypeToken<List<CurrencyRate>>() {}.type
            gson.fromJson(ratesJson, type)
        } else {
            emptyList()
        }
    }

    @VisibleForTesting fun saveRatesToSharedPreferences(rates: List<CurrencyRate>) {
        val ratesJson = gson.toJson(rates)
        localStoragePref.putString(EXCHANGE_RATES_KEY, ratesJson)
        localStoragePref.putLong(RATES_LAST_FETCH_TIME, System.currentTimeMillis())
    }

    @VisibleForTesting suspend fun fetchCurrencyRateFromApi(): Result<List<CurrencyRate>> {
        return when (val response = handleApi { apiService.getExchangeRates(EXCHANGE_API_KEY) }) {
            is Result.Success -> {
                val rates = response.data.rates.map { exchangeRate ->
                    CurrencyRate(currency = exchangeRate.key, rate = exchangeRate.value)
                }
                saveRatesToSharedPreferences(rates)
                Result.Success(rates)
            }

            is Result.ApiError -> {
                Result.ApiError(response.code, response.message)
            }

            is Result.ApiException -> {
                Result.ApiException(response.e)
            }
        }
    }

    @VisibleForTesting suspend fun fetchCurrencyRateFromApiWithRetry(): Flow<Result<List<CurrencyRate>>> {
        return flow {
            val result = fetchCurrencyRateFromApi()
            emit(result)
        }.retry(
            // Retry up to MAX_RETRY_COUNT times
            retries = MAX_RETRY_COUNT.toLong(),
            // Only retry if the exception occurs
            predicate = {
                true
            }
        )
    }

}


