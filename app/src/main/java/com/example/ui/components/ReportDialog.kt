package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AvaBackground
import com.example.ui.theme.AvaBorder
import com.example.ui.theme.AvaPrimaryPink
import com.example.ui.theme.AvaSecondaryPink
import com.example.ui.theme.AvaSurfacePrimary
import com.example.ui.theme.AvaSurfaceSecondary
import com.example.ui.theme.AvaTextPrimary
import com.example.ui.theme.AvaTextSecondary

@Composable
fun ReportDialog(
    targetId: String,
    targetType: String, // "video", "creator", "comment"
    targetTitle: String,
    onDismiss: () -> Unit,
    onSubmitReport: (reason: String, details: String) -> Unit,
    onBlockUser: (() -> Unit)? = null,
    onMuteUser: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val reportReasons = listOf(
        "Copyright Infringement (DMCA)",
        "Harassment or Bullying",
        "Spam, Scams or Bots",
        "Hate Speech or Discrimination",
        "Graphic Violence or Sensitive Content",
        "Dangerous or Illegal Acts",
        "Misleading Information"
    )

    var selectedReason by remember { mutableStateOf(reportReasons[0]) }
    var additionalDetails by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    var reportCaseId by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(AvaSurfacePrimary)
                .border(1.dp, AvaBorder, RoundedCornerShape(20.dp))
                .padding(20.dp)
        ) {
            if (!isSubmitted) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Flag,
                                contentDescription = null,
                                tint = AvaPrimaryPink,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Report $targetType",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = AvaTextPrimary
                            )
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Close",
                                tint = AvaTextSecondary
                            )
                        }
                    }

                    Text(
                        text = "Why are you reporting \"$targetTitle\"?",
                        fontSize = 13.sp,
                        color = AvaTextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
                    )

                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    ) {
                        items(reportReasons) { reason ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable { selectedReason = reason }
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = reason == selectedReason,
                                    onClick = { selectedReason = reason },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = AvaPrimaryPink,
                                        unselectedColor = AvaTextSecondary
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = reason,
                                    fontSize = 13.sp,
                                    color = if (reason == selectedReason) AvaTextPrimary else AvaTextSecondary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TextField(
                        value = additionalDetails,
                        onValueChange = { additionalDetails = it },
                        placeholder = { Text("Additional context (optional)...", color = AvaTextSecondary, fontSize = 12.sp) },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = AvaSurfaceSecondary,
                            unfocusedContainerColor = AvaSurfaceSecondary,
                            focusedTextColor = AvaTextPrimary,
                            unfocusedTextColor = AvaTextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Cancel", color = AvaTextSecondary)
                        }

                        Button(
                            onClick = {
                                val generatedId = "AVA-DMCA-" + (100000..999999).random()
                                reportCaseId = generatedId
                                onSubmitReport(selectedReason, additionalDetails)
                                isSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AvaPrimaryPink,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("button_submit_report")
                        ) {
                            Text("Submit", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Submission Confirmation Screen
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(AvaPrimaryPink.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = AvaPrimaryPink,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Report Received",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = AvaTextPrimary
                    )

                    Text(
                        text = "Case #$reportCaseId has been logged for MBS Trust & Safety review.",
                        fontSize = 13.sp,
                        color = AvaTextSecondary,
                        modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
                    )

                    // Moderation action pills
                    if (onBlockUser != null || onMuteUser != null) {
                        Text(
                            text = "Additional Protection Actions",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = AvaPrimaryPink,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            if (onMuteUser != null) {
                                Button(
                                    onClick = {
                                        onMuteUser()
                                        Toast.makeText(context, "Creator muted", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AvaSurfaceSecondary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.VolumeOff, contentDescription = null, modifier = Modifier.size(16.dp), tint = AvaTextSecondary)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Mute Creator", fontSize = 12.sp, color = AvaTextPrimary)
                                }
                            }

                            if (onBlockUser != null) {
                                Button(
                                    onClick = {
                                        onBlockUser()
                                        Toast.makeText(context, "Creator blocked", Toast.LENGTH_SHORT).show()
                                        onDismiss()
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AvaSurfaceSecondary),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Icon(imageVector = Icons.Filled.Block, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color(0xFFEF4444))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Block Creator", fontSize = 12.sp, color = Color(0xFFEF4444))
                                }
                            }
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = AvaPrimaryPink),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
