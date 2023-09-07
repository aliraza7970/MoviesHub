package com.stc.movieshub.data.remote.response

import com.stc.movieshub.model.Film
import com.google.gson.annotations.SerializedName

data class FilmResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Film>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
