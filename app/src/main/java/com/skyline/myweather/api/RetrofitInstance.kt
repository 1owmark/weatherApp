package com.skyline.myweather.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: WeatherApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.weatherapi.com/") // Базовый URL для всех запросов
            .addConverterFactory(GsonConverterFactory.create()) // Конвертер JSON -> Kotlin
            .build()
            .create(WeatherApiService::class.java)
    }
}
