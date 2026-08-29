package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.DownloadedFightEntity
import com.example.data.local.EtfcDatabase
import com.example.data.local.FavoriteFighterEntity
import com.example.data.local.PpvPassEntity
import com.example.data.local.UserPredictionEntity
import com.example.data.models.AuthResult
import com.example.data.models.Bout
import com.example.data.models.BoutType
import com.example.data.models.CameraAngle
import com.example.data.models.CdnNodeInfo
import com.example.data.models.ChatMessage
import com.example.data.models.ContentTier
import com.example.data.models.FanChatMessage
import com.example.data.models.Fighter
import com.example.data.models.HeroSlide
import com.example.data.models.HeroSlideType
import com.example.data.models.NewsArticle
import com.example.data.models.NewsCategory
import com.example.data.models.PaymentProvider
import com.example.data.models.PpvTier
import com.example.data.models.PreviousFightVideo
import com.example.data.models.StreamQuality
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.data.models.VideoCategory
import com.example.data.models.WeightClass
import com.example.data.repository.EtfcRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class AppTab(val title: String) {
    HOME("Home"),
    LIVE_ARENA("Live Arena"),
    FIGHT_CARD("Fight Card"),
    PPV_PASSES("PPV Passes"),
    FIGHTERS("Fighters"),
    SETTINGS("Settings")
}

sealed interface PaymentUiState {
    data object Idle : PaymentUiState
    data class Processing(val provider: PaymentProvider, val message: String) : PaymentUiState
    data class Success(val pass: PpvPassEntity) : PaymentUiState
    data class Error(val error: String) : PaymentUiState
}

data class EtfcUiState(
    val currentTab: AppTab = AppTab.HOME,
    val selectedBout: Bout? = null,
    val streamQuality: StreamQuality = StreamQuality.AUTO_ABR,
    val currentCameraAngle: CameraAngle = CameraAngle.MAIN_FEED,
    val isDataSaverActive: Boolean = false,
    val isAudioOnly: Boolean = false,
    val isPaused: Boolean = false,
    val currentBitrateKbps: Int = 3850,
    val currentLatencyMs: Int = 22,
    val selectedCdnNode: CdnNodeInfo? = null,
    val liveRound: Int = 3,
    val liveSecondsRemaining: Int = 174,
    val redSignificantStrikes: Int = 52,
    val blueSignificantStrikes: Int = 41,
    val redTotalStrikes: Int = 74,
    val blueTotalStrikes: Int = 59,
    val redTakedowns: Int = 1,
    val blueTakedowns: Int = 2,
    val redOctagonControlPercent: Int = 58,
    val isCheckoutDialogVisible: Boolean = false,
    val selectedPpvTier: PpvTier? = null,
    val paymentProvider: PaymentProvider = PaymentProvider.TELEBIRR,
    val phoneNumberInput: String = "911234567",
    val paymentUiState: PaymentUiState = PaymentUiState.Idle,
    val isTaleOfTheTapeOpen: Boolean = false,
    val taleFighterRed: Fighter? = null,
    val taleFighterBlue: Fighter? = null,
    val selectedDivisionFilter: WeightClass? = null,
    val searchQuery: String = "",
    val fanMessages: List<FanChatMessage> = emptyList(),
    val chatInputText: String = "",
    val liveCrowdCheerCount: Int = 1420,
    val offlinePlayFight: DownloadedFightEntity? = null,
    
    // Auth & RBAC State
    val currentUser: UserAccount? = null,
    val allDemoUsers: List<UserAccount> = emptyList(),
    val isAuthDialogVisible: Boolean = false,
    val authDialogTab: Int = 0, // 0 = Sign In, 1 = Register
    val authErrorMessage: String? = null,
    val authSuccessMessage: String? = null,
    val isProfileDrawerVisible: Boolean = false,
    
    // Real-Time Chat & Moderation State
    val liveChatMessages: List<ChatMessage> = emptyList(),
    val pinnedChatMessage: ChatMessage? = null,
    val mentionSuggestions: List<Pair<String, String>> = emptyList(),
    val isMentionPopupVisible: Boolean = false,
    val slowModeSeconds: Int = 0, // 0 = off, 5, 15, 30
    val isVipOnlyChat: Boolean = false,
    val lastMessageSentTimestamp: Long = 0L,
    val chatSnackbarMessage: String? = null,
    val selectedUserForModAction: Pair<String, String>? = null, // (userId, handle)

    // Home Screen & VOD / News Feed State
    val isLiveEventOngoing: Boolean = true,
    val isLivePreviewExpanded: Boolean = true,
    val selectedVideoCategory: VideoCategory = VideoCategory.ALL,
    val selectedNewsArticle: NewsArticle? = null,
    val activePlayingVod: PreviousFightVideo? = null,
    val currentHeroIndex: Int = 0,

    // YouTube In-App Player State
    val selectedYouTubeVideo: PreviousFightVideo? = null,
    val isYouTubePlayerOpen: Boolean = false,

    // User Settings State
    val selectedLanguage: String = "English",
    val selectedCurrency: String = "ETB (Ethiopian Birr - Br)",
    val isLowLatencyMode: Boolean = true,
    val isHardwareAccelerationEnabled: Boolean = true,
    val preferredAudioFeed: String = "Main Broadcast Commentary",
    val isHapticFeedbackEnabled: Boolean = true,
    val isLiveStartAlertEnabled: Boolean = true,
    val isBoutStartAlertEnabled: Boolean = true,
    val isKnockoutAlertEnabled: Boolean = true,
    val isBiometricAuthEnabled: Boolean = false,
    val isDrmScreenShieldEnabled: Boolean = true,
    val preferredMobileWallet: PaymentProvider = PaymentProvider.TELEBIRR,
    val chatProfanityFilterEnabled: Boolean = true,
    val cachedStorageFreedMb: Int = 0,
    val settingsSnackbarMessage: String? = null
)

class EtfcViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EtfcRepository
    val notificationManager: com.example.notifications.EtfcNotificationManager

    private val _uiState = MutableStateFlow(EtfcUiState())
    val uiState: StateFlow<EtfcUiState> = _uiState.asStateFlow()

    val downloadedFights: StateFlow<List<DownloadedFightEntity>>
    val ppvPasses: StateFlow<List<PpvPassEntity>>
    val activePass: StateFlow<PpvPassEntity?>
    val userPredictions: StateFlow<List<UserPredictionEntity>>
    val favoriteFighters: StateFlow<List<FavoriteFighterEntity>>
    val fighterProfiles: StateFlow<List<com.example.data.local.FighterProfileEntity>>

    val fighters: List<Fighter>
    val bouts: List<Bout>
    val ppvTiers: List<PpvTier>
    val cdnNodes: List<CdnNodeInfo>
    val heroSlides: List<HeroSlide>
    val previousVideos: List<PreviousFightVideo>
    val newsArticles: List<NewsArticle>

    init {
        val db = EtfcDatabase.getDatabase(application)
        repository = EtfcRepository(db.etfcDao())
        notificationManager = com.example.notifications.EtfcNotificationManager(application)

        fighters = repository.fighters
        bouts = repository.currentEventBouts
        ppvTiers = repository.ppvTiers
        cdnNodes = repository.cdnNodes
        heroSlides = repository.heroSlides
        previousVideos = repository.previousVideos
        newsArticles = repository.newsArticles

        downloadedFights = repository.downloadedFights
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        ppvPasses = repository.ppvPasses
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        activePass = repository.activePass
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

        userPredictions = repository.userPredictions
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        favoriteFighters = repository.favoriteFighters
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        fighterProfiles = repository.getAllFighterProfiles()
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        _uiState.value = _uiState.value.copy(
            selectedBout = bouts.firstOrNull(),
            selectedCdnNode = cdnNodes.firstOrNull(),
            fanMessages = repository.initialChatMessages,
            taleFighterRed = bouts.firstOrNull()?.fighterRed,
            taleFighterBlue = bouts.firstOrNull()?.fighterBlue
        )

        // Seed Meta-Engine, Users, and Chat in Room
        viewModelScope.launch {
            repository.seedFighterMetaEngineIfNeeded()
            repository.seedDefaultUsersAndChatIfNeeded(bouts.firstOrNull()?.id ?: "bout_sedo_jhonny")
        }

        // Observe All Users from Room
        viewModelScope.launch {
            repository.getAllUsersFlow().collectLatest { users ->
                _uiState.value = _uiState.value.copy(
                    allDemoUsers = users,
                    currentUser = _uiState.value.currentUser ?: users.firstOrNull { it.role == UserRole.MODERATOR } ?: users.firstOrNull()
                )
            }
        }

        // Observe Live Chat Messages for current bout
        val initialBoutId = bouts.firstOrNull()?.id ?: "bout_sedo_jhonny"
        observeChatForBout(initialBoutId)

        startLiveFightTelemetryEngine()
        startSimulatedArenaChatEngine()
    }

    private fun observeChatForBout(boutId: String) {
        viewModelScope.launch {
            repository.getChatMessagesForBout(boutId).collectLatest { messages ->
                _uiState.value = _uiState.value.copy(liveChatMessages = messages)
            }
        }
        viewModelScope.launch {
            repository.getPinnedMessage(boutId).collectLatest { pinned ->
                _uiState.value = _uiState.value.copy(pinnedChatMessage = pinned)
            }
        }
    }

    private fun startLiveFightTelemetryEngine() {
        viewModelScope.launch {
            while (true) {
                delay(1000)
                if (!_uiState.value.isPaused) {
                    val currentSec = _uiState.value.liveSecondsRemaining
                    val nextSec = if (currentSec > 1) currentSec - 1 else 300
                    val nextRound = if (currentSec <= 1) {
                        if (_uiState.value.liveRound < 5) _uiState.value.liveRound + 1 else 1
                    } else {
                        _uiState.value.liveRound
                    }

                    // Random strike exchange simulation
                    val addRedStrike = (0..10).random() > 6
                    val addBlueStrike = (0..10).random() > 7
                    val redSig = _uiState.value.redSignificantStrikes + (if (addRedStrike) 1 else 0)
                    val blueSig = _uiState.value.blueSignificantStrikes + (if (addBlueStrike) 1 else 0)
                    val redTot = _uiState.value.redTotalStrikes + (if (addRedStrike) (1..2).random() else 0)
                    val blueTot = _uiState.value.blueTotalStrikes + (if (addBlueStrike) (1..2).random() else 0)

                    // Dynamic ABR Bitrate simulation
                    val baseBitrate = when (_uiState.value.streamQuality) {
                        StreamQuality.AUTO_ABR -> (3600..4400).random()
                        StreamQuality.HD_1080P -> (6200..6600).random()
                        StreamQuality.HD_720P -> (3100..3300).random()
                        StreamQuality.SMOOTH_480P -> (1150..1250).random()
                        StreamQuality.SAVER_240P -> (330..360).random()
                        StreamQuality.AUDIO_ONLY -> 64
                    }

                    _uiState.value = _uiState.value.copy(
                        liveSecondsRemaining = nextSec,
                        liveRound = nextRound,
                        redSignificantStrikes = redSig,
                        blueSignificantStrikes = blueSig,
                        redTotalStrikes = redTot,
                        blueTotalStrikes = blueTot,
                        currentBitrateKbps = baseBitrate,
                        currentLatencyMs = (16..28).random()
                    )
                }
            }
        }
    }

    private fun startSimulatedArenaChatEngine() {
        val simulatedChatStream = listOf(
            Pair("Brian O.", "🇰🇪" to "Look at that footwork from @sedo! High level striking 🥊"),
            Pair("Bethlehem T.", "🇪🇹" to "Adwa crowd is roaring! 🇪🇹🇪🇹 Let's go champion @jhonny!"),
            Pair("Samuel K.", "🇰🇪" to "@etfc_mod referee is doing a great job separating the clinch"),
            Pair("Yohannes M.", "🇪🇹" to "Massive right hand landed! @sedo has iron chin 🔥"),
            Pair("Amina D.", "🇪🇹" to "East Africa combat sports revolution right here in Addis 🏆"),
            Pair("Kagame P.", "🇷🇼" to "Watching live from Kigali via edge CDN! 🇷🇼🔥"),
            Pair("Kato J.", "🇺🇬" to "Kampala fans cheering for ETFC 002! Amazing production 🥊")
        )

        viewModelScope.launch {
            var index = 0
            while (true) {
                delay(14000)
                if (!_uiState.value.isPaused) {
                    val targetBoutId = _uiState.value.selectedBout?.id ?: "bout_sedo_jhonny"
                    val (senderName, flagMsg) = simulatedChatStream[index % simulatedChatStream.size]
                    val (flag, msgText) = flagMsg
                    index++

                    val simUser = UserAccount(
                        userId = "usr_sim_$index",
                        email = "fan$index@arena.etfc",
                        handle = "@${senderName.lowercase().replace(" ", "_").replace(".", "")}",
                        displayName = senderName,
                        phoneNumber = "+251911999999",
                        flagEmoji = flag,
                        role = UserRole.PPV_BUYER,
                        authToken = "ETFC.SIM.TOKEN"
                    )

                    repository.sendChatMessage(targetBoutId, simUser, msgText)
                    _uiState.value = _uiState.value.copy(
                        liveCrowdCheerCount = _uiState.value.liveCrowdCheerCount + (2..5).random()
                    )
                }
            }
        }
    }

    fun selectTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
    }

    fun setStreamQuality(quality: StreamQuality) {
        val isAudio = quality == StreamQuality.AUDIO_ONLY
        val isSaver = quality == StreamQuality.SAVER_240P
        _uiState.value = _uiState.value.copy(
            streamQuality = quality,
            isAudioOnly = isAudio,
            isDataSaverActive = isSaver
        )
    }

    fun toggleDataSaver(enabled: Boolean) {
        val newQuality = if (enabled) StreamQuality.SAVER_240P else StreamQuality.AUTO_ABR
        _uiState.value = _uiState.value.copy(
            isDataSaverActive = enabled,
            streamQuality = newQuality,
            isAudioOnly = false
        )
    }

    fun toggleAudioOnly(enabled: Boolean) {
        val newQuality = if (enabled) StreamQuality.AUDIO_ONLY else StreamQuality.AUTO_ABR
        _uiState.value = _uiState.value.copy(
            isAudioOnly = enabled,
            streamQuality = newQuality
        )
    }

    fun setCameraAngle(angle: CameraAngle) {
        // RBAC check for VIP multi-cam
        if (angle != CameraAngle.MAIN_FEED && !canAccessVipCamera()) {
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "Corner Cams require VIP Ringside or Moderator tier.",
                isCheckoutDialogVisible = true,
                selectedPpvTier = ppvTiers.firstOrNull { it.id == "tier_vip_ringside" } ?: ppvTiers.first()
            )
            return
        }
        _uiState.value = _uiState.value.copy(currentCameraAngle = angle)
    }

    fun togglePlaybackPause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    fun selectBout(bout: Bout) {
        _uiState.value = _uiState.value.copy(
            selectedBout = bout,
            taleFighterRed = bout.fighterRed,
            taleFighterBlue = bout.fighterBlue
        )
        observeChatForBout(bout.id)
    }

    fun openTaleOfTheTape(fighterRed: Fighter, fighterBlue: Fighter) {
        _uiState.value = _uiState.value.copy(
            isTaleOfTheTapeOpen = true,
            taleFighterRed = fighterRed,
            taleFighterBlue = fighterBlue
        )
    }

    fun openTaleOfTheTapeByName(fighterRedName: String, fighterBlueName: String) {
        val red = fighters.find { it.name.contains(fighterRedName, ignoreCase = true) } ?: fighters.firstOrNull() ?: return
        val blue = fighters.find { it.name.contains(fighterBlueName, ignoreCase = true) } ?: fighters.getOrNull(1) ?: return
        openTaleOfTheTape(red, blue)
    }

    fun closeTaleOfTheTape() {
        _uiState.value = _uiState.value.copy(isTaleOfTheTapeOpen = false)
    }

    fun openCheckoutDialog(tier: PpvTier? = null) {
        val selected = tier ?: ppvTiers.firstOrNull { it.isPopular } ?: ppvTiers.first()
        _uiState.value = _uiState.value.copy(
            isCheckoutDialogVisible = true,
            selectedPpvTier = selected,
            paymentUiState = PaymentUiState.Idle
        )
    }

    fun closeCheckoutDialog() {
        _uiState.value = _uiState.value.copy(
            isCheckoutDialogVisible = false,
            paymentUiState = PaymentUiState.Idle
        )
    }

    fun setPaymentProvider(provider: PaymentProvider) {
        val defaultPhone = if (provider == PaymentProvider.MPESA) "712345678" else "911234567"
        _uiState.value = _uiState.value.copy(
            paymentProvider = provider,
            phoneNumberInput = defaultPhone
        )
    }

    fun updatePhoneInput(phone: String) {
        _uiState.value = _uiState.value.copy(phoneNumberInput = phone)
    }

    fun processPpvCheckout() {
        val tier = _uiState.value.selectedPpvTier ?: return
        val provider = _uiState.value.paymentProvider
        val phone = _uiState.value.phoneNumberInput.trim()

        if (phone.length < 8) {
            _uiState.value = _uiState.value.copy(
                paymentUiState = PaymentUiState.Error("Please enter a valid phone number")
            )
            return
        }

        viewModelScope.launch {
            val message = if (provider == PaymentProvider.MPESA) {
                "Initiating Safaricom STK Push to ${provider.defaultPrefix}$phone...\nPlease check your phone for PIN prompt."
            } else {
                "Contacting Telebirr API Gateway...\nAuthorizing transaction for ${provider.defaultPrefix}$phone."
            }
            _uiState.value = _uiState.value.copy(
                paymentUiState = PaymentUiState.Processing(provider, message)
            )

            val result = repository.processPpvPayment(tier, provider, "${provider.defaultPrefix}$phone")
            if (result.isSuccess) {
                val pass = result.getOrThrow()
                // Role elevation upon successful payment
                val elevatedRole = if (tier.id == "tier_vip_ringside") UserRole.VIP_MEMBER else UserRole.PPV_BUYER
                _uiState.value.currentUser?.let { user ->
                    if (user.role.tierLevel < elevatedRole.tierLevel) {
                        repository.switchUserRole(user.userId, elevatedRole)
                        _uiState.value = _uiState.value.copy(
                            currentUser = user.copy(role = elevatedRole)
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(
                    paymentUiState = PaymentUiState.Success(pass)
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    paymentUiState = PaymentUiState.Error("Payment request timed out or cancelled.")
                )
            }
        }
    }

    // =========================================================
    // AUTHENTICATION & RBAC CONTROLS
    // =========================================================

    fun openAuthDialog(initialTab: Int = 0) {
        _uiState.value = _uiState.value.copy(
            isAuthDialogVisible = true,
            authDialogTab = initialTab,
            authErrorMessage = null,
            authSuccessMessage = null
        )
    }

    fun closeAuthDialog() {
        _uiState.value = _uiState.value.copy(
            isAuthDialogVisible = false,
            authErrorMessage = null,
            authSuccessMessage = null
        )
    }

    fun setAuthTab(tab: Int) {
        _uiState.value = _uiState.value.copy(authDialogTab = tab, authErrorMessage = null)
    }

    fun openProfileDrawer() {
        _uiState.value = _uiState.value.copy(isProfileDrawerVisible = true)
    }

    fun closeProfileDrawer() {
        _uiState.value = _uiState.value.copy(isProfileDrawerVisible = false)
    }

    fun login(identifier: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginUser(identifier, password)
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        currentUser = result.user,
                        isAuthDialogVisible = false,
                        authErrorMessage = null,
                        chatSnackbarMessage = "Signed in as ${result.user.displayName} (${result.user.role.badgeTitle})"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(authErrorMessage = result.errorMessage)
                }
            }
        }
    }

    fun register(
        email: String,
        handle: String,
        displayName: String,
        password: String,
        phoneNumber: String,
        countryCode: String,
        flagEmoji: String
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(
                email = email,
                handle = handle,
                displayName = displayName,
                password = password,
                phoneNumber = phoneNumber,
                countryCode = countryCode,
                flagEmoji = flagEmoji
            )
            when (result) {
                is AuthResult.Success -> {
                    _uiState.value = _uiState.value.copy(
                        currentUser = result.user,
                        isAuthDialogVisible = false,
                        authErrorMessage = null,
                        chatSnackbarMessage = "Account created! Welcome, ${result.user.displayName}"
                    )
                }
                is AuthResult.Error -> {
                    _uiState.value = _uiState.value.copy(authErrorMessage = result.errorMessage)
                }
            }
        }
    }

    fun logout() {
        _uiState.value = _uiState.value.copy(
            currentUser = UserAccount(
                userId = "usr_guest",
                email = "guest@arena.etfc",
                handle = "@guest",
                displayName = "Guest Fan",
                phoneNumber = "",
                role = UserRole.GUEST,
                authToken = "ETFC.GUEST.TOKEN"
            ),
            isProfileDrawerVisible = false,
            chatSnackbarMessage = "Logged out. Operating in Guest Viewer mode."
        )
    }

    fun quickSwitchUser(user: UserAccount) {
        _uiState.value = _uiState.value.copy(
            currentUser = user,
            isProfileDrawerVisible = false,
            isAuthDialogVisible = false,
            chatSnackbarMessage = "Active Profile: ${user.displayName} • ${user.role.badgeTitle} ${user.role.badgeEmoji}"
        )
    }

    fun upgradeCurrentUserRole(newRole: UserRole) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.switchUserRole(user.userId, newRole)
            _uiState.value = _uiState.value.copy(
                currentUser = user.copy(role = newRole),
                chatSnackbarMessage = "Upgraded to ${newRole.badgeTitle} ${newRole.badgeEmoji}"
            )
        }
    }

    fun canAccessBout(bout: Bout): Boolean {
        if (bout.boutType == BoutType.EARLY_PRELIMS) return true
        if (activePass.value != null) return true
        val role = _uiState.value.currentUser?.role ?: UserRole.GUEST
        return role.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD)
    }

    fun canAccessVipCamera(): Boolean {
        if (activePass.value?.tierId == "tier_vip_ringside") return true
        val role = _uiState.value.currentUser?.role ?: UserRole.GUEST
        return role.canAccessTier(ContentTier.VIP_RINGSIDE_EXCLUSIVE)
    }

    fun canAccessOfflineDownloads(): Boolean {
        if (activePass.value != null) return true
        val role = _uiState.value.currentUser?.role ?: UserRole.GUEST
        return role.canAccessTier(ContentTier.OFFLINE_VAULT)
    }

    fun canModerate(): Boolean {
        val role = _uiState.value.currentUser?.role ?: UserRole.GUEST
        return role.canModerate()
    }

    // =========================================================
    // REAL-TIME LIVE CHAT & MODERATION ACTIONS
    // =========================================================

    fun updateChatInput(text: String) {
        _uiState.value = _uiState.value.copy(chatInputText = text)

        // Autocomplete mention popup calculation
        val lastWord = text.split(" ", "\n").lastOrNull() ?: ""
        if (lastWord.startsWith("@") && lastWord.length >= 1) {
            val query = lastWord.lowercase()
            val matches = repository.getAvailableMentionHandles().filter { (handle, _) ->
                handle.lowercase().startsWith(query)
            }
            _uiState.value = _uiState.value.copy(
                mentionSuggestions = matches,
                isMentionPopupVisible = matches.isNotEmpty()
            )
        } else {
            _uiState.value = _uiState.value.copy(
                mentionSuggestions = emptyList(),
                isMentionPopupVisible = false
            )
        }
    }

    fun insertMention(handle: String) {
        val current = _uiState.value.chatInputText
        val words = current.split(" ").toMutableList()
        if (words.isNotEmpty() && words.last().startsWith("@")) {
            words[words.size - 1] = "$handle "
        } else {
            words.add("$handle ")
        }
        _uiState.value = _uiState.value.copy(
            chatInputText = words.joinToString(" "),
            mentionSuggestions = emptyList(),
            isMentionPopupVisible = false
        )
    }

    fun sendFanChatMessage(customEmoji: String? = null) {
        val user = _uiState.value.currentUser
        if (user == null || user.role == UserRole.GUEST) {
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "Please sign in to participate in the live chat.",
                isAuthDialogVisible = true
            )
            return
        }

        // VIP only chat check
        if (_uiState.value.isVipOnlyChat && user.role.tierLevel < UserRole.VIP_MEMBER.tierLevel) {
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "Chat is currently locked to VIP Ringside members."
            )
            return
        }

        // Slow mode cooldown check
        val slowMode = _uiState.value.slowModeSeconds
        if (slowMode > 0 && !user.role.canModerate()) {
            val elapsedSec = (System.currentTimeMillis() - _uiState.value.lastMessageSentTimestamp) / 1000
            if (elapsedSec < slowMode) {
                _uiState.value = _uiState.value.copy(
                    chatSnackbarMessage = "Slow mode active. Please wait ${slowMode - elapsedSec}s."
                )
                return
            }
        }

        val text = _uiState.value.chatInputText.trim()
        if (text.isEmpty() && customEmoji == null) return

        val targetBoutId = _uiState.value.selectedBout?.id ?: "bout_sedo_jhonny"

        viewModelScope.launch {
            val result = repository.sendChatMessage(
                boutId = targetBoutId,
                user = user,
                messageText = text,
                cheerEmoji = customEmoji
            )
            if (result.isSuccess) {
                _uiState.value = _uiState.value.copy(
                    chatInputText = "",
                    isMentionPopupVisible = false,
                    lastMessageSentTimestamp = System.currentTimeMillis(),
                    liveCrowdCheerCount = _uiState.value.liveCrowdCheerCount + 1
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    chatSnackbarMessage = result.exceptionOrNull()?.message ?: "Failed to send message."
                )
            }
        }
    }

    fun sendCheerReaction(emoji: String) {
        val user = _uiState.value.currentUser ?: UserAccount(
            userId = "usr_guest_${UUID.randomUUID().toString().take(6)}",
            email = "guest@etfc.stream",
            handle = "@cheer_fan",
            displayName = "Arena Fan",
            phoneNumber = "",
            role = UserRole.FREE_USER,
            authToken = "ETFC.GUEST"
        )
        val targetBoutId = _uiState.value.selectedBout?.id ?: "bout_sedo_jhonny"

        viewModelScope.launch {
            repository.sendChatMessage(
                boutId = targetBoutId,
                user = user,
                messageText = "",
                cheerEmoji = emoji
            )
            _uiState.value = _uiState.value.copy(
                liveCrowdCheerCount = _uiState.value.liveCrowdCheerCount + 5
            )
        }
    }

    fun pinMessage(messageId: String) {
        val user = _uiState.value.currentUser ?: return
        val targetBoutId = _uiState.value.selectedBout?.id ?: "bout_sedo_jhonny"
        viewModelScope.launch {
            repository.pinChatMessage(messageId, targetBoutId, user)
            _uiState.value = _uiState.value.copy(chatSnackbarMessage = "Message pinned to arena feed 📌")
        }
    }

    fun unpinMessage(messageId: String) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.unpinChatMessage(messageId, user)
            _uiState.value = _uiState.value.copy(chatSnackbarMessage = "Message unpinned.")
        }
    }

    fun deleteMessage(messageId: String, reason: String = "Policy violation") {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.deleteChatMessage(messageId, user, reason)
            _uiState.value = _uiState.value.copy(chatSnackbarMessage = "Message removed by moderator.")
        }
    }

    fun timeoutUser(targetUserId: String, targetHandle: String, durationSec: Long = 60) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.timeoutUser(targetUserId, targetHandle, user, durationSec)
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "User $targetHandle timed out for ${durationSec}s.",
                selectedUserForModAction = null
            )
        }
    }

    fun banUser(targetUserId: String, targetHandle: String, reason: String = "Chat policy violation") {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            repository.banUser(targetUserId, targetHandle, user, reason)
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "User $targetHandle has been banned from the arena.",
                selectedUserForModAction = null
            )
        }
    }

    fun flagMessage(messageId: String) {
        viewModelScope.launch {
            repository.flagChatMessage(messageId)
            _uiState.value = _uiState.value.copy(chatSnackbarMessage = "Thank you. Message reported to moderators 🚩")
        }
    }

    fun setSlowMode(seconds: Int) {
        _uiState.value = _uiState.value.copy(
            slowModeSeconds = seconds,
            chatSnackbarMessage = if (seconds > 0) "Slow mode set to ${seconds}s cooldown." else "Slow mode disabled."
        )
    }

    fun toggleVipOnlyChat() {
        val next = !_uiState.value.isVipOnlyChat
        _uiState.value = _uiState.value.copy(
            isVipOnlyChat = next,
            chatSnackbarMessage = if (next) "VIP Only Chat enabled 👑" else "Chat opened to all members."
        )
    }

    fun openUserModMenu(userId: String, handle: String) {
        if (canModerate()) {
            _uiState.value = _uiState.value.copy(selectedUserForModAction = Pair(userId, handle))
        }
    }

    fun closeUserModMenu() {
        _uiState.value = _uiState.value.copy(selectedUserForModAction = null)
    }

    fun dismissSnackbar() {
        _uiState.value = _uiState.value.copy(chatSnackbarMessage = null)
    }

    // =========================================================
    // OTHER FEATURES (Offline vault, predictions, favorites)
    // =========================================================

    fun downloadFightForOffline(bout: Bout, quality: StreamQuality) {
        if (!canAccessOfflineDownloads()) {
            _uiState.value = _uiState.value.copy(
                chatSnackbarMessage = "Offline match downloads require PPV Pass.",
                isCheckoutDialogVisible = true
            )
            return
        }
        viewModelScope.launch {
            repository.startMatchDownload(bout, quality)
        }
    }

    fun deleteDownloadedFight(id: String) {
        viewModelScope.launch {
            repository.deleteDownloadedFight(id)
        }
    }

    fun playOfflineFight(fight: DownloadedFightEntity?) {
        _uiState.value = _uiState.value.copy(offlinePlayFight = fight)
    }

    fun submitPrediction(bout: Bout, fighter: Fighter, method: String, round: Int) {
        viewModelScope.launch {
            repository.savePrediction(bout.id, bout.eventName, fighter, method, round)
        }
    }

    fun toggleFavoriteFighter(fighter: Fighter, isFavorite: Boolean) {
        viewModelScope.launch {
            repository.toggleFavoriteFighter(fighter, isFavorite)
        }
    }

    fun setDivisionFilter(division: WeightClass?) {
        _uiState.value = _uiState.value.copy(selectedDivisionFilter = division)
    }

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectCdnNode(node: CdnNodeInfo) {
        _uiState.value = _uiState.value.copy(selectedCdnNode = node)
    }

    // =========================================================
    // FIGHTER META-ENGINE ROOM QUERIES
    // =========================================================

    fun getFighterStats(fighterId: String) = repository.getFighterStats(fighterId)

    fun getFightRecords(fighterId: String) = repository.getFightRecords(fighterId)

    fun getWeightClassHistory(fighterId: String) = repository.getWeightClassHistory(fighterId)

    // =========================================================
    // ETFC PUSH NOTIFICATION SYSTEM TRIGGERS
    // =========================================================

    fun triggerLiveEventNotification(bout: Bout? = _uiState.value.selectedBout) {
        val targetBout = bout ?: bouts.firstOrNull()
        if (targetBout != null) {
            notificationManager.sendLiveEventStartNotification(
                eventName = targetBout.eventName,
                mainEventTitle = "${targetBout.fighterRed.name} vs ${targetBout.fighterBlue.name}",
                venue = "Adwa 00 Museum, Addis Ababa",
                boutId = targetBout.id
            )
        }
    }

    fun triggerBoutStartNotification(bout: Bout) {
        notificationManager.sendBoutStartingNotification(
            fighterRed = bout.fighterRed.name,
            fighterBlue = bout.fighterBlue.name,
            matchTitle = bout.matchTitle,
            boutId = bout.id
        )
    }

    fun triggerPlatformUpdateNotification(
        title: String = "⚡ ETFC Stream Update",
        message: String = "Ultra Low-Latency Addis Ababa & Nairobi CDN clusters are online.",
        promoCode: String? = "ETFC2026"
    ) {
        notificationManager.sendPlatformUpdateNotification(title, message, promoCode)
    }

    fun triggerKoAlertNotification(
        winner: String = "Sedo",
        loser: String = "Jhonny",
        method: String = "KO (Adwa Hammer Cross)",
        round: Int = 3,
        time: String = "2:14"
    ) {
        notificationManager.sendKoFinishNotification(winner, loser, method, round, time)
    }

    // =========================================================
    // HOME SCREEN ACTION HANDLERS
    // =========================================================

    fun setHeroSlideIndex(index: Int) {
        _uiState.value = _uiState.value.copy(currentHeroIndex = index)
    }

    fun selectVideoCategory(category: VideoCategory) {
        _uiState.value = _uiState.value.copy(selectedVideoCategory = category)
    }

    fun openNewsArticle(article: NewsArticle?) {
        _uiState.value = _uiState.value.copy(selectedNewsArticle = article)
    }

    fun playPreviousVideo(video: PreviousFightVideo?) {
        _uiState.value = _uiState.value.copy(
            activePlayingVod = video,
            selectedYouTubeVideo = video,
            isYouTubePlayerOpen = video != null
        )
    }

    fun openYouTubePlayer(video: PreviousFightVideo?) {
        val targetVideo = video ?: previousVideos.firstOrNull()
        _uiState.value = _uiState.value.copy(
            selectedYouTubeVideo = targetVideo,
            isYouTubePlayerOpen = true
        )
    }

    fun closeYouTubePlayer() {
        _uiState.value = _uiState.value.copy(
            isYouTubePlayerOpen = false,
            selectedYouTubeVideo = null,
            activePlayingVod = null
        )
    }

    fun toggleLiveEventStatus(isLive: Boolean) {
        _uiState.value = _uiState.value.copy(isLiveEventOngoing = isLive)
    }

    fun toggleLivePreviewExpanded() {
        _uiState.value = _uiState.value.copy(isLivePreviewExpanded = !_uiState.value.isLivePreviewExpanded)
    }

    fun navigateToHeroSlideAction(slide: HeroSlide) {
        when (slide.type) {
            HeroSlideType.MATCH_PROMO_POSTER -> {
                // Open YouTube match replay if available or navigate to Fight Card / Arena
                val matchingVod = previousVideos.find { it.fighterRedName.contains(slide.redFighterName ?: "", ignoreCase = true) }
                if (matchingVod != null) {
                    openYouTubePlayer(matchingVod)
                } else if (slide.targetTab == "LIVE_ARENA") {
                    selectTab(AppTab.LIVE_ARENA)
                } else {
                    selectTab(AppTab.FIGHT_CARD)
                }
            }
            HeroSlideType.EARLY_BIRD_PPV, HeroSlideType.VIP_PASS_PROMO -> {
                val targetTier = ppvTiers.find { it.id == slide.ppvTierId } ?: ppvTiers.firstOrNull()
                openCheckoutDialog(targetTier)
            }
            HeroSlideType.UPCOMING_MATCH -> {
                selectTab(AppTab.FIGHT_CARD)
            }
            HeroSlideType.EVENT_PROMO -> {
                // Open first news article or promo
                val promoArticle = newsArticles.find { it.category == NewsCategory.TRAINING_CAMP } ?: newsArticles.firstOrNull()
                openNewsArticle(promoArticle)
            }
        }
    }

    fun downloadPreviousVideo(video: PreviousFightVideo) {
        viewModelScope.launch {
            val tier = video.contentTier
            val user = _uiState.value.currentUser
            val userRole = user?.role ?: UserRole.GUEST
            if (tier != ContentTier.FREE_PRELIMS && !userRole.canAccessTier(tier)) {
                _uiState.value = _uiState.value.copy(
                    chatSnackbarMessage = "🔒 This replay requires PPV or VIP Pass."
                )
                return@launch
            }
            // Direct playback in-app
            openYouTubePlayer(video)
        }
    }

    // =========================================================
    // FULL USER SETTINGS ACTIONS
    // =========================================================

    fun updateLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            settingsSnackbarMessage = "App language switched to $lang"
        )
    }

    fun updateCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(
            selectedCurrency = currency,
            settingsSnackbarMessage = "Currency updated to $currency"
        )
    }

    fun toggleDataSaverSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isDataSaverActive = enabled,
            settingsSnackbarMessage = if (enabled) "Data Saver enabled (capped to 240p SD)" else "Standard High Bitrate stream restored"
        )
    }

    fun toggleLowLatencySetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isLowLatencyMode = enabled,
            settingsSnackbarMessage = if (enabled) "Ultra Low Latency (<30ms) enabled" else "Standard stream buffering enabled"
        )
    }

    fun toggleHardwareAccelerationSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isHardwareAccelerationEnabled = enabled,
            settingsSnackbarMessage = if (enabled) "GPU Hardware decoding active" else "Software decoding active"
        )
    }

    fun setPreferredAudioFeedSetting(feed: String) {
        _uiState.value = _uiState.value.copy(
            preferredAudioFeed = feed,
            settingsSnackbarMessage = "Default audio feed: $feed"
        )
    }

    fun toggleHapticFeedbackSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isHapticFeedbackEnabled = enabled,
            settingsSnackbarMessage = if (enabled) "Haptic vibrations enabled on heavy strikes" else "Haptics disabled"
        )
    }

    fun toggleLiveStartAlertSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isLiveStartAlertEnabled = enabled)
    }

    fun toggleBoutStartAlertSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isBoutStartAlertEnabled = enabled)
    }

    fun toggleKnockoutAlertSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isKnockoutAlertEnabled = enabled)
    }

    fun toggleBiometricAuthSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isBiometricAuthEnabled = enabled,
            settingsSnackbarMessage = if (enabled) "Biometric fingerprint/face required for PPV checkout" else "Biometric purchase gate disabled"
        )
    }

    fun toggleDrmScreenShieldSetting(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            isDrmScreenShieldEnabled = enabled,
            settingsSnackbarMessage = if (enabled) "DRM Anti-Screen Capture Shield active" else "Screen capture allowed"
        )
    }

    fun setPreferredMobileWalletSetting(provider: PaymentProvider) {
        _uiState.value = _uiState.value.copy(
            preferredMobileWallet = provider,
            paymentProvider = provider,
            settingsSnackbarMessage = "Default payment wallet set to ${provider.displayName}"
        )
    }

    fun toggleChatProfanityFilter(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(
            chatProfanityFilterEnabled = enabled,
            settingsSnackbarMessage = if (enabled) "Live chat profanity shield active" else "Unfiltered chat"
        )
    }

    fun clearAppCache() {
        val freedMb = (85..210).random()
        _uiState.value = _uiState.value.copy(
            cachedStorageFreedMb = freedMb,
            settingsSnackbarMessage = "🧹 Freed $freedMb MB of temporary stream cache & video buffers!"
        )
    }

    fun dismissSettingsSnackbar() {
        _uiState.value = _uiState.value.copy(settingsSnackbarMessage = null)
    }
}
