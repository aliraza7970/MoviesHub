package com.stc.movieshub.data.remote.response

import com.stc.movieshub.model.Search
import com.google.gson.annotations.SerializedName

class MultiSearchResponse(
    @SerializedName("page")
    val page: Int,
    @SerializedName("results")
    val results: List<Search>,
    @SerializedName("total_pages")
    val totalPages: Int,
    @SerializedName("total_results")
    val totalResults: Int
)
