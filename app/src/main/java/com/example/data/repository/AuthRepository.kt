package com.example.data.repository

import com.example.R
import com.example.data.local.AuthTokenManager
import com.example.data.local.dao.UserDao
import com.example.data.local.entity.UserEntity
import com.example.data.mock.DemoDataProvider
import com.example.data.remote.api.AuthApiService
import com.example.data.remote.model.LoginRequest
import com.example.data.remote.model.RegisterRequest
import com.example.data.remote.model.SocialLoginRequest
import com.example.data.remote.model.UserDto
import com.example.domain.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.HttpException

data class AuthState(
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val accessToken: String? = null
)

class AuthRepository(
    private val authApiService: AuthApiService,
    private val tokenManager: AuthTokenManager,
    private val userDao: UserDao
) {
    private val _authState = MutableStateFlow(
        AuthState(
            isAuthenticated = tokenManager.hasValidToken(),
            user = if (tokenManager.hasValidToken()) DemoDataProvider.currentUser else null,
            accessToken = tokenManager.getAccessToken()
        )
    )
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.IO).launch {
            if (tokenManager.hasValidToken()) {
                val userId = tokenManager.getUserId()
                if (!userId.isNullOrBlank()) {
                    val localUser = userDao.getUserByIdOnce(userId)
                    if (localUser != null) {
                        _authState.value = _authState.value.copy(
                            isAuthenticated = true,
                            user = localUser.toDomain(),
                            accessToken = tokenManager.getAccessToken()
                        )
                    } else {
                        // Refresh profile from API
                        try {
                            val profileResponse = authApiService.getCurrentUser()
                            val domainUser = profileResponse.user.toDomain()
                            userDao.insertUser(profileResponse.user.toEntity())
                            _authState.value = _authState.value.copy(
                                isAuthenticated = true,
                                user = domainUser,
                                accessToken = tokenManager.getAccessToken()
                            )
                        } catch (_: Exception) {
                            _authState.value = _authState.value.copy(
                                isAuthenticated = true,
                                user = DemoDataProvider.currentUser,
                                accessToken = tokenManager.getAccessToken()
                            )
                        }
                    }
                }
            }
        }
    }

    fun getCurrentUser(): User {
        return _authState.value.user ?: DemoDataProvider.currentUser
    }

    suspend fun login(identifier: String, password: String): Result<User> {
        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        return try {
            val response = authApiService.login(LoginRequest(identifier.trim(), password))
            if (response.success && response.tokens != null && response.user != null) {
                tokenManager.saveSession(response.tokens, response.user.id)
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)
                val domainUser = response.user.toDomain()
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = domainUser,
                    isLoading = false,
                    errorMessage = null,
                    accessToken = response.tokens.accessToken
                )
                Result.success(domainUser)
            } else {
                val error = response.message.ifBlank { "Authentication failed" }
                _authState.value = _authState.value.copy(isLoading = false, errorMessage = error)
                Result.failure(Exception(error))
            }
        } catch (e: HttpException) {
            val errorMsg = parseErrorMessage(e)
            _authState.value = _authState.value.copy(isLoading = false, errorMessage = errorMsg)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Network connection error"
            _authState.value = _authState.value.copy(isLoading = false, errorMessage = errorMsg)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun register(email: String, username: String, displayName: String, password: String): Result<User> {
        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        return try {
            val response = authApiService.register(
                RegisterRequest(
                    email = email.trim(),
                    username = username.trim().removePrefix("@"),
                    displayName = displayName.trim(),
                    password = password
                )
            )
            if (response.success && response.tokens != null && response.user != null) {
                tokenManager.saveSession(response.tokens, response.user.id)
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)
                val domainUser = response.user.toDomain()
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = domainUser,
                    isLoading = false,
                    errorMessage = null,
                    accessToken = response.tokens.accessToken
                )
                Result.success(domainUser)
            } else {
                val error = response.message.ifBlank { "Registration failed" }
                _authState.value = _authState.value.copy(isLoading = false, errorMessage = error)
                Result.failure(Exception(error))
            }
        } catch (e: HttpException) {
            val errorMsg = parseErrorMessage(e)
            _authState.value = _authState.value.copy(isLoading = false, errorMessage = errorMsg)
            Result.failure(Exception(errorMsg))
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Failed to connect to registration API"
            _authState.value = _authState.value.copy(isLoading = false, errorMessage = errorMsg)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun socialLogin(provider: String): Result<User> {
        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)
        return try {
            val idToken = "token_oauth_${provider}_${System.currentTimeMillis()}"
            val response = authApiService.socialLogin(SocialLoginRequest(provider, idToken))
            if (response.success && response.tokens != null && response.user != null) {
                tokenManager.saveSession(response.tokens, response.user.id)
                val userEntity = response.user.toEntity()
                userDao.insertUser(userEntity)
                val domainUser = response.user.toDomain()
                _authState.value = AuthState(
                    isAuthenticated = true,
                    user = domainUser,
                    isLoading = false,
                    errorMessage = null,
                    accessToken = response.tokens.accessToken
                )
                Result.success(domainUser)
            } else {
                val error = response.message.ifBlank { "Social login failed" }
                _authState.value = _authState.value.copy(isLoading = false, errorMessage = error)
                Result.failure(Exception(error))
            }
        } catch (e: Exception) {
            val errorMsg = e.localizedMessage ?: "Social login service unavailable"
            _authState.value = _authState.value.copy(isLoading = false, errorMessage = errorMsg)
            Result.failure(Exception(errorMsg))
        }
    }

    suspend fun logout() {
        try {
            authApiService.logout()
        } catch (_: Exception) {}
        tokenManager.clearSession()
        _authState.value = AuthState(
            isAuthenticated = false,
            user = null,
            isLoading = false,
            errorMessage = null,
            accessToken = null
        )
    }

    fun continueAsGuest() {
        _authState.value = AuthState(
            isAuthenticated = false,
            user = DemoDataProvider.currentUser.copy(displayName = "Guest Viewer", username = "guest"),
            isLoading = false,
            errorMessage = null,
            accessToken = null
        )
    }

    private fun parseErrorMessage(e: HttpException): String {
        return try {
            val errorBody = e.response()?.errorBody()?.string()
            if (!errorBody.isNullOrBlank()) {
                val json = JSONObject(errorBody)
                json.optString("message", json.optString("error", "Request failed with code ${e.code()}"))
            } else {
                "Server returned status ${e.code()}"
            }
        } catch (_: Exception) {
            "Network error: ${e.code()}"
        }
    }

    private fun UserDto.toDomain(): User {
        return User(
            id = id,
            username = username,
            displayName = displayName,
            avatarDrawableRes = when {
                username.contains("pinkwave") -> R.drawable.ava_thumb_2
                username.contains("urban") -> R.drawable.ava_thumb_1
                username.contains("daily") -> R.drawable.ava_thumb_3
                username.contains("summit") -> R.drawable.ava_thumb_4
                else -> R.drawable.ava_icon_foreground
            },
            bio = bio,
            website = website,
            followersCount = followersCount,
            followingCount = followingCount,
            likesCount = likesCount,
            isVerified = isVerified,
            isFollowing = isFollowing,
            isPrivate = isPrivate
        )
    }

    private fun UserDto.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            username = username,
            displayName = displayName,
            avatarDrawableRes = when {
                username.contains("pinkwave") -> R.drawable.ava_thumb_2
                username.contains("urban") -> R.drawable.ava_thumb_1
                username.contains("daily") -> R.drawable.ava_thumb_3
                username.contains("summit") -> R.drawable.ava_thumb_4
                else -> R.drawable.ava_icon_foreground
            },
            bio = bio,
            website = website,
            followersCount = followersCount,
            followingCount = followingCount,
            likesCount = likesCount,
            isVerified = isVerified,
            isFollowing = isFollowing,
            isPrivate = isPrivate
        )
    }
}
