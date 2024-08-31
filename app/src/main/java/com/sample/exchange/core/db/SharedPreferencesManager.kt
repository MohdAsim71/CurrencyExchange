package com.sample.exchange.core.db

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject

class SharedPreferencesManager @Inject constructor(context: Context) : LocalStoragePref {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "exchange_rates_preferences",
        Context.MODE_PRIVATE
    )

    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override fun getLong(key: String, defaultValue: Long): Long {
        return sharedPreferences.getLong(key, defaultValue)
    }

    override fun putLong(key: String, value: Long) {
        sharedPreferences.edit().putLong(key, value).apply()
    }

}
