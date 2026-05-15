package com.develop.films.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
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

@Composable
fun SetupNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.MovieList.route
    ) {
        composable(Screen.MovieList.route) {
            val viewModel: MovieListViewModel = hiltViewModel()
            MovieListScreen(
                state = viewModel.state.collectAsState().value,
                onSelectGenre = { genre -> viewModel.onEvent(MovieListEvent.SelectGenre(genre)) },
                onAddMovie = { navController.navigate(Screen.AddEditMovie.route) },
                onMovieClick = { movieId ->
                    navController.navigate(Screen.MovieDetail.createRoute(movieId))
                },
                onEditMovie = { movieId ->
                    navController.navigate(Screen.AddEditMovie.createRoute(movieId))
                },
                onDeleteMovie = { movie -> viewModel.onEvent(MovieListEvent.DeleteMovie(movie)) }
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
                onEdit = { navController.navigate(Screen.AddEditMovie.createRoute(movieId)) }
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
