package com.example.restaurantreviewer.Database.Room

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Room
import com.example.restaurantreviewer.Model.*
import java.util.concurrent.Executors

private const val DATABASE_NAME = "restaurant-database"

class RestaurantRepository private constructor (context: Context){

    private val database: RestaurantDatabase = Room.databaseBuilder(
        context.applicationContext,
        RestaurantDatabase::class.java,
        DATABASE_NAME
    ).fallbackToDestructiveMigration().build()

    private val restaurantDao = database.restaurantDao()
    private val reviewDao = database.reviewDao()
    private val userDao = database.userDao()
    private val executor = Executors.newSingleThreadExecutor()

    fun getAllRestaurants(): LiveData<List<Restaurant>>
    = restaurantDao.getAllRestaurants()

    fun getRestaurantById(id: Int?): LiveData<Restaurant>
    = restaurantDao.getRestaurantById(id)

    fun getNumberOfRestaurants(): LiveData<Int> = restaurantDao.getNumberOfRestaurants()

    fun getFilteredRestaurants(filter: Filter): LiveData<List<Restaurant>>? {
            when (filter.orderBy) {
                "id" -> if (filter.avg != null)
                    return restaurantDao.getFilteredRestaurantsById(filter.asc, filter.avg!!) else
                        return restaurantDao.getFilteredRestaurantsByIdIncludeNonRated(filter.asc)
                "name" -> if (filter.avg != null)
                    return restaurantDao.getFilteredRestaurantsByName(filter.asc, filter.avg!!) else
                        return restaurantDao.getFilteredRestaurantsByNameIncludeNonRated(filter.asc)
                "avgRating" -> if (filter.avg != null)
                    return restaurantDao.getFilteredRestaurantsByRating(filter.asc, filter.avg!!) else
                        return restaurantDao.getFilteredRestaurantsByRatingIncludeNonRated(filter.asc)
                else -> return null
            }
    }

    fun insertRestaurant(restaurant: Restaurant)
    = executor.execute { restaurantDao.insertRestaurant(restaurant) }

    fun updateRestaurant(restaurant: Restaurant)
    = executor.execute { restaurantDao.updateRestaurant(restaurant) }

    fun getReviewById(id: Int?): LiveData<Review>
    = reviewDao.getReviewById(id)

    fun getAllRestaurantReviews(id: Int?): LiveData<List<ReviewWithUser>>
    = reviewDao.getAllRestaurantReviews(id)

    fun getRestaurantAverageReview(restaurant: Restaurant): LiveData<Double>
    = reviewDao.getRestaurantAverageRating(restaurant.id)

    fun insertReview(review: Review) = executor.execute { reviewDao.insertReview(review) }

    fun updateReview(review: Review) = executor.execute { reviewDao.updateReview(review) }

    fun deleteReview(review: Review) = executor.execute { reviewDao.deleteReview(review) }

    fun getUserById(id: Int?): LiveData<User> = userDao.getUserById(id)

    fun insertUser(user: User) = executor.execute { userDao.insertUser(user) }

    fun deleteAllRestaurants() = executor.execute { restaurantDao.deleteAllRestaurants() }

    fun deleteAllUsers() = executor.execute { userDao.deleteAllUsers() }

    fun deleteAllReviews() = executor.execute { reviewDao.deleteAllReviews() }

    companion object {
        private var INSTANCE: RestaurantRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = RestaurantRepository(context)
            }
        }

        fun get(): RestaurantRepository {
            return INSTANCE ?: throw IllegalStateException("Repository must be initialized")
        }
    }

}