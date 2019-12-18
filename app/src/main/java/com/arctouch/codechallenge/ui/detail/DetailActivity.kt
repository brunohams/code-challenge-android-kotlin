package com.arctouch.codechallenge.ui.detail

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.model.Movie
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.detail_activity.*

class DetailActivity : AppCompatActivity() {

    companion object {
        val MOVIE_EXTRA = "MOVIE_EXTRA"
    }

    lateinit var movie: Movie

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.detail_activity)

        // Tries to recovery de movie data from the extra
        // If for any reason the data came null, returns to previous activity with a Toast message
        intent.getParcelableExtra<Movie>(MOVIE_EXTRA)?.let {
            movie = it
        } ?: run {
            Toast.makeText(this, getString(R.string.an_unexpected_error_occurred), Toast.LENGTH_SHORT).show()
            finish()
        }

        // Load Background image
        if (movie.backdropPath != null) {
            Glide.with(this)
                    .load(movie.backdropUrl)
                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(backDropImageView)
        } else {
            backDropImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        }

        // Load poster image
        if (movie.posterPath != null) {
            Glide.with(this)
                    .load(movie.posterUrl)
                    .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                    .into(posterImageView)
        } else {
            posterImageView.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
        }

        // Set the data
        titleTextView.text = movie.title
        overViewTextView.text = movie.overview
        genresTextView.text = movie.genres?.joinToString(separator = ", ") { it.name }
        releaseDateTextView.text = movie.releaseDate

    }

}
