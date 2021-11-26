package com.example.restaurantreviewer.GUI

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreviewer.Database.Room.RestaurantRepository
import com.example.restaurantreviewer.Database.Room.observeOnce
import com.example.restaurantreviewer.GUI.RecycleAdapters.ReviewRecycleAdapter
import com.example.restaurantreviewer.Model.*
import com.example.restaurantreviewer.R
import kotlinx.android.synthetic.main.activity_restaurant.*
import java.text.SimpleDateFormat
import kotlin.math.roundToInt


class RestaurantActivity: AppCompatActivity() {

    lateinit var restRepo: RestaurantRepository
    lateinit var restaurantAdapter: ReviewRecycleAdapter
    lateinit var alertDialog: AlertDialog

    var popupReview: ReviewWithUser? = null
    var chosenRestaurant = Restaurant(0, "", "", 0.0, 0.0, "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restaurant)

        restRepo = RestaurantRepository.get()

        if (intent.extras != null) {
            val extras: Bundle = intent.extras!!
            val restaurant = extras.getSerializable(getString(R.string.RESTAURANT_DETAILS_INTENT)) as Restaurant

            chosenRestaurant = restaurant

            tvName.text = chosenRestaurant.name

            addStars()

            tvAddress.text = chosenRestaurant.address
            tvOpeningHours.text = chosenRestaurant.openingHours
            restRepo.getAllRestaurantReviews(chosenRestaurant.id).observe(
                this,
                Observer { reviews ->
                    handleRecycler(reviews as ArrayList<ReviewWithUser>)
                }
            )
        }

    }

    fun handleRecycler(reviews: ArrayList<ReviewWithUser>)
    {
        // Find the RecyclerView and make a reference to it
        val recycler = findViewById<RecyclerView>(R.id.recyclerView)

        // Setting the recyclers layoutmanager to be a LinearLayoutManager
        recycler.layoutManager = LinearLayoutManager(this)

        // Adding the lines in between the rows
        recycler.addItemDecoration(
            DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
            )
        )

        // Sets the items in the recycler to have a fixed size
        recycler.setHasFixedSize(true)

        // Gets the list of friends from the repository
        // and converts it to a list the recycler can use.

        restaurantAdapter = ReviewRecycleAdapter(reviews)
        recycler.adapter = restaurantAdapter

        restaurantAdapter.itemClickListener = { position, review ->

            popupReview = review

            val inflater = layoutInflater
            val dialoglayout: View = inflater.inflate(R.layout.pop_up, null)

            val name = dialoglayout.findViewById(R.id.tvPopUpName) as TextView
            name.text = review.reviewer.name

            val date = dialoglayout.findViewById(R.id.tvPopUpDate) as TextView
            // val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
            date.text = formatter.format(review.reviewFromUser.date!!)

            addPopUpStars(review.reviewFromUser, dialoglayout)

            val reviewText = dialoglayout.findViewById(R.id.tvPopUpReview) as TextView
            reviewText.text = review.reviewFromUser.review

            if(review.reviewFromUser.picture != null)
            {
                val picture = dialoglayout.findViewById(R.id.ivPopUpPicture) as ImageView
                val lp = LinearLayout.LayoutParams(300, 300)
                lp.gravity = Gravity.CENTER
                picture.layoutParams = lp
                picture.scaleType = ImageView.ScaleType.CENTER_INSIDE
                picture.setImageURI(Uri.parse(review.reviewFromUser.picture))
            }

            val builder = AlertDialog.Builder(this)

            builder.setView(dialoglayout)

            alertDialog = builder.create()

            alertDialog.show()
        }
    }

    fun addStars() {
        llStars.removeAllViewsInLayout()
        restRepo.getRestaurantAverageReview(chosenRestaurant).observeOnce(
            this,
            Observer { avg ->
                chosenRestaurant.avgRating = avg
            }
        )
        if (chosenRestaurant.avgRating == null) {
            val tvNoStars: TextView = TextView(this)
            llStars.addView(tvNoStars)
            tvNoStars.text = "No rating yet"
        } else {
            val stars = chosenRestaurant.avgRating!!.roundToInt()

            for (i in 1..stars) {
                val imgView = ImageView(this)

                imgView.setBackgroundResource(R.drawable.full_star)

                llStars.addView(imgView)
            }

            val emptyStars = 5-stars

            for (i in 1..emptyStars) {
                val imgView = ImageView(this)

                imgView.setBackgroundResource(R.drawable.empty_star)

                llStars.addView(imgView)
            }
        }

    }

    fun addPopUpStars(review: Review, layout: View) {
        for (i in 1..review.rating) {
            val imgView = ImageView(this)

            imgView.setBackgroundResource(R.drawable.small_full_star)

            val stars = layout.findViewById(R.id.llPopUpReviewStars) as LinearLayout?
            stars!!.addView(imgView)
        }

        val emptyStars = 5-review.rating

        for (i in 1..emptyStars) {
            val imgView = ImageView(this)

            imgView.setBackgroundResource(R.drawable.small_empty_star)

            val stars = layout.findViewById(R.id.llPopUpReviewStars) as LinearLayout?
            stars!!.addView(imgView)
        }
    }

    fun onClickBack(view: View) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }

    fun onClickLocation(view: View) {
        // Log.e("xyz", "Chosen restaurant: " + chosenRestaurant.name)
        val intent = Intent(this, MapsActivity::class.java)
        val list: ArrayList<Restaurant> = ArrayList();
        list.add(chosenRestaurant);
        intent.putExtra(getString(R.string.ALL_RESTAURANTS_INTENT), list)
        intent.putExtra("FROM_ACTIVITY", "RESTAURANT");
        startActivityForResult(intent, getString(R.string.MAP_SINGLE_CODE).toInt())
    }

    fun onClickAddReview(view: View) {
        val intent = Intent(this, EditCreateActivity::class.java)
        restRepo.getUserById(1).observeOnce(
            this,
            Observer { user ->
                val review = Review(null, userId = user.id, restaurantId = chosenRestaurant.id, rating = 0)
                intent.putExtra(getString(R.string.REVIEW_INTENT), review)
                startActivityForResult(intent, getString(R.string.SAVE_REVIEW_REQUEST_CODE).toInt())
            }
        )
    }

    fun onClickEditReview(view: View) {
        val intent = Intent(this, EditCreateActivity::class.java)
        intent.putExtra(getString(R.string.REVIEW_INTENT), popupReview!!.reviewFromUser)
        startActivityForResult(intent, getString(R.string.SAVE_REVIEW_REQUEST_CODE).toInt())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == getString(R.string.SAVE_REVIEW_REQUEST_CODE).toInt()) {
            when (resultCode) {
                getString(R.string.RESULT_NOSAVE).toInt() -> {
                    Toast.makeText(this, getString(R.string.REVIEW_NO_CHANGES), Toast.LENGTH_SHORT).show()
                }
                getString(R.string.RESULT_CREATED).toInt() -> {
                    var review: Review = data?.extras?.getSerializable(getString(R.string.REVIEW_INTENT)) as Review
                    restRepo.insertReview(review)
                    addStars()
                    Toast.makeText(this, getString(R.string.REVIEW_CREATED), Toast.LENGTH_SHORT).show()
                }
                getString(R.string.RESULT_UPDATED).toInt() -> {
                    var review: Review = data?.extras?.getSerializable(getString(R.string.REVIEW_INTENT)) as Review
                    restRepo.updateReview(review)
                    alertDialog.dismiss()
                    addStars()
                    Toast.makeText(this, getString(R.string.REVIEW_UPDATED), Toast.LENGTH_SHORT).show()
                }
                getString(R.string.RESULT_FAILED).toInt() -> {
                    Toast.makeText(this, getString(R.string.REVIEW_FAILURE), Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Toast.makeText(this, getString(R.string.UNKNOWN_RESULT_CODE_RETURNED), Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun onClickDeleteReview(view: View) {
        restRepo.deleteReview(popupReview!!.reviewFromUser)
        alertDialog.dismiss()
        addStars()
    }


}