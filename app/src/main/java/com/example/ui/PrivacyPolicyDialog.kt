package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalvixGreenNeon
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PrivacyPolicyDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = modifier
                .fillMaxWidth(0.92f)
                .heightIn(max = 680.dp)
                .testTag("privacy_policy_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Shield Icon & Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(GoalvixGreenNeon.copy(alpha = 0.15f), CircleShape)
                                .border(1.dp, GoalvixGreenNeon.copy(alpha = 0.4f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = GoalvixGreenNeon,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Privacy Policy",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = TextPrimary,
                                letterSpacing = 0.5.sp
                            )
                            Text(
                                text = "GOALVIX • Last Updated: Sept 2026",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = TextSecondary
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("close_privacy_dialog_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Scrollable Policy Content
                Column(
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState())
                        .background(DarkSurfaceElevated, RoundedCornerShape(16.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    PolicySection(
                        title = "1. Information We Collect",
                        content = "GOALVIX does not collect, track, or store any personal information. You are never required to register an account, log in, or provide your name, email, phone number, or payment details to use the application."
                    )

                    PolicySection(
                        title = "2. Prediction Content & Delivery",
                        content = "The daily match prediction images are retrieved dynamically over secure HTTPS from Google Firebase Cloud Storage directly to your device. We do not store any identifying telemetry on our servers."
                    )

                    PolicySection(
                        title = "3. Third-Party Links & Advertising",
                        content = "The app may display links to third-party sponsors (such as Adsterra) and to our community on Telegram. Clicking these links redirects you to external services governed by their respective privacy terms. We do not transmit any personal data to advertisers."
                    )

                    PolicySection(
                        title = "4. Device Permissions & Sharing",
                        content = "• INTERNET & NETWORK STATE: Used solely to fetch current prediction images.\n• NATIVE SHARING: When you voluntarily share a prediction with friends, a temporary cache file is created and shared via Android's secure FileProvider. We never access your personal files or photo gallery."
                    )

                    PolicySection(
                        title = "5. Sports Prediction Disclaimer",
                        content = "All statistics, tips, and predictions presented on GOALVIX are for educational, informational, and sports entertainment purposes only. We do not guarantee match outcomes or financial results. GOALVIX is NOT a gambling or betting operator and does not handle real money wagering. Users must be at least 18 years old or the legal age of majority in their jurisdiction."
                    )

                    PolicySection(
                        title = "6. Children's Privacy",
                        content = "GOALVIX is intended for adult sports enthusiasts. We do not knowingly collect personal identifiable information from children under the age of 13."
                    )

                    PolicySection(
                        title = "7. Contact & Inquiries",
                        content = "If you have questions or feedback regarding our privacy practices, you can contact us directly through our official Telegram channel (https://t.me/goalvix) or via email at creativezahis@gmail.com."
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Action Button
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("dismiss_privacy_policy_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoalvixGreenNeon,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "I UNDERSTAND",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun PolicySection(
    title: String,
    content: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 14.dp)
    ) {
        Text(
            text = title,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = GoalvixGreenNeon,
            letterSpacing = 0.2.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = content,
            fontSize = 12.sp,
            color = TextSecondary,
            lineHeight = 18.sp
        )
    }
}
