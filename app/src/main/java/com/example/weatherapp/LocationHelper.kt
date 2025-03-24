package com.example.weatherapp

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationHelper(private val context: Context) {
    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    // Проверка разрешений на доступ к геолокации
    fun checkLocationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }

    // Запрос разрешений на доступ к геолокации
    fun requestLocationPermission(activity: AppCompatActivity, requestCode: Int) {
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ),
            requestCode
        )
    }

    // Получение текущего местоположения
    fun getCurrentLocation(onSuccess: (Location) -> Unit, onFailure: (Exception) -> Unit) {
        if (checkLocationPermission()) {
            val locationRequest = com.google.android.gms.location.LocationRequest.Builder(
                com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, 5000
            ).setMinUpdateIntervalMillis(2000)
                .build()

            val locationCallback = object : com.google.android.gms.location.LocationCallback() {
                override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
                    locationResult.lastLocation?.let { location ->
                        onSuccess(location)
                        fusedLocationClient.removeLocationUpdates(this) // Останавливаем обновления
                    }
                }
            }

            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
        } else {
            onFailure(Exception("Разрешение на доступ к геолокации не предоставлено"))
        }
    }

    fun getCityFromLocation(context: Context, location: Location): String? {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
        return addresses?.get(0)?.locality
    }
}