package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.data.repository.MovieImportManager

/**
 * UseCase для ЯВНОГО импорта локального фильма в аккаунт
 * Требует авторизации пользователя и явного действия
 */
class ImportLocalMovieToAccountUseCase(
    private val importManager: MovieImportManager
) {
    suspend operator fun invoke(movie: Movie): Boolean {
        // ✅ Проверка что фильм локальный
        if (!movie.isLocal || movie.syncedWithAccount) {
            return false
        }
        
        // ✅ Явный импорт в аккаунт
        return importManager.importLocalMovieToAccount(movie)
    }
}
