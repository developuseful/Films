package com.develop.films.data.repository

import com.develop.films.data.local.MovieDao
import com.develop.films.data.mapper.toEntity
import com.develop.films.data.mapper.toMovie
import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MovieRepositoryImpl(
    private val movieDao: MovieDao
) : MovieRepository {

    override fun getMovies(): Flow<List<Movie>> =
        movieDao.getMovies().map { entities -> entities.map { it.toMovie() } }

    override fun getMovieByIdFlow(id: Int): Flow<Movie?> =
        movieDao.getMovieByIdFlow(id).map { it?.toMovie() }

    override suspend fun getMovieById(id: Int): Movie? =
        movieDao.getMovieById(id)?.toMovie()

    override suspend fun insertMovie(movie: Movie) {
        movieDao.insertMovie(movie.toEntity())
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie.toEntity())
    }
}
