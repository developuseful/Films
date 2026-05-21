package com.develop.films.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.develop.films.data.local.entity.MovieEntity

@Database(
    entities = [MovieEntity::class],
    version = 3,  // ✅ Обновлено с 2 на 3
    exportSchema = false
)
abstract class MovieDatabase : RoomDatabase() {
    abstract fun movieDao(): MovieDao
}
