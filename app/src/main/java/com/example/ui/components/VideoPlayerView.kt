package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Bout
import com.example.data.models.CameraAngle
import com.example.data.models.FanChatMessage
import com.example.data.models.StreamQuality
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun VideoPlayerView(
    bout: Bout,
    streamQuality: StreamQuality,
    currentCameraAngle: CameraAngle,
    isAudioOnly: Boolean,
    isDataSaver: Boolean,
    isPaused: Boolean,
    bitrateKbps: Int,
    latencyMs: Int,
    edgeNodeName: String,
    liveRound: Int,
    liveSeconds: Int,
    chatMessages: List<FanChatMessage> = emptyList(),
    redSigStrikes: Int = 52,
    blueSigStrikes: Int = 41,
    redTotalStrikes: Int = 74,
    blueTotalStrikes: Int = 59,
    redTakedowns: Int = 1,
    blueTakedowns: Int = 2,
    redControlPercent: Int = 58,
    onQualitySelected: (StreamQuality) -> Unit,
    onCameraAngleSelected: (CameraAngle) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    onToggleAudioOnly: (Boolean) -> Unit,
    onTogglePause: () -> Unit,
    onSendCheer: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // 🛡️ Enforce Anti-Screenshot and Anti-Screen-Recording DRM protection while video playback is active
    SecureScreenEffect(isSecured = !isPaused)

    var showQualityMenu by remember { mutableStateOf(false) }
    var showAngleMenu by remember { mutableStateOf(false) }
    var showChatOverlay by remember { mutableStateOf(false) }
    var showFightStatsOverlay by remember { mutableStateOf(false) }

    // Pulsing animations for live fight action
    val infiniteTransition = rememberInfiniteTransition(label = "player_anim")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    val strikeXOffset by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "strike_motion"
    )

    val minutes = liveSeconds / 60
    val seconds = liveSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(20.dp)),
        colors = CardDefaults.cardColors(containerColor = EtfcCard)
    ) {
        Column {
            // Main Video Viewport (16:9 Aspect Ratio)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color(0xFF030303))
                    .testTag("video_player_container"),
                contentAlignment = Alignment.Center
            ) {
                if (isAudioOnly) {
                    // Audio-Only Radio stream banner with animated sound waves
                    AudioOnlyOverlay(
                        bout = bout,
                        pulseAlpha = pulseAlpha,
                        bitrateKbps = bitrateKbps,
                        onSwitchToVideo = { onToggleAudioOnly(false) }
                    )
                } else {
                    // Octagon Fight Arena Action Canvas
                    ArenaFightCanvas(
                        bout = bout,
                        pulseAlpha = pulseAlpha,
                        strikeOffset = strikeXOffset,
                        cameraAngle = currentCameraAngle,
                        isPaused = isPaused
                    )
                }

                // 1. LIVE CHAT OVERLAY (Semi-transparent overlay over video stream)
                if (showChatOverlay && !isAudioOnly) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 44.dp, bottom = 44.dp, start = 10.dp, end = 100.dp)
                    ) {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(4.dp),
                            reverseLayout = true
                        ) {
                            items(chatMessages.take(4)) { msg ->
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(8.dp),
                                    border = androidx.compose.foundation.BorderStroke(
                                        0.5.dp,
                                        if (msg.isSuperFan) EtfcGold.copy(alpha = 0.5f) else Color.White.copy(alpha = 0.15f)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = msg.userFlag, fontSize = 10.sp)
                                        Text(
                                            text = msg.userName,
                                            color = if (msg.isSuperFan) EtfcGold else Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = msg.message,
                                            color = Color(0xFFE5E7EB),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }

                        // Floating reaction trigger on overlay
                        if (onSendCheer != null) {
                            Row(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(top = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                listOf("🔥", "🥊", "🇪🇹", "🏆").forEach { emoji ->
                                    Surface(
                                        color = Color.Black.copy(alpha = 0.7f),
                                        shape = CircleShape,
                                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle),
                                        modifier = Modifier.clickable { onSendCheer(emoji) }
                                    ) {
                                        Text(
                                            text = emoji,
                                            fontSize = 11.sp,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. CURRENT FIGHT STATISTICS HUD OVERLAY (Real-time Live Telemetry)
                if (showFightStatsOverlay && !isAudioOnly) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center)
                            .padding(horizontal = 14.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.85f),
                            shape = RoundedCornerShape(12.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "LIVE COMBAT HUD • R$liveRound TELEMETRY",
                                        color = EtfcGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Text(
                                        text = "⚡ AWS REAL-TIME METRICS",
                                        color = EtfcSuccess,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                // Significant Strikes Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "$redSigStrikes",
                                        color = EtfcRedPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                    Text(
                                        text = "Significant Strikes",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "$blueSigStrikes",
                                        color = EtfcBlueCorner,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }

                                val totalSig = (redSigStrikes + blueSigStrikes).coerceAtLeast(1)
                                val redRatio = redSigStrikes.toFloat() / totalSig.toFloat()
                                LinearProgressIndicator(
                                    progress = { redRatio },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = EtfcRedPrimary,
                                    trackColor = EtfcBlueCorner
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                // Total Strikes & Takedowns Row
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(horizontalAlignment = Alignment.Start) {
                                        Text(text = "Total: $redTotalStrikes", color = EtfcRedPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "TD: $redTakedowns", color = EtfcTextSecondary, fontSize = 9.sp)
                                    }
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(text = "Octagon Control", color = EtfcTextMuted, fontSize = 9.sp)
                                        Text(text = "$redControlPercent% - ${100 - redControlPercent}%", color = EtfcGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(text = "Total: $blueTotalStrikes", color = EtfcBlueCorner, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(text = "TD: $blueTakedowns", color = EtfcTextSecondary, fontSize = 9.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Top Header Overlay (Live Badge, Round Clock, DRM Status, ABR Speed)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.9f), Color.Transparent)
                            )
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Live & Round indicator
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Surface(
                            color = if (isPaused) EtfcSurfaceVariant else EtfcRedPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(5.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = if (isPaused) 0.3f else pulseAlpha))
                                )
                                Text(
                                    text = if (isPaused) "PAUSED" else "LIVE",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                        }

                        Surface(
                            color = Color.Black.copy(alpha = 0.6f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle)
                        ) {
                            Text(
                                text = "R$liveRound • $timeFormatted",
                                color = EtfcGold,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        // 🛡️ DRM Anti-Capture Status Badge
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcSuccess.copy(alpha = 0.5f))
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = EtfcSuccess,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "DRM SECURE",
                                    color = EtfcSuccess,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // ABR & Stream Telemetry info + Quick Toggle Triggers
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            color = Color.Black.copy(alpha = 0.65f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorder)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Speed,
                                    contentDescription = null,
                                    tint = if (streamQuality == StreamQuality.SAVER_240P) EtfcGold else EtfcSuccess,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = "${bitrateKbps}k • ${latencyMs}ms",
                                    color = EtfcTextPrimary,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // 3. ABR RESOLUTION SWITCHING TRIGGER
                        Surface(
                            color = if (showQualityMenu) EtfcRedPrimary else Color.Black.copy(alpha = 0.75f),
                            shape = RoundedCornerShape(4.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, if (showQualityMenu) EtfcRedPrimary else EtfcBorderSubtle),
                            modifier = Modifier
                                .clickable { showQualityMenu = !showQualityMenu }
                                .testTag("quality_selector_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(10.dp)
                                )
                                Text(
                                    text = streamQuality.resolutionLabel,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // In-Player Floating Overlay Toggle Buttons (Bottom Overlay Bar)
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Chat Overlay Toggle Button
                    Surface(
                        color = if (showChatOverlay) EtfcGold else Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (showChatOverlay) EtfcGold else EtfcBorderSubtle),
                        modifier = Modifier
                            .clickable { showChatOverlay = !showChatOverlay }
                            .testTag("chat_overlay_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Chat,
                                contentDescription = "Toggle Chat Overlay",
                                tint = if (showChatOverlay) Color.Black else Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (showChatOverlay) "Chat ON" else "Chat",
                                color = if (showChatOverlay) Color.Black else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Fight Stats HUD Toggle Button
                    Surface(
                        color = if (showFightStatsOverlay) EtfcRedPrimary else Color.Black.copy(alpha = 0.75f),
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, if (showFightStatsOverlay) EtfcRedPrimary else EtfcBorderSubtle),
                        modifier = Modifier
                            .clickable { showFightStatsOverlay = !showFightStatsOverlay }
                            .testTag("stats_hud_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Analytics,
                                contentDescription = "Toggle Fight Stats Overlay",
                                tint = Color.White,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = if (showFightStatsOverlay) "HUD ON" else "Stats HUD",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Camera Angle Pill Overlay
                Surface(
                    color = Color.Black.copy(alpha = 0.75f),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .clickable { showAngleMenu = !showAngleMenu }
                        .testTag("camera_angle_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Videocam,
                            contentDescription = null,
                            tint = EtfcRedPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                        Text(
                            text = currentCameraAngle.shortName,
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Center Play/Pause button on tap
                if (isPaused) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.8f))
                            .border(1.dp, EtfcBorderSubtle, CircleShape)
                            .clickable { onTogglePause() }
                            .testTag("play_pause_overlay_btn"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = EtfcRedPrimary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }

            // 3. ABR RESOLUTION SWITCHING CONTROLS OVERLAY DRAWER
            AnimatedVisibility(visible = showQualityMenu) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EtfcSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "AWS ELEMENTAL ADAPTIVE BITRATE (ABR)",
                            color = EtfcGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "Real-time CDN ladder",
                            color = EtfcTextMuted,
                            fontSize = 9.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    StreamQuality.entries.forEach { quality ->
                        val isSelected = quality == streamQuality
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EtfcRedPrimary.copy(alpha = 0.15f) else Color.Transparent)
                                .clickable {
                                    onQualitySelected(quality)
                                    showQualityMenu = false
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = quality.label,
                                        color = if (isSelected) EtfcRedPrimary else EtfcTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    if (quality == StreamQuality.SAVER_240P) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            color = EtfcSuccess.copy(alpha = 0.2f),
                                            shape = RoundedCornerShape(4.dp)
                                        ) {
                                            Text(
                                                text = "LOW DATA",
                                                color = EtfcSuccess,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = "${quality.description} (~${quality.dataUsagePerHourMb} MB/hr)",
                                    color = EtfcTextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = EtfcRedPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Camera Angle Selection Dropdown View
            AnimatedVisibility(visible = showAngleMenu) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(EtfcSurface)
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CameraAngle.entries.forEach { angle ->
                        val isSelected = angle == currentCameraAngle
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable {
                                    onCameraAngleSelected(angle)
                                    showAngleMenu = false
                                },
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) EtfcRedPrimary else EtfcCardInner,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) EtfcRedPrimary else EtfcBorderSubtle
                            )
                        ) {
                            Text(
                                text = angle.shortName,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .padding(vertical = 8.dp)
                                    .align(Alignment.CenterVertically),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            // Bottom Player Control Toolbar (Play/Pause, Data Saver Quick Toggle, Audio-Only, Edge Node)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EtfcSurface)
                    .border(androidx.compose.foundation.BorderStroke(1.dp, EtfcBorder), RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    IconButton(
                        onClick = onTogglePause,
                        modifier = Modifier.size(36.dp).testTag("bottom_play_pause_btn")
                    ) {
                        Icon(
                            imageVector = if (isPaused) Icons.Default.PlayArrow else Icons.Default.Pause,
                            contentDescription = if (isPaused) "Play" else "Pause",
                            tint = Color.White
                        )
                    }

                    // Quick Data Saver Toggle
                    Surface(
                        color = if (isDataSaver) EtfcSuccess.copy(alpha = 0.2f) else EtfcCardInner,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isDataSaver) EtfcSuccess else EtfcBorder
                        ),
                        modifier = Modifier
                            .clickable { onToggleDataSaver(!isDataSaver) }
                            .testTag("data_saver_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DataSaverOn,
                                contentDescription = null,
                                tint = if (isDataSaver) EtfcSuccess else EtfcTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = if (isDataSaver) "Data Saver ON" else "Data Saver",
                                color = if (isDataSaver) EtfcSuccess else EtfcTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Audio-Only Mode toggle
                    Surface(
                        color = if (isAudioOnly) EtfcBlueCorner.copy(alpha = 0.2f) else EtfcCardInner,
                        shape = RoundedCornerShape(6.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isAudioOnly) EtfcBlueCorner else EtfcBorder
                        ),
                        modifier = Modifier
                            .clickable { onToggleAudioOnly(!isAudioOnly) }
                            .testTag("audio_only_toggle_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Audiotrack,
                                contentDescription = null,
                                tint = if (isAudioOnly) EtfcBlueCorner else EtfcTextSecondary,
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = "Radio",
                                color = if (isAudioOnly) EtfcBlueCorner else EtfcTextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                // Edge CDN badge
                Text(
                    text = "CDN: $edgeNodeName",
                    color = EtfcTextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun ArenaFightCanvas(
    bout: Bout,
    pulseAlpha: Float,
    strikeOffset: Float,
    cameraAngle: CameraAngle,
    isPaused: Boolean
) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2

        // Background Cage Grid Pattern
        val cageBrush = Brush.radialGradient(
            colors = listOf(Color(0xFF150A05), Color(0xFF030303)),
            center = Offset(centerX, centerY),
            radius = width * 0.7f
        )
        drawRect(brush = cageBrush)

        // Draw Octagon Ring Mat
        val octagonRadius = height * 0.42f
        val path = Path()
        for (i in 0 until 8) {
            val angle = i * (2 * PI / 8) - (PI / 8)
            val x = centerX + octagonRadius * cos(angle).toFloat()
            val y = centerY + octagonRadius * sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()

        // Mat Floor fill
        drawPath(
            path = path,
            color = Color(0xFF0D0D0D)
        )
        // Mat Outer Border (ETFC Gold cage posts)
        drawPath(
            path = path,
            color = Color(0xFFFAB818).copy(alpha = 0.35f),
            style = Stroke(width = 3f)
        )

        // Center Mat ETFC Ring Branding
        drawCircle(
            color = Color(0xFFFAB818).copy(alpha = 0.15f),
            radius = octagonRadius * 0.45f,
            center = Offset(centerX, centerY)
        )
        drawCircle(
            color = Color(0xFFFAB818).copy(alpha = 0.4f),
            radius = octagonRadius * 0.45f,
            center = Offset(centerX, centerY),
            style = Stroke(width = 1.5f)
        )

        // Draw Arena Floodlights
        drawLine(
            color = Color(0xFFFAB818).copy(alpha = 0.12f),
            start = Offset(0f, 0f),
            end = Offset(centerX, centerY),
            strokeWidth = 24f
        )
        drawLine(
            color = Color(0xFFEF4444).copy(alpha = 0.10f),
            start = Offset(width, 0f),
            end = Offset(centerX, centerY),
            strokeWidth = 24f
        )

        // Red Corner (Left) and Blue Corner (Right) Positions
        val redX = centerX - width * 0.18f + (if (!isPaused) strikeOffset else 0f)
        val blueX = centerX + width * 0.18f - (if (!isPaused) strikeOffset else 0f)
        val fightersY = centerY + height * 0.05f

        // Red Corner Fighter Aura
        drawCircle(
            color = Color(0xFFFF4E00).copy(alpha = 0.25f * pulseAlpha),
            radius = 36f,
            center = Offset(redX, fightersY)
        )
        drawCircle(
            color = Color(0xFFFF4E00),
            radius = 20f,
            center = Offset(redX, fightersY)
        )

        // Blue Corner Fighter Aura
        drawCircle(
            color = Color(0xFF38BDF8).copy(alpha = 0.25f * pulseAlpha),
            radius = 36f,
            center = Offset(blueX, fightersY)
        )
        drawCircle(
            color = Color(0xFF38BDF8),
            radius = 20f,
            center = Offset(blueX, fightersY)
        )

        // Striking Clash Spark in the center if live
        if (!isPaused) {
            drawLine(
                color = Color(0xFFFFB703).copy(alpha = pulseAlpha),
                start = Offset(redX + 15f, fightersY - 5f),
                end = Offset(blueX - 15f, fightersY - 5f),
                strokeWidth = 3f
            )
        }

        // Referee Icon (Top middle inside cage)
        drawCircle(
            color = Color.White.copy(alpha = 0.8f),
            radius = 10f,
            center = Offset(centerX, centerY - height * 0.22f)
        )
    }

    // Fighter Names and Flag Overlay in Corner
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Red Corner Label
        Column(horizontalAlignment = Alignment.Start) {
            Surface(
                color = EtfcRedPrimary,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "RED CORNER",
                    color = Color.White,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
            Text(
                text = "${bout.fighterRed.flagEmoji} ${bout.fighterRed.name}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\"${bout.fighterRed.nickname}\"",
                color = EtfcGold,
                fontSize = 10.sp
            )
        }

        // VS Badge
        Surface(
            color = Color.Black.copy(alpha = 0.7f),
            shape = CircleShape,
            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle)
        ) {
            Text(
                text = "VS",
                color = EtfcGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Blue Corner Label
        Column(horizontalAlignment = Alignment.End) {
            Surface(
                color = EtfcBlueCorner,
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = "BLUE CORNER",
                    color = Color.Black,
                    fontSize = 8.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                )
            }
            Text(
                text = "${bout.fighterBlue.name} ${bout.fighterBlue.flagEmoji}",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "\"${bout.fighterBlue.nickname}\"",
                color = EtfcBlueCorner,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun AudioOnlyOverlay(
    bout: Bout,
    pulseAlpha: Float,
    bitrateKbps: Int,
    onSwitchToVideo: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF0D0D0D), Color(0xFF050505))
                )
            )
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Audiotrack,
                contentDescription = null,
                tint = EtfcGold,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = "ETFC Live Arena Radio Stream",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "${bout.fighterRed.name} vs ${bout.fighterBlue.name} • Title Fight Commentary",
            color = EtfcTextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Sound bars equalizer simulation
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val heights = listOf(14.dp, 24.dp, 36.dp, 20.dp, 28.dp, 16.dp, 32.dp, 18.dp)
            heights.forEachIndexed { index, barHeight ->
                val dynamicHeight = if (index % 2 == 0) barHeight * pulseAlpha else barHeight * (1.2f - pulseAlpha)
                Box(
                    modifier = Modifier
                        .width(4.dp)
                        .height(dynamicHeight.coerceAtLeast(6.dp))
                        .clip(RoundedCornerShape(2.dp))
                        .background(if (index % 3 == 0) EtfcGold else EtfcRedPrimary)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Surface(
            color = EtfcCardInner,
            shape = RoundedCornerShape(20.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
            modifier = Modifier.clickable { onSwitchToVideo() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Videocam,
                    contentDescription = null,
                    tint = EtfcRedPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Switch to Video Feed ($bitrateKbps kbps)",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
