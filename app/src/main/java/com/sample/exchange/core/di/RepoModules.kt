package com.sample.exchange.core.di

import com.sample.exchange.feature.currencyexchange.data.ExchangeRateRepoImp
import com.sample.exchange.feature.currencyexchange.data.ExchangeRateRepo
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
interface RepoModules {
    @Binds
    fun bindICatRepo(catRepository: ExchangeRateRepoImp): ExchangeRateRepo
}
