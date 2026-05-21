package com.develop.films.presentation.navigation

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object MovieList : Screen("movie_list")
    object MovieDetail : Screen("movie_detail") {
        fun createRoute(movieId: Int): String = "${route}/$movieId"
    }
    object AddEditMovie : Screen("add_edit_movie") {
        fun createRoute(movieId: Int? = null): String {
            return if (movieId != null && movieId > 0) {
                "${route}?movieId=$movieId"
            } else {
                route
            }
        }
    }

    object Settings : Screen("settings")
}
