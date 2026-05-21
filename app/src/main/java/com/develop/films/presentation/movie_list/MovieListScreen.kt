package com.develop.films.presentation.movie_list

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.develop.films.presentation.movie_list.components.MovieItem
import com.develop.films.presentation.movie_list.MovieListTab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign.Companion.Center
import androidx.compose.ui.unit.dp
import com.develop.films.domain.model.Movie
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieListScreen(
    state: MovieListState,
    onSelectGenre: (String) -> Unit,
    onSelectTab: (MovieListTab) -> Unit,
    onAddMovie: () -> Unit,
    onSettingsClick: () -> Unit,
    onMovieClick: (Int) -> Unit,
    onEditMovie: (Int) -> Unit,
    onDeleteMovie: (Movie) -> Unit
) {
    var isGenreMenuExpanded by remember { mutableStateOf(false) }
    var isFilterVisible by remember { mutableStateOf(false) }
    var showRandomDialog by remember { mutableStateOf(false) }
    var randomMovie by remember { mutableStateOf<Movie?>(null) }
    var isRandomLoading by remember { mutableStateOf(false) }
    val randomScope = rememberCoroutineScope()

    fun loadRandomMovie(filteredMovies: List<Movie>) {
        randomScope.launch {
            isRandomLoading = true
            showRandomDialog = true
            randomMovie = null
            delay(1800)
            randomMovie = filteredMovies.shuffled().firstOrNull()
            isRandomLoading = false
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Мои фильмы") },
                actions = {
                    IconButton(onClick = { isFilterVisible = !isFilterVisible }) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Фильтр"
                        )
                    }
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                PrimaryTabRow(
                    selectedTabIndex = state.selectedTab.ordinal,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    MovieListTab.values().forEach { tab ->
                        Tab(
                            selected = state.selectedTab == tab,
                            onClick = { onSelectTab(tab) },
                            text = { Text(tab.title) }
                        )
                    }
                }

                AnimatedVisibility(
                    visible = state.genreOptions.isNotEmpty() && isFilterVisible,
                    enter = fadeIn(animationSpec = tween(durationMillis = 220)) +
                            expandVertically(
                                expandFrom = Alignment.Top,
                                animationSpec = tween(durationMillis = 220)
                            ),
                    exit = fadeOut(animationSpec = tween(durationMillis = 180)) +
                            shrinkVertically(
                                shrinkTowards = Alignment.Top,
                                animationSpec = tween(durationMillis = 180)
                            )
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
                                    .padding(horizontal = 12.dp)
                                    .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, enabled = true)
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

                    }
                }

                if (state.allMovies.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 42.dp),
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
                            .fillMaxSize()
                            .padding(horizontal = 42.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Фильмов не найдено для жанра \"${state.selectedGenre}\" и вкладки \"${state.selectedTab.title}\"",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = Center
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

            Surface(
                shape = RoundedCornerShape(28.dp),
                tonalElevation = 8.dp,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 16.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { loadRandomMovie(state.movies) }) {
                        Icon(
                            imageVector = Icons.Default.Shuffle,
                            contentDescription = "Случайный фильм"
                        )
                    }
                    IconButton(onClick = onAddMovie) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Добавить фильм"
                        )
                    }
                }
            }

            if (showRandomDialog) {
                AlertDialog(
                    onDismissRequest = { showRandomDialog = false },
                    title = { Text(text = "Рандомный фильм") },
                    text = {
                        if (isRandomLoading) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                CircularProgressIndicator()
                                Spacer(modifier = Modifier.padding(12.dp))
                                Text(
                                    text = "Ищем фильм...",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        } else {
                            randomMovie?.let { movie ->
                                Column {
                                    Text(text = movie.title, style = MaterialTheme.typography.titleMedium)
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Text(
                                        text = movie.genre.orEmpty().takeIf { it.isNotBlank() }?.let { "Жанр: $it" } ?: "Жанр неизвестен",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.padding(4.dp))
                                    Text(
                                        text = movie.year?.let { "Год: $it" } ?: "Год неизвестен",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Spacer(modifier = Modifier.padding(8.dp))
                                    Text(
                                        text = movie.description.orEmpty().ifBlank { "Описание отсутствует." },
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            } ?: Text(
                                text = "Нет доступных фильмов для выбора.",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    },
                    confirmButton = {
                        if (!isRandomLoading) {
                            TextButton(onClick = {
                                loadRandomMovie(state.movies)
                            }) {
                                Text(text = "Следующий фильм")
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showRandomDialog = false }) {
                            Text(text = "Закрыть")
                        }
                    }
                )
            }
        }
    }
}


