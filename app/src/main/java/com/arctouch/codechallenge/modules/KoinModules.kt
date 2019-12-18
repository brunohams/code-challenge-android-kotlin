package com.arctouch.codechallenge.modules

import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.MovieRepository
import com.arctouch.codechallenge.ui.adapter.MovieAdapter
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { Retrofit.Builder().baseUrl(TmdbApi.URL)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .build() }
    factory { provideTmdbApi(get()) }
}

fun provideTmdbApi(retrofit: Retrofit): TmdbApi = retrofit.create(TmdbApi::class.java)

val appModule = module {
    factory { MovieAdapter(get()) }
    single { MovieRepository() }
}
