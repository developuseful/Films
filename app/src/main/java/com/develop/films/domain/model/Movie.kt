package com.develop.films.domain.model

data class Movie(
    val id: Int = 0,
    val title: String,
    val description: String?,
    val genre: String?,
    val year: Int?,
    val isWatched: Boolean,
    val isFavorite: Boolean,
    val rating: Int?, // от 1 до 10
    val comment: String?,
    
    // ✅ Новые поля для разделения локальных/облачных фильмов
    val isLocal: Boolean = false,  // true = только локально, false = с аккаунта
    val syncedWithAccount: Boolean = false,  // был ли синхронизирован
    val accountId: String? = null  // ID пользователя если синхронизирован
)

/**
 * Проверка, что фильм остаётся только локальным
 */
fun Movie.isReallyLocal(): Boolean {
    return this.isLocal && !this.syncedWithAccount && this.accountId == null
}

/**
 * Проверка, может ли быть импортирован в аккаунт
 */
fun Movie.canBeImported(): Boolean {
    return this.isReallyLocal()
}
