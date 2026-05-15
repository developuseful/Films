package com.develop.films.di

import android.app.Application
import com.develop.films.domain.repository.MovieRepository
import com.develop.films.domain.use_case.AddMovieUseCase
import com.develop.films.domain.use_case.GetMoviesUseCase

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMovieDatabase(app: Application): MovieDatabase {
        return Room.databaseBuilder(app, MovieDatabase::class.java, "movies_db").build()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(db: MovieDatabase): MovieRepository {
        return MovieRepositoryImpl(db.movieDao) // Реализация репозитория из слоя Data
    }

    @Provides
    @Singleton
    fun provideAddMovieUseCase(repo: MovieRepository) = AddMovieUseCase(repo)

    @Provides
    @Singleton
    fun provideGetMoviesUseCase(repo: MovieRepository) = GetMoviesUseCase(repo)
}