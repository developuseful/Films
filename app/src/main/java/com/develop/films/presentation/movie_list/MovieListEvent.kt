package com.develop.films.presentation.movie_list

import com.develop.films.domain.model.Movie

sealed class MovieListEvent {
    data class DeleteMovie(val movie: Movie) : MovieListEvent()
}
