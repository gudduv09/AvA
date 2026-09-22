package com.example.ui.screens.creator

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun CreatorDashboardScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedRange by remember { mutableStateOf("7 Days") }
    val timeRanges = listOf("7 Days", "28 Days", "90 Days")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AvaBackground)
            .statusBarsPadding()
    ) {
        // Header
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
                text = "Creator Analytics",
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
            // Range Filter Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    timeRanges.forEach { range ->
                        val isSelected = range == selectedRange
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) AvaPrimaryPink else Color.Transparent)
                                .clickable { selectedRange = range }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = range,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else AvaTextSecondary
                            )
                        }
                    }
                }
            }

            // High Level Metrics
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AvaSurfaceSecondary)
                        .border(0.5.dp, AvaBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = "Total Video Views", fontSize = 12.sp, color = AvaTextSecondary)
                            Text(
                                text = if (selectedRange == "7 Days") "142.8K" else "498.2K",
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Black,
                                color = AvaTextPrimary
                            )
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.TrendingUp,
                                contentDescription = null,
                                tint = AvaPrimaryPink,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "+24.6%", color = AvaPrimaryPink, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Sparkline chart
                    AnalyticsTrendChart()
                }
            }

            // 4-Grid Metric Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "Profile Visits", value = "18.4K", change = "+12%", modifier = Modifier.weight(1f))
                    MetricCard(title = "Watch Time", value = "1,420h", change = "+18%", modifier = Modifier.weight(1f))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MetricCard(title = "Total Likes", value = "89.4K", change = "+31%", modifier = Modifier.weight(1f))
                    MetricCard(title = "Shares", value = "6,190", change = "+45%", modifier = Modifier.weight(1f))
                }
            }

            // Audience Retention Section
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(AvaSurfaceSecondary)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Audience Insights",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )
                    Text(
                        text = "Peak active hours: 8:00 PM – 11:30 PM",
                        fontSize = 12.sp,
                        color = AvaTextSecondary,
                        modifier = Modifier.padding(top = 2.dp, bottom = 12.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Average Watch Percentage", fontSize = 13.sp, color = AvaTextSecondary)
                        Text(text = "78.4%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvaPrimaryPink)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Rewatch Rate", fontSize = 13.sp, color = AvaTextSecondary)
                        Text(text = "34.2%", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AvaSecondaryPink)
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
private fun MetricCard(
    title: String,
    value: String,
    change: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(AvaSurfaceSecondary)
            .border(0.5.dp, AvaBorder, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Text(text = title, fontSize = 12.sp, color = AvaTextSecondary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = AvaTextPrimary)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = change, fontSize = 11.sp, color = AvaPrimaryPink, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun AnalyticsTrendChart() {
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
    ) {
        val w = size.width
        val h = size.height
        val points = listOf(0.3f, 0.45f, 0.4f, 0.7f, 0.65f, 0.85f, 0.95f)

        val path = Path().apply {
            moveTo(0f, h * (1f - points[0]))
            points.forEachIndexed { index, fl ->
                val x = (w / (points.size - 1)) * index
                val y = h * (1f - fl)
                lineTo(x, y)
            }
        }

        drawPath(
            path = path,
            color = AvaPrimaryPink,
            style = Stroke(width = 5f)
        )
    }
}
