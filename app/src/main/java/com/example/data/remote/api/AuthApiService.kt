package com.example.data.remote.api

import com.example.data.remote.model.AuthResponse
import com.example.data.remote.model.LoginRequest
import com.example.data.remote.model.RefreshTokenRequest
import com.example.data.remote.model.RegisterRequest
import com.example.data.remote.model.SocialLoginRequest
import com.example.data.remote.model.UserProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApiService {

    @POST("api/v1/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): AuthResponse

    @POST("api/v1/auth/social-login")
    suspend fun socialLogin(
        @Body request: SocialLoginRequest
    ): AuthResponse

    @POST("api/v1/auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): AuthResponse

    @GET("api/v1/auth/me")
    suspend fun getCurrentUser(): UserProfileResponse

    @POST("api/v1/auth/logout")
    suspend fun logout(): AuthResponse
}
