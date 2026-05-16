package com.develop.films.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val genre: String?,
    val year: Int?,
    val isWatched: Boolean,
    val isFavorite: Boolean,
    val rating: Int?,
    val comment: String?
)