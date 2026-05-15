package com.develop.films.di

import android.app.Application
import androidx.room.Room
import com.develop.films.data.local.MovieDatabase
import com.develop.films.data.repository.MovieRepositoryImpl
import com.develop.films.domain.repository.MovieRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

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
        return MovieRepositoryImpl(db.movieDao())
    }
}
