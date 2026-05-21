package com.develop.films.di

import com.develop.films.domain.repository.MovieRepository
import com.develop.films.domain.use_case.AddMovieUseCase
import com.develop.films.domain.use_case.AddLocalMovieUseCase
import com.develop.films.domain.use_case.DeleteMovieUseCase
import com.develop.films.domain.use_case.GetMovieByIdFlowUseCase
import com.develop.films.domain.use_case.GetMovieByIdUseCase
import com.develop.films.domain.use_case.GetMoviesUseCase
import com.develop.films.domain.use_case.UpdateMovieUseCase
import com.develop.films.domain.use_case.ImportLocalMovieToAccountUseCase
import com.develop.films.data.repository.MovieImportManager
import com.google.firebase.database.FirebaseDatabase
import android.app.Application
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
    fun provideAddLocalMovieUseCase(repository: MovieRepository) = AddLocalMovieUseCase(repository)

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

    @Provides
    @Singleton
    fun provideUpdateMovieUseCase(repository: MovieRepository) = UpdateMovieUseCase(repository)
    
    @Provides
    @Singleton
    fun provideMovieImportManager(
        firebaseDatabase: FirebaseDatabase,
        app: Application
    ) = MovieImportManager(firebaseDatabase, app)
    
    @Provides
    @Singleton
    fun provideImportLocalMovieToAccountUseCase(
        importManager: MovieImportManager
    ) = ImportLocalMovieToAccountUseCase(importManager)
}
