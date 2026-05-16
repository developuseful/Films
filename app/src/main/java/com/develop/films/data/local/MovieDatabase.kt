package com.develop.films.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.develop.films.data.local.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 2,
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
