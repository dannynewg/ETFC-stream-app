package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.models.ContentTier
import com.example.data.models.Fighter
import com.example.data.models.HeroSlide
import com.example.data.models.HeroSlideType
import com.example.data.models.NewsArticle
import com.example.data.models.NewsCategory
import com.example.data.models.PreviousFightVideo
import com.example.data.models.UserRole
import com.example.data.models.VideoCategory
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
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import com.example.ui.viewmodel.EtfcViewModel

@Composable
fun AdminPortalDialog(
    viewModel: EtfcViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val dynamicSlides by viewModel.dynamicHeroSlides.collectAsState()
    val dynamicVideos by viewModel.dynamicPreviousVideos.collectAsState()
    val dynamicNews by viewModel.dynamicNewsArticles.collectAsState()
    val dynamicFighters by viewModel.dynamicFighters.collectAsState()

    var selectedTab by remember { mutableStateOf(uiState.adminSelectedTab) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EtfcObsidian)
                .testTag("admin_portal_dialog"),
            color = EtfcObsidian
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Surface(
                    color = EtfcSurface,
                    border = BorderStroke(1.dp, EtfcBorderGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = EtfcGold.copy(alpha = 0.18f),
                                border = BorderStroke(1.dp, EtfcGold)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(20.dp)
                                )
                            }
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ETFC ADMIN & CMS PORTAL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = EtfcGold,
                                        letterSpacing = 0.5.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EtfcLiveRed.copy(alpha = 0.2f),
                                        border = BorderStroke(0.5.dp, EtfcLiveRed)
                                    ) {
                                        Text(
                                            text = "LIVE SYNC",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = EtfcLiveRed,
                                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Manage Slider Posters, YouTube Videos, Fighter Stats & GitHub Repo Sync",
                                    fontSize = 10.5.sp,
                                    color = EtfcTextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.testTag("admin_close_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = EtfcTextPrimary
                            )
                        }
                    }
                }

                // Tab Selector
                val tabs = listOf(
                    "🖼️ Posters Slider",
                    "🎬 Fight Videos",
                    "🥊 Fighter Stats",
                    "📢 Alerts & News",
                    "🐙 GitHub & SQLite Sync"
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = EtfcSurfaceVariant,
                    contentColor = EtfcGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = EtfcGold,
                            height = 3.dp
                        )
                    },
                    divider = { HorizontalDivider(color = EtfcBorderSubtle) },
                    edgePadding = 12.dp
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = {
                                selectedTab = index
                                viewModel.setAdminTab(index)
                            },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Medium,
                                    color = if (selectedTab == index) EtfcGold else EtfcTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_$index")
                        )
                    }
                }

                // Tab Content
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    when (selectedTab) {
                        0 -> AdminPostersSliderTab(
                            slides = dynamicSlides,
                            onSaveSlide = { slide, isPinned -> viewModel.postMatchPromoPoster(slide, isPinned) },
                            onDeleteSlide = { viewModel.deleteMatchPromoPoster(it) }
                        )
                        1 -> AdminVideosArchiveTab(
                            videos = dynamicVideos,
                            onSaveVideo = { viewModel.addPreviousFightVideo(it) },
                            onDeleteVideo = { viewModel.deletePreviousFightVideo(it) }
                        )
                        2 -> AdminFighterStatsTab(
                            fighters = dynamicFighters,
                            onUpdateFighter = { id, name, nick, country, flag, bio, stance, move, reach, height, photo ->
                                viewModel.updateFighterProfile(id, name, nick, country, flag, bio, stance, move, reach, height, photo)
                            }
                        )
                        3 -> AdminAlertsAndNewsTab(
                            newsArticles = dynamicNews,
                            onSendNotification = { title, msg, type ->
                                when (type) {
                                    "LIVE" -> viewModel.triggerLiveEventNotification()
                                    "BOUT" -> viewModel.bouts.firstOrNull()?.let { viewModel.triggerBoutStartNotification(it) }
                                    "KO" -> viewModel.triggerKoAlertNotification()
                                    else -> viewModel.triggerPlatformUpdateNotification(title, msg)
                                }
                            },
                            onSaveNews = { viewModel.postNewsAnnouncement(it) },
                            onDeleteNews = { viewModel.deleteNewsAnnouncement(it) }
                        )
                        4 -> AdminGitHubSyncTab(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// TAB 1: POSTERS & HERO SLIDER MANAGER
// =============================================================================

@Composable
fun AdminPostersSliderTab(
    slides: List<HeroSlide>,
    onSaveSlide: (HeroSlide, Boolean) -> Unit,
    onDeleteSlide: (String) -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var redFighter by remember { mutableStateOf("") }
    var blueFighter by remember { mutableStateOf("") }
    var posterUrl by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("MAIN EVENT") }
    var ctaText by remember { mutableStateOf("WATCH LIVE") }
    var isPinned by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HOME HERO SLIDER POSTERS",
                        color = EtfcGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Active Posters shown on Home top carousel (${slides.size} slides)",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        isAddingNew = !isAddingNew
                        if (isAddingNew) {
                            title = "ETFC 003: THE DESTINY"
                            subtitle = "World Bantamweight Championship Unification"
                            redFighter = "Sedo \"The Addis Viper\""
                            blueFighter = "Jhonny \"El Fuego\""
                            posterUrl = "https://images.unsplash.com/photo-1549719386-74dfcbf7dbed?w=900&auto=format&fit=crop&q=80"
                            tag = "SUPER FIGHT"
                            ctaText = "PRE-ORDER PPV"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAddingNew) EtfcRedPrimary else EtfcGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_poster_btn")
                ) {
                    Icon(
                        imageVector = if (isAddingNew) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAddingNew) "CANCEL" else "NEW POSTER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Add / Edit Form Card
        if (isAddingNew) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "PUBLISH NEW HERO POSTER / PROMO",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Match / Event Title", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Subtitle / Catchphrase", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = redFighter,
                                onValueChange = { redFighter = it },
                                label = { Text("Red Fighter", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcRedPrimary,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = blueFighter,
                                onValueChange = { blueFighter = it },
                                label = { Text("Blue Fighter", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcBlueCorner,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = posterUrl,
                            onValueChange = { posterUrl = it },
                            label = { Text("Background Poster Image URL (HTTPS)", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Live Poster Preview
                        if (posterUrl.isNotBlank()) {
                            Text("Image Preview:", color = EtfcTextMuted, fontSize = 10.sp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = posterUrl,
                                    contentDescription = "Poster Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = Color.Black.copy(alpha = 0.65f),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.BottomStart)
                                ) {
                                    Text(
                                        text = "$redFighter VS $blueFighter",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { tag = it },
                                label = { Text("Header Tag", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = ctaText,
                                onValueChange = { ctaText = it },
                                label = { Text("Button Text", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                if (title.isNotBlank()) {
                                    val newSlide = HeroSlide(
                                        id = "custom_${System.currentTimeMillis()}",
                                        type = HeroSlideType.MATCH_PROMO_POSTER,
                                        tag = tag,
                                        title = title,
                                        subtitle = subtitle,
                                        eventDateOrCountdown = "LIVE NOW",
                                        venueOrLocation = "Addis Ababa Arena",
                                        primaryCtaText = ctaText,
                                        redFighterName = redFighter.ifBlank { null },
                                        blueFighterName = blueFighter.ifBlank { null },
                                        posterImageUrl = posterUrl.ifBlank { null },
                                        targetTab = "LIVE_ARENA"
                                    )
                                    onSaveSlide(newSlide, isPinned)
                                    isAddingNew = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("admin_save_slide_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE & PUBLISH TO LIVE SLIDER", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // List of Active Slides
        items(slides) { slide ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Slide Thumbnail / Poster
                    Box(
                        modifier = Modifier
                            .size(width = 80.dp, height = 55.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(EtfcSurfaceVariant)
                            .border(0.5.dp, EtfcBorderSubtle, RoundedCornerShape(6.dp))
                    ) {
                        if (!slide.posterImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = slide.posterImageUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Icon(imageVector = Icons.Default.Image, contentDescription = null, tint = EtfcTextMuted)
                            }
                        }
                    }

                    // Slide Info
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = EtfcGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = slide.tag,
                                    color = EtfcGold,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = slide.title,
                                color = EtfcTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = slide.subtitle,
                            color = EtfcTextMuted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!slide.redFighterName.isNullOrBlank()) {
                            Text(
                                text = "${slide.redFighterName} VS ${slide.blueFighterName}",
                                color = EtfcTextSecondary,
                                fontSize = 9.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    // Delete Button
                    IconButton(
                        onClick = { onDeleteSlide(slide.id) },
                        modifier = Modifier.size(32.dp).testTag("delete_slide_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EtfcRedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// TAB 2: VIDEOS & YOUTUBE ARCHIVE CMS
// =============================================================================

@Composable
fun AdminVideosArchiveTab(
    videos: List<PreviousFightVideo>,
    onSaveVideo: (PreviousFightVideo) -> Unit,
    onDeleteVideo: (String) -> Unit
) {
    var isAddingNew by remember { mutableStateOf(false) }
    var title by remember { mutableStateOf("") }
    var ytVideoId by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("ETFC 002: Battle of Adwa") }
    var redFighter by remember { mutableStateOf("") }
    var blueFighter by remember { mutableStateOf("") }
    var finishMethod by remember { mutableStateOf("KO (Overhand Right)") }
    var finishRound by remember { mutableStateOf("Round 2") }
    var duration by remember { mutableStateOf("12:40") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FIGHT VIDEO VAULT & YOUTUBE LINKS",
                        color = EtfcGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Manage match replays & highlights synced to User App (${videos.size} videos)",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        isAddingNew = !isAddingNew
                        if (isAddingNew) {
                            title = "Sedo vs Jhonny - ETFC 002 Full Fight Replay"
                            ytVideoId = "0a8G81bZ7X0"
                            redFighter = "Sedo"
                            blueFighter = "Jhonny"
                            duration = "14:22"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAddingNew) EtfcRedPrimary else EtfcGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("admin_add_video_btn")
                ) {
                    Icon(
                        imageVector = if (isAddingNew) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAddingNew) "CANCEL" else "ADD VIDEO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Add Video Form Card
        if (isAddingNew) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "ADD YOUTUBE FIGHT VIDEO TO ARCHIVE",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Video Title (e.g. Sedo vs Jhonny - ETFC 002)", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = ytVideoId,
                            onValueChange = { ytVideoId = it },
                            label = { Text("YouTube Video ID or Link (e.g. 0a8G81bZ7X0)", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        // Real-time YouTube Thumbnail Preview
                        val parsedYtId = remember(ytVideoId) {
                            if (ytVideoId.contains("v=")) {
                                ytVideoId.substringAfter("v=").substringBefore("&")
                            } else if (ytVideoId.contains("youtu.be/")) {
                                ytVideoId.substringAfter("youtu.be/").substringBefore("?")
                            } else {
                                ytVideoId.trim()
                            }
                        }

                        if (parsedYtId.isNotBlank()) {
                            val ytThumb = "https://img.youtube.com/vi/$parsedYtId/hqdefault.jpg"
                            Text("Live YouTube Thumbnail Preview:", color = EtfcTextMuted, fontSize = 10.sp)
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(140.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black)
                                    .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(8.dp))
                            ) {
                                AsyncImage(
                                    model = ytThumb,
                                    contentDescription = "YouTube Thumbnail Preview",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                                Surface(
                                    color = Color.Red,
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier
                                        .padding(8.dp)
                                        .align(Alignment.TopStart)
                                ) {
                                    Text(
                                        text = "YouTube HD",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = redFighter,
                                onValueChange = { redFighter = it },
                                label = { Text("Red Corner Fighter", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcRedPrimary,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = blueFighter,
                                onValueChange = { blueFighter = it },
                                label = { Text("Blue Corner Fighter", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcBlueCorner,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = finishMethod,
                                onValueChange = { finishMethod = it },
                                label = { Text("Result / Method", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = duration,
                                onValueChange = { duration = it },
                                label = { Text("Duration (e.g. 12:40)", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Button(
                            onClick = {
                                if (title.isNotBlank() && parsedYtId.isNotBlank()) {
                                    val newVideo = PreviousFightVideo(
                                        id = "yt_${System.currentTimeMillis()}",
                                        title = title,
                                        subtitle = "$redFighter vs $blueFighter - $finishMethod",
                                        eventName = eventName,
                                        category = VideoCategory.FULL_REPLAY,
                                        durationText = duration,
                                        durationSeconds = 720,
                                        viewsCountStr = "12.4K views",
                                        publishedTimeAgo = "Just now",
                                        fighterRedName = redFighter.ifBlank { "Sedo" },
                                        fighterBlueName = blueFighter.ifBlank { "Jhonny" },
                                        resultMethod = finishMethod,
                                        finishRoundText = finishRound,
                                        fileSizeMb = 145,
                                        contentTier = ContentTier.FREE_PRELIMS,
                                        youtubeVideoId = parsedYtId,
                                        thumbnailUrl = "https://img.youtube.com/vi/$parsedYtId/hqdefault.jpg"
                                    )
                                    onSaveVideo(newVideo)
                                    isAddingNew = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("admin_save_video_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("SAVE TO ETFC VIDEO REPOSITORY", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // List of Active Videos
        items(videos) { video ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Video Thumbnail
                    val thumbUrl = if (video.thumbnailUrl.isNotBlank()) {
                        video.thumbnailUrl
                    } else if (!video.youtubeVideoId.isNullOrBlank()) {
                        "https://img.youtube.com/vi/${video.youtubeVideoId}/hqdefault.jpg"
                    } else {
                        ""
                    }

                    Box(
                        modifier = Modifier
                            .size(width = 85.dp, height = 55.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black)
                    ) {
                        if (thumbUrl.isNotBlank()) {
                            AsyncImage(
                                model = thumbUrl,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Surface(
                            color = Color.Black.copy(alpha = 0.7f),
                            shape = RoundedCornerShape(3.dp),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(3.dp)
                        ) {
                            Text(
                                text = video.durationText,
                                color = Color.White,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                            )
                        }
                    }

                    // Video Info
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            color = EtfcTextPrimary,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${video.fighterRedName} vs ${video.fighterBlueName}",
                            color = EtfcGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Result: ${video.resultMethod} (${video.finishRoundText})",
                            color = EtfcTextMuted,
                            fontSize = 9.sp
                        )
                    }

                    // Delete
                    IconButton(
                        onClick = { onDeleteVideo(video.id) },
                        modifier = Modifier.size(32.dp).testTag("delete_video_${video.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EtfcRedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// TAB 3: FIGHTER STATS & PROFILES CMS
// =============================================================================

@Composable
fun AdminFighterStatsTab(
    fighters: List<Fighter>,
    onUpdateFighter: (
        id: String,
        name: String,
        nickname: String,
        country: String,
        flag: String,
        bio: String,
        stance: String,
        signatureMove: String,
        reachInches: Int,
        heightCm: Int,
        photoUrl: String?
    ) -> Unit
) {
    var selectedFighter by remember { mutableStateOf<Fighter?>(fighters.firstOrNull()) }

    var name by remember(selectedFighter) { mutableStateOf(selectedFighter?.name ?: "") }
    var nickname by remember(selectedFighter) { mutableStateOf(selectedFighter?.nickname ?: "") }
    var country by remember(selectedFighter) { mutableStateOf(selectedFighter?.countryName ?: "") }
    var flag by remember(selectedFighter) { mutableStateOf(selectedFighter?.flagEmoji ?: "") }
    var bio by remember(selectedFighter) { mutableStateOf(selectedFighter?.bio ?: "") }
    var stance by remember(selectedFighter) { mutableStateOf(selectedFighter?.stance ?: "Orthodox") }
    var signatureMove by remember(selectedFighter) { mutableStateOf(selectedFighter?.signatureMove ?: "") }
    var reach by remember(selectedFighter) { mutableStateOf(selectedFighter?.reachInches?.toString() ?: "72") }
    var height by remember(selectedFighter) { mutableStateOf(selectedFighter?.heightCm?.toString() ?: "178") }
    var photoUrl by remember(selectedFighter) { mutableStateOf(selectedFighter?.photoUrl ?: "") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text(
                text = "FIGHTER PROFILE, STATS & PHOTOS CMS",
                color = EtfcGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Select a fighter from the roster to update photos, records and stats in SQLite",
                color = EtfcTextMuted,
                fontSize = 11.sp
            )
        }

        // Fighter Selector Chips
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fighters.take(6).forEach { fighter ->
                    FilterChip(
                        selected = selectedFighter?.id == fighter.id,
                        onClick = { selectedFighter = fighter },
                        label = {
                            Text(
                                text = "${fighter.flagEmoji} ${fighter.name.split(" ").first()}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EtfcGold,
                            selectedLabelColor = Color.Black,
                            containerColor = EtfcCardInner,
                            labelColor = EtfcTextSecondary
                        )
                    )
                }
            }
        }

        // Edit Card
        selectedFighter?.let { fighter ->
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcBorderGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Fighter Photo Avatar Preview
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black)
                                    .border(2.dp, EtfcGold, CircleShape)
                            ) {
                                if (photoUrl.isNotBlank()) {
                                    AsyncImage(
                                        model = photoUrl,
                                        contentDescription = "Fighter Photo",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    FighterAvatarView(fighter = fighter, size = 54.dp)
                                }
                            }

                            Column {
                                Text(
                                    text = fighter.name,
                                    color = EtfcTextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "${fighter.division.label} • Record: ${fighter.wins}-${fighter.losses}-${fighter.draws} (${fighter.kos} KOs)",
                                    color = EtfcGold,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        HorizontalDivider(color = EtfcBorderSubtle)

                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text("Fighter Name", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = nickname,
                                onValueChange = { nickname = it },
                                label = { Text("Nickname", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = country,
                                onValueChange = { country = it },
                                label = { Text("Country", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = photoUrl,
                            onValueChange = { photoUrl = it },
                            label = { Text("Fighter Photo URL (HTTPS image)", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = signatureMove,
                                onValueChange = { signatureMove = it },
                                label = { Text("Signature Move", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = stance,
                                onValueChange = { stance = it },
                                label = { Text("Stance", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = height,
                                onValueChange = { height = it },
                                label = { Text("Height (cm)", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = reach,
                                onValueChange = { reach = it },
                                label = { Text("Reach (inches)", color = EtfcTextMuted) },
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = EtfcGold,
                                    unfocusedBorderColor = EtfcBorderSubtle,
                                    focusedTextColor = EtfcTextPrimary,
                                    unfocusedTextColor = EtfcTextPrimary
                                ),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Fighter History & Bio", color = EtfcTextMuted) },
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                onUpdateFighter(
                                    fighter.id,
                                    name,
                                    nickname,
                                    country,
                                    flag,
                                    bio,
                                    stance,
                                    signatureMove,
                                    reach.toIntOrNull() ?: 72,
                                    height.toIntOrNull() ?: 178,
                                    photoUrl.ifBlank { null }
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("admin_save_fighter_btn")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("UPDATE FIGHTER PROFILE IN SQLITE", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// TAB 4: ALERTS & NEWS ANNOUNCEMENTS
// =============================================================================

@Composable
fun AdminAlertsAndNewsTab(
    newsArticles: List<NewsArticle>,
    onSendNotification: (title: String, msg: String, type: String) -> Unit,
    onSaveNews: (NewsArticle) -> Unit,
    onDeleteNews: (String) -> Unit
) {
    var newsTitle by remember { mutableStateOf("") }
    var newsSummary by remember { mutableStateOf("") }
    var newsBody by remember { mutableStateOf("") }
    var newsAuthor by remember { mutableStateOf("ETFC Media Desk") }
    var isAddingNews by remember { mutableStateOf(false) }

    var customNotifTitle by remember { mutableStateOf("⚡ ETFC Flash Alert") }
    var customNotifMsg by remember { mutableStateOf("Championship Main Event walkouts starting now!") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Text(
                text = "BROADCAST PUSH NOTIFICATIONS & ANNOUNCEMENTS",
                color = EtfcGold,
                fontSize = 14.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Trigger real Android system notifications and publish home screen news articles",
                color = EtfcTextMuted,
                fontSize = 11.sp
            )
        }

        // Push Notifications Box
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null, tint = EtfcGold)
                        Text(
                            text = "INSTANT SYSTEM PUSH NOTIFICATION DISPATCHER",
                            color = EtfcGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    // Quick 1-tap alerts
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { onSendNotification("", "", "LIVE") },
                            colors = ButtonDefaults.buttonColors(containerColor = EtfcLiveRed, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🔴 LIVE START", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onSendNotification("", "", "BOUT") },
                            colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🥊 WALKOUT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { onSendNotification("", "", "KO") },
                            colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary, contentColor = Color.White),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("💥 KO ALERT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(color = EtfcBorderSubtle)

                    // Custom Notification Dispatch
                    Text("Custom Push Notification:", color = EtfcTextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = customNotifTitle,
                        onValueChange = { customNotifTitle = it },
                        label = { Text("Notification Title", color = EtfcTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorderSubtle,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = customNotifMsg,
                        onValueChange = { customNotifMsg = it },
                        label = { Text("Notification Message Content", color = EtfcTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorderSubtle,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { onSendNotification(customNotifTitle, customNotifMsg, "CUSTOM") },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(imageVector = Icons.Default.NotificationsActive, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("BROADCAST TO USER APP DEVICES", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // News Articles Section
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "OFFICIAL NEWS & PRESS RELEASES",
                    color = EtfcGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )

                Button(
                    onClick = {
                        isAddingNews = !isAddingNews
                        if (isAddingNews) {
                            newsTitle = "ETFC Announces Groundbreaking Partnership with Telebirr & Addis Arena"
                            newsSummary = "Fans can now pre-order 4K PPV passes with 1-tap mobile payment."
                            newsBody = "The Ethiopian Top Fighting Championship (ETFC) has officially expanded its streaming network across East Africa, providing ultra-low latency (<30ms) edge nodes for unmatched combat sports entertainment."
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isAddingNews) EtfcRedPrimary else EtfcGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isAddingNews) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isAddingNews) "CANCEL" else "POST ARTICLE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Add News Form
        if (isAddingNews) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "PUBLISH NEW OFFICIAL ANNOUNCEMENT",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        OutlinedTextField(
                            value = newsTitle,
                            onValueChange = { newsTitle = it },
                            label = { Text("Article Headline", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newsSummary,
                            onValueChange = { newsSummary = it },
                            label = { Text("Article Summary / Lead", color = EtfcTextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = newsBody,
                            onValueChange = { newsBody = it },
                            label = { Text("Full Article Body Content", color = EtfcTextMuted) },
                            minLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorderSubtle,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Button(
                            onClick = {
                                if (newsTitle.isNotBlank()) {
                                    val article = NewsArticle(
                                        id = "news_${System.currentTimeMillis()}",
                                        title = newsTitle,
                                        summary = newsSummary,
                                        fullContent = newsBody,
                                        category = NewsCategory.PRESS_RELEASE,
                                        author = newsAuthor,
                                        authorRole = "Staff Writer",
                                        publishedDate = "TODAY",
                                        readTimeMinutes = 3,
                                        isFeatured = true
                                    )
                                    onSaveNews(article)
                                    isAddingNews = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("PUBLISH TO USER HOME FEED", fontWeight = FontWeight.Black)
                        }
                    }
                }
            }
        }

        // Existing News Articles
        items(newsArticles) { article ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(3.dp),
                                color = EtfcGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = article.category.displayName,
                                    color = EtfcGold,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = article.title,
                                color = EtfcTextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = article.summary,
                            color = EtfcTextMuted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(
                        onClick = { onDeleteNews(article.id) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = EtfcRedPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// TAB 5: GITHUB REPO REMOTE SYNC & SQLITE BACKEND
// =============================================================================

@Composable
fun AdminGitHubSyncTab(
    viewModel: EtfcViewModel,
    uiState: com.example.ui.viewmodel.EtfcUiState
) {
    val context = LocalContext.current
    var remoteUrlInput by remember {
        mutableStateOf("https://raw.githubusercontent.com/etfc-official/etfc-content/main/etfc_content.json")
    }
    var jsonPayloadInput by remember { mutableStateOf("") }
    var copiedToClipboard by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 40.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = EtfcGold)
                Column {
                    Text(
                        text = "GITHUB REPO & SQLITE SYNC ENGINE",
                        color = EtfcGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Bi-directional sync between GitHub Repository content and on-device Room SQLite",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Status Message if syncing or finished
        if (!uiState.githubSyncStatusMessage.isNullOrBlank()) {
            item {
                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, if (uiState.githubSyncStatusMessage!!.startsWith("❌")) EtfcRedPrimary else EtfcGold),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (uiState.isSyncingGitHub) {
                            CircularProgressIndicator(color = EtfcGold, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        }
                        Text(
                            text = uiState.githubSyncStatusMessage ?: "",
                            color = if (uiState.githubSyncStatusMessage!!.startsWith("❌")) EtfcRedPrimary else EtfcGold,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // 1. Fetch & Sync from GitHub Repo URL
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null, tint = EtfcGold)
                        Text(
                            text = "1. SYNC FROM GITHUB REPO URL",
                            color = EtfcGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Fetch live raw JSON payload directly from your GitHub repository (e.g. GitHub Raw endpoint). " +
                                "All posters, YouTube videos, and fighter stats will update instantly in Room SQLite.",
                        color = EtfcTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    OutlinedTextField(
                        value = remoteUrlInput,
                        onValueChange = { remoteUrlInput = it },
                        label = { Text("GitHub Raw Content URL", color = EtfcTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorderSubtle,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.fetchAndSyncFromRemoteUrl(remoteUrlInput) },
                        enabled = !uiState.isSyncingGitHub && remoteUrlInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("admin_sync_github_url_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("FETCH & SYNC FROM GITHUB", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 2. Export Current App State as GitHub JSON
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, tint = EtfcGold)
                        Text(
                            text = "2. EXPORT CURRENT DATA AS GITHUB JSON",
                            color = EtfcGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Text(
                        text = "Generate and copy a single JSON file containing all hero posters, YouTube videos, and updated fighter stats to commit directly into your GitHub repository.",
                        color = EtfcTextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )

                    Button(
                        onClick = {
                            val json = viewModel.exportCurrentDataAsGitHubJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("ETFC GitHub Content JSON", json)
                            clipboard.setPrimaryClip(clip)
                            copiedToClipboard = true
                            jsonPayloadInput = json
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcSurfaceVariant, contentColor = EtfcGold),
                        border = BorderStroke(1.dp, EtfcBorderGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("admin_export_json_btn")
                    ) {
                        Icon(imageVector = if (copiedToClipboard) Icons.Default.Check else Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (copiedToClipboard) "COPIED TO CLIPBOARD ✅" else "EXPORT & COPY GITHUB JSON",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // 3. Direct JSON Paste & Import
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "3. DIRECT JSON IMPORT / MANUAL SYNC",
                        color = EtfcGold,
                        fontSize = 12.5.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Paste a JSON string exported from GitHub to instantly parse and write to Room database.",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )

                    OutlinedTextField(
                        value = jsonPayloadInput,
                        onValueChange = { jsonPayloadInput = it },
                        label = { Text("Paste JSON Payload here", color = EtfcTextMuted) },
                        minLines = 3,
                        maxLines = 6,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorderSubtle,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = { viewModel.syncFromGitHubJson(jsonPayloadInput) },
                        enabled = jsonPayloadInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("admin_import_json_btn")
                    ) {
                        Icon(imageVector = Icons.Default.CloudSync, contentDescription = null)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("IMPORT JSON INTO ROOM SQLITE", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        // 4. Reset to Official Defaults
        item {
            OutlinedButton(
                onClick = { viewModel.resetDynamicDataToDefaults() },
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextMuted),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("RESET TO OFFICIAL DEFAULT SEED DATA", fontSize = 11.sp)
            }
        }
    }
}
