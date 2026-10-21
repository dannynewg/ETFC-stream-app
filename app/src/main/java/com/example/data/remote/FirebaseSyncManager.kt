package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.local.AnnouncementEntity
import com.example.data.local.ChatMessageEntity
import com.example.data.local.EtfcDao
import com.example.data.local.FighterEntity
import com.example.data.local.FighterProfileEntity
import com.example.data.local.HeroSlideEntity
import com.example.data.local.MatchEntity
import com.example.data.local.NewsArticleEntity
import com.example.data.local.PreviousVideoEntity
import com.example.data.models.Bout
import com.example.data.models.ChatMessage
import com.example.data.models.Fighter
import com.example.data.models.HeroSlide
import com.example.data.models.NewsArticle
import com.example.data.models.PreviousFightVideo
import com.example.data.models.RegisteredAthlete
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.data.models.defaultEthiopianAthletes
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseSyncManager(
    private val context: Context,
    private val dao: EtfcDao,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "FirebaseSyncManager"
        const val COLL_FIGHTERS = "fighters"
        const val COLL_MATCHES = "matches"
        const val COLL_HERO_SLIDES = "hero_slides"
        const val COLL_VIDEOS = "previous_fight_videos"
        const val COLL_NEWS = "news_announcements"
        const val COLL_ANNOUNCEMENTS = "announcements"
        const val COLL_USERS = "users"
        const val COLL_CHAT = "arena_chat"
        const val COLL_ADMIN_INVITES = "admin_invites"
        const val COLL_ATHLETE_REGISTRATIONS = "athlete_registrations"
    }

    private val firestore: FirebaseFirestore by lazy {
        FirebaseFirestore.getInstance()
    }

    val userDataManager: UserDataManager by lazy {
        UserDataManager(context, dao, firestore, coroutineScope)
    }

    private val _isCloudSyncEnabled = MutableStateFlow(true)
    val isCloudSyncEnabled: StateFlow<Boolean> = _isCloudSyncEnabled.asStateFlow()

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _lastSyncTimestamp = MutableStateFlow(System.currentTimeMillis())
    val lastSyncTimestamp: StateFlow<Long> = _lastSyncTimestamp.asStateFlow()

    private val _syncStatusMessage = MutableStateFlow("Firebase Realtime Sync Active")
    val syncStatusMessage: StateFlow<String> = _syncStatusMessage.asStateFlow()

    private val _adminInvites = MutableStateFlow<List<com.example.data.models.AdminInvite>>(
        listOf(
            com.example.data.models.AdminInvite(
                email = "dannynewg@gmail.com",
                invitedBy = "Owner / Superadmin",
                timestamp = System.currentTimeMillis(),
                status = "ACTIVE_OWNER"
            )
        )
    )
    val adminInvites: StateFlow<List<com.example.data.models.AdminInvite>> = _adminInvites.asStateFlow()

    private val _athleteRegistrations = MutableStateFlow<List<RegisteredAthlete>>(defaultEthiopianAthletes)
    val athleteRegistrations: StateFlow<List<RegisteredAthlete>> = _athleteRegistrations.asStateFlow()

    private val listeners = mutableListOf<ListenerRegistration>()

    /**
     * Start real-time Firestore listeners for all collections.
     * When any remote client/admin modifies data, it automatically updates local Room tables.
     */
    fun startRealtimeSync() {
        try {
            stopRealtimeSync()
            Log.d(TAG, "Initializing Firestore real-time snapshot listeners...")

            // 1. Listen to Fighters
            val fightersListener = firestore.collection(COLL_FIGHTERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Fighters listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val name = data["name"] as? String ?: continue
                                    val nickname = data["nickname"] as? String ?: ""
                                    val division = data["division"] as? String ?: "Middleweight"
                                    val country = data["country"] as? String ?: "Ethiopia"
                                    val flagEmoji = data["flagEmoji"] as? String ?: "🇪🇹"
                                    val wins = (data["wins"] as? Number)?.toInt() ?: 0
                                    val losses = (data["losses"] as? Number)?.toInt() ?: 0
                                    val draws = (data["draws"] as? Number)?.toInt() ?: 0
                                    val kos = (data["kos"] as? Number)?.toInt() ?: 0
                                    val submissions = (data["submissions"] as? Number)?.toInt() ?: 0
                                    val isChampion = data["isChampion"] as? Boolean ?: false
                                    val rank = (data["rank"] as? Number)?.toInt() ?: 1
                                    val reachInches = (data["reachInches"] as? Number)?.toInt() ?: 72
                                    val heightCm = (data["heightCm"] as? Number)?.toInt() ?: 180
                                    val stance = data["stance"] as? String ?: "Orthodox"
                                    val signatureMove = data["signatureMove"] as? String ?: ""
                                    val bio = data["bio"] as? String ?: ""
                                    val photoUrl = data["photoUrl"] as? String
                                    val primaryColorHex = (data["primaryColorHex"] as? Number)?.toLong() ?: 0xFFEF4444

                                    val fighterEntity = FighterEntity(
                                        id = id,
                                        name = name,
                                        nickname = nickname,
                                        division = division,
                                        country = country,
                                        flagEmoji = flagEmoji,
                                        wins = wins,
                                        losses = losses,
                                        draws = draws,
                                        kos = kos,
                                        submissions = submissions,
                                        decisions = 0,
                                        isChampion = isChampion,
                                        rank = rank,
                                        reachInches = reachInches,
                                        heightCm = heightCm,
                                        stance = stance,
                                        signatureMove = signatureMove,
                                        bio = bio,
                                        photoUrl = photoUrl,
                                        primaryColorHex = primaryColorHex,
                                        discipline = "MMA",
                                        lastUpdated = System.currentTimeMillis()
                                    )
                                    dao.insertFighter(fighterEntity)

                                    val profileEntity = FighterProfileEntity(
                                        fighterId = id,
                                        name = name,
                                        nickname = nickname,
                                        countryName = country,
                                        flagEmoji = flagEmoji,
                                        division = division,
                                        stance = stance,
                                        reachInches = reachInches,
                                        heightCm = heightCm,
                                        isChampion = isChampion,
                                        rank = rank,
                                        bio = bio,
                                        signatureMove = signatureMove,
                                        primaryColorHex = primaryColorHex,
                                        hairStyle = "FADE_BEARD",
                                        discipline = "MMA",
                                        lastUpdatedTimestamp = System.currentTimeMillis(),
                                        photoUrl = photoUrl
                                    )
                                    dao.insertFighterProfile(profileEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing fighter from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(fightersListener)

            // 2. Listen to Matches
            val matchesListener = firestore.collection(COLL_MATCHES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Matches listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val eventName = data["eventName"] as? String ?: "ETFC 002: Adwa Fight Night"
                                    val eventEdition = data["eventEdition"] as? String ?: "ETFC 002"
                                    val eventDate = data["eventDate"] as? String ?: "Aug 27, 2026"
                                    val venue = data["venue"] as? String ?: "Adwa 00 Museum Arena, Addis Ababa"
                                    val division = data["division"] as? String ?: "Middleweight"
                                    val redFighterId = data["redFighterId"] as? String ?: ""
                                    val redFighterName = data["redFighterName"] as? String ?: "Fighter Red"
                                    val redFighterFlag = data["redFighterFlag"] as? String ?: "🇪🇹"
                                    val blueFighterId = data["blueFighterId"] as? String ?: ""
                                    val blueFighterName = data["blueFighterName"] as? String ?: "Fighter Blue"
                                    val blueFighterFlag = data["blueFighterFlag"] as? String ?: "🇪🇹"
                                    val isTitleFight = data["isTitleFight"] as? Boolean ?: false
                                    val isMainEvent = data["isMainEvent"] as? Boolean ?: false
                                    val isCoMain = data["isCoMain"] as? Boolean ?: false
                                    val posterUrl = data["posterUrl"] as? String
                                    val status = data["status"] as? String ?: "UPCOMING"
                                    val winnerFighterId = data["winnerFighterId"] as? String
                                    val resultMethod = data["resultMethod"] as? String
                                    val finishRound = data["finishRound"] as? String
                                    val videoUrl = data["videoUrl"] as? String
                                    val youtubeVideoId = data["youtubeVideoId"] as? String ?: "73P6qS14uP0"
                                    val thumbnailUrl = data["thumbnailUrl"] as? String
                                    val displayOrder = (data["displayOrder"] as? Number)?.toInt() ?: 0

                                    val matchEntity = MatchEntity(
                                        id = id,
                                        eventName = eventName,
                                        eventEdition = eventEdition,
                                        eventDate = eventDate,
                                        venue = venue,
                                        division = division,
                                        redFighterId = redFighterId,
                                        redFighterName = redFighterName,
                                        redFighterFlag = redFighterFlag,
                                        blueFighterId = blueFighterId,
                                        blueFighterName = blueFighterName,
                                        blueFighterFlag = blueFighterFlag,
                                        isTitleFight = isTitleFight,
                                        isMainEvent = isMainEvent,
                                        isCoMain = isCoMain,
                                        posterUrl = posterUrl,
                                        status = status,
                                        winnerFighterId = winnerFighterId,
                                        resultMethod = resultMethod,
                                        finishRound = finishRound,
                                        videoUrl = videoUrl,
                                        youtubeVideoId = youtubeVideoId,
                                        thumbnailUrl = thumbnailUrl,
                                        displayOrder = displayOrder,
                                        lastUpdated = System.currentTimeMillis()
                                    )
                                    dao.insertMatch(matchEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing match from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(matchesListener)

            // 3. Listen to Hero Slides
            val slidesListener = firestore.collection(COLL_HERO_SLIDES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Hero slides listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val title = data["title"] as? String ?: continue
                                    val subtitle = data["subtitle"] as? String ?: ""
                                    val tag = data["tag"] as? String ?: "LIVE EVENT"
                                    val eventDateOrCountdown = data["eventDateOrCountdown"] as? String ?: "SATURDAY • 8:00 PM EAT"
                                    val venueOrLocation = data["venueOrLocation"] as? String ?: "Adwa 00 Museum Arena, Addis Ababa"
                                    val primaryCtaText = data["primaryCtaText"] as? String ?: "ORDER PPV PASS"
                                    val posterImageUrl = data["posterImageUrl"] as? String
                                    val isPinned = data["isPinned"] as? Boolean ?: false

                                    val slideEntity = HeroSlideEntity(
                                        id = id,
                                        type = data["type"] as? String ?: "UPCOMING_MATCH",
                                        tag = tag,
                                        title = title,
                                        subtitle = subtitle,
                                        eventDateOrCountdown = eventDateOrCountdown,
                                        venueOrLocation = venueOrLocation,
                                        primaryCtaText = primaryCtaText,
                                        secondaryCtaText = data["secondaryCtaText"] as? String,
                                        badgeColorHex = (data["badgeColorHex"] as? Number)?.toLong() ?: 0xFFFAB818,
                                        discountText = data["discountText"] as? String,
                                        redFighterName = data["redFighterName"] as? String,
                                        blueFighterName = data["blueFighterName"] as? String,
                                        weightClassText = data["weightClassText"] as? String,
                                        matchCategoryBadge = data["matchCategoryBadge"] as? String,
                                        eventEdition = data["eventEdition"] as? String ?: "ETFC 002",
                                        redFlag = data["redFlag"] as? String ?: "🇪🇹",
                                        blueFlag = data["blueFlag"] as? String ?: "🇪🇹",
                                        posterImageUrl = posterImageUrl,
                                        isPinned = isPinned,
                                        displayOrder = (data["displayOrder"] as? Number)?.toInt() ?: 0
                                    )
                                    dao.insertHeroSlide(slideEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing hero slide from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(slidesListener)

            // 4. Listen to Fight Videos Archive
            val videosListener = firestore.collection(COLL_VIDEOS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Videos listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val title = data["title"] as? String ?: continue
                                    val subtitle = data["subtitle"] as? String ?: ""
                                    val eventName = data["eventName"] as? String ?: "ETFC 002: Adwa Fight Night"
                                    val durationText = data["durationText"] as? String ?: "15:00"
                                    val thumbnailUrl = data["thumbnailUrl"] as? String ?: "https://img.youtube.com/vi/0a8G81bZ7X0/hqdefault.jpg"
                                    val youtubeVideoId = data["youtubeVideoId"] as? String ?: "0a8G81bZ7X0"
                                    val category = data["category"] as? String ?: "FULL_REPLAY"
                                    val contentTier = data["contentTier"] as? String ?: "FREE_PRELIMS"

                                    val videoEntity = PreviousVideoEntity(
                                        id = id,
                                        title = title,
                                        subtitle = subtitle,
                                        eventName = eventName,
                                        category = category,
                                        durationText = durationText,
                                        durationSeconds = (data["durationSeconds"] as? Number)?.toInt() ?: 720,
                                        viewsCountStr = data["viewsCountStr"] as? String ?: "1.5K views",
                                        publishedTimeAgo = data["publishedTimeAgo"] as? String ?: "Recently",
                                        fighterRedName = data["fighterRedName"] as? String ?: "",
                                        fighterBlueName = data["fighterBlueName"] as? String ?: "",
                                        resultMethod = data["resultMethod"] as? String ?: "KO Round 2",
                                        finishRoundText = data["finishRoundText"] as? String ?: "Round 2",
                                        fileSizeMb = (data["fileSizeMb"] as? Number)?.toInt() ?: 150,
                                        resolutionLabel = data["resolutionLabel"] as? String ?: "1080p HD",
                                        isTitleFight = data["isTitleFight"] as? Boolean ?: false,
                                        contentTier = contentTier,
                                        likesCount = (data["likesCount"] as? Number)?.toInt() ?: 1250,
                                        videoUrl = data["videoUrl"] as? String ?: "",
                                        youtubePlaylistUrl = data["youtubePlaylistUrl"] as? String ?: "https://youtube.com/playlist?list=PLeRATZC0Qfoo&si=dk9LKYg2Tdcg-uVm",
                                        youtubePlaylistId = data["youtubePlaylistId"] as? String ?: "PLeRATZC0Qfoo",
                                        youtubeVideoId = youtubeVideoId,
                                        thumbnailUrl = thumbnailUrl,
                                        youtubeEmbedUrl = "https://www.youtube-nocookie.com/embed/$youtubeVideoId?autoplay=1&enablejsapi=1",
                                        displayOrder = (data["displayOrder"] as? Number)?.toInt() ?: 0
                                    )
                                    dao.insertVideo(videoEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing video from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(videosListener)

            // 5. Listen to News & Press Releases
            val newsListener = firestore.collection(COLL_NEWS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "News listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val title = data["title"] as? String ?: continue
                                    val summary = data["summary"] as? String ?: ""
                                    val fullContent = data["fullContent"] as? String ?: ""
                                    val author = data["author"] as? String ?: "ETFC Media Team"
                                    val authorRole = data["authorRole"] as? String ?: "ETFC Official Press"
                                    val publishedDate = data["publishedDate"] as? String ?: "TODAY"
                                    val category = data["category"] as? String ?: "PRESS_RELEASE"
                                    val imageUrl = data["imageUrl"] as? String

                                    val newsEntity = NewsArticleEntity(
                                        id = id,
                                        title = title,
                                        summary = summary,
                                        fullContent = fullContent,
                                        category = category,
                                        author = author,
                                        authorRole = authorRole,
                                        publishedDate = publishedDate,
                                        readTimeMinutes = (data["readTimeMinutes"] as? Number)?.toInt() ?: 3,
                                        isFeatured = data["isFeatured"] as? Boolean ?: false,
                                        tags = data["tags"] as? String ?: "",
                                        relatedFighterNames = data["relatedFighterNames"] as? String ?: "",
                                        imageUrl = imageUrl,
                                        timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    )
                                    dao.insertNewsArticle(newsEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing news from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(newsListener)

            // 5b. Listen to Firestore 'announcements' collection (alias for news_announcements)
            val announcementsListener = firestore.collection(COLL_ANNOUNCEMENTS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val title = (data["title"] as? String) ?: continue
                                    val summary = (data["summary"] as? String) ?: (data["content"] as? String)?.take(120) ?: ""
                                    val fullContent = (data["fullContent"] as? String) ?: (data["content"] as? String) ?: ""
                                    val author = (data["author"] as? String) ?: "ETFC Media Team"
                                    val authorRole = (data["authorRole"] as? String) ?: "ETFC Official Press"
                                    val publishedDate = (data["publishedDate"] as? String) ?: "TODAY"
                                    val category = (data["category"] as? String) ?: "PRESS_RELEASE"
                                    val imageUrl = data["imageUrl"] as? String

                                    val newsEntity = NewsArticleEntity(
                                        id = id,
                                        title = title,
                                        summary = summary,
                                        fullContent = fullContent,
                                        category = category,
                                        author = author,
                                        authorRole = authorRole,
                                        publishedDate = publishedDate,
                                        readTimeMinutes = (data["readTimeMinutes"] as? Number)?.toInt() ?: 3,
                                        isFeatured = data["isFeatured"] as? Boolean ?: true,
                                        tags = data["tags"] as? String ?: "",
                                        relatedFighterNames = data["relatedFighterNames"] as? String ?: "",
                                        imageUrl = imageUrl,
                                        timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()
                                    )
                                    dao.insertNewsArticle(newsEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing announcement from Firestore", e)
                                }
                            }
                            _lastSyncTimestamp.value = System.currentTimeMillis()
                        }
                    }
                }
            listeners.add(announcementsListener)

            // 6. Listen to Live Arena Chat
            val chatListener = firestore.collection(COLL_CHAT)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Arena chat listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val id = doc.id
                                    val boutId = data["boutId"] as? String ?: "bout_main_1"
                                    val userId = data["userId"] as? String ?: "u_guest"
                                    val userHandle = data["userHandle"] as? String ?: "@etfc_fan"
                                    val userName = data["userName"] as? String ?: (data["sender"] as? String ?: "Fan")
                                    val userFlag = data["userFlag"] as? String ?: "🇪🇹"
                                    val userRole = data["userRole"] as? String ?: "USER"
                                    val messageText = data["messageText"] as? String ?: (data["message"] as? String ?: continue)
                                    val isSuperFan = data["isSuperFan"] as? Boolean ?: (data["isVip"] as? Boolean ?: false)
                                    val isPinned = data["isPinned"] as? Boolean ?: false
                                    val isDeleted = data["isDeleted"] as? Boolean ?: false
                                    val timestamp = (data["timestamp"] as? Number)?.toLong() ?: System.currentTimeMillis()

                                    val chatEntity = ChatMessageEntity(
                                        messageId = id,
                                        boutId = boutId,
                                        userId = userId,
                                        userHandle = userHandle,
                                        userName = userName,
                                        userFlag = userFlag,
                                        userRole = userRole,
                                        messageText = messageText,
                                        timestamp = timestamp,
                                        isSuperFan = isSuperFan,
                                        isPinned = isPinned,
                                        isDeleted = isDeleted,
                                        deletedByMod = null,
                                        flagCount = 0,
                                        cheerEmoji = data["cheerEmoji"] as? String,
                                        mentionedHandles = ""
                                    )
                                    dao.insertChatMessage(chatEntity)
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error parsing chat from Firestore", e)
                                }
                            }
                        }
                    }
                }
            listeners.add(chatListener)

            // 7. Listen to Admin Team Invites
            val invitesListener = firestore.collection(COLL_ADMIN_INVITES)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Admin invites listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        val list = mutableListOf(
                            com.example.data.models.AdminInvite(
                                email = "dannynewg@gmail.com",
                                invitedBy = "Owner / Superadmin",
                                timestamp = 0L,
                                status = "ACTIVE_OWNER"
                            )
                        )
                        for (doc in querySnap.documents) {
                            val email = (doc.data?.get("email") as? String) ?: doc.id
                            if (email.isNotBlank() && !email.equals("dannynewg@gmail.com", ignoreCase = true)) {
                                val invitedBy = doc.data?.get("invitedBy") as? String ?: "dannynewg@gmail.com"
                                val ts = (doc.data?.get("timestamp") as? Number)?.toLong() ?: System.currentTimeMillis()
                                val status = doc.data?.get("status") as? String ?: "ACTIVE"
                                list.add(com.example.data.models.AdminInvite(email, invitedBy, ts, status))
                            }
                        }
                        _adminInvites.value = list
                    }
                }
            listeners.add(invitesListener)

            // 8. Listen to Firestore 'users' collection for live Role and Profile updates
            val usersListener = firestore.collection(COLL_USERS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) return@addSnapshotListener
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            for (doc in querySnap.documents) {
                                try {
                                    val data = doc.data ?: continue
                                    val userId = (data["userId"] as? String) ?: doc.id
                                    val email = (data["email"] as? String)?.trim()?.lowercase() ?: ""
                                    val roleStr = (data["role"] as? String)?.lowercase() ?: "user"
                                    val isAdmin = email.equals("dannynewg@gmail.com", ignoreCase = true) || roleStr == "admin"
                                    val roleName = if (isAdmin) com.example.data.models.UserRole.ADMIN.name else com.example.data.models.UserRole.USER.name

                                    if (email.isNotBlank()) {
                                        val existing = dao.getUserByEmail(email)
                                        if (existing != null) {
                                            dao.updateUserRole(existing.userId, roleName)
                                        }
                                    }
                                } catch (e: Exception) {
                                    Log.e(TAG, "Error updating local user from Firestore", e)
                                }
                            }
                        }
                    }
                }
            listeners.add(usersListener)

            // 9. Listen to Ethiopian Combat Athlete Registrations in Firestore
            val registrationsListener = firestore.collection(COLL_ATHLETE_REGISTRATIONS)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.w(TAG, "Athlete Registrations listener error: ${error.message}")
                        return@addSnapshotListener
                    }
                    snapshot?.let { querySnap ->
                        coroutineScope.launch(Dispatchers.IO) {
                            try {
                                val fetchedList = mutableListOf<RegisteredAthlete>()
                                for (doc in querySnap.documents) {
                                    val data = doc.data ?: continue
                                    val athlete = RegisteredAthlete.fromMap(doc.id, data)
                                    fetchedList.add(athlete)
                                }
                                val remoteIds = fetchedList.map { it.athleteId }.toSet()
                                val mergedList = fetchedList + defaultEthiopianAthletes.filterNot { it.athleteId in remoteIds }
                                _athleteRegistrations.value = mergedList.sortedByDescending { it.registrationTimestamp }
                                Log.d(TAG, "Synced ${fetchedList.size} athlete registrations from Firestore")
                            } catch (e: Exception) {
                                Log.e(TAG, "Error parsing athlete registrations from Firestore", e)
                            }
                        }
                    }
                }
            listeners.add(registrationsListener)

            _syncStatusMessage.value = "🟢 Connected to Firebase Firestore (Live Sync)"
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start realtime sync", e)
            _syncStatusMessage.value = "⚠️ Local Mode (Firebase init pending): ${e.localizedMessage}"
        }
    }

    fun isEmailInvitedAdmin(email: String): Boolean {
        if (email.trim().equals("dannynewg@gmail.com", ignoreCase = true)) return true
        return _adminInvites.value.any { it.email.equals(email.trim(), ignoreCase = true) }
    }

    suspend fun inviteAdminUser(email: String, invitedBy: String = "dannynewg@gmail.com"): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        try {
            val ts = System.currentTimeMillis()
            val map = hashMapOf(
                "email" to cleanEmail,
                "invitedBy" to invitedBy,
                "timestamp" to ts,
                "status" to "ACTIVE"
            )
            firestore.collection(COLL_ADMIN_INVITES).document(cleanEmail).set(map, SetOptions.merge()).await()

            // Update or create in Firestore 'users' collection with role = 'admin'
            val userDocs = firestore.collection(COLL_USERS).whereEqualTo("email", cleanEmail).get().await()
            if (!userDocs.isEmpty) {
                for (doc in userDocs.documents) {
                    firestore.collection(COLL_USERS).document(doc.id).update("role", "admin", "updatedAt", ts).await()
                }
            } else {
                val docId = "usr_${cleanEmail.replace("@", "_").replace(".", "_")}"
                val placeholderMap = hashMapOf(
                    "userId" to docId,
                    "email" to cleanEmail,
                    "role" to "admin",
                    "displayName" to cleanEmail.substringBefore("@").replaceFirstChar { it.uppercase() },
                    "handle" to "@${cleanEmail.substringBefore("@")}",
                    "createdTimestamp" to ts,
                    "lastLoginTimestamp" to ts,
                    "isBanned" to false,
                    "isMutedUntilTimestamp" to 0L
                )
                firestore.collection(COLL_USERS).document(docId).set(placeholderMap, SetOptions.merge()).await()
            }

            // Sync to local Room database
            val localUser = dao.getUserByEmail(cleanEmail)
            if (localUser != null) {
                dao.updateUserRole(localUser.userId, com.example.data.models.UserRole.ADMIN.name)
            }

            val currentList = _adminInvites.value.filter { !it.email.equals(cleanEmail, ignoreCase = true) }.toMutableList()
            currentList.add(com.example.data.models.AdminInvite(cleanEmail, invitedBy, ts, "ACTIVE"))
            _adminInvites.value = currentList
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create admin invite in Firestore", e)
            val currentList = _adminInvites.value.filter { !it.email.equals(cleanEmail, ignoreCase = true) }.toMutableList()
            currentList.add(com.example.data.models.AdminInvite(cleanEmail, invitedBy, System.currentTimeMillis(), "ACTIVE (Local)"))
            _adminInvites.value = currentList
            Result.success(Unit)
        }
    }

    suspend fun revokeAdminInvite(email: String): Result<Unit> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim().lowercase()
        if (cleanEmail.equals("dannynewg@gmail.com", ignoreCase = true)) {
            return@withContext Result.failure(IllegalStateException("Cannot revoke superadmin owner"))
        }
        try {
            firestore.collection(COLL_ADMIN_INVITES).document(cleanEmail).delete().await()

            // Update role to 'user' in Firestore 'users' collection
            val userDocs = firestore.collection(COLL_USERS).whereEqualTo("email", cleanEmail).get().await()
            for (doc in userDocs.documents) {
                firestore.collection(COLL_USERS).document(doc.id).update("role", "user", "updatedAt", System.currentTimeMillis()).await()
            }

            // Sync to local Room database
            val localUser = dao.getUserByEmail(cleanEmail)
            if (localUser != null) {
                dao.updateUserRole(localUser.userId, com.example.data.models.UserRole.USER.name)
            }

            _adminInvites.value = _adminInvites.value.filter { !it.email.equals(cleanEmail, ignoreCase = true) }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to revoke admin invite in Firestore", e)
            _adminInvites.value = _adminInvites.value.filter { !it.email.equals(cleanEmail, ignoreCase = true) }
            Result.success(Unit)
        }
    }

    fun stopRealtimeSync() {
        listeners.forEach { it.remove() }
        listeners.clear()
    }

    private suspend fun ensureFirebaseAuthSession() {
        try {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
        } catch (e: Exception) {
            Log.d(TAG, "Anonymous auth note: ${e.message}")
        }
    }

    // =========================================================================
    // ADMIN CLOUD WRITES (Push to Firestore in Real-Time)
    // =========================================================================

    suspend fun pushFighterToCloud(fighter: Fighter) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "name" to fighter.name,
                "nickname" to fighter.nickname,
                "division" to fighter.division.label,
                "country" to fighter.countryName,
                "flagEmoji" to fighter.flagEmoji,
                "wins" to fighter.wins,
                "losses" to fighter.losses,
                "draws" to fighter.draws,
                "kos" to fighter.kos,
                "submissions" to fighter.submissions,
                "isChampion" to fighter.isChampion,
                "rank" to fighter.rank,
                "reachInches" to fighter.reachInches,
                "heightCm" to fighter.heightCm,
                "stance" to fighter.stance,
                "signatureMove" to fighter.signatureMove,
                "bio" to fighter.bio,
                "photoUrl" to (fighter.photoUrl ?: ""),
                "primaryColorHex" to fighter.primaryColorHex,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_FIGHTERS).document(fighter.id).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced fighter ${fighter.id} to Firestore")
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore write permission note (offline cache active): ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing fighter to Firestore: ${e.message}")
        }
    }

    suspend fun deleteFighterFromCloud(fighterId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_FIGHTERS).document(fighterId).delete().await()
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore delete note: ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice deleting fighter from Firestore: ${e.message}")
        }
    }

    suspend fun pushMatchToCloud(bout: Bout, order: Int = 0) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "displayOrder" to order,
                "eventName" to bout.eventName,
                "eventDate" to "Aug 27, 2026",
                "venue" to bout.eventVenue,
                "division" to bout.weightClass.label,
                "redFighterId" to bout.fighterRed.id,
                "redFighterName" to bout.fighterRed.name,
                "redFighterFlag" to bout.fighterRed.flagEmoji,
                "blueFighterId" to bout.fighterBlue.id,
                "blueFighterName" to bout.fighterBlue.name,
                "blueFighterFlag" to bout.fighterBlue.flagEmoji,
                "isTitleFight" to bout.isTitleFight,
                "isMainEvent" to (bout.boutType.name == "MAIN_EVENT"),
                "isCoMain" to (bout.boutType.name == "CO_MAIN"),
                "status" to bout.status.name,
                "winnerFighterId" to (bout.winnerId ?: ""),
                "resultMethod" to (bout.resultMethod ?: ""),
                "posterUrl" to (bout.fighterRed.photoUrl ?: ""),
                "youtubeVideoId" to "73P6qS14uP0",
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_MATCHES).document(bout.id).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced match ${bout.id} to Firestore")
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore write match note: ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing match to Firestore: ${e.message}")
        }
    }

    suspend fun deleteMatchFromCloud(matchId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_MATCHES).document(matchId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Notice deleting match from Firestore: ${e.message}")
        }
    }

    suspend fun pushHeroSlideToCloud(slide: HeroSlide, isPinned: Boolean = false) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "type" to slide.type.name,
                "tag" to slide.tag,
                "title" to slide.title,
                "subtitle" to slide.subtitle,
                "eventDateOrCountdown" to slide.eventDateOrCountdown,
                "venueOrLocation" to slide.venueOrLocation,
                "primaryCtaText" to slide.primaryCtaText,
                "secondaryCtaText" to (slide.secondaryCtaText ?: ""),
                "badgeColorHex" to slide.badgeColorHex,
                "discountText" to (slide.discountText ?: ""),
                "redFighterName" to (slide.redFighterName ?: ""),
                "blueFighterName" to (slide.blueFighterName ?: ""),
                "weightClassText" to (slide.weightClassText ?: ""),
                "matchCategoryBadge" to (slide.matchCategoryBadge ?: ""),
                "eventEdition" to slide.eventEdition,
                "redFlag" to slide.redFlag,
                "blueFlag" to slide.blueFlag,
                "posterImageUrl" to (slide.posterImageUrl ?: ""),
                "isPinned" to isPinned,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_HERO_SLIDES).document(slide.id).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced slide ${slide.id} to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing hero slide to Firestore: ${e.message}")
        }
    }

    suspend fun deleteHeroSlideFromCloud(slideId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_HERO_SLIDES).document(slideId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Notice deleting hero slide from Firestore: ${e.message}")
        }
    }

    suspend fun pushVideoToCloud(video: PreviousFightVideo) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "title" to video.title,
                "subtitle" to video.subtitle,
                "eventName" to video.eventName,
                "category" to video.category.name,
                "contentTier" to video.contentTier.name,
                "durationSeconds" to video.durationSeconds,
                "durationText" to video.durationText,
                "viewsCountStr" to video.viewsCountStr,
                "publishedTimeAgo" to video.publishedTimeAgo,
                "fighterRedName" to video.fighterRedName,
                "fighterBlueName" to video.fighterBlueName,
                "resultMethod" to video.resultMethod,
                "finishRoundText" to video.finishRoundText,
                "fileSizeMb" to video.fileSizeMb,
                "resolutionLabel" to video.resolutionLabel,
                "isTitleFight" to video.isTitleFight,
                "likesCount" to video.likesCount,
                "videoUrl" to video.videoUrl,
                "youtubePlaylistUrl" to video.youtubePlaylistUrl,
                "youtubePlaylistId" to video.youtubePlaylistId,
                "youtubeVideoId" to (video.youtubeVideoId ?: "0a8G81bZ7X0"),
                "thumbnailUrl" to video.thumbnailUrl,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_VIDEOS).document(video.id).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced video ${video.id} to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing video to Firestore: ${e.message}")
        }
    }

    suspend fun deleteVideoFromCloud(videoId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_VIDEOS).document(videoId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Notice deleting video from Firestore: ${e.message}")
        }
    }

    suspend fun pushNewsToCloud(article: NewsArticle) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "title" to article.title,
                "summary" to article.summary,
                "fullContent" to article.fullContent,
                "author" to article.author,
                "authorRole" to article.authorRole,
                "publishedDate" to article.publishedDate,
                "category" to article.category.name,
                "readTimeMinutes" to article.readTimeMinutes,
                "isFeatured" to article.isFeatured,
                "tags" to article.tags.joinToString(","),
                "relatedFighterNames" to article.relatedFighterNames.joinToString(","),
                "imageUrl" to (article.imageUrl ?: ""),
                "relatedBoutId" to (article.relatedBoutId ?: ""),
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_NEWS).document(article.id).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced news article ${article.id} to Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing news to Firestore: ${e.message}")
        }
    }

    suspend fun deleteNewsFromCloud(articleId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_NEWS).document(articleId).delete().await()
        } catch (e: Exception) {
            Log.w(TAG, "Notice deleting news from Firestore: ${e.message}")
        }
    }

    suspend fun sendChatMessageToCloud(msg: ChatMessage) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = hashMapOf(
                "boutId" to msg.boutId,
                "userId" to msg.userId,
                "userHandle" to msg.userHandle,
                "userName" to msg.userName,
                "userFlag" to msg.userFlag,
                "userRole" to msg.userRole.name,
                "messageText" to msg.messageText,
                "timestamp" to msg.timestamp,
                "isSuperFan" to msg.isSuperFan,
                "isPinned" to msg.isPinned,
                "cheerEmoji" to (msg.cheerEmoji ?: "")
            )
            firestore.collection(COLL_CHAT).document(msg.id).set(map).await()
        } catch (e: Exception) {
            Log.w(TAG, "Notice sending chat to Firestore: ${e.message}")
        }
    }

    suspend fun pushUserToCloud(user: UserAccount) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val cleanEmail = user.email.trim().lowercase()
            val isAdmin = cleanEmail.equals("dannynewg@gmail.com", ignoreCase = true) || isEmailInvitedAdmin(cleanEmail) || user.role == UserRole.ADMIN
            val roleStr = if (isAdmin) "admin" else "user"
            val map = hashMapOf(
                "userId" to user.userId,
                "email" to cleanEmail,
                "role" to roleStr,
                "displayName" to user.displayName,
                "handle" to user.handle,
                "phoneNumber" to user.phoneNumber,
                "countryCode" to user.countryCode,
                "flagEmoji" to user.flagEmoji,
                "lastLoginTimestamp" to System.currentTimeMillis()
            )
            firestore.collection(COLL_USERS).document(user.userId).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced user ${user.userId} to Firestore")
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore user sync permission note (local cache active): ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing user to Firestore: ${e.message}")
        }
    }

    suspend fun fetchUserRole(emailOrUserId: String): String = withContext(Dispatchers.IO) {
        val clean = emailOrUserId.trim().lowercase()
        if (clean.equals("dannynewg@gmail.com", ignoreCase = true) || isEmailInvitedAdmin(clean)) {
            return@withContext "admin"
        }
        try {
            ensureFirebaseAuthSession()
            val byEmail = firestore.collection(COLL_USERS).whereEqualTo("email", clean).get().await()
            if (!byEmail.isEmpty) {
                val doc = byEmail.documents.first()
                return@withContext doc.getString("role")?.lowercase() ?: "user"
            }
            val byId = firestore.collection(COLL_USERS).document(emailOrUserId).get().await()
            if (byId.exists()) {
                return@withContext byId.getString("role")?.lowercase() ?: "user"
            }
        } catch (e: Exception) {
            Log.w(TAG, "Notice fetching user role from Firestore: ${e.message}")
        }
        return@withContext "user"
    }

    suspend fun pushAthleteRegistrationToCloud(athlete: RegisteredAthlete) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val map = athlete.toMap()
            firestore.collection(COLL_ATHLETE_REGISTRATIONS).document(athlete.athleteId).set(map, SetOptions.merge()).await()
            Log.d(TAG, "Successfully synced athlete registration ${athlete.athleteId} to Firestore")
        } catch (e: FirebaseFirestoreException) {
            Log.w(TAG, "Firestore write permission note for registration: ${e.message}")
        } catch (e: Exception) {
            Log.w(TAG, "Notice pushing athlete registration to Firestore: ${e.message}")
        }
    }

    suspend fun deleteAthleteRegistrationFromCloud(athleteId: String) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            firestore.collection(COLL_ATHLETE_REGISTRATIONS).document(athleteId).delete().await()
            Log.d(TAG, "Deleted athlete registration $athleteId from Firestore")
        } catch (e: Exception) {
            Log.w(TAG, "Error deleting athlete registration from Firestore: ${e.message}")
        }
    }

    suspend fun updateAthleteRegistrationStatusInCloud(
        athleteId: String,
        newStatus: String,
        newLeagueAssignment: String
    ) = withContext(Dispatchers.IO) {
        try {
            ensureFirebaseAuthSession()
            val updateMap = hashMapOf<String, Any>(
                "status" to newStatus,
                "leagueAssignment" to newLeagueAssignment,
                "updatedAt" to System.currentTimeMillis()
            )
            firestore.collection(COLL_ATHLETE_REGISTRATIONS).document(athleteId).update(updateMap).await()
            Log.d(TAG, "Updated status for athlete registration $athleteId to $newStatus")
        } catch (e: Exception) {
            Log.w(TAG, "Error updating athlete registration status in Firestore: ${e.message}")
        }
    }

    /**
     * Seeds initial database items to Firestore if Firestore is currently empty.
     */
    suspend fun seedInitialDataToCloud(
        fighters: List<Fighter>,
        bouts: List<Bout>,
        slides: List<HeroSlide>,
        videos: List<PreviousFightVideo>,
        news: List<NewsArticle>
    ) = withContext(Dispatchers.IO) {
        _isSyncing.value = true
        _syncStatusMessage.value = "Uploading initial datasets to Firestore..."
        try {
            fighters.forEach { pushFighterToCloud(it) }
            bouts.forEachIndexed { idx, bout -> pushMatchToCloud(bout, idx) }
            slides.forEach { pushHeroSlideToCloud(it) }
            videos.forEach { pushVideoToCloud(it) }
            news.forEach { pushNewsToCloud(it) }
            _syncStatusMessage.value = "✅ All initial datasets synced to Firebase!"
        } catch (e: Exception) {
            Log.e(TAG, "Error seeding data to Firebase", e)
            _syncStatusMessage.value = "⚠️ Sync warning: ${e.message}"
        } finally {
            _isSyncing.value = false
            _lastSyncTimestamp.value = System.currentTimeMillis()
        }
    }
}
