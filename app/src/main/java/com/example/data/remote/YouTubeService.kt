package com.example.data.remote

import android.util.Log
import com.example.data.models.ContentTier
import com.example.data.models.PreviousFightVideo
import com.example.data.models.VideoCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * High-level service interfacing with YouTube's official oEmbed API and thumbnail engine.
 * Provides Retrofit-based metadata extraction and standardizes information for UI components.
 */
class YouTubeService(
    private val api: YouTubeOEmbedService = NetworkClient.youtubeOEmbedService
) {

    private val tag = "YouTubeService"

    /**
     * Extracts YouTube Video ID and queries YouTube oEmbed API to fetch video title,
     * author, html iframe code, and high-resolution thumbnail images.
     */
    suspend fun fetchVideoMetadata(urlOrId: String): Result<YouTubeVideoMetadata> = withContext(Dispatchers.IO) {
        try {
            val videoId = YouTubeDataMapper.extractVideoId(urlOrId)
            val watchUrl = YouTubeDataMapper.buildStandardWatchUrl(videoId)

            Log.d(tag, "Fetching oEmbed metadata for URL: $watchUrl")
            val dto = api.getOEmbed(url = watchUrl, format = "json")
            val metadata = YouTubeDataMapper.mapToMetadata(videoId, dto)

            Log.d(tag, "Successfully mapped YouTube metadata: ${metadata.title}")
            Result.success(metadata)
        } catch (e: Exception) {
            Log.w(tag, "Failed to fetch oEmbed from YouTube API, falling back to CDN thumbnails: ${e.message}")
            // Fallback gracefully to direct YouTube CDN metadata
            val videoId = YouTubeDataMapper.extractVideoId(urlOrId)
            val fallback = YouTubeVideoMetadata(
                videoId = videoId,
                title = "ETFC Match Highlight ($videoId)",
                authorName = "Ethiopian Top Fight Championship (ETFC)",
                authorUrl = "https://www.youtube.com/@ETFCstream",
                thumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                hqThumbnailUrl = "https://img.youtube.com/vi/$videoId/hqdefault.jpg",
                maxResThumbnailUrl = "https://img.youtube.com/vi/$videoId/maxresdefault.jpg",
                sdThumbnailUrl = "https://img.youtube.com/vi/$videoId/sddefault.jpg",
                watchUrl = "https://www.youtube.com/watch?v=$videoId",
                embedUrl = "https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&enablejsapi=1"
            )
            Result.success(fallback)
        }
    }

    /**
     * Standardizes YouTube URL into a domain PreviousFightVideo ready for UI consumption.
     */
    suspend fun fetchAndMapToPreviousFightVideo(
        urlOrId: String,
        eventName: String = "ETFC 002: Battle of Adwa",
        category: VideoCategory = VideoCategory.FULL_REPLAY,
        fighterRedName: String = "Sedo \"The Addis Viper\"",
        fighterBlueName: String = "Jhonny \"El Fuego\"",
        resultMethod: String = "KO (Overhand Right)",
        durationText: String = "12:40",
        contentTier: ContentTier = ContentTier.FREE_PRELIMS
    ): PreviousFightVideo {
        val metaResult = fetchVideoMetadata(urlOrId)
        val metadata = metaResult.getOrNull() ?: YouTubeVideoMetadata(
            videoId = YouTubeDataMapper.extractVideoId(urlOrId),
            title = "ETFC Championship Match Replay",
            authorName = "ETFC Official",
            authorUrl = "https://www.youtube.com/@ETFCstream",
            thumbnailUrl = "https://img.youtube.com/vi/${YouTubeDataMapper.extractVideoId(urlOrId)}/hqdefault.jpg",
            hqThumbnailUrl = "https://img.youtube.com/vi/${YouTubeDataMapper.extractVideoId(urlOrId)}/hqdefault.jpg",
            maxResThumbnailUrl = "https://img.youtube.com/vi/${YouTubeDataMapper.extractVideoId(urlOrId)}/maxresdefault.jpg",
            sdThumbnailUrl = "https://img.youtube.com/vi/${YouTubeDataMapper.extractVideoId(urlOrId)}/sddefault.jpg",
            watchUrl = "https://www.youtube.com/watch?v=${YouTubeDataMapper.extractVideoId(urlOrId)}",
            embedUrl = "https://www.youtube-nocookie.com/embed/${YouTubeDataMapper.extractVideoId(urlOrId)}?autoplay=1&enablejsapi=1"
        )

        return YouTubeDataMapper.mapToPreviousFightVideo(
            metadata = metadata,
            eventName = eventName,
            category = category,
            fighterRedName = fighterRedName,
            fighterBlueName = fighterBlueName,
            resultMethod = resultMethod,
            durationText = durationText,
            contentTier = contentTier
        )
    }
}
