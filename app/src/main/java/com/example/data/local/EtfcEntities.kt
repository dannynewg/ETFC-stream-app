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
    val lastUpdatedTimestamp: Long
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


