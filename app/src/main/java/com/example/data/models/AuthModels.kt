package com.example.data.models

enum class UserRole(
    val label: String,
    val badgeTitle: String,
    val tierLevel: Int,
    val colorHex: Long,
    val badgeEmoji: String,
    val description: String
) {
    ADMIN(
        label = "ETFC Administrator",
        badgeTitle = "ADMIN",
        tierLevel = 5,
        colorHex = 0xFFFAB818,
        badgeEmoji = "👑",
        description = "Full Administrative CMS Access & Real-Time Cloud Broadcast"
    ),
    USER(
        label = "ETFC Fan Member",
        badgeTitle = "FAN",
        tierLevel = 1,
        colorHex = 0xFF00E676,
        badgeEmoji = "🥊",
        description = "Full Fan Access to All Live Streams, PPV Bouts & Arena Chat"
    ),
    GUEST(
        label = "Guest Viewer",
        badgeTitle = "GUEST",
        tierLevel = 0,
        colorHex = 0xFF9CA3AF,
        badgeEmoji = "👤",
        description = "Visitor Access"
    );

    // Compatibility properties & helper methods
    fun canAccessTier(tier: ContentTier): Boolean {
        // Users are just users and have access to all user app features!
        return this != GUEST || tier == ContentTier.FREE_PRELIMS
    }

    fun canModerate(): Boolean {
        return this == ADMIN
    }
}

enum class ContentTier(val label: String, val requiredTierLevel: Int, val description: String) {
    FREE_PRELIMS("Free Early Prelims", 0, "Early prelim bouts and live radio commentary stream"),
    STANDARD_PPV_MAIN_CARD("Standard PPV Main Card", 1, "Full main card live broadcast & 720p/1080p stream"),
    VIP_RINGSIDE_EXCLUSIVE("VIP Multi-Cam & 4K", 1, "Red/Blue corner cams, Ref Cam, exclusive audio & VIP badge"),
    OFFLINE_VAULT("Offline Match Vault", 1, "Encrypted downloadable full fight replays"),
    MODERATION_TOOLS("Arena Moderation Console", 5, "Pin messages, slow mode, user timeout and purge controls")
}

data class UserAccount(
    val userId: String,
    val email: String,
    val handle: String, // e.g. @dannynewg or @sheger_fan
    val displayName: String,
    val phoneNumber: String = "",
    val countryCode: String = "+251",
    val flagEmoji: String = "🇪🇹",
    val role: UserRole = UserRole.USER,
    val authToken: String = "",
    val createdTimestamp: Long = System.currentTimeMillis(),
    val lastLoginTimestamp: Long = System.currentTimeMillis(),
    val isBanned: Boolean = false,
    val isMutedUntilTimestamp: Long = 0L
) {
    val isAdmin: Boolean get() = role == UserRole.ADMIN || email.equals("dannynewg@gmail.com", ignoreCase = true)
}

data class AdminInvite(
    val email: String,
    val invitedBy: String = "dannynewg@gmail.com",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE"
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
