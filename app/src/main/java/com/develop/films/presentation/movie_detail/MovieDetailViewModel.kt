package com.develop.films.presentation.movie_detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.films.domain.use_case.GetMovieByIdFlowUseCase
import com.develop.films.domain.use_case.UpdateMovieUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor(
    private val getMovieByIdFlowUseCase: GetMovieByIdFlowUseCase,
    private val updateMovieUseCase: UpdateMovieUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(MovieDetailState(isLoading = true))
    val state: StateFlow<MovieDetailState> = _state.asStateFlow()

    init {
        val movieId = savedStateHandle.get<Int>("movieId") ?: 0
        if (movieId > 0) {
            viewModelScope.launch {
                getMovieByIdFlowUseCase(movieId).collect { movie ->
                    _state.value = if (movie != null) {
                        MovieDetailState(movie = movie, isLoading = false)
                    } else {
                        MovieDetailState(errorMessage = "Фильм не найден")
                    }
                }
            }
        } else {
            _state.value = MovieDetailState(errorMessage = "Фильм не найден")
        }
    }

    fun onToggleWatched(isWatched: Boolean) {
        val movie = _state.value.movie ?: return
        viewModelScope.launch {
            updateMovieUseCase(movie.copy(isWatched = isWatched))
        }
    }

    fun onToggleFavorite(isFavorite: Boolean) {
        val movie = _state.value.movie ?: return
        viewModelScope.launch {
            updateMovieUseCase(movie.copy(isFavorite = isFavorite))
        }
    }
}
