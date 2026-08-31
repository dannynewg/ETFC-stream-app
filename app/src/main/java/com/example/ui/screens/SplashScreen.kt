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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    val scale = remember { Animatable(0.7f) }
    val alpha = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "splash_glow")
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_anim"
    )

    LaunchedEffect(Unit) {
        // Smooth scale and fade-in
        alpha.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        scale.animateTo(1f, animationSpec = tween(700, easing = FastOutSlowInEasing))
        delay(1300) // Total display time ~2.0s
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0D0F14),
                        EtfcObsidian,
                        Color(0xFF080A0E)
                    )
                )
            )
            .testTag("splash_screen_container"),
        contentAlignment = Alignment.Center
    ) {
        // Background subtle radial glow
        Box(
            modifier = Modifier
                .size(320.dp)
                .scale(scale.value * 1.1f)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            EtfcGold.copy(alpha = 0.12f * glowPulse),
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
            // Main Branded Logo Badge
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color.Black,
                border = BorderStroke(2.dp, EtfcGold.copy(alpha = glowPulse)),
                shadowElevation = 16.dp,
                modifier = Modifier
                    .size(130.dp)
                    .border(
                        BorderStroke(4.dp, EtfcGoldGlow.copy(alpha = 0.3f * glowPulse)),
                        RoundedCornerShape(20.dp)
                    )
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(12.dp)
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
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 2.5.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STREAM",
                    fontSize = 34.sp,
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

            Spacer(modifier = Modifier.height(6.dp))

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

            Spacer(modifier = Modifier.height(48.dp))

            // Loading indicator
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = EtfcGold,
                strokeWidth = 2.dp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Connecting to Live Arena...",
                fontSize = 11.sp,
                color = EtfcTextMuted,
                letterSpacing = 0.5.sp
            )
        }

        // Bottom version & domain info
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
                .alpha(alpha.value)
        ) {
            Text(
                text = "etfcstream.freedev.app",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcGold.copy(alpha = 0.7f),
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Version 3.4.0 • Realtime Firebase Architecture",
                fontSize = 9.sp,
                color = EtfcTextMuted
            )
        }
    }
}
