package com.example.data.local

import android.content.Context
import android.content.SharedPreferences
import com.example.data.remote.model.TokenPair
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AuthTokenManager(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    private val _isLoggedInState = MutableStateFlow(hasValidToken())
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    private val _currentUserId = MutableStateFlow(getUserId())
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()

    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }

    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    fun hasValidToken(): Boolean {
        val token = getAccessToken()
        return !token.isNullOrBlank()
    }

    fun saveSession(tokens: TokenPair, userId: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, tokens.accessToken)
            .putString(KEY_REFRESH_TOKEN, tokens.refreshToken)
            .putString(KEY_USER_ID, userId)
            .putLong(KEY_SAVED_TIME, System.currentTimeMillis())
            .apply()

        _isLoggedInState.value = true
        _currentUserId.value = userId
    }

    fun clearSession() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .remove(KEY_USER_ID)
            .remove(KEY_SAVED_TIME)
            .apply()

        _isLoggedInState.value = false
        _currentUserId.value = null
    }

    companion object {
        private const val PREFS_NAME = "ava_auth_prefs"
        private const val KEY_ACCESS_TOKEN = "jwt_access_token"
        private const val KEY_REFRESH_TOKEN = "jwt_refresh_token"
        private const val KEY_USER_ID = "auth_user_id"
        private const val KEY_SAVED_TIME = "auth_saved_timestamp"

        @Volatile
        private var INSTANCE: AuthTokenManager? = null

        fun getInstance(context: Context): AuthTokenManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: AuthTokenManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}
