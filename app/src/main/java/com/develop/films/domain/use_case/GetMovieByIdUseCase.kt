package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository

class GetMovieByIdUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(id: Int): Movie? = repository.getMovieById(id)
}
