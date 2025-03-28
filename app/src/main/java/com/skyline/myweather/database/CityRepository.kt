package com.skyline.myweather.database

import androidx.lifecycle.LiveData

class CityRepository(private val cityDao: CityDao) {
    val allCities: LiveData<List<FavoriteCity>> = cityDao.selectAllCities()

    suspend fun insert(city: FavoriteCity) {
        val existingCity = cityDao.getCity(city.cityName, city.country)
        if (existingCity == null) cityDao.insert(city)
    }

    suspend fun delete(city: FavoriteCity) {
        cityDao.delete(city)
    }

    suspend fun getCity(cityName: String, country: String): FavoriteCity? {
        return cityDao.getCity(cityName, country)
    }
}