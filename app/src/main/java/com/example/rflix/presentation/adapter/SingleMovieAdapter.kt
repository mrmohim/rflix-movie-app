package com.example.rflix.presentation.adapter

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RatingBar
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.rflix.R
import com.example.rflix.data.response.Movie
import com.example.rflix.presentation.activities.DetailActivity
import com.example.rflix.utils.OnBottomReachedListener
import com.example.rflix.utils.SharedData


class SingleMovieAdapter(private val movieList: ArrayList<Movie>) :
    RecyclerView.Adapter<SingleMovieAdapter.MyViewHolder>() {
    lateinit var onBottomReachedListener: OnBottomReachedListener

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var cardItem: CardView = view.findViewById(R.id.cardItem)
        var movieImage: ImageView = view.findViewById(R.id.movieImage)
        var ratingBar: RatingBar = view.findViewById(R.id.ratingBar)
        var movieTitle: TextView = view.findViewById(R.id.movieTitle)
        var movieRelease: TextView = view.findViewById(R.id.movieRelease)
        var movieRating: TextView = view.findViewById(R.id.movieRating)
        var movieProgressBar: ProgressBar = view.findViewById(R.id.movieProgressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.upcoming_card_view_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (position == movieList.size - 1) {
            onBottomReachedListener.onBottomReached(position)
        }
        val movie: Movie = movieList[position]
        val imageUrl = "https://image.tmdb.org/t/p/w500" + movie.backdrop_path

        Glide.with(holder.movieImage.context)
            .load(imageUrl)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.movieProgressBar.visibility = View.GONE
                    return false
                }
            })
            .centerCrop()
            .into(holder.movieImage)

        holder.movieTitle.text = movie.title
        holder.ratingBar.rating = ((movie.vote_average / 10.0) * 5.0).toFloat()
        holder.movieRelease.text = "Release Date: " + (if(movie.release_date.isNotEmpty()) movie.release_date.substring(0, 4) else "Unknown")
        holder.movieRating.text = "Rating: ${movie.vote_average}"

        holder.cardItem.setOnClickListener {
            SharedData.clickedMovie = movie
            val intent = Intent(it.context, DetailActivity::class.java)
            it.context.startActivity(intent)
            (it.context as Activity).finish()
        }
    }

    fun setOnBottomReachedCustomListener(onBottomReachedListener: OnBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener
    }

    override fun getItemCount(): Int {
        return movieList.size
    }
}