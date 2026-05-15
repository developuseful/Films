package com.develop.films.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.films.domain.use_case.DeleteMovieUseCase
import com.develop.films.domain.use_case.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieListViewModel @Inject constructor(
    getMoviesUseCase: GetMoviesUseCase,
    private val deleteMovieUseCase: DeleteMovieUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(MovieListState())
    val state: StateFlow<MovieListState> = _state.asStateFlow()

    init {
        getMoviesUseCase()
            .onEach { movies ->
                _state.value = _state.value.copy(movies = movies)
            }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: MovieListEvent) {
        when (event) {
            is MovieListEvent.DeleteMovie -> {
                viewModelScope.launch {
                    deleteMovieUseCase(event.movie)
                }
            }
        }
    }
}

data class MovieListState(
    val movies: List<com.develop.films.domain.model.Movie> = emptyList()
)
