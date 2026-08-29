package com.example.data.models

enum class UserRole(val label: String, val badgeTitle: String, val tierLevel: Int, val colorHex: Long, val badgeEmoji: String) {
    GUEST("Guest Viewer", "GUEST", 0, 0xFF9CA3AF, "👤"),
    FREE_USER("Registered Fan", "FREE FAN", 1, 0xFF38BDF8, "🆓"),
    PPV_BUYER("PPV Passholder", "PPV PASS", 2, 0xFFEF4444, "🥊"),
    VIP_MEMBER("VIP Ringside Member", "VIP RINGSIDE", 3, 0xFFFAB818, "👑"),
    MODERATOR("Arena Moderator", "MODERATOR", 4, 0xFF10B981, "🛡️"),
    ADMIN("ETFC Administrator", "ADMIN", 5, 0xFFA855F7, "⚡");

    fun canAccessTier(tier: ContentTier): Boolean {
        return this.tierLevel >= tier.requiredTierLevel
    }

    fun canModerate(): Boolean {
        return this.tierLevel >= MODERATOR.tierLevel
    }
}

enum class ContentTier(val label: String, val requiredTierLevel: Int, val description: String) {
    FREE_PRELIMS("Free Early Prelims", 0, "Early prelim bouts and live radio commentary stream"),
    STANDARD_PPV_MAIN_CARD("Standard PPV Main Card", 2, "Full main card live broadcast & 720p/1080p stream"),
    VIP_RINGSIDE_EXCLUSIVE("VIP Multi-Cam & 4K", 3, "Red/Blue corner cams, Ref Cam, exclusive audio & VIP badge"),
    OFFLINE_VAULT("Offline Match Vault", 2, "Encrypted downloadable full fight replays"),
    MODERATION_TOOLS("Arena Moderation Console", 4, "Pin messages, slow mode, user timeout and purge controls")
}

data class UserAccount(
    val userId: String,
    val email: String,
    val handle: String, // e.g. @sedo_fan
    val displayName: String,
    val phoneNumber: String,
    val countryCode: String = "+251",
    val flagEmoji: String = "🇪🇹",
    val role: UserRole = UserRole.FREE_USER,
    val authToken: String,
    val createdTimestamp: Long = System.currentTimeMillis(),
    val lastLoginTimestamp: Long = System.currentTimeMillis(),
    val isBanned: Boolean = false,
    val isMutedUntilTimestamp: Long = 0L
)

sealed interface AuthResult {
    data class Success(val user: UserAccount, val message: String) : AuthResult
    data class Error(val errorMessage: String) : AuthResult
}

enum class ModActionType(val label: String) {
    DELETE_MESSAGE("Delete Message"),
    TIMEOUT_USER_60S("Timeout User (60s)"),
    BAN_USER("Ban User from Arena"),
    PIN_MESSAGE("Pin Message"),
    UNPIN_MESSAGE("Unpin Message"),
    SET_SLOW_MODE("Slow Mode Cooldown"),
    TOGGLE_VIP_ONLY("VIP Only Chat")
}

data class ChatMessage(
    val id: String,
    val boutId: String,
    val userId: String,
    val userHandle: String,
    val userName: String,
    val userFlag: String,
    val userRole: UserRole,
    val messageText: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isSuperFan: Boolean = false,
    val isPinned: Boolean = false,
    val isDeleted: Boolean = false,
    val deletedByMod: String? = null,
    val flagCount: Int = 0,
    val cheerEmoji: String? = null,
    val mentionedHandles: List<String> = emptyList()
) {
    fun formattedTime(): String {
        val diff = (System.currentTimeMillis() - timestamp).coerceAtLeast(0)
        val seconds = diff / 1000
        return when {
            seconds < 15 -> "Just now"
            seconds < 60 -> "${seconds}s ago"
            seconds < 3600 -> "${seconds / 60}m ago"
            else -> "${seconds / 3600}h ago"
        }
    }
}
