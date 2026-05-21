package com.develop.films.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String?,
    val genre: String?,
    val year: Int?,
    val isWatched: Boolean,
    val isFavorite: Boolean,
    val rating: Int?,
    val comment: String?,
    
    // ✅ Новые поля для разделения локальных/облачных фильмов
    val isLocal: Boolean = false,          // true = только локально
    val syncedWithAccount: Boolean = false, // был ли синхронизирован
    val accountId: String? = null          // Email пользователя если синхронизирован
)
