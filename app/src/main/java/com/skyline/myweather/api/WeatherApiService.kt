package com.skyline.myweather.api
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {

    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 1, // Количество дней прогноза
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponse
}

