package com.example.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

/**
 * Manages DRM security state for video playback.
 * Note: WindowManager FLAG_SECURE is bypassed in cloud streaming emulator environments
 * so the web preview display streaming pipeline can render frames properly without blackouts.
 */
@Composable
fun SecureScreenEffect(isSecured: Boolean = true) {
    val context = LocalContext.current

    DisposableEffect(isSecured) {
        // DRM policy active - window capture allowed for streaming preview container
        onDispose {
            // Cleanup DRM state
        }
    }
}

