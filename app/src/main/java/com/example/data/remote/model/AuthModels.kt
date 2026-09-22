package com.example.data.remote.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequest(
    @Json(name = "identifier") val identifier: String, // email or username
    @Json(name = "password") val password: String,
    @Json(name = "device_id") val deviceId: String = "android_device_mbs"
)

@JsonClass(generateAdapter = true)
data class RegisterRequest(
    @Json(name = "email") val email: String,
    @Json(name = "username") val username: String,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SocialLoginRequest(
    @Json(name = "provider") val provider: String, // "google", "apple"
    @Json(name = "id_token") val idToken: String
)

@JsonClass(generateAdapter = true)
data class RefreshTokenRequest(
    @Json(name = "refresh_token") val refreshToken: String
)

@JsonClass(generateAdapter = true)
data class TokenPair(
    @Json(name = "access_token") val accessToken: String,
    @Json(name = "refresh_token") val refreshToken: String,
    @Json(name = "expires_in") val expiresIn: Long = 3600, // seconds
    @Json(name = "token_type") val tokenType: String = "Bearer"
)

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: String,
    @Json(name = "username") val username: String,
    @Json(name = "display_name") val displayName: String,
    @Json(name = "email") val email: String = "",
    @Json(name = "avatar_url") val avatarUrl: String? = null,
    @Json(name = "bio") val bio: String = "",
    @Json(name = "website") val website: String = "",
    @Json(name = "followers_count") val followersCount: Int = 0,
    @Json(name = "following_count") val followingCount: Int = 0,
    @Json(name = "likes_count") val likesCount: Int = 0,
    @Json(name = "is_verified") val isVerified: Boolean = false,
    @Json(name = "is_following") val isFollowing: Boolean = false,
    @Json(name = "is_private") val isPrivate: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "message") val message: String,
    @Json(name = "tokens") val tokens: TokenPair? = null,
    @Json(name = "user") val user: UserDto? = null
)

@JsonClass(generateAdapter = true)
data class UserProfileResponse(
    @Json(name = "success") val success: Boolean,
    @Json(name = "user") val user: UserDto
)

@JsonClass(generateAdapter = true)
data class ApiErrorResponse(
    @Json(name = "error") val error: String,
    @Json(name = "message") val message: String,
    @Json(name = "status_code") val statusCode: Int
)
