package com.stc.movieshub.data.repository

import com.stc.movieshub.data.remote.response.GenreResponse
import com.stc.movieshub.data.remote.ApiService
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.util.Resource
import java.lang.Exception
import javax.inject.Inject

class GenreRepository @Inject constructor(private val api: ApiService) {
    suspend fun getMoviesGenre(filmType: FilmType): Resource<GenreResponse>{
        val response = try {
             api.getMovieGenres()
        } catch (e: Exception){
            return Resource.Error("Unknown error occurred!")
        }
        return Resource.Success(response)
    }
}