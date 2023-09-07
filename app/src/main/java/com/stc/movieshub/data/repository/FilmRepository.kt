package com.stc.movieshub.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.stc.movieshub.data.remote.ApiService
import com.stc.movieshub.model.Film
import com.stc.movieshub.paging.*
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.util.Resource
import kotlinx.coroutines.flow.Flow
import timber.log.Timber
import javax.inject.Inject

class FilmRepository @Inject constructor(
    private val api: ApiService
) {
    fun getTrendingFilms(filmType: FilmType): Flow<PagingData<Film>> {
        return Pager(
            config = PagingConfig(enablePlaceholders = false, pageSize = 20),
            pagingSourceFactory = {
                TrendingFilmSource(api = api, filmType)
            }
        ).flow
    }


    /** Non-paging data */
    suspend fun getDetailsFilm(filmId: Int, filmType: FilmType): Resource<Film> {
        val response = try {
             api.getDetailsFilm(filmId = filmId,  "movie")
        } catch (e: Exception) {
            return Resource.Error("Error when loading movie cast")
        }
        return Resource.Success(response)
    }


}
