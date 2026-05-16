package com.develop.films.presentation.movie_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.develop.films.domain.model.Movie
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

private const val ALL_GENRES_LABEL = "Все жанры"

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
                updateMovies(movies)
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
            is MovieListEvent.SelectGenre -> {
                val currentMovies = _state.value.allMovies
                _state.value = _state.value.copy(
                    selectedGenre = event.genre,
                    movies = filterMovies(currentMovies, event.genre, _state.value.selectedTab)
                )
            }
            is MovieListEvent.SelectTab -> {
                val currentMovies = _state.value.allMovies
                _state.value = _state.value.copy(
                    selectedTab = event.tab,
                    movies = filterMovies(currentMovies, _state.value.selectedGenre, event.tab)
                )
            }
        }
    }

    private fun updateMovies(movies: List<Movie>) {
        val genres = movies
            .mapNotNull { it.genre?.takeIf { genre -> genre.isNotBlank() } }
            .distinct()
            .sorted()
            .let { listOf(ALL_GENRES_LABEL) + it }

        val selectedGenre = _state.value.selectedGenre
            .takeIf { it in genres }
            ?: ALL_GENRES_LABEL

        _state.value = _state.value.copy(
            allMovies = movies,
            genreOptions = genres,
            selectedGenre = selectedGenre,
            movies = filterMovies(movies, selectedGenre, _state.value.selectedTab)
        )
    }

    private fun filterMovies(movies: List<Movie>, genre: String, tab: MovieListTab): List<Movie> {
        val byGenre = if (genre == ALL_GENRES_LABEL) movies
        else movies.filter { it.genre == genre }

        return when (tab) {
            MovieListTab.TO_WATCH -> byGenre.filter { !it.isWatched }
            MovieListTab.WATCHED -> byGenre.filter { it.isWatched }
            MovieListTab.FAVORITES -> byGenre.filter { it.isFavorite }
        }
    }
}

data class MovieListState(
    val allMovies: List<Movie> = emptyList(),
    val movies: List<Movie> = emptyList(),
    val selectedGenre: String = ALL_GENRES_LABEL,
    val selectedTab: MovieListTab = MovieListTab.TO_WATCH,
    val genreOptions: List<String> = listOf(ALL_GENRES_LABEL)
)
