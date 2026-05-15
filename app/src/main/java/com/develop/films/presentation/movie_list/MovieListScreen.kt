package com.develop.films.presentation.movie_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import com.develop.films.presentation.movie_list.components.MovieItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.develop.films.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListState,
    onAddMovie: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onEditMovie: (Int) -> Unit,
    onDeleteMovie: (Movie) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Мои фильмы") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Text(text = "+")
            }
        }
    ) { paddingValues ->
        if (state.movies.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Список фильмов пуст",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.movies, key = { it.id }) { movie ->
                    MovieItem(
                        movie = movie,
                        onClick = { onMovieClick(movie.id) },
                        onEdit = { onEditMovie(movie.id) },
                        onDelete = { onDeleteMovie(movie) }
                    )
                }
            }
        }
    }
}
