package com.example.weatherapp.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.database.AppDatabase
import com.example.weatherapp.database.CityRepository
import com.example.weatherapp.database.FavoriteCity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CityRepository
    val allCities: LiveData<List<FavoriteCity>>

    init {
        val cityDao = AppDatabase.getDatabase(application).cityDao()
        repository = CityRepository(cityDao)
        allCities = repository.allCities
    }

    fun insert(city: FavoriteCity) = viewModelScope.launch {
        repository.insert(city)
    }

    fun delete(city: FavoriteCity) = viewModelScope.launch {
        repository.delete(city)
        Log.d("CityViewModel", "City deleted: ${city.cityName}")
    }

    suspend fun getCity(cityName: String, country: String): FavoriteCity? {
        return withContext(Dispatchers.IO) {
            repository.getCity(cityName, country)
        }
    }
}
