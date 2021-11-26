package com.example.restaurantreviewer.Model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

@Entity
data class Review (
    @PrimaryKey (autoGenerate = true) var id: Int?,
    var userId: Int?,
    var restaurantId: Int?,
    var review: String = "",
    var rating: Int,
    var picture: String? = null,
    var date: Date? = null
    ): Serializable