package com.example.data.remote.client

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import org.json.JSONObject

class MockAuthInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val url = request.url.toString()
        val method = request.method

        // If not targeting our mockable AVA domain, proceed normally
        if (!url.contains("api.ava.mbs.com") && !url.contains("localhost") && !url.contains("api/v1/auth")) {
            return chain.proceed(request)
        }

        // Simulate network latency (250-400ms)
        try {
            Thread.sleep(300)
        } catch (_: InterruptedException) {}

        val path = request.url.encodedPath

        return when {
            path.endsWith("/login") && method == "POST" -> handleLogin(request)
            path.endsWith("/register") && method == "POST" -> handleRegister(request)
            path.endsWith("/social-login") && method == "POST" -> handleSocialLogin(request)
            path.endsWith("/refresh") && method == "POST" -> handleRefresh(request)
            path.endsWith("/me") && method == "GET" -> handleGetMe(request)
            path.endsWith("/logout") && method == "POST" -> handleLogout(request)
            else -> chain.proceed(request)
        }
    }

    private fun handleLogin(request: okhttp3.Request): Response {
        val bodyString = extractRequestBody(request)
        val json = try { JSONObject(bodyString) } catch (e: Exception) { JSONObject() }
        val identifier = json.optString("identifier", "")
        val password = json.optString("password", "")

        if (identifier.isBlank() || password.isBlank()) {
            return createJsonResponse(
                request = request,
                code = 400,
                json = """
                    {
                        "error": "BAD_REQUEST",
                        "message": "Email/username and password are required",
                        "status_code": 400
                    }
                """.trimIndent()
            )
        }

        if (password.length < 4 || password.contains("wrong") || password.contains("invalid")) {
            return createJsonResponse(
                request = request,
                code = 401,
                json = """
                    {
                        "error": "INVALID_CREDENTIALS",
                        "message": "Invalid username or password. Please check your credentials.",
                        "status_code": 401
                    }
                """.trimIndent()
            )
        }

        val cleanIdentifier = identifier.trim().removePrefix("@")
        val isVerified = cleanIdentifier.contains("ava") || cleanIdentifier.contains("official") || cleanIdentifier == "pinkwave"
        val displayName = if (cleanIdentifier.equals("ava_creator", ignoreCase = true)) {
            "AVA Official"
        } else if (cleanIdentifier.equals("pinkwave", ignoreCase = true)) {
            "Pink Wave 🌊"
        } else {
            cleanIdentifier.replaceFirstChar { it.uppercase() } + " Vibe"
        }

        val userId = if (cleanIdentifier.equals("ava_creator", ignoreCase = true)) "user_me" else "user_${cleanIdentifier.lowercase()}"
        val accessToken = generateMockJwt(userId, cleanIdentifier)
        val refreshToken = "rf_${System.currentTimeMillis()}_${(1000..9999).random()}"

        return createJsonResponse(
            request = request,
            code = 200,
            json = """
                {
                    "success": true,
                    "message": "Authentication successful",
                    "tokens": {
                        "access_token": "$accessToken",
                        "refresh_token": "$refreshToken",
                        "expires_in": 3600,
                        "token_type": "Bearer"
                    },
                    "user": {
                        "id": "$userId",
                        "username": "$cleanIdentifier",
                        "display_name": "$displayName",
                        "email": "${cleanIdentifier.lowercase()}@ava.mbs.com",
                        "avatar_url": null,
                        "bio": "Creator on AVA • Watch. Create. Vibe. ✨",
                        "website": "https://mbs.com/$cleanIdentifier",
                        "followers_count": 14200,
                        "following_count": 186,
                        "likes_count": 89400,
                        "is_verified": $isVerified,
                        "is_following": false,
                        "is_private": false
                    }
                }
            """.trimIndent()
        )
    }

    private fun handleRegister(request: okhttp3.Request): Response {
        val bodyString = extractRequestBody(request)
        val json = try { JSONObject(bodyString) } catch (e: Exception) { JSONObject() }
        val email = json.optString("email", "")
        val username = json.optString("username", "")
        val displayName = json.optString("display_name", "")
        val password = json.optString("password", "")

        if (email.isBlank() || username.isBlank() || password.isBlank()) {
            return createJsonResponse(
                request = request,
                code = 400,
                json = """
                    {
                        "error": "VALIDATION_FAILED",
                        "message": "Email, username and password are required fields",
                        "status_code": 400
                    }
                """.trimIndent()
            )
        }

        val cleanUsername = username.trim().removePrefix("@").lowercase()
        val userId = "user_${cleanUsername}_${System.currentTimeMillis().toString().takeLast(4)}"
        val finalDisplayName = if (displayName.isNotBlank()) displayName else cleanUsername.replaceFirstChar { it.uppercase() }
        val accessToken = generateMockJwt(userId, cleanUsername)
        val refreshToken = "rf_${System.currentTimeMillis()}_${(1000..9999).random()}"

        return createJsonResponse(
            request = request,
            code = 201,
            json = """
                {
                    "success": true,
                    "message": "Account created successfully",
                    "tokens": {
                        "access_token": "$accessToken",
                        "refresh_token": "$refreshToken",
                        "expires_in": 3600,
                        "token_type": "Bearer"
                    },
                    "user": {
                        "id": "$userId",
                        "username": "$cleanUsername",
                        "display_name": "$finalDisplayName",
                        "email": "$email",
                        "avatar_url": null,
                        "bio": "New creator on AVA! 🚀",
                        "website": "https://ava.mbs.com/$cleanUsername",
                        "followers_count": 0,
                        "following_count": 0,
                        "likes_count": 0,
                        "is_verified": false,
                        "is_following": false,
                        "is_private": false
                    }
                }
            """.trimIndent()
        )
    }

    private fun handleSocialLogin(request: okhttp3.Request): Response {
        val bodyString = extractRequestBody(request)
        val json = try { JSONObject(bodyString) } catch (e: Exception) { JSONObject() }
        val provider = json.optString("provider", "google")
        val userId = "user_social_${provider}_${(1000..9999).random()}"
        val username = if (provider.equals("apple", true)) "apple_creator" else "google_vibe"
        val displayName = if (provider.equals("apple", true)) "Apple ID User" else "Google Verified Creator"
        val accessToken = generateMockJwt(userId, username)
        val refreshToken = "rf_${System.currentTimeMillis()}_${(1000..9999).random()}"

        return createJsonResponse(
            request = request,
            code = 200,
            json = """
                {
                    "success": true,
                    "message": "Signed in with ${provider.replaceFirstChar { it.uppercase() }}",
                    "tokens": {
                        "access_token": "$accessToken",
                        "refresh_token": "$refreshToken",
                        "expires_in": 3600,
                        "token_type": "Bearer"
                    },
                    "user": {
                        "id": "$userId",
                        "username": "$username",
                        "display_name": "$displayName",
                        "email": "$username@gmail.com",
                        "avatar_url": null,
                        "bio": "Connected via ${provider.replaceFirstChar { it.uppercase() }} on AVA",
                        "website": "",
                        "followers_count": 120,
                        "following_count": 35,
                        "likes_count": 480,
                        "is_verified": true,
                        "is_following": false,
                        "is_private": false
                    }
                }
            """.trimIndent()
        )
    }

    private fun handleRefresh(request: okhttp3.Request): Response {
        val newAccessToken = generateMockJwt("user_me", "ava_creator")
        val newRefreshToken = "rf_${System.currentTimeMillis()}_renewed"
        return createJsonResponse(
            request = request,
            code = 200,
            json = """
                {
                    "success": true,
                    "message": "Token renewed",
                    "tokens": {
                        "access_token": "$newAccessToken",
                        "refresh_token": "$newRefreshToken",
                        "expires_in": 3600,
                        "token_type": "Bearer"
                    },
                    "user": null
                }
            """.trimIndent()
        )
    }

    private fun handleGetMe(request: okhttp3.Request): Response {
        val authHeader = request.header("Authorization")
        if (authHeader.isNullOrBlank() || !authHeader.startsWith("Bearer ")) {
            return createJsonResponse(
                request = request,
                code = 401,
                json = """
                    {
                        "error": "UNAUTHORIZED",
                        "message": "Bearer access token is missing or expired",
                        "status_code": 401
                    }
                """.trimIndent()
            )
        }

        return createJsonResponse(
            request = request,
            code = 200,
            json = """
                {
                    "success": true,
                    "user": {
                        "id": "user_me",
                        "username": "ava_creator",
                        "display_name": "AVA Official",
                        "email": "creator@ava.mbs.com",
                        "avatar_url": null,
                        "bio": "Creating the future of short-form video on AVA ✨ MBS Group",
                        "website": "https://mbsgroup.example.com",
                        "followers_count": 14200,
                        "following_count": 186,
                        "likes_count": 89400,
                        "is_verified": true,
                        "is_following": false,
                        "is_private": false
                    }
                }
            """.trimIndent()
        )
    }

    private fun handleLogout(request: okhttp3.Request): Response {
        return createJsonResponse(
            request = request,
            code = 200,
            json = """
                {
                    "success": true,
                    "message": "Logged out successfully"
                }
            """.trimIndent()
        )
    }

    private fun extractRequestBody(request: okhttp3.Request): String {
        return try {
            val copy = request.newBuilder().build()
            val buffer = okio.Buffer()
            copy.body?.writeTo(buffer)
            buffer.readUtf8()
        } catch (e: Exception) {
            ""
        }
    }

    private fun generateMockJwt(userId: String, username: String): String {
        val header = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
        val exp = (System.currentTimeMillis() / 1000) + 3600
        val payloadRaw = """{"sub":"$userId","name":"$username","exp":$exp,"iss":"mbs-ava-auth"}"""
        val payload = android.util.Base64.encodeToString(payloadRaw.toByteArray(), android.util.Base64.NO_WRAP or android.util.Base64.URL_SAFE)
        val signature = "mbs_ava_sig_" + (100000..999999).random()
        return "$header.$payload.$signature"
    }

    private fun createJsonResponse(request: okhttp3.Request, code: Int, json: String): Response {
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code in 200..299) "OK" else "Error")
            .body(json.toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json; charset=utf-8")
            .build()
    }
}
