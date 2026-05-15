package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository

class AddMovieUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movie: Movie) {
        if (movie.title.isBlank()) throw IllegalArgumentException("Название не может быть пустым")
        repository.insertMovie(movie)
    }
}