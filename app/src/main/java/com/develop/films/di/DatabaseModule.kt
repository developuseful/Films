package com.develop.films.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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
        val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE movies ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }

        return Room.databaseBuilder(app, MovieDatabase::class.java, "movies_db")
            .addMigrations(migration1to2)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(db: MovieDatabase): MovieRepository {
        return MovieRepositoryImpl(db.movieDao())
    }
}
