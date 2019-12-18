package com.arctouch.codechallenge.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.ui.adapter.MovieAdapter
import com.arctouch.codechallenge.ui.search.SearchActivity
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.android.ext.android.inject

class HomeActivity : AppCompatActivity() {

    // Injects
    lateinit var viewModel: HomeViewModel
    val movieAdapter: MovieAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        setContentView(R.layout.home_activity)

        recyclerView.adapter = movieAdapter

        // Search by all movies - Activity
        fab_search.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // Observe upcoming movies
        viewModel.getUpcomingMovies().observe(this, Observer {
            val moviesWithGenres = it.map { movie ->
                movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
            }
            movieAdapter.movies = moviesWithGenres
            movieAdapter.notifyDataSetChanged()
            progressBar.visibility = View.GONE
        })

        // Pagination on scroll
        recyclerView.addOnScrollListener(object: RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = (recyclerView.layoutManager as LinearLayoutManager)

                if (dy > 0) {

                    val visibleItemCount = layoutManager.childCount
                    val totalItemCount = layoutManager.itemCount
                    val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                    if (!viewModel.isLoading && !viewModel.isLastPage) {
                        if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                                && firstVisibleItemPosition >= 0) {
                            viewModel.nextPageUpcomingMovies()
                        }

                    }

                }

            }

        })

    }

}
