package com.example.data.remote.client

import com.example.data.local.AuthTokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val tokenManager: AuthTokenManager) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val accessToken = tokenManager.getAccessToken()

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept", "application/json")
            .header("User-Agent", "AVA-Android/1.0.0 (MBS Group)")

        if (!accessToken.isNullOrBlank()) {
            requestBuilder.header("Authorization", "Bearer $accessToken")
        }

        return chain.proceed(requestBuilder.build())
    }
}
