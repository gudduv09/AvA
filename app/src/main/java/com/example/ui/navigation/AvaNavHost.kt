package com.example.ui.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.example.data.local.AppDatabase
import com.example.data.local.AuthTokenManager
import com.example.data.remote.client.ApiClient
import com.example.data.repository.AuthRepository
import com.example.data.repository.InteractionRepository
import com.example.data.repository.MessagingRepository
import com.example.data.repository.UserRepository
import com.example.data.repository.VideoRepository
import com.example.ui.components.AvaBottomNav
import com.example.ui.components.AvaSplashScreen
import com.example.ui.components.NavItem
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.AuthViewModel
import com.example.ui.screens.create.CreateCameraScreen
import com.example.ui.screens.create.CreateViewModel
import com.example.ui.screens.create.PostPublishScreen
import com.example.ui.screens.create.VideoEditorScreen
import com.example.ui.screens.creator.CreatorDashboardScreen
import com.example.ui.screens.discover.DiscoverScreen
import com.example.ui.screens.discover.DiscoverViewModel
import com.example.ui.screens.feed.FeedScreen
import com.example.ui.screens.feed.FeedViewModel
import com.example.ui.screens.inbox.InboxScreen
import com.example.ui.screens.inbox.InboxViewModel
import com.example.ui.screens.onboarding.OnboardingScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.profile.ProfileViewModel
import com.example.ui.screens.settings.SettingsScreen
import com.example.ui.theme.AvaBackground

enum class Screen {
    SPLASH,
    ONBOARDING,
    AUTH,
    MAIN_NAV,
    CREATE_CAMERA,
    CREATE_EDITOR,
    CREATE_PUBLISH,
    CREATOR_DASHBOARD,
    SETTINGS
}

@Composable
fun AvaApp(
    database: AppDatabase
) {
    val context = LocalContext.current

    // Local & Remote Auth Infrastructure
    val tokenManager = remember { AuthTokenManager.getInstance(context) }
    val authApiService = remember { ApiClient.getAuthApiService(tokenManager) }
    val authRepository = remember { AuthRepository(authApiService, tokenManager, database.userDao()) }

    // Repositories
    val videoRepository = remember { VideoRepository(database) }
    val userRepository = remember { UserRepository(database) }
    val interactionRepository = remember { InteractionRepository(database) }
    val messagingRepository = remember { MessagingRepository(database) }

    // ViewModels
    val authViewModel = remember { AuthViewModel(authRepository) }
    val feedViewModel = remember { FeedViewModel(videoRepository, userRepository, interactionRepository) }
    val discoverViewModel = remember { DiscoverViewModel(videoRepository, userRepository) }
    val createViewModel = remember { CreateViewModel(videoRepository, userRepository) }
    val inboxViewModel = remember { InboxViewModel(interactionRepository, messagingRepository) }
    val profileViewModel = remember { ProfileViewModel(videoRepository, userRepository, authRepository) }

    var currentScreen by remember { mutableStateOf(Screen.SPLASH) }
    var selectedNavItem by remember { mutableStateOf(NavItem.HOME) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
    ) {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                Screen.SPLASH -> {
                    AvaSplashScreen(
                        onAnimationFinished = {
                            currentScreen = Screen.ONBOARDING
                        }
                    )
                }
                Screen.ONBOARDING -> {
                    OnboardingScreen(
                        onFinishOnboarding = {
                            currentScreen = Screen.MAIN_NAV
                        },
                        onNavigateToAuth = {
                            currentScreen = Screen.AUTH
                        }
                    )
                }
                Screen.AUTH -> {
                    AuthScreen(
                        viewModel = authViewModel,
                        onAuthSuccess = {
                            currentScreen = Screen.MAIN_NAV
                        },
                        onDismiss = {
                            currentScreen = Screen.MAIN_NAV
                        }
                    )
                }
                Screen.MAIN_NAV -> {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Current Tab Screen
                        when (selectedNavItem) {
                            NavItem.HOME -> {
                                FeedScreen(
                                    viewModel = feedViewModel,
                                    onNavigateToSearch = {
                                        selectedNavItem = NavItem.DISCOVER
                                    },
                                    onNavigateToCreatorProfile = { _ ->
                                        selectedNavItem = NavItem.PROFILE
                                    },
                                    onUseSound = { soundTitle ->
                                        createViewModel.selectSound(soundTitle)
                                        currentScreen = Screen.CREATE_CAMERA
                                    }
                                )
                            }
                            NavItem.DISCOVER -> {
                                DiscoverScreen(
                                    viewModel = discoverViewModel,
                                    onNavigateToVideo = { _ ->
                                        selectedNavItem = NavItem.HOME
                                    },
                                    onNavigateToProfile = { _ ->
                                        selectedNavItem = NavItem.PROFILE
                                    }
                                )
                            }
                            NavItem.CREATE -> {
                                // Handled via full screen modal
                            }
                            NavItem.INBOX -> {
                                InboxScreen(
                                    viewModel = inboxViewModel
                                )
                            }
                            NavItem.PROFILE -> {
                                ProfileScreen(
                                    viewModel = profileViewModel,
                                    onNavigateToSettings = { currentScreen = Screen.SETTINGS },
                                    onNavigateToDashboard = { currentScreen = Screen.CREATOR_DASHBOARD },
                                    onNavigateToVideo = { _ -> selectedNavItem = NavItem.HOME },
                                    onNavigateToAuth = { currentScreen = Screen.AUTH }
                                )
                            }
                        }

                        // Bottom Navigation Bar
                        AvaBottomNav(
                            selectedItem = selectedNavItem,
                            onItemSelected = { item ->
                                if (item == NavItem.CREATE) {
                                    currentScreen = Screen.CREATE_CAMERA
                                } else {
                                    selectedNavItem = item
                                }
                            },
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
                Screen.CREATE_CAMERA -> {
                    CreateCameraScreen(
                        viewModel = createViewModel,
                        onClose = { currentScreen = Screen.MAIN_NAV },
                        onNavigateToEditor = { currentScreen = Screen.CREATE_EDITOR }
                    )
                }
                Screen.CREATE_EDITOR -> {
                    VideoEditorScreen(
                        viewModel = createViewModel,
                        onBack = { currentScreen = Screen.CREATE_CAMERA },
                        onNavigateToPublish = { currentScreen = Screen.CREATE_PUBLISH }
                    )
                }
                Screen.CREATE_PUBLISH -> {
                    PostPublishScreen(
                        viewModel = createViewModel,
                        onBack = { currentScreen = Screen.CREATE_EDITOR },
                        onPublished = {
                            selectedNavItem = NavItem.HOME
                            currentScreen = Screen.MAIN_NAV
                        }
                    )
                }
                Screen.CREATOR_DASHBOARD -> {
                    CreatorDashboardScreen(
                        onBack = { currentScreen = Screen.MAIN_NAV }
                    )
                }
                Screen.SETTINGS -> {
                    SettingsScreen(
                        onBack = { currentScreen = Screen.MAIN_NAV },
                        authViewModel = authViewModel,
                        onNavigateToAuth = { currentScreen = Screen.AUTH }
                    )
                }
            }
        }
    }
}
