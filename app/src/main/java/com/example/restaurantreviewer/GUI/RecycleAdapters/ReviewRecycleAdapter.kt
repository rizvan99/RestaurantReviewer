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
import com.example.restaurantreviewer.Database.Room.RestaurantRepository
import com.example.restaurantreviewer.Model.Review
import com.example.restaurantreviewer.Model.ReviewWithUser
import com.example.restaurantreviewer.R
import java.text.SimpleDateFormat

class ReviewRecycleAdapter : RecyclerView.Adapter<ReviewRecycleAdapter.ReviewViewHolder> {

    val reviewList: ArrayList<ReviewWithUser>

    lateinit var context: Context
    lateinit var restRepo: RestaurantRepository

    var itemClickListener: ((position: Int, review: ReviewWithUser) -> Unit)? = null

    constructor(reviews: ArrayList<ReviewWithUser>) {
        reviewList = reviews
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.review_cell, parent, false)

        restRepo = RestaurantRepository.get()

        context = parent.context

        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {

        val element = reviewList[position]

        holder.nameTxt.text = element.reviewer.name
        val formatter: SimpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        holder.dateTxt.text = formatter.format(element.reviewFromUser.date!!)

        addStars(holder.reviewStars, context, element.reviewFromUser)


        val text = element.reviewFromUser.review.toCharArray()
        if(text.size > 30)
        {
            val charsToShow = String(text.take(30).toCharArray()) + "..."
            holder.reviewTxt.text = charsToShow
        }
        else {
            holder.reviewTxt.text = element.reviewFromUser.review
        }


        if(element.reviewFromUser.picture != null)
        {
            holder.reviewImage.setImageResource(R.drawable.image)
        }

        val colours = intArrayOf(
            Color.parseColor("#DFDFDF"),
            Color.parseColor("#CCCCCC")
        )

        holder.itemView.setBackgroundColor(colours[position % colours.size])

        holder.itemView.setOnClickListener {
            itemClickListener?.invoke(position, element)
        }
    }

    fun addStars(reviewStars: LinearLayout, context: Context, review: Review) {
        reviewStars.removeAllViewsInLayout()
        for (i in 1..review.rating) {
            val imgView = ImageView(context)

            imgView.setBackgroundResource(R.drawable.small_full_star)

            reviewStars.addView(imgView)
        }

        val emptyStars = 5-review.rating

        for (i in 1..emptyStars) {
            val imgView = ImageView(context)

            imgView.setBackgroundResource(R.drawable.small_empty_star)

            reviewStars.addView(imgView)
        }
    }

    override fun getItemCount(): Int {
        return reviewList.size
    }

    class ReviewViewHolder : RecyclerView.ViewHolder {

        var view: View
        var nameTxt: TextView
        var dateTxt: TextView
        var reviewTxt: TextView
        var reviewStars: LinearLayout
        var reviewImage: ImageView

        constructor(v: View) : super(v) {
            view = v
            nameTxt = itemView.findViewById(R.id.tvName)
            dateTxt = itemView.findViewById(R.id.tvDate)
            reviewTxt = itemView.findViewById(R.id.tvReview)
            reviewStars = itemView.findViewById(R.id.llReviewStars)
            reviewImage = itemView.findViewById(R.id.ivPicture)
        }
    }
}