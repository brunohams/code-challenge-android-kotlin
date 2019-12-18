package com.arctouch.codechallenge.ui.search

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.ui.adapter.MovieAdapter
import com.arctouch.codechallenge.util.getInputMethodManager
import kotlinx.android.synthetic.main.search_activity.*
import org.koin.android.ext.android.inject
import timber.log.Timber

class SearchActivity : AppCompatActivity() {

    // Injects
    lateinit var viewModel: SearchViewModel
    val movieAdapter: MovieAdapter by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)
        setContentView(R.layout.search_activity)

        recyclerView.adapter = movieAdapter

        // Set focus on searchEditText and open keyboard
        if (viewModel.searchQuery.isEmpty()) {
            searchEditText.requestFocus()
            getInputMethodManager().toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

        // Search for the movies on keyboard IME action
        searchEditText.setOnEditorActionListener { v, actionId, event ->

            if(actionId == EditorInfo.IME_ACTION_SEARCH) {

                // Close the keyboard
                getInputMethodManager().hideSoftInputFromWindow(currentFocus?.windowToken, 0)

                // Search from the movies
                viewModel.searchMovies(searchEditText.text.toString())

                // Shows progress bar
                progressBar.visibility = View.VISIBLE

                // Hide search label
                searchTextView.visibility = View.GONE

                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener  false

        }

        // Observe upcoming movies
        viewModel.getMovies().observe(this, Observer {
            searchTextView.visibility = View.GONE
            val moviesWithGenres = it.map { movie ->
                movie.copy(genres = Cache.genres.filter { movie.genreIds?.contains(it.id) == true })
            }
            movieAdapter.movies = moviesWithGenres

            // No movies were found message
            if (moviesWithGenres.isEmpty()) {
                searchTextView.text = resources.getString(R.string.no_movies_were_found)
                searchTextView.visibility = View.VISIBLE
            }

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
                            viewModel.nextPageMovies()
                        }

                    }

                }

            }

        })

    }

}
