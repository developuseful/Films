package com.develop.films.domain.use_case

import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository

/**
 * UseCase для добавления фильма как ЛОКАЛЬНОГО
 * Гарантирует что фильм будет отмечен как локальный
 * и не будет автоматически синхронизирован
 */
class AddLocalMovieUseCase(private val repository: MovieRepository) {
    suspend operator fun invoke(movie: Movie) {
        if (movie.title.isBlank()) {
            throw IllegalArgumentException("Название не может быть пустым")
        }
        
        // ✅ Создаём копию фильма с флагом isLocal=true
        val localMovie = movie.copy(
            isLocal = true,  // Это ЛОКАЛЬНЫЙ фильм
            syncedWithAccount = false,  // Не синхронизирован
            accountId = null  // Не привязан к аккаунту
        )
        
        repository.insertMovie(localMovie)
    }
}
