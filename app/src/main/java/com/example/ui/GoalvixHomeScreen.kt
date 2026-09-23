package com.example.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.example.R
import com.example.data.PredictionConstants
import com.example.data.PredictionState
import com.example.util.ShareHelper
import kotlinx.coroutines.launch
import com.example.ui.theme.DarkBg
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoalvixGold
import com.example.ui.theme.GoalvixGoldLight
import com.example.ui.theme.GoalvixGreen
import com.example.ui.theme.GoalvixGreenNeon
import com.example.ui.theme.TelegramBg
import com.example.ui.theme.TelegramBlue
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalvixHomeScreen(
    viewModel: GoalvixViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val freeState by viewModel.freePredictionState.collectAsState()
    val premiumState by viewModel.premiumPredictionState.collectAsState()
    val isAdFlowInitiated by viewModel.isAdFlowInitiated.collectAsState()
    val isPremiumUnlocked by viewModel.isPremiumUnlocked.collectAsState()
    val isRefreshing by viewModel.isRefreshing.collectAsState()

    // Detect app resume from browser after Adsterra link tap
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onAppResumed()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val safeInsets = WindowInsets.safeDrawing.asPaddingValues()
    val pullToRefreshState = rememberPullToRefreshState()
    val coroutineScope = rememberCoroutineScope()
    var showPrivacyDialog by remember { mutableStateOf(false) }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { viewModel.refreshAll() },
        state = pullToRefreshState,
        modifier = modifier
            .fillMaxSize()
            .background(DarkBg),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullToRefreshState,
                isRefreshing = isRefreshing,
                modifier = Modifier.align(Alignment.TopCenter),
                containerColor = DarkSurfaceElevated,
                color = GoalvixGreenNeon
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = safeInsets.calculateTopPadding() + 8.dp,
                    bottom = safeInsets.calculateBottomPadding() + 24.dp
                ),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar / Branding Header
            GoalvixHeader(
                isRefreshing = isRefreshing,
                onRefresh = { viewModel.refreshAll() },
                onOpenPrivacyPolicy = { showPrivacyDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Section 1: FREE PREDICTION
            val freeImageUrl = (freeState as? PredictionState.Success)?.imageUrl
            PredictionSectionCard(
                title = "FREE PREDICTION",
                badgeText = "TODAY'S PICK",
                badgeColor = GoalvixGreenNeon,
                state = freeState,
                onRetry = { viewModel.loadFreePrediction() },
                testTag = "free_prediction_card",
                onShare = {
                    coroutineScope.launch {
                        ShareHelper.sharePrediction(
                            context = context,
                            imageUrl = freeImageUrl,
                            title = "Free Prediction"
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Action: GET PREMIUM PREDICTIONS
            Button(
                onClick = {
                    viewModel.openAdsterraLink(context)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(16.dp), spotColor = GoalvixGreenNeon)
                    .testTag("get_premium_button"),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoalvixGreenNeon,
                    contentColor = Color.Black
                ),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "GET PREMIUM PREDICTIONS",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            // Fallback manual return button if ad flow initiated but not yet unlocked
            if (isAdFlowInitiated && !isPremiumUnlocked) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { viewModel.onManualUnlockClicked() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("im_back_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = GoalvixGold
                    ),
                    border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                        brush = Brush.horizontalGradient(
                            listOf(GoalvixGold, GoalvixGoldLight)
                        )
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = GoalvixGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "I'M BACK — VIEW PREMIUM",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Section 2: PREMIUM PREDICTION (Visible once unlocked)
            val premiumImageUrl = (premiumState as? PredictionState.Success)?.imageUrl
            AnimatedVisibility(
                visible = isPremiumUnlocked,
                enter = fadeIn() + slideInVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 24.dp)
                ) {
                    PredictionSectionCard(
                        title = "PREMIUM PREDICTION",
                        badgeText = "VIP ACCESS",
                        badgeColor = GoalvixGold,
                        state = premiumState,
                        onRetry = { viewModel.loadPremiumPrediction() },
                        testTag = "premium_prediction_card",
                        onShare = {
                            coroutineScope.launch {
                                ShareHelper.sharePrediction(
                                    context = context,
                                    imageUrl = premiumImageUrl,
                                    title = "VIP Premium Prediction"
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Telegram Section Divider / Header
            Text(
                text = "COMMUNITY & SUPPORT",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = TextMuted,
                letterSpacing = 1.2.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Button 1: JOIN PREMIUM ON TELEGRAM
            Button(
                onClick = {
                    openExternalUrl(context, PredictionConstants.TELEGRAM_CHANNEL_URL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("join_premium_telegram_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TelegramBlue,
                    contentColor = Color.White
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "JOIN PREMIUM ON TELEGRAM",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Button 2: JOIN OUR TELEGRAM CHANNEL
            Surface(
                onClick = {
                    openExternalUrl(context, PredictionConstants.TELEGRAM_CHANNEL_URL)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("join_telegram_channel_button"),
                shape = RoundedCornerShape(14.dp),
                color = TelegramBg,
                border = androidx.compose.foundation.BorderStroke(1.dp, TelegramBlue.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = TelegramBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "JOIN OUR TELEGRAM CHANNEL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer: Privacy Policy & Disclaimer
            Surface(
                onClick = { showPrivacyDialog = true },
                shape = RoundedCornerShape(10.dp),
                color = DarkSurface,
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder),
                modifier = Modifier.testTag("footer_privacy_link")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = GoalvixGreenNeon,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Privacy Policy & Terms",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "18+ • Predictions for informational & entertainment purposes only",
                fontSize = 11.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "© 2026 GOALVIX • All rights reserved",
                fontSize = 10.sp,
                color = TextMuted.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
        }
    }

    if (showPrivacyDialog) {
        PrivacyPolicyDialog(
            onDismiss = { showPrivacyDialog = false }
        )
    }
}

@Composable
fun GoalvixHeader(
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Official GOALVIX Logo Graphic
                Image(
                    painter = painterResource(id = R.drawable.goalvix_logo),
                    contentDescription = "GOALVIX Logo",
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "GOALVIX",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .background(GoalvixGreenNeon.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "PRO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = GoalvixGreenNeon
                            )
                        }
                    }
                    Text(
                        text = "Football Predictions",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = TextSecondary
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Privacy Policy Header Button
                IconButton(
                    onClick = onOpenPrivacyPolicy,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("header_privacy_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Privacy Policy",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Quick Refresh Button
                IconButton(
                    onClick = onRefresh,
                    modifier = Modifier
                        .size(38.dp)
                        .testTag("refresh_button")
                ) {
                    if (isRefreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp,
                            color = GoalvixGreenNeon
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh predictions",
                            tint = TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionSectionCard(
    title: String,
    badgeText: String,
    badgeColor: Color,
    state: PredictionState,
    onRetry: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier,
    onShare: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(testTag),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Section Title & Badge
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary,
                        letterSpacing = 0.5.sp
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (onShare != null && state is PredictionState.Success) {
                        IconButton(
                            onClick = onShare,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("${testTag}_share_icon_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share $title with Friends",
                                tint = badgeColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = badgeColor.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = badgeText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }

            // Image Container - strictly object-fit: contain, preserving original aspect ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated),
                contentAlignment = Alignment.Center
            ) {
                when (state) {
                    is PredictionState.Loading -> {
                        PredictionSkeletonPlaceholder(
                            badgeColor = badgeColor,
                            testTag = "${testTag}_skeleton"
                        )
                    }

                    is PredictionState.Success -> {
                        SubcomposeAsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(state.imageUrl)
                                .crossfade(true)
                                .build(),
                            contentDescription = title,
                            // Strict requirement: displayed at full available width while preserving original aspect ratio
                            // Never crop, never stretch, object-fit: contain
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            loading = {
                                PredictionSkeletonPlaceholder(
                                    badgeColor = badgeColor,
                                    testTag = "${testTag}_image_skeleton"
                                )
                            },
                            error = {
                                PredictionUnavailableFallback(onRetry = onRetry)
                            }
                        )
                    }

                    is PredictionState.Error -> {
                        PredictionUnavailableFallback(onRetry = onRetry)
                    }
                }
            }

            // Share Action Button when prediction is loaded
            if (state is PredictionState.Success && onShare != null) {
                Spacer(modifier = Modifier.height(10.dp))
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("${testTag}_share_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = badgeColor
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.dp, badgeColor.copy(alpha = 0.35f)),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Share with Friends",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = badgeColor
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PredictionUnavailableFallback(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = 220.dp)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(DarkSurface, CircleShape)
                    .border(1.dp, DarkCardBorder, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Prediction temporarily unavailable",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Today's picks will appear as soon as updated.",
                fontSize = 12.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedButton(
                onClick = onRetry,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoalvixGreenNeon),
                border = androidx.compose.foundation.BorderStroke(1.dp, GoalvixGreenNeon.copy(alpha = 0.5f)),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "Check Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun openExternalUrl(context: Context, url: String) {
    try {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "Could not open link in browser", Toast.LENGTH_SHORT).show()
    }
}
