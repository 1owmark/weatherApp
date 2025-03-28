package com.skyline.myweather

import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skyline.myweather.viewmodel.CityViewModel


class SelectCityActivity : AppCompatActivity() {

    private lateinit var searchViewCity: androidx.appcompat.widget.SearchView
    private lateinit var cityViewModel: CityViewModel

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

        // Настройка внешнего вида поиска
        searchViewCity = findViewById(R.id.searchViewCity)
        val editText1 = searchViewCity.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewCity.isSubmitButtonEnabled = true
        val textColor = ContextCompat.getColor(this, R.color.white)
        val hintColor = ContextCompat.getColor(this, R.color.hintColor)
        editText1.setTextColor(textColor)
        editText1.setHintTextColor(hintColor)

        searchViewCity.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    val sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE)
                    sharedPreferences.edit().putString("current_city", query).apply()

                    // Запускаем MainActivity
                    val intent = Intent(this@SelectCityActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
}