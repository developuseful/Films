package com.develop.films.presentation.movie_list

import com.develop.films.domain.model.Movie

enum class MovieListTab(val title: String) {
    TO_WATCH("К просмотру"),
    WATCHED("Смотрел"),
    FAVORITES("Избранное")
}

sealed class MovieListEvent {
    data class DeleteMovie(val movie: Movie) : MovieListEvent()
    data class SelectGenre(val genre: String) : MovieListEvent()
    data class SelectTab(val tab: MovieListTab) : MovieListEvent()
}
