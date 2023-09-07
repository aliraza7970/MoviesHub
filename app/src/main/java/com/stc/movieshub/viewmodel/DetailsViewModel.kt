package com.stc.movieshub.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stc.movieshub.data.repository.FilmRepository
import com.stc.movieshub.model.Film
import com.stc.movieshub.util.FilmType
import com.stc.movieshub.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(val repository: FilmRepository) : ViewModel() {

    private var _detailsFilms = mutableStateOf<Film?>(null)
    val detailsMovies: State<Film?> = _detailsFilms

    fun getDetailsFilms(filmId: Int, filmType: FilmType) {
        viewModelScope.launch {
            repository.getDetailsFilm(filmId = filmId, filmType).also {
                if (it is Resource.Success) {
                    _detailsFilms.value = it.data
                }
            }
        }
    }






}
