package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.PaymentProvider
import com.example.data.models.StreamQuality
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderGold
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
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.EtfcViewModel

/**
 * SettingsScreen:
 * Comprehensive User Settings & App Preferences screen.
 */
@Composable
fun SettingsScreen(
    viewModel: EtfcViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val activePass by viewModel.activePass.collectAsState()
    val userRole = uiState.currentUser?.role ?: UserRole.VIP_MEMBER
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isEditProfileDialogOpen by remember { mutableStateOf(false) }
    var isPromoCodeDialogOpen by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }
    var isAboutDialogOpen by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.settingsSnackbarMessage) {
        uiState.settingsSnackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg, duration = SnackbarDuration.Short)
            viewModel.dismissSettingsSnackbar()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .testTag("settings_screen")
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Header Title
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = null,
                                tint = EtfcGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "SETTINGS & PREFERENCES",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcTextPrimary,
                                letterSpacing = 0.5.sp
                            )
                        }
                        Text(
                            text = "Manage your stream, profile, payments & notifications",
                            fontSize = 11.sp,
                            color = EtfcTextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EtfcGold.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, EtfcGold)
                    ) {
                        Text(
                            text = "v2.4.0",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // 2. Profile & RBAC Account Card
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcBorderGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(EtfcGold, EtfcRedPrimary)
                                        )
                                    )
                                    .border(2.dp, EtfcGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = userRole.badgeEmoji,
                                    fontSize = 24.sp
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = uiState.currentUser?.displayName ?: "Yonas Habesha",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EtfcTextPrimary
                                    )
                                    Surface(
                                        color = EtfcGold.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, EtfcGold)
                                    ) {
                                        Text(
                                            text = userRole.badgeTitle.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EtfcGold,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = uiState.currentUser?.email ?: "yonas.ethiopia@etfc.com",
                                    fontSize = 12.sp,
                                    color = EtfcTextSecondary
                                )
                                Text(
                                    text = "Wallet: +251 ${uiState.phoneNumberInput} (${uiState.preferredMobileWallet.displayName})",
                                    fontSize = 11.sp,
                                    color = EtfcTextMuted
                                )
                            }
                        }

                        HorizontalDivider(color = EtfcBorderSubtle)

                        // Role & Profile Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { viewModel.openAuthDialog(0) },
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, EtfcBorder),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("switch_account_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Switch Role / User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { viewModel.openProfileDrawer() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcGold,
                                    contentColor = Color.Black
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("edit_profile_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Profile", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // 2.5 ADMIN CMS & GITHUB REPO SYNC CONTROL PANEL
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1408)),
                    border = BorderStroke(1.5.dp, EtfcGold),
                    modifier = Modifier.fillMaxWidth().testTag("admin_cms_settings_card")
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EtfcGold.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, EtfcGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier.padding(6.dp).size(22.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ADMIN CMS & GITHUB REPO SYNC",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 13.sp,
                                        color = EtfcGold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EtfcSuccess.copy(alpha = 0.2f),
                                        border = BorderStroke(0.5.dp, EtfcSuccess)
                                    ) {
                                        Text(
                                            text = "ONLINE",
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EtfcSuccess,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Post match posters, YouTube links, update fighter stats & sync SQLite from GitHub",
                                    fontSize = 10.5.sp,
                                    color = EtfcTextMuted
                                )
                            }
                        }

                        HorizontalDivider(color = EtfcBorderGold.copy(alpha = 0.4f))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.openAdminPortal() },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                                    .testTag("launch_admin_portal_btn")
                            ) {
                                Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("OPEN ADMIN PORTAL", fontSize = 11.sp, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }

            // 3. YouTube Match Archive & In-App Playback Section
            item {
                SettingsSectionContainer(
                    title = "YOUTUBE FIGHT ARCHIVE & PLAYLIST",
                    subtitle = "Embedded in-app player integration for previous matches and event replays",
                    icon = Icons.Default.SmartDisplay,
                    iconTint = EtfcRedPrimary
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Official YouTube Playlist",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EtfcTextPrimary
                                )
                                Text(
                                    text = "Adwa Fight Night 002 archive & fight highlights",
                                    fontSize = 11.sp,
                                    color = EtfcTextMuted
                                )
                            }

                            Button(
                                onClick = { viewModel.openYouTubePlayer(null) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcRedPrimary,
                                    contentColor = Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("play_yt_playlist_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayCircleFilled,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PLAY IN-APP", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                val intent = Intent(
                                    Intent.ACTION_VIEW,
                                    Uri.parse("https://youtube.com/playlist?list=PLeRATZC0Qfoo&si=dk9LKYg2Tdcg-uVm")
                                )
                                context.startActivity(intent)
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorder),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcGold),
                            modifier = Modifier.fillMaxWidth().height(38.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open Playlist on YouTube App", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            // 4. Video & Streaming Quality Settings
            item {
                SettingsSectionContainer(
                    title = "STREAMING QUALITY & CDN ROUTING",
                    subtitle = "Adaptive bitrate streaming and East Africa Edge node controls",
                    icon = Icons.Default.Tv,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        // Quality chips
                        Text(
                            text = "Default Broadcast Resolution",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val qualities = listOf(
                                StreamQuality.AUTO_ABR to "Auto ABR",
                                StreamQuality.HD_1080P to "1080p 60fps",
                                StreamQuality.HD_720P to "720p HD",
                                StreamQuality.SMOOTH_480P to "480p SD",
                                StreamQuality.SAVER_240P to "240p Saver"
                            )

                            qualities.forEach { (quality, label) ->
                                val isSelected = uiState.streamQuality == quality
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setStreamQuality(quality) },
                                    label = {
                                        Text(
                                            text = label,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Normal
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = EtfcBorder,
                                        selectedBorderColor = EtfcGold
                                    ),
                                    modifier = Modifier.testTag("quality_chip_${quality.name}")
                                )
                            }
                        }

                        HorizontalDivider(color = EtfcBorderSubtle)

                        // Switches: Low Latency, Data Saver, Hardware Acceleration
                        SettingsSwitchRow(
                            title = "Ultra Low Latency Mode (<30ms)",
                            subtitle = "Direct live edge stream with minimal commentary delay",
                            icon = Icons.Default.Speed,
                            checked = uiState.isLowLatencyMode,
                            onCheckedChange = { viewModel.toggleLowLatencySetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "Mobile Cellular Data Saver",
                            subtitle = "Automatically caps video bitrate to 240p on cellular networks",
                            icon = Icons.Default.Wifi,
                            checked = uiState.isDataSaverActive,
                            onCheckedChange = { viewModel.toggleDataSaverSetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "GPU Hardware Video Acceleration",
                            subtitle = "Uses dedicated hardware decoders for smooth 60fps frame rendering",
                            icon = Icons.Default.GraphicEq,
                            checked = uiState.isHardwareAccelerationEnabled,
                            onCheckedChange = { viewModel.toggleHardwareAccelerationSetting(it) }
                        )
                    }
                }
            }

            // 5. Octagon Audio & Arena Experience
            item {
                SettingsSectionContainer(
                    title = "OCTAGON AUDIO & ARENA HAPTICS",
                    subtitle = "Corner audio routing, referee mics & tactile strike feedback",
                    icon = Icons.Default.Headphones,
                    iconTint = EtfcBlueCorner
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Default Octagon Audio Track",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextPrimary
                        )

                        val audioFeeds = listOf(
                            "Main Broadcast Commentary",
                            "Red Corner Microphone",
                            "Blue Corner Microphone",
                            "Cage Atmosphere Only"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            audioFeeds.take(2).forEach { feed ->
                                val isSelected = uiState.preferredAudioFeed == feed
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setPreferredAudioFeedSetting(feed) },
                                    label = { Text(feed, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcBlueCorner,
                                        selectedLabelColor = Color.White,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = EtfcBorder,
                                        selectedBorderColor = EtfcBlueCorner
                                    )
                                )
                            }
                        }

                        SettingsSwitchRow(
                            title = "Heavy Strike Haptic Feedback",
                            subtitle = "Vibrates device on massive hooks, head kicks & knockdowns",
                            icon = Icons.Default.Vibration,
                            checked = uiState.isHapticFeedbackEnabled,
                            onCheckedChange = { viewModel.toggleHapticFeedbackSetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "Live Chat Profanity Filter",
                            subtitle = "Shields harassment and toxic messages in live arena fan chat",
                            icon = Icons.Default.Security,
                            checked = uiState.chatProfanityFilterEnabled,
                            onCheckedChange = { viewModel.toggleChatProfanityFilter(it) }
                        )
                    }
                }
            }

            // 6. Payment, Mobile Wallet & PPV Purchases
            item {
                SettingsSectionContainer(
                    title = "PAYMENT WALLETS & PPV PASSES",
                    subtitle = "1-Tap checkout preferences with Telebirr, M-Pesa & CBE Birr",
                    icon = Icons.Default.Payments,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "Preferred Mobile Checkout Wallet",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextPrimary
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            PaymentProvider.entries.forEach { provider ->
                                val isSelected = uiState.preferredMobileWallet == provider
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setPreferredMobileWalletSetting(provider) },
                                    label = {
                                        Text(
                                            text = provider.displayName,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Black else FontWeight.Medium
                                        )
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = EtfcBorder,
                                        selectedBorderColor = EtfcGold
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        // Active Pass Status
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("ACTIVE PPV PASS", color = EtfcGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    Text(
                                        text = activePass?.title ?: "No Active Pass (Free Prelims)",
                                        color = EtfcTextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = if (activePass != null) "Unlocked 4K Stream + Replays" else "Tap below to unlock main card & title fights",
                                        color = EtfcTextMuted,
                                        fontSize = 10.sp
                                    )
                                }

                                Button(
                                    onClick = { viewModel.selectTab(AppTab.PPV_PASSES) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EtfcGold,
                                        contentColor = Color.Black
                                    ),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("EXPLORE PPV", fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }
            }

            // 7. Push Notifications & Event Alerts
            item {
                SettingsSectionContainer(
                    title = "EVENT ALERTS & NOTIFICATIONS",
                    subtitle = "Get notified before walkouts, title fight starts and instant KO finishes",
                    icon = Icons.Default.NotificationsActive,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SettingsSwitchRow(
                            title = "Event Broadcast Reminders",
                            subtitle = "Alert 30 min and 10 min before main broadcast goes live",
                            icon = Icons.Default.Notifications,
                            checked = uiState.isLiveStartAlertEnabled,
                            onCheckedChange = { viewModel.toggleLiveStartAlertSetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "Fight Walkout & Bout Start Alerts",
                            subtitle = "Instant alert when main card fighters make cage entrance",
                            icon = Icons.Default.SportsMma,
                            checked = uiState.isBoutStartAlertEnabled,
                            onCheckedChange = { viewModel.toggleBoutStartAlertSetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "Instant Knockout & Decision Results",
                            subtitle = "High-priority push alert whenever a fight terminates with a KO/TKO",
                            icon = Icons.Default.Speed,
                            checked = uiState.isKnockoutAlertEnabled,
                            onCheckedChange = { viewModel.toggleKnockoutAlertSetting(it) }
                        )

                        OutlinedButton(
                            onClick = {
                                viewModel.triggerKoAlertNotification(
                                    winner = "SEDO",
                                    loser = "JHONNY",
                                    method = "KO (Adwa Hammer Cross)",
                                    round = 3,
                                    time = "2:48"
                                )
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderGold),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcGold),
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("test_notification_btn")
                        ) {
                            Text("🔔 Test Local Fight Alert Notification", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // 8. Language, Currency & Regional Settings
            item {
                SettingsSectionContainer(
                    title = "LANGUAGE & CURRENCY",
                    subtitle = "Localized Ethiopian & East African regional formats",
                    icon = Icons.Default.Language,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = "App Interface Language",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextPrimary
                        )

                        val languages = listOf("English", "አማርኛ (Amharic)", "ኦሮምኛ (Afaan)", "ትግርኛ (Tigrinya)")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            languages.forEach { lang ->
                                val isSelected = uiState.selectedLanguage == lang
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateLanguage(lang) },
                                    label = { Text(lang, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = EtfcBorder,
                                        selectedBorderColor = EtfcGold
                                    )
                                )
                            }
                        }

                        HorizontalDivider(color = EtfcBorderSubtle)

                        Text(
                            text = "Billing Currency",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextPrimary
                        )

                        val currencies = listOf("ETB (Ethiopian Birr - Br)", "KES (Kenyan Shilling - Ksh)", "USD ($)")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            currencies.forEach { curr ->
                                val isSelected = uiState.selectedCurrency == curr
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.updateCurrency(curr) },
                                    label = { Text(curr, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    ),
                                    border = FilterChipDefaults.filterChipBorder(
                                        enabled = true,
                                        selected = isSelected,
                                        borderColor = EtfcBorder,
                                        selectedBorderColor = EtfcGold
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // 9. Security, DRM Shield & Storage Cache
            item {
                SettingsSectionContainer(
                    title = "SECURITY, DRM & STORAGE",
                    subtitle = "Biometric purchases, anti-piracy shield & stream buffer cleanup",
                    icon = Icons.Default.Security,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SettingsSwitchRow(
                            title = "Biometric / PIN Gate on Checkout",
                            subtitle = "Require fingerprint/PIN before processing mobile wallet charges",
                            icon = Icons.Default.Fingerprint,
                            checked = uiState.isBiometricAuthEnabled,
                            onCheckedChange = { viewModel.toggleBiometricAuthSetting(it) }
                        )

                        SettingsSwitchRow(
                            title = "Anti-Piracy DRM Screen Shield",
                            subtitle = "Blocks unauthorized screen capture/recording during live PPV bouts",
                            icon = Icons.Default.Lock,
                            checked = uiState.isDrmScreenShieldEnabled,
                            onCheckedChange = { viewModel.toggleDrmScreenShieldSetting(it) }
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Temporary Stream Buffer Cache",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EtfcTextPrimary
                                )
                                Text(
                                    text = "Cleans up temporary video chunks and cached player thumbnails",
                                    fontSize = 11.sp,
                                    color = EtfcTextMuted
                                )
                            }

                            Button(
                                onClick = { viewModel.clearAppCache() },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcSurfaceVariant,
                                    contentColor = EtfcGold
                                ),
                                border = BorderStroke(1.dp, EtfcBorderGold),
                                modifier = Modifier.testTag("clear_cache_btn")
                            ) {
                                Icon(imageVector = Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CLEAR CACHE", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // 10. Support, Community & About ETFC
            item {
                SettingsSectionContainer(
                    title = "ABOUT ETFC & HELPLINE",
                    subtitle = "Customer support, official community channels & broadcast licensing",
                    icon = Icons.Default.HelpOutline,
                    iconTint = EtfcGold
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SettingsNavRow(
                            title = "Telegram Official Support Bot",
                            subtitle = "@etfc_support_bot • Instant 24/7 assistance",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/etfc_official"))
                                context.startActivity(intent)
                            }
                        )

                        SettingsNavRow(
                            title = "WhatsApp Customer Helpline",
                            subtitle = "+251 911 000 000 • Addis Ababa Support Desk",
                            onClick = {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/251911000000"))
                                context.startActivity(intent)
                            }
                        )

                        SettingsNavRow(
                            title = "Broadcast License & Terms of Service",
                            subtitle = "ETFC Combat Sports Entertainment Group 2026",
                            onClick = { isAboutDialogOpen = true }
                        )
                    }
                }
            }
        }

        // Snackbar Host for feedback
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        )
    }

    // About Dialog
    if (isAboutDialogOpen) {
        AlertDialog(
            onDismissRequest = { isAboutDialogOpen = false },
            title = {
                Text(
                    text = "ETFC Streaming Platform",
                    color = EtfcGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Ethiopian Top Fighting Championship (ETFC) v2.4.0\n\n" +
                            "Official combat sports broadcast network of Ethiopia and East Africa. " +
                            "Featuring live PPV broadcasts, real-time corner audio switching, Telebirr & M-Pesa 1-tap checkout, " +
                            "interactive fighter chat and full YouTube match archives.\n\n" +
                            "© 2026 ETFC Entertainment. All rights reserved.",
                    color = EtfcTextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { isAboutDialogOpen = false },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
                ) {
                    Text("OK", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = EtfcCard,
            shape = RoundedCornerShape(12.dp)
        )
    }
}

@Composable
fun SettingsSectionContainer(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconTint: Color = EtfcGold,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
                Column {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcTextPrimary,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = subtitle,
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )
                }
            }

            HorizontalDivider(color = EtfcBorderSubtle)

            content()
        }
    }
}

@Composable
fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    icon: ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (checked) EtfcGold else EtfcTextMuted,
                modifier = Modifier.size(18.dp)
            )
            Column {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcTextPrimary
                )
                Text(
                    text = subtitle,
                    fontSize = 10.5.sp,
                    color = EtfcTextMuted,
                    lineHeight = 14.sp
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EtfcGold,
                uncheckedThumbColor = EtfcTextMuted,
                uncheckedTrackColor = EtfcCardInner
            ),
            modifier = Modifier.testTag("switch_${title.lowercase().replace(" ", "_")}")
        )
    }
}

@Composable
fun SettingsNavRow(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = EtfcTextMuted
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = EtfcTextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
