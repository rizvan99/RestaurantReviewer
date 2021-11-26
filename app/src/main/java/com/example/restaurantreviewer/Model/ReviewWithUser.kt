package com.example.restaurantreviewer.Model

import androidx.room.Embedded
import androidx.room.Relation

data class ReviewWithUser(
    @Embedded val reviewFromUser: Review,
    @Relation(
        parentColumn = "userId",
        entityColumn = "id"
    )
    val reviewer: User
)
