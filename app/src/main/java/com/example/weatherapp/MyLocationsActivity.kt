package com.example.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.favoritecityrecycler.FavoriteCityAdapter
import com.example.weatherapp.viewmodel.CityViewModel

class MyLocationsActivity : AppCompatActivity() {

    private lateinit var btnBack: ImageButton
    private lateinit var cityViewModel: CityViewModel
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: FavoriteCityAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var searchViewCity: androidx.appcompat.widget.SearchView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_my_locations)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        searchViewCity = findViewById(R.id.searchViewCity)
        val editText1 = searchViewCity.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchViewCity.isSubmitButtonEnabled = true
        val textColor = ContextCompat.getColor(this, R.color.black)
        val hintColor = ContextCompat.getColor(this, R.color.hint2)
        editText1.setTextColor(textColor)
        editText1.setHintTextColor(hintColor)
        cityViewModel = ViewModelProvider(this).get(CityViewModel::class.java)
        btnBack = findViewById(R.id.btnBack)
        sharedPreferences = getSharedPreferences("User_Prefs", MODE_PRIVATE)
        editor = sharedPreferences.edit()


        recyclerView = findViewById(R.id.favoriteCitiesRecycler)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.isNestedScrollingEnabled = false

        btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = FavoriteCityAdapter(
            listOf(),
            onItemClick = { location ->
                editor.putString("current_city", location.cityName).apply()
                startActivity(Intent(this, MainActivity::class.java))
            },
            onDeleteClick = { location ->
                cityViewModel.delete(location)
                Toast.makeText(this, "Удалено: ${location.cityName}", Toast.LENGTH_SHORT).show()
                updateAdapter()
            }
        )
        recyclerView.adapter = adapter
        updateAdapter()

        searchViewCity.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    editor.putFloat("latitude", 0.0F).apply()
                    editor.putFloat("longitude", 0.0F).apply()
                    editor.putString("current_city", query)
                    editor.apply()

                    val intent = Intent(this@MyLocationsActivity, MainActivity::class.java)
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

    private fun updateAdapter() {
        cityViewModel.allCities.observe(this, Observer { cities ->
            Log.d("MyLocationsActivity", "Cities updated: ${cities.size}")
            adapter.updateData(cities)
        })
    }

    override fun onResume() {
        super.onResume()
        updateAdapter()
    }


}