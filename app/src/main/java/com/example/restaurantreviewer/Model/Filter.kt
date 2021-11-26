package com.example.restaurantreviewer.Model

import java.io.Serializable

class Filter(
    var orderBy: String,
    var asc: Boolean,
    var avg: Double?
) : Serializable