package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository

class DeleteMovieUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movie: Movie) {
        repository.deleteMovie(movie)
    }
}
