package com.skyline.myweather

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class OptionsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var switchLocation: SwitchCompat
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_options)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack = findViewById(R.id.btnBack)
        switchLocation = findViewById(R.id.locationSwitch)
        locationHelper = LocationHelper(this)

        switchLocation.thumbDrawable = ContextCompat.getDrawable(this, R.drawable.custom_thumb)
        switchLocation.trackDrawable = ContextCompat.getDrawable(this, R.drawable.switch_track)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        switchLocation.isChecked = locationHelper.checkLocationPermission()

        switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                if (!locationHelper.checkLocationPermission()) {
                    locationHelper.requestLocationPermission(this)
                } else {
                    getLocationAndSaveCity()
                }
            } else {
                openAppSettings()
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        switchLocation.isChecked = locationHelper.checkLocationPermission()
    }

    private fun getLocationAndSaveCity() {
        locationHelper.getCurrentLocation(
            onSuccess = { location ->
                val cityName = locationHelper.getCityFromLocation(this, location)
                if (cityName != null) {
                    val prefs = getSharedPreferences("User_Prefs", MODE_PRIVATE)
                    prefs.edit().putString("current_city", cityName).apply()

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            },
            onFailure = {
                switchLocation.isChecked = false // Если не удалось получить локацию
            }
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LocationHelper.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.any { it == PackageManager.PERMISSION_GRANTED }) {
                getLocationAndSaveCity()
            } else {
                switchLocation.isChecked = false
            }
        }
    }

}