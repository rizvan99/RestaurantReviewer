package com.example.restaurantreviewer.Database.Room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.restaurantreviewer.Model.Review
import com.example.restaurantreviewer.Model.ReviewWithUser

@Dao
interface ReviewDao {

    @Query("SELECT * FROM Review WHERE id=(:id)")
    fun getReviewById(id: Int?): LiveData<Review>

    @Query("SELECT * FROM Review WHERE restaurantId=(:id)")
    fun getAllRestaurantReviews(id: Int?): LiveData<List<ReviewWithUser>>

    @Query("SELECT AVG(rating) FROM Review WHERE restaurantId=(:restaurantId)")
    fun getRestaurantAverageRating(restaurantId: Int?): LiveData<Double>

    @Insert
    fun insertReview(review: Review)

    @Update
    fun updateReview(review: Review)

    @Delete
    fun deleteReview(review: Review)

    @Query("DELETE FROM Review")
    fun deleteAllReviews()
}