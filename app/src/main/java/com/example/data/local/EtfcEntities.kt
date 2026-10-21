package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_fights")
data class DownloadedFightEntity(
    @PrimaryKey val id: String,
    val boutId: String,
    val title: String,
    val eventName: String,
    val redFighterName: String,
    val blueFighterName: String,
    val weightClass: String,
    val durationMinutes: Int,
    val quality: String,
    val fileSizeBytes: Long,
    val downloadProgress: Float, // 0.0 to 1.0
    val isCompleted: Boolean,
    val downloadTimestamp: Long,
    val localFilePath: String,
    val encryptedToken: String
)

@Entity(tableName = "ppv_passes")
data class PpvPassEntity(
    @PrimaryKey val passId: String,
    val tierId: String,
    val title: String,
    val eventName: String,
    val priceCurrency: String,
    val amountPaid: Double,
    val paymentProvider: String, // "Safaricom M-Pesa" or "Telebirr"
    val transactionRef: String,
    val phoneNumber: String,
    val status: String, // "ACTIVE", "EXPIRED", "PENDING"
    val purchaseTimestamp: Long,
    val validUntilTimestamp: Long,
    val signatureToken: String
)

@Entity(tableName = "user_predictions")
data class UserPredictionEntity(
    @PrimaryKey val boutId: String,
    val eventName: String,
    val fighterPickId: String,
    val fighterPickName: String,
    val winMethod: String, // "KO/TKO", "Submission", "Decision"
    val roundPick: Int,
    val timestamp: Long
)

@Entity(tableName = "favorite_fighters")
data class FavoriteFighterEntity(
    @PrimaryKey val fighterId: String,
    val name: String,
    val nickname: String,
    val division: String,
    val timestamp: Long
)

// ==========================================
// FIGHTER META-ENGINE SCHEMA (Room Database)
// ==========================================

@Entity(tableName = "fighter_profiles")
data class FighterProfileEntity(
    @PrimaryKey val fighterId: String,
    val name: String,
    val nickname: String,
    val countryName: String,
    val flagEmoji: String,
    val division: String,
    val stance: String,
    val reachInches: Int,
    val heightCm: Int,
    val isChampion: Boolean,
    val rank: Int?,
    val bio: String,
    val signatureMove: String,
    val primaryColorHex: Long,
    val hairStyle: String,
    val discipline: String,
    val lastUpdatedTimestamp: Long,
    val photoUrl: String? = null
)

@Entity(tableName = "fighter_statistics")
data class FighterStatsEntity(
    @PrimaryKey val fighterId: String,
    val totalFights: Int,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val noContests: Int = 0,
    val kos: Int,
    val submissions: Int,
    val decisions: Int,
    val koRatePercent: Float,
    val finishRatePercent: Float,
    val strikeAccuracyPercent: Int,
    val takedownDefensePercent: Int,
    val takedownAccuracyPercent: Int,
    val strikesLandedPerMin: Float,
    val strikesAbsorbedPerMin: Float,
    val takedownsPer15Min: Float,
    val avgFightTimeMinutes: Float,
    val winStreak: Int,
    val performanceBonusCount: Int
)

@Entity(tableName = "fight_record_history")
data class FightRecordHistoryEntity(
    @PrimaryKey val recordId: String,
    val fighterId: String,
    val opponentName: String,
    val opponentCountry: String,
    val result: String, // "WIN", "LOSS", "DRAW", "NC"
    val method: String, // "KO/TKO", "Submission", "Unanimous Decision", "Split Decision"
    val round: Int,
    val timeInRound: String, // e.g. "2:45"
    val eventName: String,
    val boutDate: String,
    val weightClass: String,
    val isTitleBout: Boolean,
    val orderIndex: Int
)

@Entity(tableName = "weight_class_history")
data class WeightClassHistoryEntity(
    @PrimaryKey val historyId: String,
    val fighterId: String,
    val weightClass: String,
    val startDate: String,
    val endDate: String?, // null if active
    val boutsInDivision: Int,
    val recordInDivision: String,
    val wasChampion: Boolean,
    val titleDefenses: Int,
    val notes: String
)

// ==========================================
// AUTHENTICATION & RBAC SCHEMA (Room Database)
// ==========================================

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey val userId: String,
    val email: String,
    val handle: String,
    val displayName: String,
    val passwordHash: String,
    val phoneNumber: String,
    val countryCode: String,
    val flagEmoji: String,
    val role: String, // "GUEST", "FREE_USER", "PPV_BUYER", "VIP_MEMBER", "MODERATOR", "ADMIN"
    val authToken: String,
    val createdTimestamp: Long,
    val lastLoginTimestamp: Long,
    val isBanned: Boolean = false,
    val isMutedUntilTimestamp: Long = 0L
)

// ==========================================
// REAL-TIME LIVE CHAT & MODERATION SCHEMA
// ==========================================

@Entity(tableName = "live_chat_messages")
data class ChatMessageEntity(
    @PrimaryKey val messageId: String,
    val boutId: String,
    val userId: String,
    val userHandle: String,
    val userName: String,
    val userFlag: String,
    val userRole: String,
    val messageText: String,
    val timestamp: Long,
    val isSuperFan: Boolean = false,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedByMod: String? = null,
    val flagCount: Int = 0,
    val cheerEmoji: String? = null,
    val mentionedHandles: String = "" // comma separated e.g. "@sedo,@etfc_mod"
)

@Entity(tableName = "chat_moderation_logs")
data class ModerationLogEntity(
    @PrimaryKey val logId: String,
    val modUserId: String,
    val modHandle: String,
    val targetUserId: String?,
    val targetMessageId: String?,
    val actionType: String,
    val reason: String,
    val timestamp: Long
)

// ==========================================
// ADMIN & DYNAMIC FEED / SYNC SCHEMA
// ==========================================

@Entity(tableName = "hero_slides")
data class HeroSlideEntity(
    @PrimaryKey val id: String,
    val type: String, // MATCH_PROMO_POSTER, UPCOMING_MATCH, EARLY_BIRD_PPV, EVENT_PROMO, VIP_PASS_PROMO
    val tag: String,
    val title: String,
    val subtitle: String,
    val eventDateOrCountdown: String,
    val venueOrLocation: String,
    val primaryCtaText: String,
    val secondaryCtaText: String?,
    val badgeColorHex: Long = 0xFFFAB818,
    val discountText: String? = null,
    val redFighterName: String? = null,
    val blueFighterName: String? = null,
    val weightClassText: String? = null,
    val matchCategoryBadge: String? = null,
    val eventEdition: String = "ETFC 002",
    val redFlag: String = "🇪🇹",
    val blueFlag: String = "🇪🇹",
    val hasChampionshipBelt: Boolean = false,
    val targetTab: String = "PPV_PASSES",
    val ppvTierId: String? = "tier_early_bird",
    val posterImageUrl: String? = null,
    val displayOrder: Int = 0,
    val isPinned: Boolean = false
)

@Entity(tableName = "previous_fight_videos")
data class PreviousVideoEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val eventName: String,
    val category: String, // ALL, FULL_REPLAY, KO_HIGHLIGHTS, PRESS_CONFERENCE, FIGHTER_BREAKDOWN
    val durationText: String,
    val durationSeconds: Int,
    val viewsCountStr: String,
    val publishedTimeAgo: String,
    val fighterRedName: String,
    val fighterBlueName: String,
    val resultMethod: String,
    val finishRoundText: String,
    val fileSizeMb: Int,
    val resolutionLabel: String = "1080p HD",
    val isTitleFight: Boolean = false,
    val contentTier: String = "FREE_PRELIMS",
    val likesCount: Int = 1250,
    val videoUrl: String = "",
    val youtubePlaylistUrl: String = "https://youtube.com/playlist?list=PLeRATZC0Qfoo&si=dk9LKYg2Tdcg-uVm",
    val youtubePlaylistId: String = "PLeRATZC0Qfoo",
    val youtubeVideoId: String = "0a8G81bZ7X0",
    val thumbnailUrl: String = "https://img.youtube.com/vi/0a8G81bZ7X0/hqdefault.jpg",
    val youtubeEmbedUrl: String = "https://www.youtube-nocookie.com/embed/0a8G81bZ7X0?autoplay=1&enablejsapi=1",
    val displayOrder: Int = 0
)

@Entity(tableName = "news_announcements")
data class NewsArticleEntity(
    @PrimaryKey val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val category: String, // PRESS_RELEASE, FIGHT_NEWS, TICKETS_PPV, TRAINING_CAMP, TECH_ANNOUNCEMENT
    val author: String,
    val authorRole: String,
    val publishedDate: String,
    val readTimeMinutes: Int,
    val isFeatured: Boolean = false,
    val tags: String = "",
    val relatedFighterNames: String = "",
    val imageUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)

// ==========================================
// OFFLINE ENGINE CORE SCHEMA (Fighter, Match, Announcement)
// ==========================================

@Entity(tableName = "fighters")
data class FighterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val nickname: String,
    val division: String,
    val country: String,
    val flagEmoji: String,
    val wins: Int,
    val losses: Int,
    val draws: Int = 0,
    val kos: Int = 0,
    val submissions: Int = 0,
    val decisions: Int = 0,
    val isChampion: Boolean = false,
    val rank: Int? = null,
    val reachInches: Int = 74,
    val heightCm: Int = 182,
    val stance: String = "Orthodox",
    val signatureMove: String = "Right Hook",
    val bio: String = "",
    val photoUrl: String? = null,
    val primaryColorHex: Long = 0xFFEF4444,
    val discipline: String = "MMA",
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "matches")
data class MatchEntity(
    @PrimaryKey val id: String,
    val eventName: String,
    val eventEdition: String = "ETFC 002",
    val eventDate: String,
    val venue: String,
    val division: String,
    val redFighterId: String,
    val redFighterName: String,
    val redFighterFlag: String = "🇪🇹",
    val blueFighterId: String,
    val blueFighterName: String,
    val blueFighterFlag: String = "🇪🇹",
    val isTitleFight: Boolean = false,
    val isMainEvent: Boolean = false,
    val isCoMain: Boolean = false,
    val posterUrl: String? = null,
    val status: String = "UPCOMING", // UPCOMING, LIVE, COMPLETED
    val winnerFighterId: String? = null,
    val resultMethod: String? = null,
    val finishRound: String? = null,
    val videoUrl: String? = null,
    val youtubeVideoId: String? = null,
    val thumbnailUrl: String? = null,
    val displayOrder: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

@Entity(tableName = "announcements")
data class AnnouncementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val subtitle: String,
    val content: String,
    val category: String = "NEWS", // NEWS, FIGHT_UPDATE, PRESS_RELEASE, EVENT_ALERT, PPV
    val author: String = "ETFC Official",
    val publishedAt: String,
    val imageUrl: String? = null,
    val actionUrl: String? = null,
    val isFeatured: Boolean = false,
    val tags: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
