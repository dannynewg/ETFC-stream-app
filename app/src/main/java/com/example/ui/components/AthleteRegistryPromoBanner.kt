package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.filled.AppRegistration
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

/**
 * High-impact Ethiopian Combat Athlete Registry Promo Banner displayed on the Home Screen.
 */
@Composable
fun AthleteRegistryPromoBanner(
    onRegisterClick: () -> Unit,
    onViewRosterClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(
                border = BorderStroke(
                    1.5.dp,
                    Brush.horizontalGradient(
                        listOf(EtfcGold, EtfcGold.copy(alpha = 0.3f), EtfcGold)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onRegisterClick() }
            .testTag("athlete_registry_home_promo_banner"),
        colors = CardDefaults.cardColors(containerColor = EtfcCard)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF241A06), Color(0xFF131318))
                    )
                )
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Top Badge Strip with pulsating dot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = EtfcGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.5f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(EtfcGold.copy(alpha = pulseAlpha))
                            )
                            Text(
                                text = "OFFICIAL NATIONAL CALLOUT & LEAGUE PLACEMENT",
                                color = EtfcGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    Text(
                        text = "🇪🇹 ETFC REGISTRY",
                        color = EtfcTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Title & Description
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "ETHIOPIAN COMBAT ATHLETE REGISTRY",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "የኢትዮጵያ የውጊያ ስፖርት አትሌቶች ብሔራዊ ምዝገባ",
                        color = EtfcGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "National registry for elite Boxing, MMA, Muay Thai, Taekwondo & Kickboxing athletes. Register now to have your credentials vetted by the ETFC matchmaking team for regional and national fight league assignment.",
                        color = EtfcTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }

                // Quick stats pill row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    RegistryStatBadge(icon = Icons.Default.Groups, text = "50+ Active Roster")
                    RegistryStatBadge(icon = Icons.Default.SportsMma, text = "6 Disciplines")
                    RegistryStatBadge(icon = Icons.Default.Verified, text = "League Vetting")
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1.3f)
                            .height(40.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AppRegistration, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("APPLY FOR VETTING", fontWeight = FontWeight.Black, fontSize = 11.sp)
                    }

                    OutlinedButton(
                        onClick = onViewRosterClick,
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = EtfcCardInner,
                            contentColor = Color.White
                        ),
                        border = BorderStroke(1.dp, EtfcBorderSubtle),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                    ) {
                        Text("ACTIVE ROSTER", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = EtfcTextPrimary)
                        Spacer(modifier = Modifier.width(2.dp))
                        Icon(imageVector = Icons.Default.ChevronRight, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun RegistryStatBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    text: String
) {
    Surface(
        color = EtfcCardInner,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(0.5.dp, EtfcBorderSubtle)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(11.dp))
            Text(text = text, color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}
