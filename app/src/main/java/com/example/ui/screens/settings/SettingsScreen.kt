package com.example.ui.screens.settings

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvaGeometricMark
import com.example.ui.screens.auth.AuthViewModel
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    authViewModel: AuthViewModel? = null,
    onNavigateToAuth: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isPrivateAccount by remember { mutableStateOf(false) }
    var isDataSaverOn by remember { mutableStateOf(false) }
    var isPushNotifOn by remember { mutableStateOf(true) }
    var cacheSizeMb by remember { mutableStateOf("48.2 MB") }

    val authState = authViewModel?.authState?.collectAsStateWithLifecycle()?.value

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
    ) {
        // Top Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(imageVector = Icons.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
            }
            Text(
                text = "Settings and Privacy",
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AvaTextPrimary
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Section
            item {
                SettingsSectionHeader("Account & Authentication")
                SettingsContainer {
                    if (authState?.isAuthenticated == true && authState.user != null) {
                        val user = authState.user
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                androidx.compose.foundation.Image(
                                    painter = painterResource(id = user.avatarDrawableRes),
                                    contentDescription = "User Avatar",
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(androidx.compose.foundation.shape.CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = user.displayName,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AvaTextPrimary
                                    )
                                    Text(
                                        text = "@${user.username}",
                                        fontSize = 13.sp,
                                        color = AvaSecondaryPink
                                    )
                                }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF00E676).copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(androidx.compose.foundation.shape.CircleShape)
                                            .background(Color(0xFF00E676))
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Logged In", fontSize = 11.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                                }
                            }

                            if (!authState.accessToken.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AvaSurfaceSecondary)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Key,
                                        contentDescription = null,
                                        tint = AvaTextSecondary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "JWT: " + authState.accessToken.take(24) + "...",
                                        fontSize = 11.sp,
                                        color = AvaTextSecondary
                                    )
                                }
                            }
                        }

                        SettingsNavigationRow(
                            icon = Icons.Filled.SwapHoriz,
                            title = "Switch Account / Re-login"
                        ) {
                            onNavigateToAuth?.invoke()
                        }

                        SettingsNavigationRow(
                            icon = Icons.Filled.ExitToApp,
                            title = "Sign Out"
                        ) {
                            authViewModel?.logout()
                            Toast.makeText(context, "Signed out of AVA account", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp)
                        ) {
                            Text(
                                text = "Guest Viewer Session",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvaTextPrimary
                            )
                            Text(
                                text = "Sign in to sync videos, save drafts, and follow creators",
                                fontSize = 12.sp,
                                color = AvaTextSecondary,
                                modifier = Modifier.padding(top = 2.dp, bottom = 10.dp)
                            )
                            Button(
                                onClick = { onNavigateToAuth?.invoke() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = AvaPrimaryPink,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("settings_button_login")
                            ) {
                                Text("Log In / Create Account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }

                    SettingsNavigationRow(icon = Icons.Filled.Security, title = "Security & Two-Factor") {
                        Toast.makeText(context, "Two-factor authentication enabled via AVA API", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            // Privacy Section
            item {
                SettingsSectionHeader("Privacy & Content")
                SettingsContainer {
                    SettingsSwitchRow(
                        icon = Icons.Filled.Lock,
                        title = "Private Account",
                        subtitle = "Only approved followers can see your videos",
                        checked = isPrivateAccount,
                        onCheckedChange = { isPrivateAccount = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Filled.Notifications,
                        title = "Push Notifications",
                        subtitle = "Notify on likes, comments, and messages",
                        checked = isPushNotifOn,
                        onCheckedChange = { isPushNotifOn = it }
                    )
                    SettingsSwitchRow(
                        icon = Icons.Filled.DataSaverOn,
                        title = "Data Saver",
                        subtitle = "Reduce video resolution on cellular data",
                        checked = isDataSaverOn,
                        onCheckedChange = { isDataSaverOn = it }
                    )
                }
            }

            // Storage & Cache
            item {
                SettingsSectionHeader("Cache & Cellular")
                SettingsContainer {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.CleaningServices, contentDescription = null, tint = AvaTextSecondary)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(text = "Cached Media", fontSize = 14.sp, color = AvaTextPrimary, fontWeight = FontWeight.SemiBold)
                                Text(text = cacheSizeMb, fontSize = 12.sp, color = AvaTextSecondary)
                            }
                        }

                        Button(
                            onClick = {
                                cacheSizeMb = "0.0 MB"
                                Toast.makeText(context, "Cache Cleared", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AvaSurfaceSecondary,
                                contentColor = AvaPrimaryPink
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("button_clear_cache")
                        ) {
                            Text("Clear")
                        }
                    }
                }
            }

            // API & Cloud Services Section
            item {
                SettingsSectionHeader("API & Network Backend")
                SettingsContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Base Endpoint", fontSize = 13.sp, color = AvaTextSecondary)
                            Text("https://api.ava.mbs.com/v1", fontSize = 12.sp, color = AvaTextPrimary, fontWeight = FontWeight.Medium)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Gateway Status", fontSize = 13.sp, color = AvaTextSecondary)
                            Text("Connected (HTTP/2)", fontSize = 12.sp, color = Color(0xFF00E676), fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Auth Scheme", fontSize = 13.sp, color = AvaTextSecondary)
                            Text("OAuth2 / JWT Bearer", fontSize = 12.sp, color = AvaTextPrimary)
                        }
                    }
                }
            }

            // About AVA (MBS Group Pvt. Ltd.)
            item {
                SettingsSectionHeader("About")
                SettingsContainer {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AvaGeometricMark(size = 42.dp, tint = AvaPrimaryPink)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "AVA",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AvaTextPrimary,
                            letterSpacing = 2.sp
                        )
                        Text(
                            text = "“Watch. Create. Vibe.”",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = AvaSecondaryPink,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        Text(
                            text = "A product of MBS Group Pvt. Ltd.",
                            fontSize = 12.sp,
                            color = AvaTextSecondary,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        Text(
                            text = "Version 1.0.0 (Build 100)",
                            fontSize = 11.sp,
                            color = AvaTextSecondary.copy(alpha = 0.6f),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        color = AvaPrimaryPink,
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    )
}

@Composable
private fun SettingsContainer(content: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AvaSurfaceSecondary)
            .border(0.5.dp, AvaBorder, RoundedCornerShape(14.dp))
    ) {
        content()
    }
}

@Composable
private fun SettingsNavigationRow(
    icon: ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = AvaTextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 14.sp, color = AvaTextPrimary, fontWeight = FontWeight.Medium)
        }
        Icon(imageVector = Icons.Filled.ChevronRight, contentDescription = null, tint = AvaTextSecondary)
    }
}

@Composable
private fun SettingsSwitchRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = AvaTextSecondary)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = title, fontSize = 14.sp, color = AvaTextPrimary, fontWeight = FontWeight.Medium)
                Text(text = subtitle, fontSize = 11.sp, color = AvaTextSecondary)
            }
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = AvaPrimaryPink,
                uncheckedThumbColor = AvaTextSecondary,
                uncheckedTrackColor = AvaSurfacePrimary
            )
        )
    }
}
