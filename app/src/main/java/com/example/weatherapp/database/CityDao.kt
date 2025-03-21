package com.example.weatherapp.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CityDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(city: FavoriteCity)

    @Delete
    suspend fun delete(city: FavoriteCity)

    @Query("Select * from favorite_cities")
    fun selectAllCities(): LiveData<List<FavoriteCity>>

    @Query("SELECT * FROM favorite_cities WHERE city_name = :cityName AND country = :country LIMIT 1")
    suspend fun getCity(cityName: String, country: String): FavoriteCity? // проверяем, есть ли город в базе
}