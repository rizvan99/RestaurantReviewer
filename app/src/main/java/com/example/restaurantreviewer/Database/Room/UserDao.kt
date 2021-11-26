package com.example.restaurantreviewer.Database.Room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.restaurantreviewer.Model.User

@Dao
interface UserDao {

    @Query("SELECT * FROM User WHERE id=(:id)")
    fun getUserById(id: Int?): LiveData<User>

    @Insert
    fun insertUser(user: User)

    @Query("DELETE FROM User")
    fun deleteAllUsers()
}