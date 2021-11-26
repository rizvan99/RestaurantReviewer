package com.example.restaurantreviewer.GUI.RecycleAdapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantreviewer.Model.Restaurant
import com.example.restaurantreviewer.R
import kotlin.math.roundToInt

class RestaurantRecyclerAdapter : RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantHolder> {

    private val restaurants: ArrayList<Restaurant>
    private val inflater: LayoutInflater
    private val clickListener: IItemClickListener

    lateinit var context: Context

    constructor(context: Context, restaurants: ArrayList<Restaurant>, clickListener: IItemClickListener) {
        this.inflater = LayoutInflater.from(context)
        this.restaurants = restaurants
        this.clickListener = clickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantHolder {
        val view: View = inflater.inflate(R.layout.cell_main, parent, false)

        context = parent.context

        return RestaurantHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantHolder, position: Int) {
        val restaurant = restaurants[position]

        holder.tvName.text = restaurant.name
        holder.tvAddress.text = restaurant.address

        addStars(holder.llstars, context, restaurant)

        val colours = intArrayOf(
            Color.parseColor("#DFDFDF"),
            Color.parseColor("#CCCCCC")
        )

        holder.view.setBackgroundColor(colours[position % colours.size])

        holder.view.setOnClickListener { _ -> clickListener.onRestaurantClick(restaurant, position) }
    }

    fun addStars(reviewStars: LinearLayout, context: Context, restaurant: Restaurant) {
        reviewStars.removeAllViewsInLayout()
        if (restaurant.avgRating == null) {
            val tvNoStars: TextView = TextView(context)
            reviewStars.addView(tvNoStars)
            tvNoStars.text = "No rating yet"
        } else {
            val roundedRating = restaurant.avgRating!!.roundToInt()

            for (i in 1..roundedRating) {
                val imgView = ImageView(context)

                imgView.setBackgroundResource(R.drawable.small_full_star)

                reviewStars.addView(imgView)
            }

            val emptyStars = 5- roundedRating

            for (i in 1..emptyStars) {
                val imgView = ImageView(context)

                imgView.setBackgroundResource(R.drawable.small_empty_star)

                reviewStars.addView(imgView)
            }
        }

    }

    override fun getItemCount(): Int {
        return restaurants.size
    }

    class RestaurantHolder : RecyclerView.ViewHolder {

        var view: View
        var tvName: TextView
        var tvAddress: TextView
        var tvAvgRating: TextView
        var llstars: LinearLayout

        constructor(v: View) : super(v) {
            view = v
            tvName = v.findViewById(R.id.tvNameMain)
            tvAddress = v.findViewById(R.id.tvAddressMain)
            tvAvgRating = v.findViewById(R.id.tvAvgRatingMain)
            llstars = v.findViewById(R.id.llAvgRatingMain)
        }

    }

}