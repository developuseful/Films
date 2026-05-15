package com.develop.films.presentation.movie_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import com.develop.films.presentation.movie_list.components.MovieItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.develop.films.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListState,
    onSelectGenre: (String) -> Unit,
    onAddMovie: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onEditMovie: (Int) -> Unit,
    onDeleteMovie: (Movie) -> Unit
) {
    var isGenreMenuExpanded by remember { mutableStateOf(false) }
    var isFilterVisible by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Мои фильмы") },
                actions = {
                    IconButton(onClick = { isFilterVisible = !isFilterVisible }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтр"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddMovie) {
                Text(text = "+")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AnimatedVisibility(
                visible = state.genreOptions.isNotEmpty() && isFilterVisible,
                enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                    expandVertically(expandFrom = Alignment.Top, animationSpec = tween(durationMillis = 220)),
                exit = fadeOut(animationSpec = tween(durationMillis = 180)) +
                    shrinkVertically(shrinkTowards = Alignment.Top, animationSpec = tween(durationMillis = 180))
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ExposedDropdownMenuBox(
                        expanded = isGenreMenuExpanded,
                        onExpandedChange = { isGenreMenuExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.selectedGenre,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Фильтр жанра") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenreMenuExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                        )

                        ExposedDropdownMenu(
                            expanded = isGenreMenuExpanded,
                            onDismissRequest = { isGenreMenuExpanded = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            state.genreOptions.forEach { genre ->
                                DropdownMenuItem(
                                    text = { Text(text = genre) },
                                    onClick = {
                                        onSelectGenre(genre)
                                        isGenreMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Divider()
                }
            }

            if (state.allMovies.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Список фильмов пуст",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else if (state.movies.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Фильмов не найдено для жанра \"${state.selectedGenre}\"",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
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
}
