package com.example.data.models

enum class HeroSlideType {
    MATCH_PROMO_POSTER,
    UPCOMING_MATCH,
    EARLY_BIRD_PPV,
    EVENT_PROMO,
    VIP_PASS_PROMO
}

data class HeroSlide(
    val id: String,
    val type: HeroSlideType,
    val tag: String,
    val title: String,
    val subtitle: String,
    val eventDateOrCountdown: String,
    val venueOrLocation: String,
    val primaryCtaText: String,
    val secondaryCtaText: String? = null,
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
    val posterImageUrl: String? = null
)

enum class VideoCategory(val displayName: String) {
    ALL("All Archive"),
    FULL_REPLAY("Full Replays"),
    KO_HIGHLIGHTS("KO Highlights"),
    PRESS_CONFERENCE("Press Conferences"),
    FIGHTER_BREAKDOWN("Technique & Camp")
}

data class PreviousFightVideo(
    val id: String,
    val title: String,
    val subtitle: String,
    val eventName: String,
    val category: VideoCategory,
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
    val contentTier: ContentTier = ContentTier.FREE_PRELIMS,
    val likesCount: Int = 1250,
    val videoUrl: String = "https://cdn.etfc-stream.com/vod/etfc001-highlight.mp4",
    val youtubePlaylistUrl: String = "https://youtube.com/playlist?list=PLeRATZC0Qfoo&si=dk9LKYg2Tdcg-uVm",
    val youtubePlaylistId: String = "PLeRATZC0Qfoo",
    val youtubeVideoId: String? = null,
    val thumbnailUrl: String = "https://img.youtube.com/vi/${youtubeVideoId ?: "0a8G81bZ7X0"}/hqdefault.jpg",
    val youtubeEmbedUrl: String = "https://www.youtube-nocookie.com/embed/${youtubeVideoId ?: "0a8G81bZ7X0"}?autoplay=1&enablejsapi=1"
)

enum class NewsCategory(val displayName: String, val colorHex: Long) {
    PRESS_RELEASE("OFFICIAL PRESS RELEASE", 0xFFFAB818),
    FIGHT_NEWS("FIGHT REPORT", 0xFFFF4B60),
    TICKETS_PPV("PPV & TICKETS", 0xFF10B981),
    TRAINING_CAMP("INSIDE THE CAMP", 0xFF38BDF8),
    TECH_ANNOUNCEMENT("PLATFORM UPDATE", 0xFFA855F7)
}

data class NewsArticle(
    val id: String,
    val title: String,
    val summary: String,
    val fullContent: String,
    val category: NewsCategory,
    val author: String,
    val authorRole: String,
    val publishedDate: String,
    val readTimeMinutes: Int,
    val isFeatured: Boolean = false,
    val tags: List<String> = emptyList(),
    val relatedFighterNames: List<String> = emptyList(),
    val relatedBoutId: String? = null,
    val imageUrl: String? = null
)
