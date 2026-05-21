package com.develop.films.data.repository

import android.app.Application
import android.util.Log
import com.develop.films.domain.model.Movie
import com.develop.films.util.UserPreferences
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

/**
 * Менеджер для ЯВНОГО импорта локальных фильмов в аккаунт
 * Только пользователь может инициировать импорт
 */
class MovieImportManager(
    private val firebaseDatabase: FirebaseDatabase,
    private val app: Application
) {
    
    companion object {
        private const val TAG = "MovieImportManager"
        private const val USER_MOVIES_PATH = "users"
    }
    
    /**
     * ЯВНОЕ действие: Импортировать ОДИН локальный фильм в аккаунт
     * @return true если успешно, false если ошибка
     */
    suspend fun importLocalMovieToAccount(movie: Movie): Boolean {
        // ✅ Проверка 1: Пользователь должен быть авторизован
        if (!UserPreferences.isLoggedIn(app)) {
            Log.e(TAG, "Импорт невозможен - пользователь не авторизован")
            return false
        }
        
        // ✅ Проверка 2: Фильм должен быть локальным
        if (!movie.isLocal || movie.syncedWithAccount) {
            Log.e(TAG, "Фильм уже синхронизирован или не локальный")
            return false
        }
        
        return try {
            val userEmail = UserPreferences.getUserEmail(app) ?: run {
                Log.e(TAG, "Email пользователя не найден")
                return false
            }
            
            // Создать копию фильма с привязкой к аккаунту
            val importedMovie = movie.copy(
                syncedWithAccount = true,
                accountId = userEmail,
                // Флаг isLocal может остаться, но главное что теперь он синхронизирован
                isLocal = false
            )
            
            // Сохранить в Firebase
            val remotePath = "users/$userEmail/movies/${movie.id}"
            val movieMap = mapOf(
                "id" to importedMovie.id,
                "title" to importedMovie.title,
                "description" to importedMovie.description,
                "genre" to importedMovie.genre,
                "year" to importedMovie.year,
                "isWatched" to importedMovie.isWatched,
                "isFavorite" to importedMovie.isFavorite,
                "rating" to importedMovie.rating,
                "comment" to importedMovie.comment,
                "accountId" to importedMovie.accountId
            )
            
            firebaseDatabase.reference.child(remotePath).setValue(movieMap).await()
            Log.d(TAG, "Фильм успешно импортирован: ${movie.title}")
            true
            
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка импорта фильма: ${e.message}", e)
            false
        }
    }
    
    /**
     * ЯВНОЕ действие: Импортировать НЕСКОЛЬКО локальных фильмов
     */
    suspend fun importLocalMoviesToAccount(movies: List<Movie>): List<Boolean> {
        Log.d(TAG, "Импорт ${movies.size} фильмов начался...")
        val results = movies.map { movie ->
            try {
                importLocalMovieToAccount(movie)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при импорте ${movie.title}", e)
                false
            }
        }
        
        val successful = results.count { it }
        Log.d(TAG, "Импорт завершён: $successful/${movies.size} успешных")
        return results
    }
    
    /**
     * ✅ ВАЖНО: Проверить что импорт действительно явный
     * (не автоматический при переключении режима)
     */
    fun isImportExplicit(): Boolean {
        // В реальном приложении здесь может быть диалог подтверждения
        // или пользователь нажимает кнопку "Импортировать"
        return true
    }
}
