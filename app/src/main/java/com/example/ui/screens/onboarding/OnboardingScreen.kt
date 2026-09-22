package com.example.ui.screens.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.mock.DemoDataProvider
import com.example.domain.model.User
import com.example.ui.components.AvaGeometricMark
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OnboardingScreen(
    onFinishOnboarding: () -> Unit,
    onNavigateToAuth: () -> Unit = {}
) {
    var step by remember { mutableIntStateOf(0) }
    val selectedInterests = remember { mutableStateListOf("Music", "Dance", "Cyberpunk") }
    val selectedLanguages = remember { mutableStateListOf("English") }
    val followedCreators = remember { mutableStateListOf<String>() }

    val allInterests = listOf(
        "Music", "Dance", "Cyberpunk", "Streetwear", "Fitness",
        "Coffee & Food", "Tech & Gadgets", "Gaming", "Aesthetic",
        "Travel", "Comedy", "Anime", "Cinema", "Art & Design"
    )

    val allLanguages = listOf("English", "Hindi", "Spanish", "French", "Japanese", "German")

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Header with Logo and Skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AvaGeometricMark(size = 32.dp, tint = AvaPrimaryPink)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "AVA",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        color = AvaTextPrimary,
                        letterSpacing = 2.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    TextButton(
                        onClick = onNavigateToAuth,
                        modifier = Modifier.testTag("onboarding_login_button")
                    ) {
                        Text(
                            text = "Log In",
                            color = AvaPrimaryPink,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                    TextButton(
                        onClick = onFinishOnboarding,
                        modifier = Modifier.testTag("onboarding_skip_button")
                    ) {
                        Text(
                            text = "Skip",
                            color = AvaTextSecondary,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Step Content with animation
            AnimatedContent(
                targetState = step,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                modifier = Modifier.weight(1f)
            ) { targetStep ->
                when (targetStep) {
                    0 -> WelcomeStep(onNavigateToAuth = onNavigateToAuth)
                    1 -> InterestsStep(
                        allInterests = allInterests,
                        selected = selectedInterests,
                        onToggle = { interest ->
                            if (selectedInterests.contains(interest)) selectedInterests.remove(interest)
                            else selectedInterests.add(interest)
                        }
                    )
                    2 -> LanguagesStep(
                        allLanguages = allLanguages,
                        selected = selectedLanguages,
                        onToggle = { lang ->
                            if (selectedLanguages.contains(lang)) selectedLanguages.remove(lang)
                            else selectedLanguages.add(lang)
                        }
                    )
                    3 -> SuggestedCreatorsStep(
                        creators = DemoDataProvider.creators.drop(1),
                        followed = followedCreators,
                        onToggle = { creatorId ->
                            if (followedCreators.contains(creatorId)) followedCreators.remove(creatorId)
                            else followedCreators.add(creatorId)
                        }
                    )
                    else -> PermissionsStep()
                }
            }

            // Bottom Navigation Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (step > 0) {
                    OutlinedButton(
                        onClick = { step-- },
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = AvaTextSecondary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                }

                Button(
                    onClick = {
                        if (step < 4) {
                            step++
                        } else {
                            onFinishOnboarding()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AvaPrimaryPink,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(2f)
                        .height(50.dp)
                        .testTag("onboarding_continue_button")
                ) {
                    Text(
                        text = if (step == 4) "Enter AVA" else "Continue",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun WelcomeStep(
    onNavigateToAuth: () -> Unit = {}
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AvaGeometricMark(size = 84.dp, tint = AvaPrimaryPink, glow = true)
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Welcome to AVA",
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold,
            color = AvaTextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Watch. Create. Vibe.",
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            color = AvaSecondaryPink,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "The premium platform for short-video creators and trendsetters. A product of MBS Group Pvt. Ltd.",
            fontSize = 14.sp,
            color = AvaTextSecondary,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        androidx.compose.material3.OutlinedButton(
            onClick = onNavigateToAuth,
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(contentColor = AvaPrimaryPink),
            border = androidx.compose.foundation.BorderStroke(1.dp, AvaPrimaryPink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .testTag("onboarding_welcome_signin_button")
        ) {
            Icon(
                imageVector = Icons.Filled.Person,
                contentDescription = null,
                tint = AvaPrimaryPink,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Already have an account? Sign In",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsStep(
    allInterests: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Choose Your Vibes",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AvaTextPrimary
        )
        Text(
            text = "Select 3 or more topics to personalize your For You feed",
            fontSize = 14.sp,
            color = AvaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            allInterests.forEach { interest ->
                val isSelected = selected.contains(interest)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) AvaPrimaryPink else AvaSurfaceSecondary)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AvaSecondaryPink else AvaBorder,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .clickable { onToggle(interest) }
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                ) {
                    Text(
                        text = interest,
                        color = if (isSelected) Color.White else AvaTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

@Composable
private fun LanguagesStep(
    allLanguages: List<String>,
    selected: List<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Content Languages",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AvaTextPrimary
        )
        Text(
            text = "Select languages you prefer watching and listening in",
            fontSize = 14.sp,
            color = AvaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 20.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(allLanguages) { lang ->
                val isSelected = selected.contains(lang)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvaSurfaceSecondary)
                        .border(
                            width = 1.dp,
                            color = if (isSelected) AvaPrimaryPink else AvaBorder,
                            shape = RoundedCornerShape(14.dp)
                        )
                        .clickable { onToggle(lang) }
                        .padding(horizontal = 18.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = lang,
                        fontSize = 15.sp,
                        color = if (isSelected) AvaTextPrimary else AvaTextSecondary,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Filled.Check,
                            contentDescription = null,
                            tint = AvaPrimaryPink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SuggestedCreatorsStep(
    creators: List<User>,
    followed: List<String>,
    onToggle: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Suggested Creators",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AvaTextPrimary
        )
        Text(
            text = "Follow top creators to start populating your Following feed",
            fontSize = 14.sp,
            color = AvaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(creators) { creator ->
                val isFollowed = followed.contains(creator.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Image(
                            painter = painterResource(id = creator.avatarDrawableRes),
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = creator.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvaTextPrimary
                            )
                            Text(
                                text = "@${creator.username}",
                                fontSize = 12.sp,
                                color = AvaTextSecondary
                            )
                        }
                    }

                    Button(
                        onClick = { onToggle(creator.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isFollowed) AvaSurfacePrimary else AvaPrimaryPink,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Text(
                            text = if (isFollowed) "Following" else "Follow",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PermissionsStep() {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Ready to Vibe",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = AvaTextPrimary
        )
        Text(
            text = "AVA uses device features for the best camera, audio, and notification experience",
            fontSize = 14.sp,
            color = AvaTextSecondary,
            modifier = Modifier.padding(top = 4.dp, bottom = 24.dp)
        )

        PermissionCard(
            title = "Camera & Microphone",
            desc = "To record clips, apply filters, and record voice effects",
            icon = Icons.Filled.CameraAlt
        )

        Spacer(modifier = Modifier.height(14.dp))

        PermissionCard(
            title = "Notifications",
            desc = "Stay notified about likes, comments, and direct messages",
            icon = Icons.Filled.Notifications
        )

        Spacer(modifier = Modifier.height(14.dp))

        PermissionCard(
            title = "Sound & Audio",
            desc = "Listen to trending audio and use voice effects",
            icon = Icons.Filled.Mic
        )
    }
}

@Composable
private fun PermissionCard(
    title: String,
    desc: String,
    icon: ImageVector
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AvaSurfaceSecondary)
            .border(1.dp, AvaBorder, RoundedCornerShape(14.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(AvaSurfacePrimary),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AvaPrimaryPink,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary
            )
            Text(
                text = desc,
                fontSize = 12.sp,
                color = AvaTextSecondary,
                lineHeight = 16.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
