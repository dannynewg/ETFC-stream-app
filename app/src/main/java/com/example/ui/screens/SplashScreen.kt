package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcGoldGlow
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import kotlinx.coroutines.delay

/**
 * Modern Animated Splash Screen Component
 * Performs the initial Firebase Auth check and securely routes to either
 * the Home Screen or the unified Auth Screen.
 */
@Composable
fun SplashScreen(
    onNavigateToHome: () -> Unit,
    onNavigateToAuth: () -> Unit,
    isUserAuthenticated: Boolean? = null
) {
    val scale = remember { Animatable(0.65f) }
    val alpha = remember { Animatable(0f) }
    val badgeGlow = remember { Animatable(0.4f) }

    var authStatusMessage by remember { mutableStateOf("Initializing Live Arena...") }
    var isAuthCheckDone by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_anim"
    )

    LaunchedEffect(Unit) {
        // 1. Entrance animation (scale + fade-in)
        alpha.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
        scale.animateTo(1f, animationSpec = tween(650, easing = FastOutSlowInEasing))
        
        // 2. Perform Firebase Auth & Session check
        authStatusMessage = "Checking Firebase Auth session..."
        delay(600)

        val firebaseUser = try {
            com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        } catch (e: Exception) {
            null
        }

        val authenticated = isUserAuthenticated ?: (firebaseUser != null)
        isAuthCheckDone = true

        if (authenticated) {
            val name = firebaseUser?.displayName ?: "Champion Fan"
            authStatusMessage = "Welcome back, $name! Loading Arena..."
            delay(750)
            onNavigateToHome()
        } else {
            authStatusMessage = "Welcome to ETFC! Preparing Sign In..."
            delay(750)
            onNavigateToAuth()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1117),
                        EtfcObsidian,
                        Color(0xFF07090D)
                    )
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Background radiant ambient glow
        Box(
            modifier = Modifier
                .size(340.dp)
                .scale(scale.value * 1.15f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EtfcGold.copy(alpha = 0.15f * glowPulse),
                            Color.Transparent
                        )
                    ),
                    shape = CircleShape
                )
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(24.dp)
                .alpha(alpha.value)
                .scale(scale.value)
        ) {
            // Main Branded Logo Badge with Octagon / Shield styling
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Black,
                border = BorderStroke(2.dp, EtfcGold.copy(alpha = glowPulse)),
                shadowElevation = 20.dp,
                modifier = Modifier
                    .size(136.dp)
                    .border(
                        BorderStroke(4.dp, EtfcGoldGlow.copy(alpha = 0.35f * glowPulse)),
                        RoundedCornerShape(22.dp)
                    )
                    .testTag("splash_logo_badge")
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(14.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_etfc_logo),
                        contentDescription = "ETFC Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Brand Title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "ETFC",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STREAM",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcTextPrimary,
                    letterSpacing = 2.5.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Tagline / Subtitle
            Text(
                text = "ETHIOPIAN FIGHT CHAMPIONSHIP",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextSecondary,
                letterSpacing = 2.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = EtfcLiveRed.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, EtfcLiveRed.copy(alpha = 0.6f))
            ) {
                Text(
                    text = "OFFICIAL MMA & PPV BROADCAST",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcLiveRed,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(44.dp))

            // Status & Loading Indicator
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                if (!isAuthCheckDone) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = EtfcGold,
                        strokeWidth = 2.5.dp
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Auth Success",
                        tint = EtfcSuccess,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Text(
                    text = authStatusMessage,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = if (isAuthCheckDone) EtfcGold else EtfcTextMuted,
                    letterSpacing = 0.3.sp
                )
            }
        }

        // Bottom version & domain info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(alpha.value)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = EtfcSurface.copy(alpha = 0.7f),
                border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.3f))
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Secure Portal",
                        tint = EtfcGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "etfcstream.rf.gd",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Version 3.4.0 • Firebase Cloud Engine",
                fontSize = 9.sp,
                color = EtfcTextMuted
            )
        }
    }
}

/**
 * Backward compatibility overload
 */
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    SplashScreen(
        onNavigateToHome = onSplashFinished,
        onNavigateToAuth = onSplashFinished,
        isUserAuthenticated = true
    )
}
