package com.example.weatherapp.api

import com.google.gson.annotations.SerializedName

// Ответ от API для текущей погоды и прогноза
data class WeatherResponse(
    @SerializedName("location") val location: Location, // Информация о локации
    @SerializedName("current") val current: CurrentWeather, // Текущая погода
    @SerializedName("forecast") val forecast: Forecast // Прогноз на текущий день и следующие 3 дня
)

// Локация, содержащая только нужные поля
data class Location(
    @SerializedName("name") val name: String, // Город
    @SerializedName("country") val country: String,
    @SerializedName("lat") val lat: Float, // Широта
    @SerializedName("lon") val lon: Float // Долгота
)

// Текущая погода
data class CurrentWeather(
    @SerializedName("temp_c") val tempC: Double, // Температура в градусах Цельсия
    @SerializedName("feelslike_c") val feelsLikeC: Double, // Ощущается как (Цельсий)
    @SerializedName("wind_kph") val windKph: Double, // Скорость ветра в км/ч
    @SerializedName("wind_dir") val windDirection: String, // Направление ветра
    @SerializedName("pressure_mb") val pressureMb: Double, // Давление в миллибарах (можно преобразовать в рт. ст.)
    @SerializedName("humidity") val humidity: Int, // Влажность в процентах
    @SerializedName("condition") val condition: Condition // Описание погоды
)

// Условия погоды (ясно, облачно и т.д.)
data class Condition(
    @SerializedName("icon") val icon: String, // Иконка погоды
    @SerializedName("code") val code: Int
)

// Прогноз погоды на текущий день и следующие 3 дня
data class Forecast(
    @SerializedName("forecastday") val forecastDays: List<ForecastDay> // Список прогнозов на каждый день
)

// Прогноз на один день
data class ForecastDay(
    @SerializedName("date") val date: String, // Дата прогноза
    @SerializedName("day") val day: DayWeather, // Погода днем
    @SerializedName("night") val night: NightWeather, // Погода ночью
    @SerializedName("hour") val hourlyForecast: List<HourlyWeather>
)

// Погода днем
data class DayWeather(
    @SerializedName("maxtemp_c") val maxTempC: Double, // Максимальная температура в Цельсии
    @SerializedName("mintemp_c") val minTempC: Double, // Минимальная температура в Цельсии
    @SerializedName("condition") val condition: Condition, // Состояние погоды (днем)
)

// Погода ночью
data class NightWeather(
    @SerializedName("maxtemp_c") val maxTempC: Double, // Максимальная температура ночью
    @SerializedName("mintemp_c") val minTempC: Double, // Минимальная температура ночью
    @SerializedName("condition") val condition: Condition, // Состояние погоды (ночью)
)

data class HourlyWeather(
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val tempC: Double,
    @SerializedName("condition") val condition: Condition
)
