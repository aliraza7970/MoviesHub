package com.stc.movieshub.viewmodel

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.filter
import com.stc.movieshub.data.repository.GenreRepository
import com.stc.movieshub.data.repository.FilmRepository
import com.stc.movieshub.model.Genre
import com.stc.movieshub.model.Film
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val filmRepository: FilmRepository,
    private val genreRepository: GenreRepository,
) : ViewModel() {
    private var _filmGenres = mutableStateListOf(Genre(null, "All"))
    val filmGenres: SnapshotStateList<Genre> = _filmGenres

    var selectedGenre: MutableState<Genre> = mutableStateOf(Genre(null, "All"))
    var selectedFilmType: MutableState<FilmType> = mutableStateOf(FilmType.MOVIE)

    var searchParam = mutableStateOf("")
    var previousSearch = mutableStateOf("")

    private var _trendingMovies = mutableStateOf<Flow<PagingData<Film>>>(emptyFlow())
    val trendingMoviesState: State<Flow<PagingData<Film>>> = _trendingMovies

    init {
        refreshAll()
    }

    fun refreshAll(
        genreId: Int? = selectedGenre.value.id,
        filmType: FilmType = selectedFilmType.value
    ) {
        if (filmGenres.size == 1) {
            getFilmGenre(selectedFilmType.value)
        }
        if (genreId == null) {
            selectedGenre.value = Genre(null, "All")
        }
        getTrendingFilms(genreId, filmType)
    }

    fun filterBySetSelectedGenre(genre: Genre) {
        selectedGenre.value = genre
        refreshAll(genre.id)
    }

    fun getFilmGenre(filmType: FilmType = selectedFilmType.value) {
        viewModelScope.launch {
            val defaultGenre = Genre(null, "All")
            when (val results = genreRepository.getMoviesGenre(filmType)) {
                is Resource.Success -> {
                    _filmGenres.clear()
                    _filmGenres.add(defaultGenre)
                    selectedGenre.value = defaultGenre
                    results.data?.genres?.forEach {
                        _filmGenres.add(it)
                    }
                }
                is Resource.Error -> {
                    Timber.e("Error loading Genres")
                }
                else -> { }
            }
        }
    }

    private fun getTrendingFilms(genreId: Int?, filmType: FilmType) {
        viewModelScope.launch {
            _trendingMovies.value = if (genreId != null) {
                filmRepository.getTrendingFilms(filmType).map { results ->
                    results.filter { movie ->
                        movie.genreIds!!.contains(genreId)
                    }
                }.cachedIn(viewModelScope)
            } else {
                filmRepository.getTrendingFilms(filmType).cachedIn(viewModelScope)
            }
        }
    }

    fun getSearchFilms( filmType: FilmType) {
        viewModelScope.launch {
            _trendingMovies.value =
                filmRepository.getTrendingFilms(filmType).map { results ->
                    results.filter { movie ->
                      movie.title.lowercase().contains(searchParam.value.lowercase())|| movie.releaseDate.lowercase().contains(searchParam.value.lowercase())
                    }
                }.cachedIn(viewModelScope)

        }
    }

}