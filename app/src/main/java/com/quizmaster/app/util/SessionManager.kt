package com.quizmaster.app.util

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Lightweight session store backed by SharedPreferences. */
@Singleton
class SessionManager @Inject constructor(@ApplicationContext ctx: Context) {

    private val prefs: SharedPreferences =
        ctx.getSharedPreferences("quizmaster_session", Context.MODE_PRIVATE)

    var currentUserId: Int
        get() = prefs.getInt(KEY_USER_ID, -1)
        set(value) = prefs.edit().putInt(KEY_USER_ID, value).apply()

    var currentUserRole: String
        get() = prefs.getString(KEY_ROLE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_ROLE, value).apply()

    var displayName: String
        get() = prefs.getString(KEY_DISPLAY_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_DISPLAY_NAME, value).apply()

    val isLoggedIn: Boolean get() = currentUserId != -1

    fun logout() = prefs.edit().clear().apply()

    companion object {
        private const val KEY_USER_ID = "user_id"
        private const val KEY_ROLE = "role"
        private const val KEY_DISPLAY_NAME = "display_name"
    }
}
