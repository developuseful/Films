package com.develop.films.data.local


import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.develop.films.data.local.entity.MovieEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MovieDao {
    @Query("SELECT * FROM movies")
    fun getMovies(): Flow<List<MovieEntity>>

    @Query("SELECT * FROM movies WHERE id = :id")
    suspend fun getMovieById(id: Int): MovieEntity?

    @Query("SELECT * FROM movies WHERE id = :id")
    fun getMovieByIdFlow(id: Int): Flow<MovieEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovie(movie: MovieEntity)

    @Delete
    suspend fun deleteMovie(movie: MovieEntity)
}