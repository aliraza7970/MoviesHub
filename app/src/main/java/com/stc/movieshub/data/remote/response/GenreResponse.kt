package com.stc.movieshub.data.remote.response

import com.stc.movieshub.model.Genre
import com.google.gson.annotations.SerializedName

data class GenreResponse(
    @SerializedName("genres")
    val genres: List<Genre>
)