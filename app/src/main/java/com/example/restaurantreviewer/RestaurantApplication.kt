package com.example.restaurantreviewer

import android.app.Application
import com.example.restaurantreviewer.Database.Room.RestaurantRepository

class RestaurantApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        RestaurantRepository.initialize(this)
        println("---Application has started up!!!")
    }

}