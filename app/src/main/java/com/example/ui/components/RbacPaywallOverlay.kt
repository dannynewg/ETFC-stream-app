package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Bout
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

@Composable
fun RbacPaywallOverlay(
    bout: Bout,
    currentUser: UserAccount?,
    onUnlockPpv: () -> Unit,
    onSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        EtfcObsidian.copy(alpha = 0.85f),
                        EtfcObsidian.copy(alpha = 0.98f)
                    )
                )
            )
            .testTag("rbac_paywall_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Lock Icon with glowing gold ring
            Surface(
                shape = CircleShape,
                color = EtfcGold.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(2.dp, EtfcGold),
                modifier = Modifier.size(60.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "PPV Locked",
                        tint = EtfcGold,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                color = EtfcRedPrimary.copy(alpha = 0.2f),
                shape = RoundedCornerShape(6.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcRedPrimary)
            ) {
                Text(
                    text = "🔒 PPV MAIN CARD ACCESS REQUIRED",
                    color = EtfcRedPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "${bout.fighterRed.name} vs ${bout.fighterBlue.name}",
                color = EtfcTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Text(
                text = "${bout.matchTitle} • 5 Rounds Championship",
                color = EtfcGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Live High-Definition streaming is restricted to Verified PPV Passholders, VIP Members, and Arena Staff.",
                color = EtfcTextSecondary,
                fontSize = 11.sp,
                textAlign = TextAlign.Center,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            // CTA Buttons
            Button(
                onClick = onUnlockPpv,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("paywall_unlock_btn"),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text("UNLOCK PPV PASS (TELEBIRR / M-PESA)", fontWeight = FontWeight.Black, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (currentUser == null || currentUser.role == UserRole.GUEST || currentUser.role == UserRole.FREE_USER) {
                OutlinedButton(
                    onClick = onSignIn,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("paywall_signin_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcTextMuted)
                ) {
                    Text("Already purchased? Sign in with account", fontSize = 11.sp)
                }
            }
        }
    }
}
