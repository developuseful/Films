package com.develop.films.presentation.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import com.develop.films.util.UserPreferences
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.develop.films.presentation.movie_add_edit.AddEditMovieScreen
import com.develop.films.presentation.movie_add_edit.AddEditMovieViewModel
import com.develop.films.presentation.movie_detail.MovieDetailScreen
import com.develop.films.presentation.movie_detail.MovieDetailViewModel
import com.develop.films.presentation.movie_list.MovieListEvent
import com.develop.films.presentation.movie_list.MovieListScreen
import com.develop.films.presentation.movie_list.MovieListViewModel
import com.develop.films.presentation.settings.SettingsScreen
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.develop.films.firebase.FirebaseHelper
import com.google.android.gms.common.api.ApiException

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val startDestination = remember {
        if (UserPreferences.isLoggedIn(context)) {
            Screen.MovieList.route
        } else {
            Screen.Login.route
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.MovieList.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onContinueWithoutAccount = {
                    navController.navigate(Screen.MovieList.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Screen.MovieList.route) {
            val viewModel: MovieListViewModel = hiltViewModel()

            MovieListScreen(
                state = viewModel.state.collectAsState().value,
                onSelectGenre = { genre -> viewModel.onEvent(MovieListEvent.SelectGenre(genre)) },
                onSelectTab = { tab -> viewModel.onEvent(MovieListEvent.SelectTab(tab)) },
                onAddMovie = { navController.navigate(Screen.AddEditMovie.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
                onEditMovie = { movieId ->
                    navController.navigate(Screen.AddEditMovie.createRoute(movieId))
                },
                onDeleteMovie = { movie -> viewModel.onEvent(MovieListEvent.DeleteMovie(movie)) }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack = { navController.popBackStack() },
                onSignOut = {
                    FirebaseHelper.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Login.route) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onSignInSuccess = { }
            )
        }

        composable(
            route = "${Screen.MovieDetail.route}/{movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val viewModel: MovieDetailViewModel = hiltViewModel()
            MovieDetailScreen(
                state = viewModel.state.collectAsState().value,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(Screen.AddEditMovie.createRoute(movieId)) },
                onToggleWatched = { isWatched -> viewModel.onToggleWatched(isWatched) },
                onToggleFavorite = { isFavorite -> viewModel.onToggleFavorite(isFavorite) }
            )
        }

        composable(
            route = "${Screen.AddEditMovie.route}?movieId={movieId}",
            arguments = listOf(
                navArgument("movieId") {
                    type = NavType.IntType
                    defaultValue = 0
                }
            )
        ) { backStackEntry ->
            val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
            val viewModel: AddEditMovieViewModel = hiltViewModel()
            AddEditMovieScreen(
                movieId = movieId,
                state = viewModel.state.collectAsState().value,
                uiEvent = viewModel.uiEvent,
                onEvent = viewModel::onEvent,
                onPopBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onContinueWithoutAccount: () -> Unit
) {
    val context = LocalContext.current
    var errorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    val googleSignInClient = remember(context) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build()
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    UserPreferences.saveGoogleAccount(
                        context,
                        account.displayName.orEmpty(),
                        account.email.orEmpty()
                    )
                    onLoginSuccess()
                } else {
                    errorMessage = "Не удалось получить аккаунт Google"
                }
            } catch (e: ApiException) {
                errorMessage = "Ошибка входа: ${e.statusCode}"
            }
        } else {
            errorMessage = "Вход отменён"
        }
    }

    var showContinueWithoutAccountDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            onLoginSuccess()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Войдите через Google",
                style = MaterialTheme.typography.headlineMedium,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Button(
                onClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            ) {
                Text(text = "Войти через Google")
            }
            Spacer(modifier = Modifier.padding(12.dp))
            Text(
                text = "Продолжить без аккаунта можно, но данные будут храниться только на устройстве. Если телефон потеряется или сломается, восстановить список будет нельзя.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.padding(12.dp))
            Button(
                onClick = { showContinueWithoutAccountDialog = true }
            ) {
                Text(text = "Продолжить без аккаунта")
            }
            if (showContinueWithoutAccountDialog) {
                AlertDialog(
                    onDismissRequest = { showContinueWithoutAccountDialog = false },
                    title = { Text(text = "Продолжить без аккаунта") },
                    text = {
                        Text(
                            text = "Вы используете локальный режим. В случае потери, кражи или поломки телефона список нельзя будет восстановить.",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    confirmButton = {
                        TextButton(onClick = {
                            showContinueWithoutAccountDialog = false
                            UserPreferences.saveLocalMode(context)
                            onContinueWithoutAccount()
                        }) {
                            Text(text = "Продолжить")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showContinueWithoutAccountDialog = false }) {
                            Text(text = "Отмена")
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.padding(8.dp))
            if (!errorMessage.isNullOrBlank()) {
                Text(
                    text = errorMessage ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
