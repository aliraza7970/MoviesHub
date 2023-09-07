package com.stc.movieshub.di

import android.app.Application
import androidx.room.Room
import com.stc.movieshub.data.local.WatchListDatabase
import com.stc.movieshub.data.preferences.UserPreferences
import com.stc.movieshub.data.remote.ApiService
import com.stc.movieshub.data.repository.*
import com.stc.movieshub.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Singleton
    @Provides
    fun providesLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Singleton
    @Provides
    fun providesOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .callTimeout(15, TimeUnit.SECONDS)
            .connectTimeout(15, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .readTimeout(15, TimeUnit.SECONDS)
            .build()
    }

    @Singleton
    @Provides
    fun providesAPIService(okHttpClient: OkHttpClient): ApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(ApiService::class.java)
    }

    @Singleton
    @Provides
    fun provideMoviesRepository(api: ApiService) = FilmRepository(api = api)

    @Singleton
    @Provides
    fun provideSearchRepository(api: ApiService) = SearchRepository(api = api)

    @Singleton
    @Provides
    fun providesGenresRepository(api: ApiService) = GenreRepository(api)



    @Provides
    @Singleton
    fun providesWatchListDatabase(application: Application): WatchListDatabase {
        return Room.databaseBuilder(
            application.applicationContext,
            WatchListDatabase::class.java,
            "watch_list_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun providesDataStore(application: Application): UserPreferences {
        return UserPreferences(application.applicationContext)
    }
}
