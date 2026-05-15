package com.develop.films.presentation.movie_detail

import com.develop.films.domain.model.Movie

data class MovieDetailState(
    val movie: Movie? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
