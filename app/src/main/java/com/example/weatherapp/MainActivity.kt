package com.example.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.api.Forecast
import com.example.weatherapp.api.HourlyWeather
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.api.WeatherResponse
import com.example.weatherapp.database.FavoriteCity
import com.example.weatherapp.favoritecityrecycler.FavoriteCityAdapter
import com.example.weatherapp.viewmodel.CityViewModel
import com.example.weatherapp.weathernextdays.NextDaysAdapter
import com.example.weatherapp.weathernextdays.NextDaysWeather
import com.example.weatherapp.weatherperhoursrecycler.WeatherAdapter
import com.example.weatherapp.weatherperhoursrecycler.WeatherItem
import com.google.android.material.navigation.NavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var toolbar: androidx.appcompat.widget.Toolbar
    private lateinit var imageButton: ImageButton
    private lateinit var currentDegreesTW: TextView
    private lateinit var currentConditionIW: ImageView
    private lateinit var feelsLikeTW: TextView
    private lateinit var pressureTW: TextView
    private lateinit var humidityTW: TextView
    private lateinit var windTW: TextView
    private lateinit var cityName: TextView
    private lateinit var recyclerWeatherHours: RecyclerView
    private lateinit var weatherAdapter: WeatherAdapter
    private lateinit var recyclerDailyWeather: RecyclerView
    private lateinit var btnFavorite: ImageButton
    private var isFavorite = false
    private lateinit var cityViewModel: CityViewModel
    private var currentCityName: String = ""
    private var currentCountry: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        imageButton = findViewById(R.id.custom_button)
        toolbar = findViewById(R.id.toolbar)
        currentDegreesTW = findViewById(R.id.currentDegrees)
        currentConditionIW = findViewById(R.id.currentConditionIW)
        feelsLikeTW = findViewById(R.id.feelsLikeTW)
        pressureTW = findViewById(R.id.pressureTW)
        humidityTW = findViewById(R.id.humidityTW)
        windTW = findViewById(R.id.windTW)
        cityName = findViewById(R.id.cityNameTW)
        recyclerWeatherHours = findViewById(R.id.recyclerWeatherHours)
        recyclerDailyWeather = findViewById(R.id.recyclerDailyWeather)
        btnFavorite = findViewById(R.id.btnFavorite)
        cityViewModel = ViewModelProvider(this).get(CityViewModel::class.java)


        recyclerWeatherHours.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerDailyWeather.layoutManager = LinearLayoutManager(this)

        btnFavorite.setOnClickListener {
            toggleFavoriteCity()
        }

        setSupportActionBar(toolbar)
        supportActionBar?.title = ""

        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
            0, 0)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        toggle.isDrawerIndicatorEnabled = false
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_locations -> {
                    val intent = Intent(this, MyLocationsActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_properties -> {
                    val intent = Intent(this, OptionsActivity::class.java)
                    startActivity(intent)
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        imageButton.setOnClickListener {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE)
        val savedCity = sharedPreferences.getString("current_city", "Москва")

        val latitude = sharedPreferences.getFloat("latitude", 0.0F)
        val longitude = sharedPreferences.getFloat("latitude", 0.0F)

        if (latitude != 0.0F && longitude != 0.0F) {
            // Используем координаты для запроса
            loadWeather(latitude = latitude, longitude = longitude)
        } else {
            // Используем сохраненный город или город по умолчанию
            loadWeather(savedCity)
        }
    }

    private fun toggleFavoriteCity() {
        if (currentCityName.isNotEmpty() && currentCountry.isNotEmpty()) {
            val favoriteCity = FavoriteCity(cityName = currentCityName, country = currentCountry)

            CoroutineScope(Dispatchers.IO).launch {
                val existingCity = cityViewModel.getCity(currentCityName, currentCountry)

                withContext(Dispatchers.Main) {
                    if (existingCity == null) {
                        cityViewModel.insert(favoriteCity)
                        isFavorite = true
                        Toast.makeText(this@MainActivity, "Город добавлен в избранное", Toast.LENGTH_SHORT).show()
                    } else {
                        cityViewModel.delete(existingCity)
                        isFavorite = false
                        Toast.makeText(this@MainActivity, "Город удален из избранного", Toast.LENGTH_SHORT).show()
                    }

                    updateFavoriteButton(btnFavorite, isFavorite) // Обновление иконки
                }
            }
        } else {
            Toast.makeText(this, "Ошибка: данные о городе не найдены", Toast.LENGTH_SHORT).show()
        }
    }


    private fun loadWeather(city: String? = null, latitude: Float? = null, longitude: Float? = null) {
        val apiKey = getString(R.string.weather_api_key)
        val locationQuery = if (latitude != null && longitude != null) {
            "$latitude,$longitude" // Формат "широта,долгота"
        } else {
            city ?: "Москва"
        }
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.api.getForecast(apiKey, locationQuery, days = 3)

                withContext(Dispatchers.Main) {
                    updateUI(response)
                    checkIfCityIsFavorite(response.location.name, response.location.country)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkIfCityIsFavorite(cityName: String, country: String) {
        CoroutineScope(Dispatchers.IO).launch {
            // Используем метод getCity из CityDao
            val city = cityViewModel.getCity(cityName, country)
            withContext(Dispatchers.Main) {
                // Обновляем состояние isFavorite
                isFavorite = city != null
                // Обновляем иконку кнопки
                updateFavoriteButton(btnFavorite, isFavorite)
            }
        }
    }


    private fun updateUI(weatherResponse: WeatherResponse) {
        val currentWeather = weatherResponse.current
        val condition = currentWeather.condition
        val location = weatherResponse.location
        currentCityName = location.name
        currentCountry = location.country
        val forecastDays = weatherResponse.forecast.forecastDays.take(3)
        currentDegreesTW.text = "${currentWeather.tempC.toInt()}°C"
        feelsLikeTW.text = "${currentWeather.feelsLikeC.toInt()}°C"
        windTW.text = "${getWindDirectionInRussian(currentWeather.windDirection)}, " +
                "${convertWindSpeedToMps(currentWeather.windKph).toInt()} м/с"
        pressureTW.text = "${convertPressureToMmHg(currentWeather.pressureMb).toInt()} мм рт. ст."
        humidityTW.text = "${currentWeather.humidity} %"
        cityName.text = location.name
        determineTheWeather(condition.code)

        val dailyWeatherList = forecastDays.mapIndexed { index, forecastDay ->
            val date = forecastDay.date // Дата (например, "2023-10-11")
            val dayOfWeek = if (index == 0) "Сегодня" else getDayOfWeek(date) // День недели
            val dayTemp = forecastDay.day.maxTempC.toInt() // Дневная температура
            val nightTemp = forecastDay.day.minTempC.toInt() // Ночная температура
            val icon = getWeatherIcon(forecastDay.day.condition.code) // Иконка для дня

            NextDaysWeather(
                date = formatDate(date),
                dayOfWeek = dayOfWeek,
                icon = icon,
                dayTemp = dayTemp,
                nightTemp = nightTemp
            )
        }

        val filteredForecast = getNextHoursForecast(weatherResponse.forecast)

        val weatherItems = filteredForecast.map {
            WeatherItem(
                time = it.time.substringAfterLast(" "),
                temperature = it.tempC.toInt(),
                icon = getWeatherIcon(it.condition.code)
            )
        }

        weatherAdapter = WeatherAdapter(weatherItems)
        recyclerWeatherHours.adapter = weatherAdapter
        weatherAdapter.notifyDataSetChanged()

        val adapter = NextDaysAdapter(dailyWeatherList)
        recyclerDailyWeather.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun formatDate(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val outputFormat = SimpleDateFormat("d MMMM", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        return outputFormat.format(date)
    }

    private fun getDayOfWeek(dateString: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = inputFormat.parse(dateString)
        val calendar = Calendar.getInstance()
        calendar.time = date
        return when (calendar.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "Понедельник"
            Calendar.TUESDAY -> "Вторник"
            Calendar.WEDNESDAY -> "Среда"
            Calendar.THURSDAY -> "Четверг"
            Calendar.FRIDAY -> "Пятница"
            Calendar.SATURDAY -> "Суббота"
            Calendar.SUNDAY -> "Воскресенье"
            else -> ""
        }
    }

    fun getWindDirectionInRussian(direction: String): String {
        return when (direction) {
            "N" -> "С"
            "NE", "NNE", "ENE"-> "СВ"
            "E" -> "В"
            "SE", "ESE", "SSE" -> "ЮВ"
            "S" -> "Ю"
            "SW", "SSW", "WSW" -> "ЮЗ"
            "W" -> "З"
            "NW", "WNW", "NNW" -> "СЗ"
            else -> "Направление неизвестно"
        }
    }

    fun convertWindSpeedToMps(windKph: Double): Double {
        return windKph * 0.27778
    }

    fun convertPressureToMmHg(pressureMb: Double): Double {
        return pressureMb * 0.750062
    }

    fun determineTheWeather(code: Int) {
        when (code) {
            1000 -> currentConditionIW.setImageResource(R.drawable.sun)
            1003 -> currentConditionIW.setImageResource(R.drawable.partly_cloudlly)
            1006, 1009 -> currentConditionIW.setImageResource(R.drawable.clouds)
            1030, 1135, 1147 -> currentConditionIW.setImageResource(R.drawable.fog)
            1063, 1072, 1150, 1153, 1168, 1171, 1180, 1186, 1189, 1192, 1195,
            1198, 1201, 1240, 1243, 1246 ->  currentConditionIW.setImageResource(R.drawable.rain)
            1066, 1069, 1114, 1117, in 1204..1237, in 1249..1264 -> currentConditionIW.setImageResource(R.drawable.snow)
            1087, in 1273..1282 -> currentConditionIW.setImageResource(R.drawable.thunder)
        }
    }
    private fun getWeatherIcon(code: Int): Int {
        return when (code) {
            1000 -> R.drawable.sun
            1003 -> R.drawable.partly_cloudlly
            1006, 1009 -> R.drawable.clouds
            1030, 1135, 1147 -> R.drawable.fog
            1063, 1072, 1150, 1153, 1168, 1171, 1180, 1186, 1189, 1192, 1195,
            1198, 1201, 1240, 1243, 1246 -> R.drawable.rain
            1066, 1069, 1114, 1117, in 1204..1237, in 1249..1264 -> R.drawable.snow
            1087, in 1273..1282 -> R.drawable.thunder
            else -> R.drawable.sun
        }
    }

    fun getNextHoursForecast(forecast: Forecast): List<HourlyWeather> {
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val todayForecast = forecast.forecastDays.firstOrNull()?.hourlyForecast ?: return emptyList()
        val tomorrowForecast = forecast.forecastDays.getOrNull(1)?.hourlyForecast ?: emptyList()

        // Фильтруем данные для текущего дня, начиная со следующего часа
        val filteredTodayForecast = todayForecast.filter {
            val hour = it.time.substringAfterLast(" ").substringBefore(":").toInt()
            hour > currentHour
        }

        // Добавляем данные на следующий день
        val combinedForecast = filteredTodayForecast + tomorrowForecast

        Log.d("WeatherData", "Combined Forecast: $combinedForecast")
        return combinedForecast
    }

    private fun updateFavoriteButton(button: ImageButton, isFavorite: Boolean) {
        if (isFavorite) {
            button.setImageResource(R.drawable.ic_favorite_filled)
        }
        else {
            button.setImageResource(R.drawable.ic_favorite_bordered)
        }
    }


    override fun onResume() {
        super.onResume()
        checkIfCityIsFavorite(currentCityName, currentCountry)
    }
}