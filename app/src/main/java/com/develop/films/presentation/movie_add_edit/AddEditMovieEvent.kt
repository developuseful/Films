package com.develop.films.presentation.movie_add_edit

sealed class AddEditMovieEvent {
    data class EnteredTitle(val value: String) : AddEditMovieEvent()
    data class EnteredDescription(val value: String) : AddEditMovieEvent()
    data class EnteredGenre(val value: String) : AddEditMovieEvent()
    data class EnteredYear(val value: String) : AddEditMovieEvent()
    data class EnteredRating(val value: String) : AddEditMovieEvent()
    data class EnteredComment(val value: String) : AddEditMovieEvent()
    data class ChangeWatched(val isWatched: Boolean) : AddEditMovieEvent()
    object SaveMovie : AddEditMovieEvent()
}
