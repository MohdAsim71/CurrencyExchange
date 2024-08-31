package com.sample.exchange.core.di

import android.content.Context
import com.google.gson.Gson
import com.sample.exchange.core.db.LocalStoragePref
import com.sample.exchange.core.db.SharedPreferencesManager
import com.sample.exchange.core.network.ApiService
import com.sample.exchange.utils.BASE_URL
import com.sample.exchange.utils.CONNECT_TIMEOUT_IN_SECONDS
import com.sample.exchange.utils.READ_TIMEOUT_IN_SECONDS
import com.sample.exchange.utils.WRITE_TIMEOUT_IN_SECONDS
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object AppModule {


    @Provides
    fun provideSharedPreferencesManager(@ApplicationContext context: Context): SharedPreferencesManager {
        return SharedPreferencesManager(context)
    }

    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context): LocalStoragePref {
        return SharedPreferencesManager(context)
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        return OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS) // Connection timeout
            .readTimeout(READ_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS) // Read timeout
            .writeTimeout(WRITE_TIMEOUT_IN_SECONDS, TimeUnit.SECONDS) // Write timeout
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }

}
