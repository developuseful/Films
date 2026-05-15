package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow

class GetMovieByIdFlowUseCase(private val repository: MovieRepository) {
    operator fun invoke(id: Int): Flow<Movie?> = repository.getMovieByIdFlow(id)
}
