package com.example.restaurantreviewer.Database.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.restaurantreviewer.Model.Filter
import com.example.restaurantreviewer.Model.Restaurant

@Dao
interface RestaurantDao {

    @Query("SELECT * FROM Restaurant")
    fun getAllRestaurants(): LiveData<List<Restaurant>>

    @Query("SELECT * FROM Restaurant WHERE id=(:id)")
    fun getRestaurantById(id: Int?): LiveData<Restaurant>

    @Query("SELECT COUNT(id) FROM Restaurant")
    fun getNumberOfRestaurants(): LiveData<Int>

    @Query("""SELECT * FROM Restaurant 
        WHERE avgRating >= (:avg)
        ORDER BY
        CASE WHEN :asc = 1 THEN avgRating END ASC,
        CASE WHEN :asc = 0 THEN avgRating END DESC
    """)
    fun getFilteredRestaurantsByRating(asc: Boolean, avg: Double): LiveData<List<Restaurant>>

    @Query("""SELECT * FROM Restaurant 
        WHERE avgRating >= (:avg) 
        ORDER BY
        CASE WHEN :asc = 1 THEN name END ASC,
        CASE WHEN :asc = 0 THEN name END DESC
    """)
    fun getFilteredRestaurantsByName(asc: Boolean, avg: Double): LiveData<List<Restaurant>>

    @Query("""SELECT * FROM Restaurant 
        WHERE avgRating >= (:avg) 
        ORDER BY
        CASE WHEN :asc = 1 THEN id END ASC,
        CASE WHEN :asc = 0 THEN id END DESC
    """)
    fun getFilteredRestaurantsById(asc: Boolean, avg: Double): LiveData<List<Restaurant>>

    @Query("""SELECT * FROM Restaurant 
        ORDER BY
        CASE WHEN :asc = 1 THEN avgRating END ASC,
        CASE WHEN :asc = 0 THEN avgRating END DESC
    """)
    fun getFilteredRestaurantsByRatingIncludeNonRated(asc: Boolean): LiveData<List<Restaurant>>

    @Query("""SELECT * FROM Restaurant 
        ORDER BY
        CASE WHEN :asc = 1 THEN name END ASC,
        CASE WHEN :asc = 0 THEN name END DESC
    """)
    fun getFilteredRestaurantsByNameIncludeNonRated(asc: Boolean): LiveData<List<Restaurant>>

    @Query("""SELECT * FROM Restaurant 
        ORDER BY
        CASE WHEN :asc = 1 THEN id END ASC,
        CASE WHEN :asc = 0 THEN id END DESC
    """)
    fun getFilteredRestaurantsByIdIncludeNonRated(asc: Boolean): LiveData<List<Restaurant>>

    @Insert
    fun insertRestaurant(restaurant: Restaurant)

    @Update
    fun updateRestaurant(restaurant: Restaurant)

    @Query("DELETE FROM Restaurant")
    fun deleteAllRestaurants()
}