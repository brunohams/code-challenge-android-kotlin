package com.arctouch.codechallenge.data.api

import com.arctouch.codechallenge.data.model.GenreResponse
import com.arctouch.codechallenge.data.model.Movie
import com.arctouch.codechallenge.data.model.MoviesResponse
import kotlinx.coroutines.Deferred
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TmdbApi {

    companion object {
        const val URL = "https://api.themoviedb.org/3/"
        const val API_KEY = "1f54bd990f1cdfb230adb312546d765d"
        const val DEFAULT_LANGUAGE = "pt-BR"
        const val DEFAULT_REGION = "BR"
    }

    @GET("genre/movie/list?api_key=${API_KEY}")
    fun genres(
        @Query("language") language: String
    ): Deferred<Response<GenreResponse>>

    @GET("movie/upcoming?api_key=${API_KEY}")
    fun upcomingMovies(
        @Query("language") language: String,
        @Query("page") page: Long,
        @Query("region") region: String
    ): Deferred<Response<MoviesResponse>>

    @GET("search/movie?api_key=${API_KEY}")
    fun movies(
        @Query("language") language: String,
        @Query("page") page: Long,
        @Query("region") region: String,
        @Query("query") query: String
    ): Deferred<Response<MoviesResponse>>
}
