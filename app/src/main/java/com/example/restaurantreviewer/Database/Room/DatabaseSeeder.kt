package com.example.restaurantreviewer.Database.Room

import com.example.restaurantreviewer.Model.Restaurant
import com.example.restaurantreviewer.Model.Review
import com.example.restaurantreviewer.Model.User
import java.time.LocalDate
import java.util.*

class DatabaseSeeder {

    fun seed() {
        println("---starting to seed")
        val repo = RestaurantRepository.get()
        val restaurantList = arrayListOf<Restaurant>(
            Restaurant(id = null, name = "Sunset", address = "Torvet", latitude = 55.466032505214926, longitude = 8.452232584193803, openingHours = "Monday"),
            Restaurant(id = null, name = "McDonalds", address = "Torvet", latitude = 55.468098157014694, longitude = 8.45254039167325, openingHours = "All the time"),
            Restaurant(id = null, name = "den Niende", address = "Torvet", latitude = 55.46636373996595, longitude = 8.452380026522546, openingHours = "Never"),
            Restaurant(id = null, name = "Nara Sushi", address = "Byen", latitude = 55.46585147013918, longitude = 8.451391595837377, openingHours = "Wednesday"),
            Restaurant(id = null, name = "Jensens Bøfhus", address = "Ved Bilka", latitude = 55.50933840505413, longitude = 8.455268984195957, openingHours = "Tuesday"),
            Restaurant(id = null, name = "Burger King", address = "Broen", latitude = 55.46840415124429, longitude = 8.459849939246734, openingHours = "Every day 07-23")
        )
        val userList = arrayListOf<User>(
            User(id = null, name = "Benny"),
            User(id = null, name = "Hans"),
            User(id = null, name = "Anders"),
            User(id = null, name = "Per"),
        )
        val reviewList = arrayListOf<Review>(
            Review(id = null, userId = 1, restaurantId = 1, review = "Good service", rating = 5, picture = null, date = Date()),
            Review(id = null, userId = 2, restaurantId = 2, review = "Very bad service. The food was very bad, and I am very disappointed!", rating = 1, picture = "", date = Date()),
            Review(id = null, userId = 3, restaurantId = 3, review = "Okay", rating = 3, picture = null, date = Date()),
            Review(id = null, userId = 4, restaurantId = 4, review = "I didn't like the food", rating = 2, picture = null, date = Date()),
            Review(id = null, userId = 4, restaurantId = 5, review = "It tasted great", rating = 4, picture = null, date = Date()),
            Review(id = null, userId = 2, restaurantId = 2, review = "Bad service, bad place, bad company. Ruined my mood!!!!!!", rating = 1, picture = null, date = Date()),
            Review(id = null, userId = 3, restaurantId = 2, review = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", rating = 2, picture = null, date = Date())
        )
        restaurantList.forEach { restaurant -> repo.insertRestaurant(restaurant) }
        userList.forEach { user -> repo.insertUser(user) }
        reviewList.forEach { review -> repo.insertReview(review) }
        println("---finished seeding!")
    }

    fun clean() {
        val repo = RestaurantRepository.get()
        repo.deleteAllReviews()
        repo.deleteAllUsers()
        repo.deleteAllRestaurants()
    }

}