package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.AuthTokenManager
import com.example.data.remote.client.ApiClient
import com.example.data.repository.AuthRepository
import com.example.data.repository.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class AuthRepositoryTest {

    private lateinit var db: AppDatabase
    private lateinit var tokenManager: AuthTokenManager
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        tokenManager = AuthTokenManager.getInstance(context)
        tokenManager.clearSession()

        val apiService = ApiClient.getAuthApiService(tokenManager)
        repository = AuthRepository(apiService, tokenManager, db.userDao())
    }

    @After
    fun tearDown() {
        tokenManager.clearSession()
        db.close()
    }

    @Test
    fun testSuccessfulLoginPersistsTokenAndUser() = runBlocking {
        val result = repository.login("ava_creator", "ava123")
        assertTrue("Login should succeed", result.isSuccess)

        val authState = repository.authState.first { it.isAuthenticated }
        assertTrue(authState.isAuthenticated)
        assertNotNull(authState.user)
        assertEquals("ava_creator", authState.user?.username)
        assertEquals("AVA Official", authState.user?.displayName)
        assertTrue(tokenManager.hasValidToken())

        // Verify user persisted to Room database
        val userInDb = db.userDao().getUserByIdOnce("user_me")
        assertNotNull(userInDb)
        assertEquals("ava_creator", userInDb?.username)
    }

    @Test
    fun testLoginWithInvalidPasswordFails() = runBlocking {
        val result = repository.login("ava_creator", "wrong_password")
        assertTrue("Login should fail with wrong password", result.isFailure)
        assertFalse(tokenManager.hasValidToken())
    }

    @Test
    fun testRegistrationSucceeds() = runBlocking {
        val result = repository.register(
            username = "new_viber",
            email = "viber@example.com",
            displayName = "New Viber",
            password = "password123"
        )
        assertTrue("Registration should succeed", result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("new_viber", user?.username)
        assertTrue(tokenManager.hasValidToken())
    }

    @Test
    fun testSocialLoginGoogle() = runBlocking {
        val result = repository.socialLogin("google")
        assertTrue("Google sign-in should succeed", result.isSuccess)
        val user = result.getOrNull()
        assertNotNull(user)
        assertEquals("google_vibe", user?.username)
        assertTrue(tokenManager.hasValidToken())
    }

    @Test
    fun testLogoutClearsSessionAndState() = runBlocking {
        // First login
        repository.login("ava_creator", "ava123")
        assertTrue(tokenManager.hasValidToken())

        // Then logout
        repository.logout()
        assertFalse(tokenManager.hasValidToken())
        assertNull(tokenManager.getAccessToken())

        val authState = repository.authState.value
        assertFalse(authState.isAuthenticated)
        assertNull(authState.user)
    }
}
