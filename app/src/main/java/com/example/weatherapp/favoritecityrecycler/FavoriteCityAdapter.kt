package com.example.weatherapp.favoritecityrecycler

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherapp.R
import com.example.weatherapp.database.FavoriteCity

class FavoriteCityAdapter(
    private var locations: List<FavoriteCity>,
    private val onItemClick: (FavoriteCity) -> Unit,
    private val onDeleteClick: (FavoriteCity) -> Unit) : RecyclerView.Adapter<FavoriteCityAdapter.LocationViewHolder>() {

    class LocationViewHolder(itemView: View,
        private val onItemClick: (FavoriteCity) -> Unit,
        private val onDeleteClick: (FavoriteCity) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val cityName: TextView = itemView.findViewById(R.id.cityName)
        private val deleteElement: ImageView = itemView.findViewById(R.id.deleteElement)

        fun bind(location: FavoriteCity) {
            cityName.text = location.cityName

            itemView.setOnClickListener { onItemClick(location) }

            deleteElement.setOnClickListener { onDeleteClick(location) }
        }
    }

    fun updateData(newList: List<FavoriteCity>) {
        this.locations = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_city_item, parent, false)
        return LocationViewHolder(view, onItemClick, onDeleteClick) // Передаем функции
    }

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        holder.bind(locations[position])
    }

    override fun getItemCount(): Int = locations.size
}