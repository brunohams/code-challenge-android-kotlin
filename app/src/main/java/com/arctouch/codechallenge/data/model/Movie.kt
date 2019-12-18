package com.arctouch.codechallenge.data.model

import android.os.Parcelable
import com.arctouch.codechallenge.data.api.TmdbApi
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Movie(
        val id: Int,
        val title: String,
        val overview: String?,
        val genres: List<Genre>?,
        @SerializedName("genre_ids") val genreIds: List<Int>?,
        @SerializedName("poster_path") val posterPath: String?,
        @SerializedName("backdrop_path") val backdropPath: String?,
        @SerializedName("release_date") val releaseDate: String?
): Parcelable {

    // Images URLS
    private val POSTER_URL = "https://image.tmdb.org/t/p/w154"
    private val BACKDROP_URL = "https://image.tmdb.org/t/p/w780"

    // Computed values
    val posterUrl: String
        get() = POSTER_URL + posterPath + "?api_key=" + TmdbApi.API_KEY

    val backdropUrl: String
        get() = BACKDROP_URL + backdropPath + "?api_key=" + TmdbApi.API_KEY

}
