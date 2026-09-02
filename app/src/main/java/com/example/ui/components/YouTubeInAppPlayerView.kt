package com.example.ui.components

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.RenderProcessGoneDetail
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.PreviousFightVideo
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderGold
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

/**
 * YouTubeInAppPlayerDialog:
 * Complete In-App YouTube player with embedded WebView HTML5 player,
 * playlist browser, match telemetry, and full controls.
 */
@Composable
fun YouTubeInAppPlayerDialog(
    video: PreviousFightVideo,
    playlistVideos: List<PreviousFightVideo>,
    onSelectVideo: (PreviousFightVideo) -> Unit,
    onDismiss: () -> Unit,
    onOpenTaleOfTheTape: (fighterRed: String, fighterBlue: String) -> Unit = { _, _ -> }
) {
    val context = LocalContext.current
    var isBookmarked by remember { mutableStateOf(false) }
    var likesCount by remember(video) { mutableStateOf(video.likesCount) }
    var hasLiked by remember(video) { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var isPlayerLoading by remember { mutableStateOf(true) }

    Dialog(
        onDismissRequest = {
            webViewRef?.destroy()
            onDismiss()
        },
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(EtfcObsidian)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("youtube_inapp_player_screen"),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Top Header Bar
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EtfcSurface)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            IconButton(
                                onClick = {
                                    webViewRef?.destroy()
                                    onDismiss()
                                },
                                modifier = Modifier.testTag("youtube_player_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = EtfcGold
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Surface(
                                        color = EtfcRedPrimary,
                                        shape = RoundedCornerShape(4.dp),
                                        modifier = Modifier.padding(end = 6.dp)
                                    ) {
                                        Text(
                                            text = "YOUTUBE ARCHIVE",
                                            color = Color.White,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "ETFC Official Playlist",
                                        color = EtfcGold,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = video.eventName,
                                    color = EtfcTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    webViewRef?.reload()
                                },
                                modifier = Modifier.testTag("youtube_player_refresh_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = "Reload Player",
                                    tint = EtfcTextSecondary
                                )
                            }
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(video.youtubePlaylistUrl))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.testTag("youtube_open_external_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.OpenInNew,
                                    contentDescription = "Open in YouTube App",
                                    tint = EtfcGold
                                )
                            }
                            IconButton(
                                onClick = {
                                    webViewRef?.destroy()
                                    onDismiss()
                                },
                                modifier = Modifier.testTag("youtube_player_close_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Close Player",
                                    tint = EtfcTextPrimary
                                )
                            }
                        }
                    }
                }

                // 2. In-App Embedded YouTube WebView Player
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .background(Color.Black)
                    ) {
                        YouTubeEmbeddedPlayer(
                            playlistId = video.youtubePlaylistId,
                            videoId = video.youtubeVideoId,
                            onWebViewCreated = { webView ->
                                webViewRef = webView
                            },
                            onPageFinished = {
                                isPlayerLoading = false
                            },
                            modifier = Modifier.fillMaxSize()
                        )

                        if (isPlayerLoading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.7f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    CircularProgressIndicator(color = EtfcGold, modifier = Modifier.size(36.dp))
                                    Text(
                                        text = "Connecting to ETFC YouTube Stream...",
                                        color = EtfcGold,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }
                }

                // 3. Match Title, Controls & Actions
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EtfcSurface)
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                if (video.isTitleFight) {
                                    Surface(
                                        color = EtfcGold.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(1.dp, EtfcGold),
                                        modifier = Modifier.padding(bottom = 6.dp)
                                    ) {
                                        Text(
                                            text = "🏆 UNDISPUTED TITLE FIGHT",
                                            color = EtfcGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = video.title,
                                    color = EtfcTextPrimary,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 22.sp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "${video.viewsCountStr} • ${video.publishedTimeAgo} • Duration: ${video.durationText}",
                                    color = EtfcTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = video.subtitle,
                            color = EtfcTextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )

                        // Action Bar: Like, Save, Share, Tale of the Tape
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedButton(
                                onClick = {
                                    if (hasLiked) {
                                        likesCount--
                                        hasLiked = false
                                    } else {
                                        likesCount++
                                        hasLiked = true
                                    }
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (hasLiked) EtfcGold.copy(alpha = 0.15f) else Color.Transparent,
                                    contentColor = if (hasLiked) EtfcGold else EtfcTextPrimary
                                ),
                                border = BorderStroke(1.dp, if (hasLiked) EtfcGold else EtfcBorder),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ThumbUp,
                                    contentDescription = "Like",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("$likesCount", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = { isBookmarked = !isBookmarked },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = if (isBookmarked) EtfcGold.copy(alpha = 0.15f) else Color.Transparent,
                                    contentColor = if (isBookmarked) EtfcGold else EtfcTextPrimary
                                ),
                                border = BorderStroke(1.dp, if (isBookmarked) EtfcGold else EtfcBorder),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBookmarked) "Saved" else "Save", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                        type = "text/plain"
                                        putExtra(Intent.EXTRA_SUBJECT, video.title)
                                        putExtra(Intent.EXTRA_TEXT, "Watch '${video.title}' on ETFC YouTube: ${video.youtubePlaylistUrl}")
                                    }
                                    context.startActivity(Intent.createChooser(shareIntent, "Share Match Replay"))
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Transparent,
                                    contentColor = EtfcTextPrimary
                                ),
                                border = BorderStroke(1.dp, EtfcBorder),
                                modifier = Modifier.weight(1f).height(38.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Share,
                                    contentDescription = "Share",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Share", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Match Telemetry & Result Banner
                        Card(
                            colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("OFFICIAL MATCH OUTCOME", color = EtfcGold, fontSize = 9.sp, fontWeight = FontWeight.Black)
                                    Text(video.resultMethod, color = EtfcTextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text(video.finishRoundText, color = EtfcTextMuted, fontSize = 11.sp)
                                }
                                Button(
                                    onClick = { onOpenTaleOfTheTape(video.fighterRedName, video.fighterBlueName) },
                                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = EtfcObsidian),
                                    shape = RoundedCornerShape(6.dp),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.SportsMma, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("TALE OF TAPE", fontSize = 10.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }
                    }
                }

                // 4. Playlist Match Selector: YouTube Playlist Queue
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SmartDisplay,
                                    contentDescription = null,
                                    tint = EtfcRedPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    text = "ETFC Match Playlist Queue",
                                    color = EtfcTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "${playlistVideos.size} Matches",
                                color = EtfcGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Tap any match below to switch videos instantly inside the app player:",
                            color = EtfcTextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // 5. Playlist Items List
                items(playlistVideos) { itemVideo ->
                    val isCurrent = itemVideo.id == video.id
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clickable {
                                if (!isCurrent) {
                                    onSelectVideo(itemVideo)
                                }
                            }
                            .testTag("playlist_item_${itemVideo.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) EtfcGold.copy(alpha = 0.12f) else EtfcSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) EtfcGold else EtfcBorderSubtle
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Thumbnail representation
                            Box(
                                modifier = Modifier
                                    .size(width = 80.dp, height = 48.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color.Black),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isCurrent) Icons.Default.SmartDisplay else Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = if (isCurrent) EtfcGold else EtfcTextSecondary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.75f),
                                    shape = RoundedCornerShape(2.dp),
                                    modifier = Modifier
                                        .align(Alignment.BottomEnd)
                                        .padding(2.dp)
                                ) {
                                    Text(
                                        text = itemVideo.durationText,
                                        color = Color.White,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 2.dp, vertical = 1.dp)
                                    )
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                if (isCurrent) {
                                    Text(
                                        text = "▶ NOW PLAYING IN-APP",
                                        color = EtfcGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                Text(
                                    text = itemVideo.title,
                                    color = if (isCurrent) EtfcGold else EtfcTextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "${itemVideo.fighterRedName} vs ${itemVideo.fighterBlueName} • ${itemVideo.resultMethod}",
                                    color = EtfcTextMuted,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Embedded HTML5 WebView component tailored for YouTube iframe playback
 */
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun YouTubeEmbeddedPlayer(
    playlistId: String,
    videoId: String? = null,
    onWebViewCreated: (WebView) -> Unit = {},
    onPageFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val embedUrl = remember(playlistId, videoId) {
        if (videoId != null) {
            "https://www.youtube-nocookie.com/embed/$videoId?autoplay=1&playsinline=1&enablejsapi=1&rel=0&modestbranding=1"
        } else {
            "https://www.youtube-nocookie.com/embed/videoseries?list=$playlistId&autoplay=1&playsinline=1&enablejsapi=1&rel=0&modestbranding=1"
        }
    }

    val htmlData = remember(embedUrl) {
        """
        <!DOCTYPE html>
        <html>
        <head>
            <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
            <style>
                * { margin: 0; padding: 0; box-sizing: border-box; }
                html, body {
                    width: 100%;
                    height: 100%;
                    background-color: #07090E;
                    overflow: hidden;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                }
                .video-container {
                    position: relative;
                    width: 100%;
                    height: 100%;
                }
                iframe {
                    width: 100%;
                    height: 100%;
                    border: 0;
                }
            </style>
        </head>
        <body>
            <div class="video-container">
                <iframe 
                    src="$embedUrl" 
                    title="ETFC Match Video"
                    allow="accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture; web-share" 
                    allowfullscreen>
                </iframe>
            </div>
        </body>
        </html>
        """.trimIndent()
    }

    var webViewInstance by remember { mutableStateOf<WebView?>(null) }
    var hasRendererCrashed by remember { mutableStateOf(false) }

    if (hasRendererCrashed) {
        val context = LocalContext.current
        Box(
            modifier = modifier
                .background(Color.Black)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "Player recovery mode active",
                    color = EtfcGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Rendering safely with stream compatibility",
                    color = EtfcTextMuted,
                    fontSize = 11.sp
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            hasRendererCrashed = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcCardInner),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderGold)
                    ) {
                        Text("Retry In-App", color = EtfcGold, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                    Button(
                        onClick = {
                            val directUrl = if (videoId != null) "https://www.youtube.com/watch?v=$videoId" else "https://www.youtube.com/playlist?list=$playlistId"
                            try {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(directUrl))
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold)
                    ) {
                        Text("Watch on YouTube", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }
    } else {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewInstance = this
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                    setBackgroundColor(0xFF07090E.toInt())
                    // Set layer type to NONE/SOFTWARE to avoid MESA GPU rendernode crashes in virtualized environments
                    try {
                        setLayerType(View.LAYER_TYPE_SOFTWARE, null)
                    } catch (_: Exception) {}
                    settings.apply {
                        javaScriptEnabled = true
                        domStorageEnabled = true
                        mediaPlaybackRequiresUserGesture = false
                        loadWithOverviewMode = true
                        useWideViewPort = true
                        allowFileAccess = false
                        cacheMode = WebSettings.LOAD_NO_CACHE
                        mixedContentMode = WebSettings.MIXED_CONTENT_NEVER_ALLOW
                    }
                    webChromeClient = WebChromeClient()
                    webViewClient = object : WebViewClient() {
                        override fun onPageFinished(view: WebView?, url: String?) {
                            super.onPageFinished(view, url)
                            onPageFinished()
                        }

                        override fun onRenderProcessGone(view: WebView?, detail: RenderProcessGoneDetail?): Boolean {
                            Log.w("YouTubePlayer", "Renderer process gone detected, recovering safely: didCrash=${detail?.didCrash()}")
                            hasRendererCrashed = true
                            try {
                                view?.destroy()
                            } catch (_: Exception) {}
                            return true
                        }

                        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                            val requestUrl = request?.url?.toString() ?: return false
                            return if (requestUrl.contains("youtube.com") || requestUrl.contains("youtu.be") || requestUrl.contains("youtube-nocookie.com")) {
                                false // Load within webview
                            } else {
                                try {
                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(requestUrl))
                                    ctx.startActivity(intent)
                                } catch (_: Exception) {}
                                true
                            }
                        }
                    }
                    tag = embedUrl
                    loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlData, "text/html", "UTF-8", null)
                    onWebViewCreated(this)
                }
            },
            update = { webView ->
                if (webView.tag != embedUrl) {
                    webView.tag = embedUrl
                    webView.loadDataWithBaseURL("https://www.youtube-nocookie.com", htmlData, "text/html", "UTF-8", null)
                }
            },
            modifier = modifier
        )
    }

    DisposableEffect(embedUrl) {
        onDispose {
            try {
                webViewInstance?.stopLoading()
                webViewInstance?.loadUrl("about:blank")
                webViewInstance?.destroy()
                webViewInstance = null
            } catch (_: Exception) {}
        }
    }
}
