package com.develop.films.presentation.movie_detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.develop.films.domain.model.Movie

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    state: MovieDetailState,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    onToggleWatched: (Boolean) -> Unit,
    onToggleFavorite: (Boolean) -> Unit
) {

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.errorMessage != null -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = state.errorMessage,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
                state.movie != null -> {
                    MovieDetailContent(
                        movie = state.movie,
                        onEdit = onEdit,
                        onToggleWatched = onToggleWatched,
                        onToggleFavorite = onToggleFavorite
                    )
                }
            }
        }
    }
}

@Composable
private fun MovieDetailContent(
    movie: Movie,
    onEdit: () -> Unit,
    onToggleWatched: (Boolean) -> Unit,
    onToggleFavorite: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Text(
            text = movie.title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )


        Text(
            text = if (movie.genre != null) "Жанр: ${movie.genre}" else "Жанр: - ",
            style = MaterialTheme.typography.bodyLarge
        )

        Text(
            text = if (movie.year != null) "Год: ${movie.year}" else "Год: - ",
            style = MaterialTheme.typography.bodyLarge
        )


        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = movie.isWatched,
                onCheckedChange = onToggleWatched
            )
            Text(
                text = if (movie.isWatched) "Просмотрено" else "Не просмотрено",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Checkbox(
                checked = movie.isFavorite,
                onCheckedChange = onToggleFavorite
            )
            Text(
                text = if (movie.isFavorite) "Избранное" else "Не в избранном",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Divider()

        Text(text = "Описание", style = MaterialTheme.typography.titleMedium)
        Text(
            text = movie.description.orEmpty().ifBlank { "Описание отсутствует" },
            style = MaterialTheme.typography.bodyMedium
        )

        Divider()

        Text(text = "Комментарий", style = MaterialTheme.typography.titleMedium)
        Text(
            text = movie.comment.orEmpty().ifBlank { "Комментарий отсутствует" },
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onEdit,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Изменить заметку о фильме")
        }
    }
}
