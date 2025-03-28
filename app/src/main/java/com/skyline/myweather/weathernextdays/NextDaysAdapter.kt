package com.skyline.myweather.weathernextdays

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.skyline.myweather.R

class NextDaysAdapter(private val items: List<NextDaysWeather>) :
    RecyclerView.Adapter<NextDaysAdapter.DailyWeatherViewHolder>() {

    class DailyWeatherViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val date: TextView = view.findViewById(R.id.tvDate)
        val dayOfWeek: TextView = view.findViewById(R.id.tvDayOfWeek)
        val icon: ImageView = view.findViewById(R.id.imgIcon)
        val dayTemp: TextView = view.findViewById(R.id.tvDayTemp)
        val nightTemp: TextView = view.findViewById(R.id.tvNightTemp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DailyWeatherViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_next_days_weather, parent, false)
        return DailyWeatherViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: DailyWeatherViewHolder, position: Int) {
        val item = items[position]
        holder.date.text = item.date
        holder.dayOfWeek.text = item.dayOfWeek
        holder.icon.setImageResource(item.icon)
        holder.dayTemp.text = "${item.dayTemp}°C"
        holder.nightTemp.text = "${item.nightTemp}°C"
    }
}
