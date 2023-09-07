@file:Suppress("KDocUnresolvedReference")

package com.stc.movieshub.data.remote

import com.stc.movieshub.BuildConfig
import com.stc.movieshub.data.remote.response.*
import com.stc.movieshub.model.Film
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    /** **Movies** */
    @GET("trending/movie/day")
    suspend fun getTrendingMovies(
        @Query("page") page: Int = 0,
        @Query("api_key") apiKey: String = BuildConfig.MOVIESHUB_API_KEY,
        @Query("language") language: String = "en"
    ): FilmResponse


    @GET("genre/movie/list")
    suspend fun getMovieGenres(
        @Query("api_key") apiKey: String = BuildConfig.MOVIESHUB_API_KEY,
        @Query("language") language: String = "en"
    ): GenreResponse

    @GET("search/multi")
    suspend fun multiSearch(
        @Query("query") searchParams: String,
        @Query("page") page: Int = 0,
        @Query("include_adult") includeAdult: Boolean = true,
        @Query("api_key") apiKey: String = BuildConfig.MOVIESHUB_API_KEY,
        @Query("language") language: String = "en"
    ): MultiSearchResponse


    /** DetailsFilm*/
    @GET("{film_path}/{film_id}?")
    suspend fun getDetailsFilm(
        @Path("film_id") filmId: Int,
        @Path("film_path") filmPath: String,
        @Query("api_key") apiKey: String = BuildConfig.MOVIESHUB_API_KEY,
        @Query("language") language: String = "en-US"
    ): Film


}
