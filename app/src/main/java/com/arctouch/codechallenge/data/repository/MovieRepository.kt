package com.arctouch.codechallenge.data.repository

import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.model.MoviesResponse
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

class MovieRepository: KoinComponent {

    // Injects
    val tmdbApi: TmdbApi by inject()

    suspend fun fetchUpcomingMovies(page: Long): MoviesResponse? {
        // Note (Here i removed the region filter, because the default region doesn't return many movies
        //  and because of that, you can't test the pagination feature)
        val request = tmdbApi.upcomingMovies(TmdbApi.DEFAULT_LANGUAGE, page, "")
        var data: MoviesResponse? = null
        val job = GlobalScope.launch {

            try {

                val response = request.await()
                if (response.isSuccessful) {
                    response.body()?.let {
                        data = it
                    }
                }

            }
            catch (e: Exception) {
                Timber.e(e)
            }

        }

        job.join()
        return data
    }

    suspend fun fetchMovies(page: Long, query: String): MoviesResponse? {
        val request = tmdbApi.movies(TmdbApi.DEFAULT_LANGUAGE, page, TmdbApi.DEFAULT_REGION, query)
        var data: MoviesResponse? = null
        val job = GlobalScope.launch {

            try {

                val response = request.await()
                Timber.e("AW2: $response")
                if (response.isSuccessful) {
                    response.body()?.let {
                        data = it
                    }
                }

            }
            catch (e: Exception) {
                Timber.e(e)
            }

        }

        job.join()
        return data
    }

}