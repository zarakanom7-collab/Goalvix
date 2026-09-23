package com.example.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextSecondary

/**
 * Custom Modifier providing a smooth, high-contrast shimmer gradient wave animation
 * suitable for dark-themed sports betting and prediction card skeleton states.
 */
@Composable
fun Modifier.shimmerEffect(
    baseColor: Color = Color(0xFF141C2B),
    highlightColor: Color = Color(0xFF28364F),
    durationMillis: Int = 1300
): Modifier {
    val transition = rememberInfiniteTransition(label = "GoalvixShimmerTransition")
    val translateAnim by transition.animateFloat(
        initialValue = -400f,
        targetValue = 1400f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = durationMillis, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "GoalvixShimmerTranslate"
    )

    return this.background(
        brush = Brush.linearGradient(
            colors = listOf(
                baseColor,
                highlightColor,
                baseColor
            ),
            start = Offset(translateAnim, translateAnim),
            end = Offset(translateAnim + 350f, translateAnim + 350f)
        )
    )
}

/**
 * A realistic, sports-themed skeleton placeholder displayed while prediction images
 * are being retrieved from Firebase Cloud Storage and decoded by Coil.
 */
@Composable
fun PredictionSkeletonPlaceholder(
    badgeColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = "prediction_shimmer_skeleton"
) {
    val transition = rememberInfiniteTransition(label = "PulseTransition")
    val pulseAlpha by transition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "PulseAlpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(270.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp))
            .testTag(testTag)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Skeleton Top Row: League & Date Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(110.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect()
                )

                Box(
                    modifier = Modifier
                        .width(70.dp)
                        .height(18.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .shimmerEffect(
                            baseColor = badgeColor.copy(alpha = 0.12f),
                            highlightColor = badgeColor.copy(alpha = 0.3f)
                        )
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Center Match Fixture Skeleton (Team A - VS - Team B)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Team A Crest & Name Skeleton
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(68.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }

                // VS / Score Box Shimmer
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .width(52.dp)
                            .height(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .shimmerEffect(
                                baseColor = Color(0xFF182233),
                                highlightColor = Color(0xFF2E3E5B)
                            )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(10.dp)
                            .clip(RoundedCornerShape(3.dp))
                            .shimmerEffect()
                    )
                }

                // Team B Crest & Name Skeleton
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .shimmerEffect()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .width(68.dp)
                            .height(12.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .shimmerEffect()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Market / Tip Shimmer Pill
            Box(
                modifier = Modifier
                    .width(180.dp)
                    .height(28.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .shimmerEffect(
                        baseColor = badgeColor.copy(alpha = 0.15f),
                        highlightColor = badgeColor.copy(alpha = 0.35f)
                    )
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Loading Status Footer with Pulsing Soccer Ball
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = badgeColor.copy(alpha = pulseAlpha),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Analyzing match data & fetching predictions...",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = TextSecondary.copy(alpha = pulseAlpha)
                )
            }
        }
    }
}
