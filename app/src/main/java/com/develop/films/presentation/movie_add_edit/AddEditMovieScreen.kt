package com.develop.films.presentation.movie_add_edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditMovieScreen(
    movieId: Int,
    state: AddEditMovieState,
    uiEvent: Flow<AddEditMovieViewModel.UiEvent>,
    onEvent: (AddEditMovieEvent) -> Unit,
    onPopBack: () -> Unit
) {
    val scaffoldState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var isGenreMenuExpanded by remember { mutableStateOf(false) }

    val genreOptions = remember {
        listOf(
            "Боевик",
            "Драма",
            "Комедия",
            "Триллер",
            "Фантастика",
            "Мелодрама",
            "Ужасы",
            "Детектив",
            "Анимация",
            "Приключения"
        )
    }

    LaunchedEffect(uiEvent) {
        uiEvent.collectLatest { event ->
            when (event) {
                is AddEditMovieViewModel.UiEvent.SaveMovie -> onPopBack()
                is AddEditMovieViewModel.UiEvent.ShowError -> {
                    coroutineScope.launch {
                        scaffoldState.showSnackbar(event.message)
                    }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = if (movieId > 0) "Редактировать фильм" else "Добавить фильм")
                },
                navigationIcon = {
                    IconButton(onClick = onPopBack) {
                        Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Назад")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = scaffoldState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(innerPadding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { onEvent(AddEditMovieEvent.EnteredTitle(it)) },
                label = { Text("Название") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = state.description,
                onValueChange = { onEvent(AddEditMovieEvent.EnteredDescription(it)) },
                label = { Text("Описание") },
                modifier = Modifier.fillMaxWidth()
            )

            // Контейнер Material 3 для выпадающих списков
            ExposedDropdownMenuBox(
                expanded = isGenreMenuExpanded,
                onExpandedChange = { isGenreMenuExpanded = it },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.genre,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Жанр") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isGenreMenuExpanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        // menuAnchor связывает размеры TextField и выпадающего меню
                        .menuAnchor(MenuAnchorType.PrimaryEditable, enabled = true)
                )

                ExposedDropdownMenu(
                    expanded = isGenreMenuExpanded,
                    onDismissRequest = { isGenreMenuExpanded = false },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    genreOptions.forEach { genre ->
                        DropdownMenuItem(
                            text = { Text(text = genre) },
                            onClick = {
                                onEvent(AddEditMovieEvent.EnteredGenre(genre))
                                isGenreMenuExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.isWatched,
                    onCheckedChange = { onEvent(AddEditMovieEvent.ChangeWatched(it)) }
                )
                Text(
                    text = "Просмотрено",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = state.isFavorite,
                    onCheckedChange = { onEvent(AddEditMovieEvent.ChangeFavorite(it)) }
                )
                Text(
                    text = "Избранное",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = state.year,
                    onValueChange = { onEvent(AddEditMovieEvent.EnteredYear(it)) },
                    label = { Text("Год") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = state.rating,
                    onValueChange = { onEvent(AddEditMovieEvent.EnteredRating(it)) },
                    label = { Text("Рейтинг") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedTextField(
                value = state.comment,
                onValueChange = { onEvent(AddEditMovieEvent.EnteredComment(it)) },
                label = { Text("Комментарий") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = { onEvent(AddEditMovieEvent.SaveMovie) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (state.isExisting) "Сохранить" else "Добавить")
            }
        }
    }
}