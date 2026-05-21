package com.develop.films.util

import android.content.Context

object UserPreferences {
    private const val PREFS_NAME = "films_prefs"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_IS_LOCAL_MODE = "is_local_mode"
    private const val KEY_USER_NAME = "user_name"
    private const val KEY_USER_EMAIL = "user_email"

    private fun prefs(context: Context) =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun isLoggedIn(context: Context): Boolean = prefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
    fun isLocalMode(context: Context): Boolean = prefs(context).getBoolean(KEY_IS_LOCAL_MODE, false)
    fun getUserName(context: Context): String? = prefs(context).getString(KEY_USER_NAME, null)
    fun getUserEmail(context: Context): String? = prefs(context).getString(KEY_USER_EMAIL, null)

    fun saveGoogleAccount(context: Context, name: String, email: String) {
        prefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_LOCAL_MODE, false)
            putString(KEY_USER_NAME, name)
            putString(KEY_USER_EMAIL, email)
            apply()
        }
    }

    fun saveLocalMode(context: Context) {
        prefs(context).edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putBoolean(KEY_IS_LOCAL_MODE, true)
            remove(KEY_USER_NAME)
            remove(KEY_USER_EMAIL)
            apply()
        }
    }

    fun clear(context: Context) {
        prefs(context).edit().clear().apply()
    }
}
