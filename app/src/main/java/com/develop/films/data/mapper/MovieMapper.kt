package com.develop.films.data.mapper

import com.develop.films.data.local.entity.MovieEntity
import com.develop.films.domain.model.Movie

fun MovieEntity.toMovie(): Movie = Movie(
    id = id,
    title = title,
    description = description,
    genre = genre,
    year = year,
    isWatched = isWatched,
    rating = rating,
    comment = comment
)

fun Movie.toEntity(): MovieEntity = MovieEntity(
    id = id,
    title = title,
    description = description,
    genre = genre,
    year = year,
    isWatched = isWatched,
    rating = rating,
    comment = comment
)
