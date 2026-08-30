package com.example.data.remote

import com.example.data.models.ContentTier
import com.example.data.models.PreviousFightVideo
import com.example.data.models.VideoCategory
import java.util.regex.Pattern

data class YouTubeVideoMetadata(
    val videoId: String,
    val title: String,
    val authorName: String,
    val authorUrl: String,
    val thumbnailUrl: String,
    val hqThumbnailUrl: String,
    val maxResThumbnailUrl: String,
    val sdThumbnailUrl: String,
    val watchUrl: String,
    val embedUrl: String,
    val htmlEmbed: String? = null
)

object YouTubeDataMapper {

    /**
     * Extracts YouTube Video ID from various URL formats:
     * - https://www.youtube.com/watch?v=0a8G81bZ7X0
     * - https://youtu.be/0a8G81bZ7X0
     * - https://www.youtube.com/embed/0a8G81bZ7X0
     * - https://www.youtube-nocookie.com/embed/0a8G81bZ7X0
     * - or raw video ID: 0a8G81bZ7X0
     */
    fun extractVideoId(input: String?): String {
        if (input.isNullOrBlank()) return "0a8G81bZ7X0"
        val trimmed = input.trim()

        return when {
            trimmed.contains("v=") -> {
                trimmed.substringAfter("v=").substringBefore("&").substringBefore("#")
            }
            trimmed.contains("youtu.be/") -> {
                trimmed.substringAfter("youtu.be/").substringBefore("?").substringBefore("#")
            }
            trimmed.contains("/embed/") -> {
                trimmed.substringAfter("/embed/").substringBefore("?").substringBefore("#")
            }
            trimmed.contains("/v/") -> {
                trimmed.substringAfter("/v/").substringBefore("?").substringBefore("#")
            }
            trimmed.matches(Regex("^[a-zA-Z0-9_-]{11}$")) -> {
                trimmed
            }
            else -> {
                val pattern = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v%3D|watch\\?feature=player_embedded&v=|%2Fvideos%2F|embed%2F|youtu.be%2F|%2Fv%2F)[^#\\&\\?\\n]*"
                val compiledPattern = Pattern.compile(pattern)
                val matcher = compiledPattern.matcher(trimmed)
                if (matcher.find()) {
                    matcher.group()
                } else {
                    trimmed.filter { it.isLetterOrDigit() || it == '-' || it == '_' }.take(11).ifBlank { "0a8G81bZ7X0" }
                }
            }
        }
    }

    /**
     * Converts a raw video ID or URL into a standardized full YouTube watch URL for oEmbed query.
     */
    fun buildStandardWatchUrl(urlOrId: String): String {
        val id = extractVideoId(urlOrId)
        return "https://www.youtube.com/watch?v=$id"
    }

    /**
     * Maps YouTube oEmbed response into domain YouTubeVideoMetadata with full high-resolution thumbnails.
     */
    fun mapToMetadata(urlOrId: String, dto: YouTubeOEmbedDto): YouTubeVideoMetadata {
        val videoId = extractVideoId(urlOrId)
        val defaultTitle = dto.title?.takeIf { it.isNotBlank() } ?: "ETFC Championship Match Replay"
        val author = dto.authorName?.takeIf { it.isNotBlank() } ?: "Ethiopian Top Fight Championship (ETFC)"
        val authorUrl = dto.authorUrl?.takeIf { it.isNotBlank() } ?: "https://www.youtube.com/@ETFCstream"

        // High-resolution thumbnails via YouTube CDN
        val oEmbedThumb = dto.thumbnailUrl ?: "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        val hqThumb = "https://img.youtube.com/vi/$videoId/hqdefault.jpg"
        val maxResThumb = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg"
        val sdThumb = "https://img.youtube.com/vi/$videoId/sddefault.jpg"
        val watchUrl = "https://www.youtube.com/watch?v=$videoId"
        val embedUrl = "https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&enablejsapi=1"

        return YouTubeVideoMetadata(
            videoId = videoId,
            title = defaultTitle,
            authorName = author,
            authorUrl = authorUrl,
            thumbnailUrl = oEmbedThumb,
            hqThumbnailUrl = hqThumb,
            maxResThumbnailUrl = maxResThumb,
            sdThumbnailUrl = sdThumb,
            watchUrl = watchUrl,
            embedUrl = embedUrl,
            htmlEmbed = dto.html
        )
    }

    /**
     * Converts YouTube metadata to PreviousFightVideo entity / model for the app vault.
     */
    fun mapToPreviousFightVideo(
        metadata: YouTubeVideoMetadata,
        eventName: String = "ETFC 002: Battle of Adwa",
        category: VideoCategory = VideoCategory.FULL_REPLAY,
        fighterRedName: String = "Sedo \"The Addis Viper\"",
        fighterBlueName: String = "Jhonny \"El Fuego\"",
        resultMethod: String = "KO / TKO (Round 2)",
        durationText: String = "14:25",
        contentTier: ContentTier = ContentTier.FREE_PRELIMS
    ): PreviousFightVideo {
        return PreviousFightVideo(
            id = "yt_${metadata.videoId}",
            title = metadata.title,
            subtitle = "$fighterRedName vs $fighterBlueName - $resultMethod",
            eventName = eventName,
            category = category,
            durationText = durationText,
            durationSeconds = 865,
            viewsCountStr = "28.5K views",
            publishedTimeAgo = "ETFC Official Archive",
            fighterRedName = fighterRedName,
            fighterBlueName = fighterBlueName,
            resultMethod = resultMethod,
            finishRoundText = "Round 2",
            fileSizeMb = 160,
            contentTier = contentTier,
            youtubeVideoId = metadata.videoId,
            thumbnailUrl = metadata.hqThumbnailUrl,
            youtubeEmbedUrl = metadata.embedUrl
        )
    }
}
