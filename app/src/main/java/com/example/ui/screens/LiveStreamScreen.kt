package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PpvPassEntity
import com.example.data.models.Bout
import com.example.data.models.CameraAngle
import com.example.data.models.ChatMessage
import com.example.data.models.FanChatMessage
import com.example.data.models.Fighter
import com.example.data.models.StreamQuality
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.components.LiveChatContainer
import com.example.ui.components.RbacPaywallOverlay
import com.example.ui.components.VideoPlayerView
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

@Composable
fun LiveStreamScreen(
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
    redSigStrikes: Int,
    blueSigStrikes: Int,
    redTotalStrikes: Int,
    blueTotalStrikes: Int,
    redTakedowns: Int,
    blueTakedowns: Int,
    redControlPercent: Int,
    activePass: PpvPassEntity?,
    fanMessages: List<FanChatMessage>,
    crowdCheerCount: Int,
    // New Auth & Real-Time Chat params
    currentUser: UserAccount?,
    canAccessBout: Boolean,
    liveChatMessages: List<ChatMessage>,
    pinnedChatMessage: ChatMessage?,
    chatInput: String,
    mentionSuggestions: List<Pair<String, String>>,
    isMentionPopupVisible: Boolean,
    slowModeSeconds: Int,
    isVipOnlyChat: Boolean,
    onQualitySelected: (StreamQuality) -> Unit,
    onCameraAngleSelected: (CameraAngle) -> Unit,
    onToggleDataSaver: (Boolean) -> Unit,
    onToggleAudioOnly: (Boolean) -> Unit,
    onTogglePause: () -> Unit,
    onOpenTaleOfTheTape: (Fighter, Fighter) -> Unit,
    onOpenCheckout: () -> Unit,
    onChatInputChanged: (String) -> Unit,
    onInsertMention: (String) -> Unit,
    onSendChatMessage: (String?) -> Unit,
    onSendCheer: (String) -> Unit,
    onPinMessage: (String) -> Unit,
    onUnpinMessage: (String) -> Unit,
    onDeleteMessage: (String, String) -> Unit,
    onTimeoutUser: (String, String, Long) -> Unit,
    onBanUser: (String, String, String) -> Unit,
    onFlagMessage: (String) -> Unit,
    onSetSlowMode: (Int) -> Unit,
    onToggleVipOnly: () -> Unit,
    onOpenAuthDialog: () -> Unit,
    onOpenProfileDrawer: () -> Unit,
    onPredictWinner: (Fighter) -> Unit,
    onBackToHome: () -> Unit = {}
) {
    var userVotedFighterId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .padding(horizontal = 14.dp)
            .testTag("live_stream_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Navigation & Back Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EtfcCardInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorder),
                    modifier = Modifier
                        .clickable { onBackToHome() }
                        .testTag("live_stream_back_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.ChevronLeft,
                            contentDescription = "Exit to Home",
                            tint = EtfcGold,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "Back to Home",
                            color = EtfcTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EtfcLiveRed.copy(alpha = 0.2f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcLiveRed)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(
                            shape = androidx.compose.foundation.shape.CircleShape,
                            color = EtfcLiveRed,
                            modifier = Modifier.size(6.dp)
                        ) {}
                        Text(
                            text = "ETFC 003 LIVE ARENA",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcLiveRed
                        )
                    }
                }
            }
        }
        // User Auth / RBAC Status Badge Bar
        item {
            Spacer(modifier = Modifier.height(2.dp))
            val currentRole = currentUser?.role ?: UserRole.GUEST
            val roleColor = Color(currentRole.colorHex)

            Surface(
                color = EtfcCardInner,
                shape = RoundedCornerShape(10.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenProfileDrawer() }
                    .testTag("live_user_status_bar")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(text = currentUser?.flagEmoji ?: "🇪🇹", fontSize = 14.sp)
                        Text(
                            text = currentUser?.displayName ?: "Guest Viewer",
                            color = EtfcTextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "(${currentUser?.handle ?: "@guest"})",
                            color = EtfcGold,
                            fontSize = 10.sp
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            color = roleColor.copy(alpha = 0.18f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(0.5.dp, roleColor)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                Text(text = currentRole.badgeEmoji, fontSize = 9.sp)
                                Text(
                                    text = currentRole.badgeTitle,
                                    color = roleColor,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "Profile",
                            tint = EtfcTextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        // Video Player Container with RBAC Paywall Protection
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(16.dp))
            ) {
                // Interactive Video Player & Fight Canvas
                VideoPlayerView(
                    bout = bout,
                    streamQuality = streamQuality,
                    currentCameraAngle = currentCameraAngle,
                    isAudioOnly = isAudioOnly,
                    isDataSaver = isDataSaver,
                    isPaused = isPaused,
                    bitrateKbps = bitrateKbps,
                    latencyMs = latencyMs,
                    edgeNodeName = edgeNodeName,
                    liveRound = liveRound,
                    liveSeconds = liveSeconds,
                    chatMessages = fanMessages,
                    onSendCheer = onSendCheer,
                    onQualitySelected = onQualitySelected,
                    onCameraAngleSelected = onCameraAngleSelected,
                    onToggleDataSaver = onToggleDataSaver,
                    onToggleAudioOnly = onToggleAudioOnly,
                    onTogglePause = onTogglePause
                )

                // Enforce RBAC Access Control on the stream
                if (!canAccessBout) {
                    RbacPaywallOverlay(
                        bout = bout,
                        currentUser = currentUser,
                        onUnlockPpv = onOpenCheckout,
                        onSignIn = onOpenAuthDialog
                    )
                }
            }
        }

        // PPV Ticket Pass Status Banner
        item {
            if (activePass != null) {
                Surface(
                    color = Color(0xFF071B0F),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcSuccess.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "🎟️", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "ACTIVE PPV ACCESS: ${activePass.title}",
                                    color = EtfcSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Verified via ${activePass.paymentProvider} • Ref ${activePass.transactionRef}",
                                    color = EtfcTextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Surface(
                            color = EtfcSuccess,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "UNLOCKED",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            } else {
                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.3f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenCheckout() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "👑", fontSize = 16.sp)
                            Column {
                                Text(
                                    text = "Upgrade to VIP Ringside Pass",
                                    color = EtfcGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Corner cams, ref audio, 4K 60fps & 0-latency edge CDN",
                                    color = EtfcTextSecondary,
                                    fontSize = 9.sp
                                )
                            }
                        }
                        Text(
                            text = "BUY PASS →",
                            color = EtfcGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }
        }

        // Live Fight Telemetry Engine HUD Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "LIVE FIGHT TELEMETRY",
                                color = EtfcGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EtfcLiveRed.copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcLiveRed)
                            ) {
                                Text(
                                    text = "ROUND $liveRound",
                                    color = EtfcLiveRed,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { onOpenTaleOfTheTape(bout.fighterRed, bout.fighterBlue) },
                            modifier = Modifier.size(28.dp).testTag("open_tale_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Compare,
                                contentDescription = "Tale of the Tape",
                                tint = EtfcGold,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    TelemetryStatRow(
                        label = "SIGNIFICANT STRIKES",
                        redVal = "$redSigStrikes",
                        blueVal = "$blueSigStrikes",
                        redFraction = if (redSigStrikes + blueSigStrikes > 0) redSigStrikes.toFloat() / (redSigStrikes + blueSigStrikes) else 0.5f
                    )

                    TelemetryStatRow(
                        label = "TOTAL STRIKES LANDED",
                        redVal = "$redTotalStrikes",
                        blueVal = "$blueTotalStrikes",
                        redFraction = if (redTotalStrikes + blueTotalStrikes > 0) redTotalStrikes.toFloat() / (redTotalStrikes + blueTotalStrikes) else 0.5f
                    )

                    TelemetryStatRow(
                        label = "TAKEDOWNS COMPLETED",
                        redVal = "$redTakedowns",
                        blueVal = "$blueTakedowns",
                        redFraction = if (redTakedowns + blueTakedowns > 0) redTakedowns.toFloat() / (redTakedowns + blueTakedowns) else 0.5f
                    )

                    TelemetryStatRow(
                        label = "OCTAGON CONTROL %",
                        redVal = "$redControlPercent%",
                        blueVal = "${100 - redControlPercent}%",
                        redFraction = redControlPercent / 100f
                    )
                }
            }
        }

        // Live Fan Prediction Voting Box
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "LIVE FAN PREDICTION: WHO WINS?",
                        color = EtfcTextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = {
                                userVotedFighterId = bout.fighterRed.id
                                onPredictWinner(bout.fighterRed)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userVotedFighterId == bout.fighterRed.id) EtfcRedPrimary else Color(0xFF3B1214)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("vote_red_btn")
                        ) {
                            Text(
                                text = "🔴 ${bout.fighterRed.name.take(10)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Button(
                            onClick = {
                                userVotedFighterId = bout.fighterBlue.id
                                onPredictWinner(bout.fighterBlue)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (userVotedFighterId == bout.fighterBlue.id) EtfcBlueCorner else Color(0xFF102138)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("vote_blue_btn")
                        ) {
                            Text(
                                text = "🔵 ${bout.fighterBlue.name.take(10)}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }

        // REAL-TIME ARENA LIVE CHAT & MODERATION HUB
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(14.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(420.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Chat Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "ARENA LIVE CHAT",
                                color = EtfcGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            )
                            Surface(
                                shape = CircleShape,
                                color = EtfcLiveRed,
                                modifier = Modifier.size(6.dp)
                            ) {}
                            Text(
                                text = "• $crowdCheerCount cheers",
                                color = EtfcTextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (currentUser?.role?.canModerate() == true) {
                                Surface(
                                    color = EtfcGold.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcGold)
                                ) {
                                    Text(
                                        text = "MOD 🛡️",
                                        color = EtfcGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Embedded Real-Time Chat Container with Mentions & Moderation
                    LiveChatContainer(
                        messages = liveChatMessages,
                        pinnedMessage = pinnedChatMessage,
                        currentUser = currentUser,
                        chatInput = chatInput,
                        mentionSuggestions = mentionSuggestions,
                        isMentionPopupVisible = isMentionPopupVisible,
                        slowModeSeconds = slowModeSeconds,
                        isVipOnlyChat = isVipOnlyChat,
                        onChatInputChange = onChatInputChanged,
                        onInsertMention = onInsertMention,
                        onSendMessage = onSendChatMessage,
                        onSendCheerReaction = onSendCheer,
                        onPinMessage = onPinMessage,
                        onUnpinMessage = onUnpinMessage,
                        onDeleteMessage = onDeleteMessage,
                        onTimeoutUser = onTimeoutUser,
                        onBanUser = onBanUser,
                        onFlagMessage = onFlagMessage,
                        onSetSlowMode = onSetSlowMode,
                        onToggleVipOnly = onToggleVipOnly,
                        onOpenAuthDialog = onOpenAuthDialog,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun TelemetryStatRow(
    label: String,
    redVal: String,
    blueVal: String,
    redFraction: Float
) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = redVal,
                color = EtfcRedPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(42.dp)
            )
            Text(
                text = label,
                color = EtfcTextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = blueVal,
                color = EtfcBlueCorner,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.width(42.dp),
                textAlign = TextAlign.End
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            LinearProgressIndicator(
                progress = { redFraction.coerceIn(0.05f, 0.95f) },
                modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = EtfcRedPrimary,
                trackColor = EtfcCardInner
            )
            LinearProgressIndicator(
                progress = { (1f - redFraction).coerceIn(0.05f, 0.95f) },
                modifier = Modifier.weight(1f).height(4.dp).clip(RoundedCornerShape(2.dp)),
                color = EtfcBlueCorner,
                trackColor = EtfcCardInner
            )
        }
    }
}
