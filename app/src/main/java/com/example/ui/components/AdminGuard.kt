package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcTextSecondary

/**
 * AdminGuard:
 * Custom Jetpack Compose wrapper component that conditionally renders administrative controls
 * (such as the Admin CRM portal launch button, CMS moderation panels, and admin actions)
 * ONLY if the authenticated user's role is 'admin' (or user is the primary superadmin 'dannynewg@gmail.com').
 *
 * Usage:
 * ```kotlin
 * AdminGuard(currentUser = uiState.currentUser) {
 *     Button(onClick = { viewModel.openAdminPortal() }) {
 *         Text("OPEN ADMIN CRM PORTAL")
 *     }
 * }
 * ```
 */
@Composable
fun AdminGuard(
    currentUser: UserAccount?,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isAdmin = currentUser != null && (
            currentUser.isAdmin ||
            currentUser.role == UserRole.ADMIN ||
            currentUser.email.equals("dannynewg@gmail.com", ignoreCase = true)
    )

    if (isAdmin) {
        content()
    } else {
        fallback()
    }
}

@Composable
fun AdminGuard(
    userRole: UserRole?,
    userEmail: String? = null,
    fallback: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    val isAdmin = userRole == UserRole.ADMIN ||
            userEmail?.equals("dannynewg@gmail.com", ignoreCase = true) == true

    if (isAdmin) {
        content()
    } else {
        fallback()
    }
}

/**
 * AdminAccessDeniedScreen:
 * A secure fallback screen displayed when a non-admin user attempts to enter the Admin Portal.
 */
@Composable
fun AdminAccessDeniedScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(20.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = EtfcRedPrimary.copy(alpha = 0.15f),
                border = BorderStroke(1.dp, EtfcRedPrimary),
                modifier = Modifier.size(72.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Restricted",
                        tint = EtfcRedPrimary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Text(
                text = "ACCESS RESTRICTED",
                color = EtfcRedPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Text(
                text = "The Admin CMS & CRM Portal is restricted to authorized ETFC Arena administrators. Your account currently does not possess elevated administrative privileges.",
                color = EtfcTextSecondary,
                fontSize = 13.sp,
                textAlign = TextAlign.Center,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EtfcGold)
            ) {
                Text(
                    text = "RETURN TO ARENA",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

