package com.develop.films.domain.repository

import com.develop.films.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    fun getMovies(): Flow<List<Movie>>
    suspend fun getMovieById(id: Int): Movie?
    suspend fun insertMovie(movie: Movie)
    suspend fun deleteMovie(movie: Movie)
}