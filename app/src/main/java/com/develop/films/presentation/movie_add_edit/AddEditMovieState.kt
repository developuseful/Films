package com.develop.films.presentation.movie_add_edit

data class AddEditMovieState(
    val id: Int = 0,
    val title: String = "",
    val description: String = "",
    val genre: String = "",
    val year: String = "",
    val isWatched: Boolean = false,
    val rating: String = "",
    val comment: String = "",
    val isExisting: Boolean = false
)
