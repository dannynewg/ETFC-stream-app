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
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VerifiedUser
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
 * Professional Settings & User Profile Management Screen
 */
@Composable
fun SettingsScreen(
    viewModel: EtfcViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val activePass by viewModel.activePass.collectAsState()
    val userRole = uiState.currentUser?.role ?: UserRole.USER
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var isEditProfileDialogOpen by remember { mutableStateOf(false) }
    var editNameInput by remember { mutableStateOf(uiState.currentUser?.displayName ?: "") }
    var editHandleInput by remember { mutableStateOf(uiState.currentUser?.handle ?: "") }
    var editPhoneInput by remember { mutableStateOf(uiState.currentUser?.phoneNumber ?: "") }

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
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // =========================================================================
            // 1. USER PROFILE & IDENTITY CARD
            // =========================================================================
            item {
                val user = uiState.currentUser
                val isSuperAdmin = user?.email.equals("dannynewg@gmail.com", ignoreCase = true)

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, if (isSuperAdmin) EtfcGold else EtfcBorder),
                    modifier = Modifier.fillMaxWidth().testTag("profile_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EtfcCardInner,
                                    border = BorderStroke(2.dp, if (isSuperAdmin) EtfcGold else EtfcBorderGold),
                                    modifier = Modifier.size(54.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = user?.flagEmoji ?: "🇪🇹",
                                            fontSize = 24.sp
                                        )
                                    }
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = user?.displayName ?: "Fan Account",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EtfcTextPrimary
                                        )
                                        if (isSuperAdmin) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(text = "👑", fontSize = 14.sp)
                                        }
                                    }
                                    Text(
                                        text = user?.email ?: "guest@etfc.stream",
                                        fontSize = 11.sp,
                                        color = EtfcGold
                                    )
                                    Text(
                                        text = "Handle: ${user?.handle ?: "@guest"} • ${user?.phoneNumber ?: "+251 911 234 567"}",
                                        fontSize = 10.sp,
                                        color = EtfcTextMuted
                                    )
                                }
                            }

                            // Edit profile button
                            IconButton(
                                onClick = {
                                    editNameInput = user?.displayName ?: ""
                                    editHandleInput = user?.handle ?: ""
                                    editPhoneInput = user?.phoneNumber ?: ""
                                    isEditProfileDialogOpen = true
                                },
                                modifier = Modifier.testTag("edit_profile_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = EtfcBorderSubtle)
                        Spacer(modifier = Modifier.height(10.dp))

                        // Role & Account Action Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isSuperAdmin) EtfcGold.copy(alpha = 0.2f) else EtfcSurfaceVariant,
                                border = BorderStroke(1.dp, if (isSuperAdmin) EtfcGold else EtfcBorder)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.VerifiedUser,
                                        contentDescription = null,
                                        tint = if (isSuperAdmin) EtfcGold else EtfcTextSecondary,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Text(
                                        text = if (isSuperAdmin) "SUPERADMIN" else userRole.badgeTitle.uppercase(),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        color = if (isSuperAdmin) EtfcGold else EtfcTextSecondary
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.openAuthDialog() },
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, EtfcBorder),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextSecondary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("switch_account_btn")
                                ) {
                                    Icon(Icons.Default.SwitchAccount, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Switch", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { viewModel.logout() },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EtfcSurfaceVariant, contentColor = Color(0xFFFF6B6B)),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp).testTag("logout_btn")
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Sign Out", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 2. SUBSCRIPTION & LIVE PPV PASS STATUS (600 ETB Single Tier)
            // =========================================================================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcBorderGold),
                    modifier = Modifier.fillMaxWidth().testTag("subscription_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Text(
                                    text = "ETFC 003 LIVE ACCESS PASS",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EtfcGold,
                                    letterSpacing = 0.5.sp
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (activePass != null || userRole.canAccessTier(com.example.data.models.ContentTier.STANDARD_PPV_MAIN_CARD)) EtfcSuccess.copy(alpha = 0.2f) else EtfcLiveRed.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, if (activePass != null || userRole.canAccessTier(com.example.data.models.ContentTier.STANDARD_PPV_MAIN_CARD)) EtfcSuccess else EtfcLiveRed)
                            ) {
                                Text(
                                    text = if (activePass != null || userRole.canAccessTier(com.example.data.models.ContentTier.STANDARD_PPV_MAIN_CARD)) "ACTIVE" else "LOCKED",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (activePass != null || userRole.canAccessTier(com.example.data.models.ContentTier.STANDARD_PPV_MAIN_CARD)) EtfcSuccess else EtfcLiveRed,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        if (activePass != null || userRole.canAccessTier(com.example.data.models.ContentTier.STANDARD_PPV_MAIN_CARD)) {
                            Text(
                                text = "All-access live broadcast pass unlocked. Full 1080p single HLS feed active.",
                                fontSize = 11.sp,
                                color = EtfcTextSecondary
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EtfcCardInner,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = "PASS: ${activePass?.title ?: "ETFC 003 All-Access Pass (600 ETB)"}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = EtfcGold
                                    )
                                    Text(
                                        text = "Signature: ${activePass?.signatureToken ?: "SECURE-AUTH-ACTIVE"}",
                                        fontSize = 9.sp,
                                        color = EtfcTextMuted
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = "Get complete live access to all ETFC 003 championship and prelim bouts for a single flat price.",
                                fontSize = 11.sp,
                                color = EtfcTextSecondary
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(text = "Single Event Price", fontSize = 10.sp, color = EtfcTextMuted)
                                    Text(text = "600 ETB", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EtfcGold)
                                }
                                Button(
                                    onClick = {
                                        val tier = viewModel.ppvTiers.firstOrNull()
                                        viewModel.openCheckoutDialog(tier)
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                                    modifier = Modifier.testTag("buy_pass_settings_btn")
                                ) {
                                    Text("BUY PASS (600 ETB)", fontWeight = FontWeight.Black, fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // 3. ADMIN CRM PORTAL (AdminGuard: only for dannynewg@gmail.com or ADMIN role)
            // =========================================================================
            if (userRole == UserRole.ADMIN || uiState.currentUser?.email.equals("dannynewg@gmail.com", ignoreCase = true) || uiState.currentUser?.isAdmin == true) {
                item {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = EtfcCard),
                        border = BorderStroke(1.dp, EtfcBorderGold),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { viewModel.openAdminPortal() }
                            .testTag("admin_crm_portal_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Surface(
                                    shape = CircleShape,
                                    color = EtfcGold.copy(alpha = 0.2f),
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.AdminPanelSettings,
                                            contentDescription = "Admin Portal",
                                            tint = EtfcGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = "ETFC ADMIN CRM & FIGHT CRM",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EtfcGold
                                    )
                                    Text(
                                        text = "Manage fighters, matches, cloud sync & invite admins",
                                        fontSize = 10.sp,
                                        color = EtfcTextSecondary
                                    )
                                }
                            }
                            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EtfcGold)
                        }
                    }
                }
            }

            // =========================================================================
            // 4. PLAYBACK & NETWORK PREFERENCES
            // =========================================================================
            item {
                SettingsSectionContainer(title = "PLAYBACK & STREAMING OPTIONS", icon = Icons.Default.Tune) {
                    // Stream Resolution Preference
                    Text(
                        text = "Preferred Streaming Quality (Single HLS Ladder)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcTextPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(StreamQuality.AUTO_ABR, StreamQuality.HD_1080P, StreamQuality.HD_720P, StreamQuality.SAVER_240P).forEach { quality ->
                            val isSelected = uiState.streamQuality == quality
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setStreamQuality(quality) },
                                label = {
                                    Text(
                                        quality.resolutionLabel,
                                        fontSize = 10.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EtfcGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = EtfcCardInner,
                                    labelColor = EtfcTextSecondary
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = EtfcBorderSubtle)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Low Latency Edge Mode
                    SettingsToggleRow(
                        title = "Ultra-Low Latency Mode (<25ms)",
                        subtitle = "Direct AWS elemental synchronization for live punch tracking",
                        isChecked = uiState.isLowLatencyMode,
                        onCheckedChange = { viewModel.toggleLowLatencyMode(it) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hardware Acceleration
                    SettingsToggleRow(
                        title = "Hardware Video Acceleration",
                        subtitle = "Utilize device GPU for 60fps frame rendering and reduced battery usage",
                        isChecked = uiState.isHardwareAccelerationEnabled,
                        onCheckedChange = { viewModel.toggleHardwareAcceleration(it) }
                    )
                }
            }

            // =========================================================================
            // 5. NOTIFICATION ALERTS
            // =========================================================================
            item {
                SettingsSectionContainer(title = "MATCH NOTIFICATIONS & ALERTS", icon = Icons.Default.NotificationsActive) {
                    SettingsToggleRow(
                        title = "Live Event Broadcast Alerts",
                        subtitle = "Receive notifications when ETFC 003 arena doors open",
                        isChecked = uiState.isLiveStartAlertEnabled,
                        onCheckedChange = { viewModel.toggleLiveStartAlert(it) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsToggleRow(
                        title = "Bout Walkout Notifications",
                        subtitle = "Alert when main card fighters start cage entrance",
                        isChecked = uiState.isBoutStartAlertEnabled,
                        onCheckedChange = { viewModel.toggleBoutStartAlert(it) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsToggleRow(
                        title = "Knockout & Finish Instant Replays",
                        subtitle = "Immediate push notification upon KO/TKO or submission finish",
                        isChecked = uiState.isKnockoutAlertEnabled,
                        onCheckedChange = { viewModel.toggleKnockoutAlert(it) }
                    )
                }
            }

            // =========================================================================
            // 6. SECURITY & DRM PROTECTION
            // =========================================================================
            item {
                SettingsSectionContainer(title = "SECURITY & DRM SHIELD", icon = Icons.Default.Security) {
                    SettingsToggleRow(
                        title = "Anti-Piracy DRM Screen Shield",
                        subtitle = "Blocks unauthorized background screen recorders & capture tools",
                        isChecked = uiState.isDrmScreenShieldEnabled,
                        onCheckedChange = { viewModel.toggleDrmScreenShield(it) }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    SettingsToggleRow(
                        title = "Biometric Arena Lock",
                        subtitle = "Require fingerprint/face auth when entering PPV streams",
                        isChecked = uiState.isBiometricAuthEnabled,
                        onCheckedChange = { viewModel.toggleBiometricAuth(it) }
                    )
                }
            }

            // =========================================================================
            // 7. ABOUT ETFC & OFFICIAL CONTACT INFO
            // =========================================================================
            item {
                SettingsSectionContainer(title = "ABOUT ETFC & CONTACT SUPPORT", icon = Icons.Default.HelpOutline) {
                    // cPanel Domain link
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EtfcCardInner)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://etfcstream.rf.gd"))
                                context.startActivity(intent)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Official Web Portal", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EtfcTextPrimary)
                                Text("etfcstream.rf.gd", fontSize = 10.sp, color = EtfcGold)
                            }
                        }
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact Phone
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EtfcCardInner)
                            .clickable {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+251911234567"))
                                context.startActivity(intent)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Call, contentDescription = null, tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Customer Support Helpline", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EtfcTextPrimary)
                                Text("+251 911 234 567 (Addis Ababa)", fontSize = 10.sp, color = EtfcTextSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EtfcTextMuted, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Contact Email
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EtfcCardInner)
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:support@etfc.stream"))
                                context.startActivity(intent)
                            }
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Email, contentDescription = null, tint = EtfcBlueCorner, modifier = Modifier.size(18.dp))
                            Column {
                                Text("Email Support & Media Press", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EtfcTextPrimary)
                                Text("support@etfc.stream", fontSize = 10.sp, color = EtfcTextSecondary)
                            }
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = EtfcTextMuted, modifier = Modifier.size(16.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // App Info & Headquarters
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "ETFC STREAM v3.4.0 (Build 304)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold
                        )
                        Text(
                            text = "Ethiopian Fight Championship • Adwa 00 Museum, Addis Ababa",
                            fontSize = 9.sp,
                            color = EtfcTextMuted
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        )
    }

    // =========================================================================
    // EDIT PROFILE DIALOG
    // =========================================================================
    if (isEditProfileDialogOpen) {
        AlertDialog(
            onDismissRequest = { isEditProfileDialogOpen = false },
            containerColor = EtfcCard,
            title = {
                Text(
                    text = "Edit Fighter Profile",
                    color = EtfcGold,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editNameInput,
                        onValueChange = { editNameInput = it },
                        label = { Text("Display Name") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorder,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editHandleInput,
                        onValueChange = { editHandleInput = it },
                        label = { Text("Arena Handle (@username)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorder,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = editPhoneInput,
                        onValueChange = { editPhoneInput = it },
                        label = { Text("Phone Number (+251)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorder,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.updateProfile(editNameInput, editHandleInput, editPhoneInput)
                        isEditProfileDialogOpen = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
                ) {
                    Text("SAVE CHANGES", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { isEditProfileDialogOpen = false }) {
                    Text("Cancel", color = EtfcTextMuted)
                }
            }
        )
    }
}

@Composable
fun SettingsSectionContainer(
    title: String,
    icon: ImageVector,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcBorder),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            content()
        }
    }
}

@Composable
fun SettingsToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) },
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = 12.dp)) {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextPrimary
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = EtfcTextSecondary
            )
        }
        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.Black,
                checkedTrackColor = EtfcGold,
                uncheckedThumbColor = EtfcTextMuted,
                uncheckedTrackColor = EtfcCardInner
            )
        )
    }
}
