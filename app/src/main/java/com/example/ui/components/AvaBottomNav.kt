package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

enum class NavItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Home", Icons.Filled.Home, Icons.Outlined.Home),
    DISCOVER("Discover", Icons.Filled.Explore, Icons.Outlined.Explore),
    CREATE("Create", Icons.Filled.Add, Icons.Filled.Add),
    INBOX("Inbox", Icons.Filled.Mail, Icons.Outlined.Mail),
    PROFILE("Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AvaBottomNav(
    selectedItem: NavItem,
    onItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier,
    hasUnreadInbox: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AvaBackground.copy(alpha = 0.95f))
            .border(
                width = 0.5.dp,
                color = AvaBorder.copy(alpha = 0.6f)
            )
            .navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem.values().forEach { item ->
                if (item == NavItem.CREATE) {
                    // Visually prominent AVA create button
                    Box(
                        modifier = Modifier
                            .testTag("nav_create_button")
                            .size(46.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.linearGradient(
                                    listOf(AvaPrimaryPink, AvaSecondaryPink)
                                )
                            )
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                onItemSelected(NavItem.CREATE)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Add,
                            contentDescription = "Create Video",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                } else {
                    val isSelected = item == selectedItem
                    val interactionSource = remember { MutableInteractionSource() }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .testTag("nav_item_${item.name.lowercase()}")
                            .weight(1f)
                            .height(58.dp)
                            .clickable(
                                interactionSource = interactionSource,
                                indication = null
                            ) {
                                onItemSelected(item)
                            }
                    ) {
                        Box(contentAlignment = Alignment.TopEnd) {
                            Icon(
                                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                contentDescription = item.title,
                                tint = if (isSelected) AvaPrimaryPink else AvaTextSecondary,
                                modifier = Modifier.size(24.dp)
                            )
                            if (item == NavItem.INBOX && hasUnreadInbox) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(AvaPrimaryPink, CircleShape)
                                )
                            }
                        }

                        Text(
                            text = item.title,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AvaTextPrimary else AvaTextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }
        }
    }
}
