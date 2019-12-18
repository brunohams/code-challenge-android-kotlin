package com.arctouch.codechallenge.ui.home

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

class HomeViewModel: ViewModel(), KoinComponent{

    // Flags
    var isLoading = false
    var isLastPage = false

    // Current Page
    var page = 1L
    var totalPages = 1

    // API Repository
    val movieRepository: MovieRepository by inject()

    // Live data of upcoming movies
    private lateinit var upcomingMovies: MutableLiveData<MutableList<Movie>>

    // Return the data to the activity (either from network or memory if already initialized)
    fun getUpcomingMovies(): LiveData<MutableList<Movie>> {

        if (!::upcomingMovies.isInitialized) {
            upcomingMovies = MutableLiveData()
            fetchUpcomingMovies()
        }
        return upcomingMovies

    }

    // Fetch data from the network layer
    private fun fetchUpcomingMovies(){

        // Note (I know that is not the recommended way to call a coroutine scope with GlobalScope,
        // I don´t know yet how to implement in the right way)
        GlobalScope.launch {
            val response = movieRepository.fetchUpcomingMovies(page)
            totalPages = response?.totalPages ?: 0 // Set the total pages

            // Append movies to the mutable list
            response?.results?.let { results ->

                val movies = upcomingMovies.value ?: mutableListOf()
                movies.addAll(results)
                upcomingMovies.postValue(movies)

            }

        }

    }

    // Fetch more data from the next page
    fun nextPageUpcomingMovies(){
        if (page < totalPages) {
            page ++
            fetchUpcomingMovies()
        }
    }

}