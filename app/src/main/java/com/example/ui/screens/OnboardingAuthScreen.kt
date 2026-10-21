package com.example.ui.screens

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.R
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderGold
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import com.example.ui.viewmodel.EtfcViewModel

/**
 * Clean & Straightforward Prototype Access Screen.
 * Completely replaces the confusing multi-step sign-in/sign-up forms with
 * instant 1-tap "LOGIN AS USER" and "LOGIN AS ADMIN" buttons for testing.
 */
@Composable
fun OnboardingAuthScreen(
    viewModel: EtfcViewModel,
    onDismiss: (() -> Unit)? = null
) {
    OnboardingAuthScreen(
        onLoginAsUser = { viewModel.loginAsDemoUser(UserRole.USER) },
        onLoginAsAdmin = { viewModel.loginAsDemoUser(UserRole.ADMIN) },
        onDismiss = onDismiss
    )
}

@Composable
fun OnboardingAuthScreen(
    onLoginAsUser: () -> Unit,
    onLoginAsAdmin: () -> Unit,
    onDismiss: (() -> Unit)? = null
) {
    val scrollState = rememberScrollState()

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(900),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF141720),
                        EtfcObsidian,
                        Color(0xFF090A0E)
                    )
                )
            )
            .testTag("onboarding_auth_screen")
    ) {
        // Dismiss button if shown as a dialog overlay
        if (onDismiss != null) {
            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(36.dp)
                    .background(EtfcCardInner, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 24.dp, vertical = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Brand Logo Box with Gold Ring
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = Color.Black,
                border = BorderStroke(2.dp, EtfcGold),
                shadowElevation = 12.dp,
                modifier = Modifier.size(90.dp)
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

            Spacer(modifier = Modifier.height(16.dp))

            // Title & Brand Mark
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ETFC",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "STREAM",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcTextPrimary,
                    letterSpacing = 2.sp
                )
            }

            Text(
                text = "Official Ethiopian Fight Championship Arena",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextMuted,
                letterSpacing = 0.5.sp
            )
            Text(
                text = "የኢትዮጵያ የውጊያ ሻምፒዮና መተግበሪያ",
                fontSize = 11.sp,
                color = EtfcGold.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Prototype Indicator Badge
            Surface(
                color = EtfcGold.copy(alpha = 0.15f),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EtfcGold.copy(alpha = pulseAlpha))
                    )
                    Text(
                        text = "PROTOTYPE TEST BUILD • INSTANT ACCESS",
                        color = EtfcGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.8.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Main Action Card
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.5.dp, EtfcBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SELECT ACCESS ROLE",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Tap a button below to launch into the app directly:",
                            color = EtfcTextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // 1. LOGIN AS USER BUTTON
                    Button(
                        onClick = onLoginAsUser,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("btn_login_as_user")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Black.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SportsMma,
                                        contentDescription = null,
                                        tint = Color.Black,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }
                                Column {
                                    Text(
                                        text = "LOGIN AS USER (FAN & ATHLETE)",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.Black,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "Fan streaming, PPV arena & Athlete Registration",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.Black.copy(alpha = 0.75f)
                                    )
                                }
                            }
                            Icon(
                                imageVector = Icons.Default.ArrowForward,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    // 2. LOGIN AS ADMIN BUTTON (Only visible when running in standalone Admin App)
                    if (BuildConfig.IS_ADMIN_APP) {
                        OutlinedButton(
                            onClick = onLoginAsAdmin,
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = EtfcCardInner,
                                contentColor = Color.White
                            ),
                            border = BorderStroke(1.5.dp, EtfcGold.copy(alpha = 0.8f)),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp)
                                .testTag("btn_login_as_admin")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .clip(CircleShape)
                                            .background(EtfcGold.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = null,
                                            tint = EtfcGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                            Text(
                                                text = "LOGIN AS ADMIN & PROMOTER",
                                                fontSize = 14.sp,
                                                fontWeight = FontWeight.Black,
                                                color = Color.White,
                                                letterSpacing = 0.5.sp
                                            )
                                            Surface(
                                                color = EtfcGold.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = "CRM & CMS",
                                                    color = EtfcGold,
                                                    fontSize = 9.sp,
                                                    fontWeight = FontWeight.Black,
                                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                        Text(
                                            text = "Athlete Registry CRM, Vetting, Matches & Control",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = EtfcTextMuted
                                        )
                                    }
                                }
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Information
            Text(
                text = "No credentials or sign-up needed for beta testing.",
                color = EtfcTextMuted,
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = "v1.0.002 • ETFC Stream Beta Edition",
                color = EtfcTextMuted.copy(alpha = 0.6f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}
