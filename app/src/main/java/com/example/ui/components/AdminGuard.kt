package com.example.ui.components

import androidx.compose.runtime.Composable
import com.example.data.models.UserAccount
import com.example.data.models.UserRole

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
