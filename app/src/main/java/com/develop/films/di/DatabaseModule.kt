package com.develop.films.di

import android.app.Application
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.develop.films.data.local.MovieDatabase
import com.develop.films.data.repository.MovieRepositoryImpl
import com.develop.films.domain.repository.MovieRepository
import com.google.firebase.database.FirebaseDatabase
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
        // Миграция 1 -> 2
        val migration1to2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE movies ADD COLUMN isFavorite INTEGER NOT NULL DEFAULT 0")
            }
        }
        
        // ✅ Миграция 2 -> 3: Добавляем поля для локальных/облачных фильмов
        val migration2to3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Поле isLocal: true = только локально, false = облачный
                database.execSQL(
                    "ALTER TABLE movies ADD COLUMN isLocal INTEGER NOT NULL DEFAULT 0"
                )
                
                // Поле syncedWithAccount: был ли синхронизирован
                database.execSQL(
                    "ALTER TABLE movies ADD COLUMN syncedWithAccount INTEGER NOT NULL DEFAULT 0"
                )
                
                // Поле accountId: ID пользователя если синхронизирован
                database.execSQL(
                    "ALTER TABLE movies ADD COLUMN accountId TEXT"
                )
            }
        }

        return Room.databaseBuilder(app, MovieDatabase::class.java, "movies_db")
            .addMigrations(migration1to2, migration2to3)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun provideMovieRepository(
        db: MovieDatabase,
        realtimeDatabase: FirebaseDatabase,
        app: Application
    ): MovieRepository {
        return MovieRepositoryImpl(db.movieDao(), realtimeDatabase, app)
    }
}
