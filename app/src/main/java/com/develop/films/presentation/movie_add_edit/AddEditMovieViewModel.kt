package com.develop.films.presentation.movie_add_edit

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.films.domain.model.Movie
import com.develop.films.domain.use_case.AddMovieUseCase
import com.develop.films.domain.use_case.GetMovieByIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditMovieViewModel @Inject constructor(
    private val getMovieByIdUseCase: GetMovieByIdUseCase,
    private val addMovieUseCase: AddMovieUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(AddEditMovieState())
    val state: StateFlow<AddEditMovieState> = _state.asStateFlow()

    private val _uiEvent = Channel<UiEvent>(Channel.BUFFERED)
    val uiEvent = _uiEvent.receiveAsFlow()

    init {
        val movieId = savedStateHandle.get<Int>("movieId") ?: 0
        if (movieId > 0) {
            loadMovie(movieId)
        }
    }

    fun onEvent(event: AddEditMovieEvent) {
        when (event) {
            is AddEditMovieEvent.EnteredTitle ->
                _state.value = _state.value.copy(title = event.value)
            is AddEditMovieEvent.EnteredDescription ->
                _state.value = _state.value.copy(description = event.value)
            is AddEditMovieEvent.EnteredGenre ->
                _state.value = _state.value.copy(genre = event.value)
            is AddEditMovieEvent.EnteredYear ->
                _state.value = _state.value.copy(year = event.value)
            is AddEditMovieEvent.EnteredRating ->
                _state.value = _state.value.copy(rating = event.value)
            is AddEditMovieEvent.EnteredComment ->
                _state.value = _state.value.copy(comment = event.value)
            is AddEditMovieEvent.ChangeWatched ->
                _state.value = _state.value.copy(isWatched = event.isWatched)
            is AddEditMovieEvent.ChangeFavorite ->
                _state.value = _state.value.copy(isFavorite = event.isFavorite)
            AddEditMovieEvent.SaveMovie -> {
                saveMovie()
            }
        }
    }

    private fun loadMovie(movieId: Int) {
        viewModelScope.launch {
            getMovieByIdUseCase(movieId)?.let { movie ->
                _state.value = _state.value.copy(
                    id = movie.id,
                    title = movie.title,
                    description = movie.description.orEmpty(),
                    genre = movie.genre.orEmpty(),
                    year = movie.year?.toString().orEmpty(),
                    isWatched = movie.isWatched,
                    isFavorite = movie.isFavorite,
                    rating = movie.rating?.toString().orEmpty(),
                    comment = movie.comment.orEmpty(),
                    isExisting = true
                )
            }
        }
    }

    private fun saveMovie() {
        viewModelScope.launch {
            val currentState = state.value
            if (currentState.title.isBlank()) {
                _uiEvent.send(UiEvent.ShowError("Введите название фильма"))
                return@launch
            }

            val movie = Movie(
                id = currentState.id,
                title = currentState.title.trim(),
                description = currentState.description.trim().ifEmpty { null },
                genre = currentState.genre.trim().ifEmpty { null },
                year = currentState.year.toIntOrNull(),
                isWatched = currentState.isWatched,
                isFavorite = currentState.isFavorite,
                rating = currentState.rating.toIntOrNull(),
                comment = currentState.comment.trim().ifEmpty { null }
            )

            addMovieUseCase(movie)
            _uiEvent.send(UiEvent.SaveMovie)
        }
    }

    sealed class UiEvent {
        object SaveMovie : UiEvent()
        data class ShowError(val message: String) : UiEvent()
    }
}
