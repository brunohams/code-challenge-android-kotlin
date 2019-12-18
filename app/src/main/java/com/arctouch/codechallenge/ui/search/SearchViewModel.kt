package com.arctouch.codechallenge.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.repository.MovieRepository
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class SearchViewModel: ViewModel(), KoinComponent{

    // Flags
    var isLoading = false
    var isLastPage = false

    // Search Query
    var searchQuery: String = ""

    // Current Page
    var page = 1L
    var totalPages = 1

    // API Repository
    val movieRepository: MovieRepository by inject()

    // Live data of movies
    private lateinit var movies: MutableLiveData<MutableList<Movie>>

    // Return the data to the activity (either from network or memory if already initialized)
    fun getMovies(): LiveData<MutableList<Movie>> {

        if (!::movies.isInitialized) {
            movies = MutableLiveData()
            fetchMovies()
        }
        return movies

    }

    // Fetch data from the network layer
    private fun fetchMovies(){

        // Must be a query
        if (searchQuery.isEmpty()) return

        // Note (I know that is not the recommended way to call a coroutine scope with GlobalScope,
        // I don´t know yet how to implement in the right way)
        GlobalScope.launch {
            val response = movieRepository.fetchMovies(page, searchQuery)
            totalPages = response?.totalPages ?: 0 // Set the total pages
            Timber.e(" response: $response")

            response?.results?.let { results ->

                val total = movies.value ?: mutableListOf()
                total.addAll(results)
                movies.postValue(total)

            }

        }

    }

    // Fetch more data from the next page
    fun nextPageMovies(){
        if (page < totalPages) {
            page ++
            fetchMovies()
        }
    }

    // Search for movies
    // Set page to 1, clear all the movies from the memory
    fun searchMovies(query: String) {
        page = 1
        movies.value = mutableListOf()
        searchQuery = query
        fetchMovies()
    }

}