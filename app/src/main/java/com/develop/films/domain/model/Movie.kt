package com.develop.films.domain.model

data class Movie(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val genre: String?,
    val year: Int?,
    val isWatched: Boolean,
    val isFavorite: Boolean,
    val rating: Int?, // от 1 до 10
    val comment: String?
)