package com.example.restaurantreviewer.GUI.RecycleAdapters

import com.example.restaurantreviewer.Model.Restaurant

interface IItemClickListener {
    fun onRestaurantClick(restaurant: Restaurant, position: Int)
}