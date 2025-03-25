package com.example.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageButton
import android.widget.Toast
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
    private lateinit var sharedPreferences: SharedPreferences

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
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE)

        switchLocation.thumbDrawable = ContextCompat.getDrawable(this, R.drawable.custom_thumb)
        switchLocation.trackDrawable = ContextCompat.getDrawable(this, R.drawable.switch_track)

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        switchLocation.isChecked = locationHelper.checkLocationPermission()

        switchLocation.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                // Открываем настройки, чтобы пользователь включил разрешение на геолокацию
                openAppSettings()
            } else {
                // Если пользователь выключает Switch, удаляем сохраненные координаты
                getSharedPreferences("User_Prefs", MODE_PRIVATE).edit()
                    .remove("latitude")
                    .remove("longitude")
                    .apply()
                openAppSettings() // Также открываем настройки для отклонения разрешения
            }
        }
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }
}