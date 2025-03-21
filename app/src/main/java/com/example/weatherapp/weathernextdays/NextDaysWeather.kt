package com.example.weatherapp.weathernextdays

data class NextDaysWeather(
    val date: String,
    val dayOfWeek: String,
    val icon: Int,
    val dayTemp: Int,
    val nightTemp: Int
)
