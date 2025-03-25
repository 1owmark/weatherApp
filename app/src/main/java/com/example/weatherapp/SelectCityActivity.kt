package com.example.weatherapp

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.view.View
import android.Manifest
import android.location.Location
import android.widget.SearchView
import android.widget.Toast
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherapp.api.RetrofitInstance
import com.example.weatherapp.viewmodel.CityViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SelectCityActivity : AppCompatActivity() {

    private lateinit var searchViewCity: androidx.appcompat.widget.SearchView
    private val LOCATION_PERMISSION_REQUEST_CODE = 1
    private lateinit var cityViewModel: CityViewModel
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_select_city)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cityViewModel = ViewModelProvider(this).get(CityViewModel::class.java)
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        searchViewCity = findViewById(R.id.searchViewCity)
        val editText1 = searchViewCity.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewCity.isSubmitButtonEnabled = true
        val textColor = ContextCompat.getColor(this, R.color.white)
        val hintColor = ContextCompat.getColor(this, R.color.hintColor)
        editText1.setTextColor(textColor)
        editText1.setHintTextColor(hintColor)
        locationHelper = LocationHelper(this)

        cityViewModel.allCities.observe(this, Observer { cities ->
            if (cities.isNotEmpty()) {
                val editor = sharedPreferences.edit()
                val lastCity = cities.last()

                editor.putString("current_city", lastCity.cityName)
                editor.apply()

                val intent = Intent(this@SelectCityActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })

        searchViewCity.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    // Сохраняем город в SharedPreferences
                    editor.putString("current_city", query)
                    editor.apply()

                    // Запускаем MainActivity
                    val intent = Intent(this@SelectCityActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish() // Закрываем SelectCityActivity
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })

        if (!locationHelper.checkLocationPermission()) {
            locationHelper.requestLocationPermission(this, LOCATION_PERMISSION_REQUEST_CODE)
        } else {
            saveLocation()
        }

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED || (permissions.size > 1 && grantResults[1] == PackageManager.PERMISSION_GRANTED))) {
                saveLocation() // Если хотя бы одно разрешение получено, получаем координаты
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this, "Разрешение на геолокацию не предоставлено", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun saveLocation() {
        locationHelper.getCurrentLocation(
            onSuccess = { location ->
                val cityName = locationHelper.getCityFromLocation(this, location)
                if (cityName != null) {
                    sharedPreferences.edit().putString("current_city", cityName).apply()
                } else {
                    Toast.makeText(this, "Не удалось определить город", Toast.LENGTH_SHORT).show()
                }
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            },
            onFailure = {
                Toast.makeText(this, "Не удалось получить местоположение", Toast.LENGTH_SHORT).show()
            }
        )
    }
}