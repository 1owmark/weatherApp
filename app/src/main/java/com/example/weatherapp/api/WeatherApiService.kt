package com.example.weatherapp.api
import retrofit2.http.GET
import retrofit2.http.Query


interface WeatherApiService {
    @GET("v1/current.json") // Это часть URL, которая добавляется к базовому адресу API
    suspend fun getCurrentWeather(
        @Query("key") apiKey: String,  // API-ключ (нужен для авторизации)
        @Query("q") location: String,  // Город или координаты (широта, долгота)
    ): WeatherResponse

    @GET("v1/forecast.json")
    suspend fun getForecast(
        @Query("key") apiKey: String,
        @Query("q") location: String,
        @Query("days") days: Int = 1, // Количество дней прогноза
        @Query("aqi") aqi: String = "no",
        @Query("alerts") alerts: String = "no"
    ): WeatherResponse
}

