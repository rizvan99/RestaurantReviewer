package com.example.restaurantreviewer.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity
data class Restaurant (
    @PrimaryKey (autoGenerate = true) var id: Int?,
    var name: String,
    var address: String,
    var latitude: Double,
    var longitude: Double,
    var openingHours: String,
    var avgRating: Double? = null,
        ): Serializable