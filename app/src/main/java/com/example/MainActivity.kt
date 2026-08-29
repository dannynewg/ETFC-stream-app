package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserRole
import com.example.notifications.EtfcNotificationManager
import com.example.ui.components.AuthDialog
import com.example.ui.components.OfflinePlaybackDialog
import com.example.ui.components.PaymentCheckoutDialog
import com.example.ui.components.TaleOfTheTapeDialog
import com.example.ui.components.UserProfileDrawer
import com.example.ui.screens.FightCardScreen
import com.example.ui.screens.FightersScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LiveStreamScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.TicketsPpvScreen
import com.example.ui.components.YouTubeInAppPlayerDialog
import androidx.compose.material.icons.filled.Settings
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
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.EtfcViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EtfcViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        handleNotificationIntent(intent)

        setContent {
            MyApplicationTheme {
                // Request Android 13+ Notification Permission on first launch
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission(),
                        onResult = { isGranted ->
                            // Notification permission handled
                        }
                    )
                    LaunchedEffect(Unit) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }

                EtfcMainApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleNotificationIntent(intent)
    }

    private fun handleNotificationIntent(intent: Intent?) {
        val targetTab = intent?.getStringExtra(EtfcNotificationManager.EXTRA_TARGET_TAB)
        if (targetTab != null) {
            when (targetTab) {
                "HOME" -> viewModel.selectTab(AppTab.HOME)
                "LIVE_ARENA" -> viewModel.selectTab(AppTab.LIVE_ARENA)
                "FIGHT_CARD" -> viewModel.selectTab(AppTab.FIGHT_CARD)
                "PPV_PASSES" -> viewModel.selectTab(AppTab.PPV_PASSES)
                "FIGHTERS" -> viewModel.selectTab(AppTab.FIGHTERS)
                "SETTINGS" -> viewModel.selectTab(AppTab.SETTINGS)
            }
        }
        val boutId = intent?.getStringExtra(EtfcNotificationManager.EXTRA_BOUT_ID)
        if (boutId != null) {
            val bout = viewModel.bouts.firstOrNull { it.id == boutId }
            if (bout != null) {
                viewModel.selectBout(bout)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EtfcMainApp(viewModel: EtfcViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val downloadedFights by viewModel.downloadedFights.collectAsState()
    val ppvPasses by viewModel.ppvPasses.collectAsState()
    val activePass by viewModel.activePass.collectAsState()
    val favoriteFighters by viewModel.favoriteFighters.collectAsState()

    val currentRole = uiState.currentUser?.role ?: UserRole.GUEST
    val roleColor = Color(currentRole.colorHex)

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EtfcObsidian),
        containerColor = EtfcObsidian,
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EtfcSurface)
            ) {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = EtfcSurface,
                        titleContentColor = EtfcTextPrimary
                    ),
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.Black,
                                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold),
                                modifier = Modifier.height(34.dp).width(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    androidx.compose.foundation.Image(
                                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.ic_etfc_logo),
                                        contentDescription = "ETFC Logo",
                                        modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp),
                                        contentScale = androidx.compose.ui.layout.ContentScale.Fit
                                    )
                                }
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ETFC",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = EtfcGold,
                                        letterSpacing = 1.2.sp
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "STREAM",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 17.sp,
                                        color = EtfcTextPrimary,
                                        letterSpacing = 1.2.sp
                                    )
                                }
                                Text(
                                    text = "OFFICIAL FIGHT CHAMPIONSHIP",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = EtfcTextMuted
                                )
                            }
                        }
                    },
                    actions = {
                        var showNotificationMenu by remember { mutableStateOf(false) }

                        // Push Notification Center Trigger
                        Box {
                            IconButton(
                                onClick = { showNotificationMenu = true },
                                modifier = Modifier.testTag("notification_menu_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = "Notification Center",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showNotificationMenu,
                                onDismissRequest = { showNotificationMenu = false },
                                modifier = Modifier.background(EtfcSurfaceVariant)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("🔴 Live Event Start Alert", color = EtfcGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Test ETFC 002 Adwa live broadcast notification", color = EtfcTextSecondary, fontSize = 10.sp)
                                        }
                                    },
                                    onClick = {
                                        showNotificationMenu = false
                                        viewModel.triggerLiveEventNotification()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("🥊 Bout Walkout Alert", color = EtfcTextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Test fighter entering cage notification", color = EtfcTextSecondary, fontSize = 10.sp)
                                        }
                                    },
                                    onClick = {
                                        showNotificationMenu = false
                                        viewModel.bouts.firstOrNull()?.let { viewModel.triggerBoutStartNotification(it) }
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("💥 KO Finish Highlight Alert", color = EtfcRedPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Test fight stoppage & instant replay alert", color = EtfcTextSecondary, fontSize = 10.sp)
                                        }
                                    },
                                    onClick = {
                                        showNotificationMenu = false
                                        viewModel.triggerKoAlertNotification()
                                    }
                                )
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text("⚡ Platform & PPV Promo", color = EtfcSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Test Addis CDN node & Telebirr promo alert", color = EtfcTextSecondary, fontSize = 10.sp)
                                        }
                                    },
                                    onClick = {
                                        showNotificationMenu = false
                                        viewModel.triggerPlatformUpdateNotification()
                                    }
                                )
                            }
                        }

                        // Auth & RBAC Profile Badge
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = roleColor.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, roleColor),
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clickable { viewModel.openProfileDrawer() }
                                .testTag("top_user_profile_btn")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = currentRole.badgeEmoji, fontSize = 11.sp)
                                Text(
                                    text = uiState.currentUser?.displayName?.take(10) ?: "Sign In",
                                    color = roleColor,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                )
                // Subtle divider below top bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(EtfcBorder)
                )
            }
        },
        bottomBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(EtfcSurface)
            ) {
                // Subtle divider above bottom bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(EtfcBorder)
                )
                NavigationBar(
                    containerColor = EtfcSurface,
                    tonalElevation = 0.dp,
                    modifier = Modifier.testTag("main_bottom_nav")
                ) {
                    // Home
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.HOME,
                        onClick = { viewModel.selectTab(AppTab.HOME) },
                        icon = {
                            Icon(imageVector = Icons.Default.Home, contentDescription = "Home")
                        },
                        label = { Text("Home", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcGold,
                            selectedTextColor = EtfcGold,
                            indicatorColor = EtfcGold.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_home")
                    )

                    // Live Arena
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.LIVE_ARENA,
                        onClick = { viewModel.selectTab(AppTab.LIVE_ARENA) },
                        icon = {
                            Icon(imageVector = Icons.Default.Tv, contentDescription = "Live Arena")
                        },
                        label = { Text("Live", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcLiveRed,
                            selectedTextColor = EtfcLiveRed,
                            indicatorColor = EtfcLiveRed.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_live")
                    )

                    // Fight Card
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.FIGHT_CARD,
                        onClick = { viewModel.selectTab(AppTab.FIGHT_CARD) },
                        icon = {
                            Icon(imageVector = Icons.Default.SportsMma, contentDescription = "Fight Card")
                        },
                        label = { Text("Fights", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcGold,
                            selectedTextColor = EtfcGold,
                            indicatorColor = EtfcGold.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_card")
                    )

                    // PPV Passes
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.PPV_PASSES,
                        onClick = { viewModel.selectTab(AppTab.PPV_PASSES) },
                        icon = {
                            Icon(imageVector = Icons.Default.ConfirmationNumber, contentDescription = "PPV Passes")
                        },
                        label = { Text("PPV", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcGold,
                            selectedTextColor = EtfcGold,
                            indicatorColor = EtfcGold.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_ppv")
                    )

                    // Fighters
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.FIGHTERS,
                        onClick = { viewModel.selectTab(AppTab.FIGHTERS) },
                        icon = {
                            Icon(imageVector = Icons.Default.People, contentDescription = "Fighters")
                        },
                        label = { Text("Roster", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcGold,
                            selectedTextColor = EtfcGold,
                            indicatorColor = EtfcGold.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_fighters")
                    )

                    // Settings
                    NavigationBarItem(
                        selected = uiState.currentTab == AppTab.SETTINGS,
                        onClick = { viewModel.selectTab(AppTab.SETTINGS) },
                        icon = {
                            Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
                        },
                        label = { Text("Settings", fontSize = 10.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = EtfcGold,
                            selectedTextColor = EtfcGold,
                            indicatorColor = EtfcGold.copy(alpha = 0.15f),
                            unselectedIconColor = EtfcTextMuted,
                            unselectedTextColor = EtfcTextMuted
                        ),
                        modifier = Modifier.testTag("nav_tab_settings")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState.currentTab) {
                AppTab.HOME -> {
                    HomeScreen(viewModel = viewModel)
                }

                AppTab.LIVE_ARENA -> {
                    uiState.selectedBout?.let { bout ->
                        LiveStreamScreen(
                            bout = bout,
                            streamQuality = uiState.streamQuality,
                            currentCameraAngle = uiState.currentCameraAngle,
                            isAudioOnly = uiState.isAudioOnly,
                            isDataSaver = uiState.isDataSaverActive,
                            isPaused = uiState.isPaused,
                            bitrateKbps = uiState.currentBitrateKbps,
                            latencyMs = uiState.currentLatencyMs,
                            edgeNodeName = uiState.selectedCdnNode?.edgeServer ?: "addis-edge-01",
                            liveRound = uiState.liveRound,
                            liveSeconds = uiState.liveSecondsRemaining,
                            redSigStrikes = uiState.redSignificantStrikes,
                            blueSigStrikes = uiState.blueSignificantStrikes,
                            redTotalStrikes = uiState.redTotalStrikes,
                            blueTotalStrikes = uiState.blueTotalStrikes,
                            redTakedowns = uiState.redTakedowns,
                            blueTakedowns = uiState.blueTakedowns,
                            redControlPercent = uiState.redOctagonControlPercent,
                            activePass = activePass,
                            fanMessages = uiState.fanMessages,
                            crowdCheerCount = uiState.liveCrowdCheerCount,
                            currentUser = uiState.currentUser,
                            canAccessBout = viewModel.canAccessBout(bout),
                            liveChatMessages = uiState.liveChatMessages,
                            pinnedChatMessage = uiState.pinnedChatMessage,
                            chatInput = uiState.chatInputText,
                            mentionSuggestions = uiState.mentionSuggestions,
                            isMentionPopupVisible = uiState.isMentionPopupVisible,
                            slowModeSeconds = uiState.slowModeSeconds,
                            isVipOnlyChat = uiState.isVipOnlyChat,
                            onQualitySelected = { viewModel.setStreamQuality(it) },
                            onCameraAngleSelected = { viewModel.setCameraAngle(it) },
                            onToggleDataSaver = { viewModel.toggleDataSaver(it) },
                            onToggleAudioOnly = { viewModel.toggleAudioOnly(it) },
                            onTogglePause = { viewModel.togglePlaybackPause() },
                            onOpenTaleOfTheTape = { red, blue -> viewModel.openTaleOfTheTape(red, blue) },
                            onOpenCheckout = { viewModel.openCheckoutDialog() },
                            onChatInputChanged = { viewModel.updateChatInput(it) },
                            onInsertMention = { viewModel.insertMention(it) },
                            onSendChatMessage = { viewModel.sendFanChatMessage(it) },
                            onSendCheer = { viewModel.sendCheerReaction(it) },
                            onPinMessage = { viewModel.pinMessage(it) },
                            onUnpinMessage = { viewModel.unpinMessage(it) },
                            onDeleteMessage = { id, reason -> viewModel.deleteMessage(id, reason) },
                            onTimeoutUser = { id, handle, sec -> viewModel.timeoutUser(id, handle, sec) },
                            onBanUser = { id, handle, reason -> viewModel.banUser(id, handle, reason) },
                            onFlagMessage = { viewModel.flagMessage(it) },
                            onSetSlowMode = { viewModel.setSlowMode(it) },
                            onToggleVipOnly = { viewModel.toggleVipOnlyChat() },
                            onOpenAuthDialog = { viewModel.openAuthDialog() },
                            onOpenProfileDrawer = { viewModel.openProfileDrawer() },
                            onPredictWinner = { viewModel.submitPrediction(bout, it, "KO/TKO", 3) }
                        )
                    }
                }

                AppTab.FIGHT_CARD -> {
                    FightCardScreen(
                        bouts = viewModel.bouts,
                        onSelectBoutForStream = {
                            viewModel.selectBout(it)
                            viewModel.selectTab(AppTab.LIVE_ARENA)
                        },
                        onOpenTaleOfTheTape = { red, blue -> viewModel.openTaleOfTheTape(red, blue) },
                        onDownloadBout = { bout, quality -> viewModel.downloadFightForOffline(bout, quality) }
                    )
                }

                AppTab.PPV_PASSES -> {
                    TicketsPpvScreen(
                        ppvTiers = viewModel.ppvTiers,
                        activePass = activePass,
                        allPasses = ppvPasses,
                        onSelectTierToBuy = { tier -> viewModel.openCheckoutDialog(tier) }
                    )
                }

                AppTab.FIGHTERS -> {
                    FightersScreen(
                        fighters = viewModel.fighters,
                        favoriteFighters = favoriteFighters,
                        searchQuery = uiState.searchQuery,
                        selectedDivision = uiState.selectedDivisionFilter,
                        onSearchChanged = { viewModel.updateSearchQuery(it) },
                        onDivisionSelected = { viewModel.setDivisionFilter(it) },
                        onToggleFavorite = { fighter, isFav -> viewModel.toggleFavoriteFighter(fighter, isFav) },
                        onCompareFighter = { fighter ->
                            val other = viewModel.fighters.firstOrNull { it.id != fighter.id && it.division == fighter.division }
                                ?: viewModel.fighters.first { it.id != fighter.id }
                            viewModel.openTaleOfTheTape(fighter, other)
                        }
                    )
                }

                AppTab.SETTINGS -> {
                    SettingsScreen(viewModel = viewModel)
                }
            }

            // Notification / Feedback SnackBar
            AnimatedVisibility(
                visible = uiState.chatSnackbarMessage != null,
                enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(12.dp)
            ) {
                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold),
                    shadowElevation = 10.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(0.95f)
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.chatSnackbarMessage ?: "",
                            color = EtfcGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "DISMISS",
                            color = EtfcTextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier
                                .clickable { viewModel.dismissSnackbar() }
                                .padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }

    // Modal Overlays

    // 1. Tale of the Tape Comparison Dialog
    if (uiState.isTaleOfTheTapeOpen && uiState.taleFighterRed != null && uiState.taleFighterBlue != null) {
        TaleOfTheTapeDialog(
            fighterRed = uiState.taleFighterRed!!,
            fighterBlue = uiState.taleFighterBlue!!,
            onDismiss = { viewModel.closeTaleOfTheTape() }
        )
    }

    // 2. M-Pesa & Telebirr Payment Dialog
    if (uiState.isCheckoutDialogVisible && uiState.selectedPpvTier != null) {
        PaymentCheckoutDialog(
            tier = uiState.selectedPpvTier!!,
            selectedProvider = uiState.paymentProvider,
            phoneNumber = uiState.phoneNumberInput,
            paymentState = uiState.paymentUiState,
            onProviderSelected = { viewModel.setPaymentProvider(it) },
            onPhoneChanged = { viewModel.updatePhoneInput(it) },
            onConfirmPayment = { viewModel.processPpvCheckout() },
            onDismiss = { viewModel.closeCheckoutDialog() },
            onSuccessDismiss = {
                viewModel.closeCheckoutDialog()
                viewModel.selectTab(AppTab.LIVE_ARENA)
            }
        )
    }

    // 3. Encrypted Offline Match Playback Dialog
    uiState.offlinePlayFight?.let { offlineFight ->
        OfflinePlaybackDialog(
            fight = offlineFight,
            onDismiss = { viewModel.playOfflineFight(null) }
        )
    }

    // 4. In-App Embedded YouTube Video Player Dialog
    if (uiState.isYouTubePlayerOpen && uiState.selectedYouTubeVideo != null) {
        YouTubeInAppPlayerDialog(
            video = uiState.selectedYouTubeVideo!!,
            playlistVideos = viewModel.previousVideos,
            onSelectVideo = { viewModel.playPreviousVideo(it) },
            onDismiss = { viewModel.closeYouTubePlayer() },
            onOpenTaleOfTheTape = { red, blue -> viewModel.openTaleOfTheTapeByName(red, blue) }
        )
    }

    // 5. Authentication (Sign In & Register) Dialog
    AuthDialog(
        isVisible = uiState.isAuthDialogVisible,
        currentTab = uiState.authDialogTab,
        errorMessage = uiState.authErrorMessage,
        demoUsers = uiState.allDemoUsers,
        onTabSelected = { viewModel.setAuthTab(it) },
        onLogin = { id, pass -> viewModel.login(id, pass) },
        onRegister = { email, handle, name, pass, phone, code, flag ->
            viewModel.register(email, handle, name, pass, phone, code, flag)
        },
        onQuickSwitchUser = { viewModel.quickSwitchUser(it) },
        onDismiss = { viewModel.closeAuthDialog() }
    )

    // 6. User Profile & RBAC Drawer
    UserProfileDrawer(
        isVisible = uiState.isProfileDrawerVisible,
        currentUser = uiState.currentUser,
        demoUsers = uiState.allDemoUsers,
        onUpgradeRole = { viewModel.upgradeCurrentUserRole(it) },
        onQuickSwitchUser = { viewModel.quickSwitchUser(it) },
        onOpenAuthDialog = {
            viewModel.closeProfileDrawer()
            viewModel.openAuthDialog()
        },
        onLogout = { viewModel.logout() },
        onDismiss = { viewModel.closeProfileDrawer() }
    )
}
