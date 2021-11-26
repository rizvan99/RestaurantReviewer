package com.example.restaurantreviewer.GUI

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurantreviewer.Database.Room.DatabaseSeeder
import com.example.restaurantreviewer.Database.Room.RestaurantRepository
import com.example.restaurantreviewer.Database.Room.observeOnce
import com.example.restaurantreviewer.GUI.RecycleAdapters.IItemClickListener
import com.example.restaurantreviewer.GUI.RecycleAdapters.RestaurantRecyclerAdapter
import com.example.restaurantreviewer.Model.Filter
import com.example.restaurantreviewer.Model.Restaurant
import com.example.restaurantreviewer.R
import kotlinx.android.synthetic.main.activity_main.*

private lateinit var restRepo: RestaurantRepository
private val FILTER_DATA = "filter"
private var filter: Filter = Filter("id", true, null)
private lateinit var viewedRestaurants: List<Restaurant>


class MainActivity : AppCompatActivity(), IItemClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val seeder = DatabaseSeeder()

        restRepo = RestaurantRepository.get()
        restRepo.getNumberOfRestaurants().observeOnce(
            this,
            Observer { count ->
                println("There are $count restaurants in database")
                if (count == 0) {
                    seeder.seed()
                }
            }
        )

        //region Turn-safety
        if (savedInstanceState != null) {
            filter = savedInstanceState.getSerializable(FILTER_DATA) as Filter
        } else {
            restRepo.getAllRestaurants().observeOnce(
                this,
                Observer { restaurants ->
                    restaurants.forEach { restaurant -> calculateAverageRating(restaurant) }
                }
            )
        }
        //endregion

        // rvMain = RecyclerView Main
        rvMain.layoutManager = LinearLayoutManager(this)

        // Adding the lines in between the rows
        rvMain.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        getFilteredRestaurants()
    }

    fun getFilteredRestaurants() {
        restRepo.getFilteredRestaurants(filter)?.observeOnce(
            this,
            Observer { restaurants ->
                restaurants?.let {
                    viewedRestaurants = restaurants
                    updateList(restaurants)
                }
            }
        )
    }

    fun updateList(restaurants: List<Restaurant>) {
        rvMain.adapter = RestaurantRecyclerAdapter(this, restaurants as ArrayList<Restaurant>, this)
    }

    fun calculateAverageRating(restaurant: Restaurant) {
        restRepo.getRestaurantAverageReview(restaurant).observeOnce(
            this,
            Observer { avg ->
                restaurant.avgRating = avg
                restRepo.updateRestaurant(restaurant)
            }
        )
    }

    //region Menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_map -> {
                openMap()
                true
            }
            R.id.filter_reset -> {
                filter = Filter("id", true, null)
                getFilteredRestaurants()
                true
            }
            R.id.filter_byname_asc -> {
                filter.asc = true
                filter.orderBy = "name"
                getFilteredRestaurants()
                true
            }
            R.id.filter_byname_desc -> {
                filter.asc = false
                filter.orderBy = "name"
                getFilteredRestaurants()
                true
            }
            R.id.filter_byrating_asc -> {
                filter.asc = true
                filter.orderBy = "avgRating"
                getFilteredRestaurants()
                true
            }
            R.id.filter_byrating_desc -> {
                filter.asc = false
                filter.orderBy = "avgRating"
                getFilteredRestaurants()
                true
            }
            R.id.filter_minRating_2 -> {
                filter.avg = 2.0
                getFilteredRestaurants()
                true
            }
            R.id.filter_minRating_3 -> {
                filter.avg = 3.0
                getFilteredRestaurants()
                true
            }
            R.id.filter_minRating_4 -> {
                filter.avg = 4.0
                getFilteredRestaurants()
                true
            }
        }
        return super.onOptionsItemSelected(item)
    }
    //endregion

    override fun onRestaurantClick(restaurant: Restaurant, position: Int) {
        val intent = Intent(this, RestaurantActivity::class.java)
        intent.putExtra(getString(R.string.RESTAURANT_DETAILS_INTENT), restaurant)
        intent.putExtra("FROM_ACTIVITY", "MAIN");
        startActivity(intent)
    }

    fun openMap() {
        intent = Intent(this, MapsActivity::class.java)
        intent.putExtra(getString(R.string.ALL_RESTAURANTS_INTENT), viewedRestaurants as ArrayList<Restaurant>)
        intent.putExtra("FROM_ACTIVITY", "MAIN");
        startActivity(intent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(FILTER_DATA, filter)
    }
}