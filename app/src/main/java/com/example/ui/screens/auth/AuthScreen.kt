package com.example.ui.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvaGeometricMark
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun AuthScreen(
    viewModel: AuthViewModel,
    onAuthSuccess: () -> Unit,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val identifier by viewModel.identifier.collectAsStateWithLifecycle()
    val email by viewModel.email.collectAsStateWithLifecycle()
    val displayName by viewModel.displayName.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isPasswordVisible by viewModel.isPasswordVisible.collectAsStateWithLifecycle()
    val uiErrorMessage by viewModel.uiErrorMessage.collectAsStateWithLifecycle()

    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onDismiss != null) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("auth_button_dismiss")
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Close",
                            tint = AvaTextSecondary
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.size(48.dp))
                }

                // API Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(AvaSurfaceSecondary)
                        .border(0.5.dp, AvaBorder, RoundedCornerShape(20.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF00E676))
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "AVA API v1 • Online",
                        fontSize = 11.sp,
                        color = AvaTextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.size(48.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Logo and Branding
            AvaGeometricMark(
                size = 56.dp,
                tint = AvaPrimaryPink,
                modifier = Modifier.testTag("auth_logo")
            )
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "AVA",
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = AvaTextPrimary,
                letterSpacing = 4.sp
            )
            Text(
                text = "“Watch. Create. Vibe.”",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = AvaSecondaryPink,
                modifier = Modifier.padding(top = 2.dp)
            )
            Text(
                text = "MBS Group Pvt. Ltd. Authenticated Platform",
                fontSize = 11.sp,
                color = AvaTextSecondary.copy(alpha = 0.7f),
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Segmented Switch: Log In vs Create Account
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AvaSurfaceSecondary)
                    .border(1.dp, AvaBorder, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentTab == AuthTab.LOGIN) AvaPrimaryPink else Color.Transparent)
                        .clickable { viewModel.setTab(AuthTab.LOGIN) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Log In",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTab == AuthTab.LOGIN) Color.White else AvaTextSecondary
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (currentTab == AuthTab.REGISTER) AvaPrimaryPink else Color.Transparent)
                        .clickable { viewModel.setTab(AuthTab.REGISTER) }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Create Account",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (currentTab == AuthTab.REGISTER) Color.White else AvaTextSecondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Fill Demo Accounts
            Text(
                text = "Quick Demo Profiles:",
                fontSize = 12.sp,
                color = AvaTextSecondary,
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(bottom = 6.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                DemoAccountChip(
                    label = "@ava_creator",
                    onClick = {
                        viewModel.quickFillAccount("ava_creator", "vibe2026", "AVA Creator", "creator@ava.mbs.com")
                    }
                )
                DemoAccountChip(
                    label = "@pinkwave",
                    onClick = {
                        viewModel.quickFillAccount("pinkwave", "tokyo999", "Pink Wave 🌊", "wave@dance.com")
                    }
                )
                DemoAccountChip(
                    label = "@urbanframe",
                    onClick = {
                        viewModel.quickFillAccount("urbanframe", "neon4k", "Urban Frame", "urban@photo.com")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Error Banner
            AnimatedVisibility(
                visible = uiErrorMessage != null || authState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val error = uiErrorMessage ?: authState.errorMessage ?: ""
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3B121E))
                        .border(1.dp, Color(0xFFFF5252).copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Filled.ErrorOutline,
                        contentDescription = null,
                        tint = Color(0xFFFF5252),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = error,
                        fontSize = 12.sp,
                        color = Color(0xFFFF8A80),
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Input Fields
            if (currentTab == AuthTab.REGISTER) {
                // Email field
                OutlinedTextField(
                    value = email,
                    onValueChange = viewModel::onEmailChanged,
                    label = { Text("Email Address") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Email, contentDescription = null, tint = AvaTextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_email"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    colors = authTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Display Name field
                OutlinedTextField(
                    value = displayName,
                    onValueChange = viewModel::onDisplayNameChanged,
                    label = { Text("Display Name / Channel Name") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Filled.Badge, contentDescription = null, tint = AvaTextSecondary)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_auth_display_name"),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    colors = authTextFieldColors(),
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            // Identifier (Username or Email for Login, Username for Register)
            OutlinedTextField(
                value = identifier,
                onValueChange = viewModel::onIdentifierChanged,
                label = { Text(if (currentTab == AuthTab.LOGIN) "Username or Email" else "Choose Username (@)") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.AlternateEmail, contentDescription = null, tint = AvaTextSecondary)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_identifier"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = authTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = viewModel::onPasswordChanged,
                label = { Text("Password") },
                leadingIcon = {
                    Icon(imageVector = Icons.Filled.Lock, contentDescription = null, tint = AvaTextSecondary)
                },
                trailingIcon = {
                    IconButton(onClick = viewModel::togglePasswordVisibility) {
                        Icon(
                            imageVector = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                            contentDescription = "Toggle password visibility",
                            tint = AvaTextSecondary
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_auth_password"),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    focusManager.clearFocus()
                    viewModel.submit(onAuthSuccess)
                }),
                colors = authTextFieldColors(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Submit Button
            Button(
                onClick = {
                    focusManager.clearFocus()
                    viewModel.submit(onAuthSuccess)
                },
                enabled = !authState.isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AvaPrimaryPink,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("button_auth_submit")
            ) {
                if (authState.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(22.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (currentTab == AuthTab.LOGIN) "Connecting to API..." else "Registering Account...",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = if (currentTab == AuthTab.LOGIN) "Log In to AVA" else "Create Account",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Social Login Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = AvaBorder)
                Text(
                    text = "or connect with",
                    fontSize = 12.sp,
                    color = AvaTextSecondary,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = AvaBorder)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Google Button
                OutlinedButton(
                    onClick = { viewModel.loginWithSocial("google", onAuthSuccess) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AvaTextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(AvaBorder, AvaBorder))),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("button_auth_google")
                ) {
                    Text(
                        text = "Google",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                // Apple Button
                OutlinedButton(
                    onClick = { viewModel.loginWithSocial("apple", onAuthSuccess) },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AvaTextPrimary),
                    border = ButtonDefaults.outlinedButtonBorder.copy(brush = Brush.linearGradient(listOf(AvaBorder, AvaBorder))),
                    modifier = Modifier
                        .weight(1f)
                        .height(46.dp)
                        .testTag("button_auth_apple")
                ) {
                    Text(
                        text = "Apple",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue as Guest button
            TextButton(
                onClick = { viewModel.continueAsGuest(onAuthSuccess) },
                modifier = Modifier.testTag("button_auth_guest")
            ) {
                Text(
                    text = "Continue as Guest Viewer",
                    color = AvaTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Terms & Privacy Note
            Text(
                text = "By signing in, you agree to MBS Group's Terms of Service and Privacy Policy. All short-form video content is protected under AVA community guidelines.",
                fontSize = 11.sp,
                color = AvaTextSecondary.copy(alpha = 0.5f),
                textAlign = TextAlign.Center,
                lineHeight = 16.sp,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@Composable
private fun DemoAccountChip(
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AvaSurfaceSecondary)
            .border(0.5.dp, AvaBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = AvaSecondaryPink
        )
    }
}

@Composable
private fun authTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = AvaPrimaryPink,
    unfocusedBorderColor = AvaBorder,
    focusedLabelColor = AvaPrimaryPink,
    unfocusedLabelColor = AvaTextSecondary,
    cursorColor = AvaPrimaryPink,
    focusedTextColor = AvaTextPrimary,
    unfocusedTextColor = AvaTextPrimary,
    focusedContainerColor = AvaSurfacePrimary,
    unfocusedContainerColor = AvaSurfaceSecondary
)
