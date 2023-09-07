package com.stc.movieshub.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.stc.movieshub.data.remote.ApiService
import com.stc.movieshub.model.Film
import com.stc.movieshub.util.FilmType
import retrofit2.HttpException
import java.io.IOException

class TrendingFilmSource(private val api: ApiService, private val filmType: FilmType) :
    PagingSource<Int, Film>() {
    override fun getRefreshKey(state: PagingState<Int, Film>): Int? = state.anchorPosition

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Film> {
        return try {
            val nextPage = params.key ?: 1

            val trendingFilms =
                 api.getTrendingMovies(page = nextPage)

            LoadResult.Page(
                data = trendingFilms.results,
                prevKey = if (nextPage == 1) null else nextPage - 1,
                nextKey = if (trendingFilms.results.isEmpty()) null else trendingFilms.page + 1
            )
        } catch (e: IOException) {
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            return LoadResult.Error(e)
        }
    }
}
