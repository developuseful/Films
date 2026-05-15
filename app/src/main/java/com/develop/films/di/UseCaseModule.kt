package com.develop.films.di

import com.develop.films.domain.repository.MovieRepository
import com.develop.films.domain.use_case.AddMovieUseCase
import com.develop.films.domain.use_case.DeleteMovieUseCase
import com.develop.films.domain.use_case.GetMovieByIdFlowUseCase
import com.develop.films.domain.use_case.GetMovieByIdUseCase
import com.develop.films.domain.use_case.GetMoviesUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAddMovieUseCase(repository: MovieRepository) = AddMovieUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMoviesUseCase(repository: MovieRepository) = GetMoviesUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMovieByIdUseCase(repository: MovieRepository) = GetMovieByIdUseCase(repository)

    @Provides
    @Singleton
    fun provideGetMovieByIdFlowUseCase(repository: MovieRepository) = GetMovieByIdFlowUseCase(repository)

    @Provides
    @Singleton
    fun provideDeleteMovieUseCase(repository: MovieRepository) = DeleteMovieUseCase(repository)
}
