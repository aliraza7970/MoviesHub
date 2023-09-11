package com.stc.movieshub.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watch_list_table")
data class MyListMovie(
    @PrimaryKey val mediaId: Int,
    val imagePath: String?,
    val backDropImagePath: String?,
    val title: String,
    val releaseDate: String,
    val rating: Double,
    val addedOn: String
)

@Entity(tableName = "GenresList")
data class GenresList(
    @PrimaryKey val mediaId: Int,
    val movieID: Int,
    val genresId: Int
)
