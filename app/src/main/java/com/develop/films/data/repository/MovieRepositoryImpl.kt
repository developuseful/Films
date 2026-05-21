package com.develop.films.data.repository

import android.app.Application
import android.util.Log
import com.develop.films.data.local.MovieDao
import com.develop.films.data.mapper.toEntity
import com.develop.films.data.mapper.toMovie
import com.develop.films.domain.model.Movie
import com.develop.films.domain.repository.MovieRepository
import com.develop.films.util.UserPreferences
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import java.security.MessageDigest

class MovieRepositoryImpl(
    private val movieDao: MovieDao,
    private val firebaseDatabase: FirebaseDatabase,
    private val app: Application
) : MovieRepository {

    companion object {
        private const val TAG = "MovieRepositoryImpl"
        private const val USER_MOVIES_PATH = "users"
        private const val SHARED_MOVIES_PATH = "shared_movies"
    }

    override fun getMovies(): Flow<List<Movie>> = flow {
        if (isRemoteSyncEnabled()) {
            try {
                syncRemoteMoviesIfNeeded()
            } catch (e: Exception) {
                Log.e(TAG, "Realtime DB initial sync failed", e)
            }
        }
        emitAll(movieDao.getMovies().map { entities -> entities.map { it.toMovie() } })
    }

    override fun getMovieByIdFlow(id: Int): Flow<Movie?> =
        movieDao.getMovieByIdFlow(id).map { it?.toMovie() }

    override suspend fun getMovieById(id: Int): Movie? =
        movieDao.getMovieById(id)?.toMovie()

    override suspend fun insertMovie(movie: Movie) {
        movieDao.insertMovie(movie.toEntity())
        syncRemoteWithLocalDataIfNeeded()
    }

    override suspend fun deleteMovie(movie: Movie) {
        movieDao.deleteMovie(movie.toEntity())
        syncRemoteWithLocalDataIfNeeded()
    }

    private suspend fun syncRemoteMoviesIfNeeded() {
        val remoteMovies = loadMoviesFromRemote()
        val localMovies = loadLocalMovies()

        when {
            remoteMovies.isEmpty() && localMovies.isNotEmpty() -> saveMoviesToRemote(localMovies)
            remoteMovies.isNotEmpty() && localMovies.isEmpty() -> replaceLocalMovies(remoteMovies)
            remoteMovies.isNotEmpty() && localMovies.isNotEmpty() -> {
                val merged = mergeLocalAndRemote(localMovies, remoteMovies)
                replaceLocalMovies(merged)
                saveMoviesToRemote(merged)
            }
            else -> {
                // both empty, nothing to sync
            }
        }
    }

    private suspend fun syncRemoteWithLocalDataIfNeeded() {
        if (!isRemoteSyncEnabled()) return
        val localMovies = loadLocalMovies()
        saveMoviesToRemote(localMovies)
    }

    private fun isRemoteSyncEnabled(): Boolean =
        UserPreferences.isLoggedIn(app) && !UserPreferences.isLocalMode(app)

    /**
     * Преобразует email в безопасный для Firebase ключ с помощью SHA-256 хэширования
     * Это гарантирует отсутствие запрещённых символов и скрывает реальный email пользователя
     */
    private fun encodeEmailForFirebase(email: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val hash = digest.digest(email.toByteArray(Charsets.UTF_8))
        return hash.joinToString("") { "%02x".format(it) }
    }

    private fun getRemotePath(): String {
        val email = UserPreferences.getUserEmail(app)
        return if (!email.isNullOrBlank()) {
            val safeKey = encodeEmailForFirebase(email)
            "$USER_MOVIES_PATH/$safeKey/movies"
        } else {
            SHARED_MOVIES_PATH
        }
    }

    private suspend fun loadMoviesFromRemote(): List<Movie> {
        return try {
            val snapshot = firebaseDatabase.reference.child(getRemotePath()).get().await()
            if (!snapshot.exists()) return emptyList()
            snapshot.children.mapNotNull { child ->
                child.getValue(FirebaseMovieDto::class.java)?.toMovie()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load movies from Realtime DB", e)
            emptyList()
        }
    }

    private suspend fun saveMoviesToRemote(movies: List<Movie>) {
        try {
            val path = firebaseDatabase.reference.child(getRemotePath())
            if (movies.isEmpty()) {
                path.removeValue().await()
                Log.d(TAG, "Cleared remote movies for user")
            } else {
                val movieMap = movies.associate { it.id.toString() to it.toRemoteMap() }
                path.setValue(movieMap).await()
                Log.d(TAG, "Successfully saved ${movies.size} movies to Realtime DB")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save movies to Realtime DB", e)
            throw e // Пробрасываем исключение для обработки выше
        }
    }

    private suspend fun loadLocalMovies(): List<Movie> {
        return movieDao.getMovies().first().map { it.toMovie() }
    }

    private suspend fun replaceLocalMovies(movies: List<Movie>) {
        movieDao.deleteAllMovies()
        movieDao.insertMovies(movies.map { it.toEntity() })
    }

    private fun mergeLocalAndRemote(localMovies: List<Movie>, remoteMovies: List<Movie>): List<Movie> {
        val mergedById = localMovies.associateBy { it.id }.toMutableMap()
        remoteMovies.forEach { mergedById[it.id] = it }
        return mergedById.values.sortedBy { it.id }
    }

    private data class FirebaseMovieDto(
        var id: Int = 0,
        var title: String? = null,
        var description: String? = null,
        var genre: String? = null,
        var year: Int? = null,
        var isWatched: Boolean = false,
        var isFavorite: Boolean = false,
        var rating: Int? = null,
        var comment: String? = null
    ) {
        fun toMovie(): Movie = Movie(
            id = id,
            title = title.orEmpty(),
            description = description,
            genre = genre,
            year = year,
            isWatched = isWatched,
            isFavorite = isFavorite,
            rating = rating,
            comment = comment
        )
    }

    private fun Movie.toRemoteMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "genre" to genre,
        "year" to year,
        "isWatched" to isWatched,
        "isFavorite" to isFavorite,
        "rating" to rating,
        "comment" to comment
    )
}