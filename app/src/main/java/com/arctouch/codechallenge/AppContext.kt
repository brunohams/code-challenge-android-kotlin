package com.arctouch.codechallenge

import android.app.Application
import android.content.Context
import com.arctouch.codechallenge.data.Cache
import com.arctouch.codechallenge.data.api.TmdbApi
import com.arctouch.codechallenge.data.repository.MovieRepository
import com.arctouch.codechallenge.modules.appModule
import com.arctouch.codechallenge.modules.networkModule
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.KoinComponent
import org.koin.core.context.startKoin
import org.koin.core.inject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber

/**
 * Application Context
 */
class AppContext : Application() {

    override fun onCreate() {

        // Cache Genres
        cacheGenres()

        // Initiate Koin (Dependency Injection)
        startKoin {
            androidLogger()
            androidContext(this@AppContext)
            modules(listOf(networkModule, appModule))
        }

        // Timber Logs
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        instance = this
        super.onCreate()
    }

    private fun cacheGenres() {
        val request = Retrofit.Builder().baseUrl(TmdbApi.URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(TmdbApi::class.java).genres(TmdbApi.DEFAULT_LANGUAGE)

        GlobalScope.launch {
            val response = request.await()
            response.body()?.genres?.let {
                Cache.cacheGenres(it)
            }
        }
    }

    companion object {
        var instance: AppContext? = null
            private set

        val context: Context?
            get() = instance
    }

}