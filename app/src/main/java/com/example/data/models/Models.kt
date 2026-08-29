package com.example.data.models

enum class WeightClass(val label: String, val limitLbs: Int) {
    HEAVYWEIGHT("Heavyweight", 265),
    CRUISERWEIGHT("Cruiserweight", 205),
    MIDDLEWEIGHT("Middleweight", 185),
    WELTERWEIGHT("Welterweight", 170),
    LIGHTWEIGHT("Lightweight", 155),
    FEATHERWEIGHT("Featherweight", 145),
    BANTAMWEIGHT("Bantamweight", 135),
    FLYWEIGHT("Flyweight", 125),
    WOMENS_FLYWEIGHT("Women's Flyweight", 125)
}

enum class MatchCategory(val label: String, val shortBadge: String) {
    MMA("MMA Bout", "MMA"),
    BOXING("Boxing Match", "BOXING"),
    MUAY_THAI("Muay Thai Match", "MUAY THAI")
}

enum class FighterHairStyle {
    BALD_BEARD,      // e.g., Sedo
    CURLY_BEARD,     // e.g., Jhonny
    FADE_BEARD,      // e.g., Elezar, Edris, Desalegn
    HIGH_TOP,        // e.g., Stephen
    CURLY_SHORT,     // e.g., Esubalew, Habtamu
    SHAVED_CLEAN,    // e.g., Boyka, Surafel
    HIJAB_HOOD,      // e.g., Zahara
    BRAIDS_TIED      // e.g., Yabsira, Robel
}

enum class BoutType(val label: String, val badgeColorHex: Long) {
    MAIN_EVENT("MAIN EVENT", 0xFFFAB818),
    CO_MAIN("CO-MAIN EVENT", 0xFFFF6B7D),
    MAIN_CARD("MAIN CARD", 0xFF38BDF8),
    EARLY_PRELIMS("PRELIMS", 0xFF6B7280)
}

enum class BoutStatus {
    LIVE,
    UPCOMING,
    COMPLETED
}

data class Fighter(
    val id: String,
    val name: String,
    val nickname: String,
    val countryName: String,
    val flagEmoji: String,
    val division: WeightClass,
    val wins: Int,
    val losses: Int,
    val draws: Int,
    val kos: Int,
    val submissions: Int,
    val strikeAccuracyPercent: Int,
    val takedownDefensePercent: Int,
    val reachInches: Int,
    val heightCm: Int,
    val stance: String,
    val isChampion: Boolean,
    val rank: Int?, // null if champion
    val bio: String,
    val signatureMove: String,
    val primaryColorHex: Long,
    val hairStyle: FighterHairStyle = FighterHairStyle.FADE_BEARD,
    val discipline: MatchCategory = MatchCategory.MMA
)

data class Bout(
    val id: String,
    val eventName: String = "ETFC 002: Adwa Fight Night",
    val eventVenue: String = "Adwa 00 Museum — August 27",
    val boutType: BoutType,
    val weightClass: WeightClass,
    val matchCategory: MatchCategory = MatchCategory.MMA,
    val matchTitle: String = "MMA BOUT",
    val fighterRed: Fighter,
    val fighterBlue: Fighter,
    val isTitleFight: Boolean,
    val scheduledRounds: Int,
    val status: BoutStatus,
    val liveRound: Int = 1,
    val liveClockSeconds: Int = 300,
    val winnerId: String? = null,
    val resultMethod: String? = null,
    val redSignificantStrikes: Int = 0,
    val blueSignificantStrikes: Int = 0,
    val redTotalStrikes: Int = 0,
    val blueTotalStrikes: Int = 0,
    val redTakedowns: Int = 0,
    val blueTakedowns: Int = 0,
    val redControlTimeSec: Int = 0,
    val blueControlTimeSec: Int = 0
)

enum class StreamQuality(
    val label: String,
    val bitrateKbps: Int,
    val resolutionLabel: String,
    val dataUsagePerHourMb: Int,
    val description: String
) {
    AUTO_ABR("Auto (ABR)", 0, "Adaptive", 800, "Dynamic bandwidth ladder based on CDN network response"),
    HD_1080P("1080p 60fps", 6500, "Full HD", 2900, "Crystal clear venue camera broadcast for high-speed Wi-Fi"),
    HD_720P("720p 30fps", 3200, "HD", 1400, "Balanced high definition for stable 4G/LTE mobile"),
    SMOOTH_480P("480p", 1200, "SD Standard", 540, "Smooth playback on variable 3G/4G connections"),
    SAVER_240P("240p Data Saver", 350, "Lean Profile", 150, "Ultra-lean profile tailored for bandwidth-constrained rural networks"),
    AUDIO_ONLY("Audio Commentary", 64, "Radio Stream", 28, "Live arena commentary with 0% video data consumption")
}

enum class CameraAngle(val label: String, val shortName: String) {
    MAIN_FEED("Main Arena Broadcast", "Main Cam"),
    RED_CORNER("Red Corner Cam", "Red Cam"),
    BLUE_CORNER("Blue Corner Cam", "Blue Cam"),
    OCTAGON_OVERHEAD("Overhead Cage Cam", "Sky Cam")
}

enum class PaymentProvider(val displayName: String, val currencyCode: String, val defaultPrefix: String) {
    MPESA("Safaricom M-Pesa", "KES", "+254"),
    TELEBIRR("Telebirr Mobile", "ETB", "+251")
}

data class PpvTier(
    val id: String,
    val title: String,
    val subtitle: String,
    val priceEtb: Int,
    val priceKes: Int,
    val badge: String?,
    val isPopular: Boolean,
    val features: List<String>
)

data class FanChatMessage(
    val id: String,
    val userName: String,
    val userFlag: String,
    val message: String,
    val timeAgo: String,
    val isSuperFan: Boolean = false,
    val cheerEmoji: String? = null
)

data class CdnNodeInfo(
    val nodeId: String,
    val city: String,
    val country: String,
    val pingMs: Int,
    val edgeServer: String
)
