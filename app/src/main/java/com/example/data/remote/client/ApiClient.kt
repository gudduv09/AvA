package com.example.data.remote.client

import com.example.data.local.AuthTokenManager
import com.example.data.remote.api.AuthApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {

    private const val BASE_URL = "https://api.ava.mbs.com/"

    private var authApiService: AuthApiService? = null

    fun getAuthApiService(tokenManager: AuthTokenManager): AuthApiService {
        return authApiService ?: synchronized(this) {
            authApiService ?: createAuthApiService(tokenManager).also {
                authApiService = it
            }
        }
    }

    private fun createAuthApiService(tokenManager: AuthTokenManager): AuthApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(MockAuthInterceptor()) // Intercepts auth endpoints for mock/offline reliability
            .addInterceptor(AuthInterceptor(tokenManager))
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()

        return retrofit.create(AuthApiService::class.java)
    }
}
