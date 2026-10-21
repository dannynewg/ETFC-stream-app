package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.example.data.models.Bout
import com.example.data.models.ContentTier
import com.example.data.models.HeroSlide
import com.example.data.models.HeroSlideType
import com.example.data.models.NewsArticle
import com.example.data.models.PreviousFightVideo
import com.example.data.models.UserRole
import com.example.data.models.VideoCategory
import com.example.ui.components.DominantLiveEventTopSection
import com.example.ui.components.Etfc003LiveCountdownStripBanner
import com.example.ui.components.HeroSection
import com.example.ui.components.YouTubeInAppPlayerDialog
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderGold
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcGoldGlow
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSuccessBg
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import com.example.ui.viewmodel.AppTab
import com.example.ui.viewmodel.EtfcViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    viewModel: EtfcViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser = uiState.currentUser
    val userRole = currentUser?.role ?: UserRole.GUEST
    val activeBout = uiState.selectedBout ?: viewModel.bouts.firstOrNull()

    val heroSlides by viewModel.dynamicHeroSlides.collectAsState()
    val dynamicMatches by viewModel.dynamicMatches.collectAsState()
    val previousVideos by viewModel.dynamicPreviousVideos.collectAsState()
    val newsArticles by viewModel.dynamicNewsArticles.collectAsState()

    val filteredVideos = remember(uiState.selectedVideoCategory, previousVideos) {
        if (uiState.selectedVideoCategory == VideoCategory.ALL) {
            previousVideos
        } else {
            previousVideos.filter { it.category == uiState.selectedVideoCategory }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .testTag("home_screen_feed"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // =========================================================================
        // 1. ETFC 003 LIVE COUNTDOWN & 600 ETB PPV ACCESS STRIP BANNER
        // (Renders only when active events exist in Firestore matches collection)
        // =========================================================================
        if (dynamicMatches.isNotEmpty() || viewModel.bouts.isNotEmpty()) {
            val primaryMatch = dynamicMatches.firstOrNull() ?: viewModel.bouts.firstOrNull()
            item(key = "etfc003_strip_banner") {
                Etfc003LiveCountdownStripBanner(
                    isPassUnlocked = userRole.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD),
                    eventName = primaryMatch?.eventName ?: "ETFC 003: Champions Collide",
                    venue = primaryMatch?.eventVenue ?: "Adwa Museum Arena, Addis Ababa",
                    onClick = {
                        if (userRole.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD)) {
                            viewModel.selectTab(AppTab.LIVE_ARENA)
                        } else {
                            val singleTier = viewModel.ppvTiers.firstOrNull()
                            viewModel.openCheckoutDialog(singleTier)
                        }
                    }
                )
            }
        }

        // =========================================================================
        // 2. SLIDING HERO SECTION (Upcoming Match Cards, Early Bird PPV CTA, Promos)
        // =========================================================================
        item(key = "sliding_hero_section") {
            HeroSection(
                slides = heroSlides,
                onCtaClick = { slide -> viewModel.navigateToHeroSlideAction(slide) },
                onSecondaryCtaClick = { slide ->
                    if (slide.type == HeroSlideType.UPCOMING_MATCH) {
                        viewModel.selectTab(AppTab.FIGHT_CARD)
                    } else if (slide.type == HeroSlideType.EARLY_BIRD_PPV) {
                        val singleTier = viewModel.ppvTiers.firstOrNull()
                        viewModel.openCheckoutDialog(singleTier)
                    } else {
                        viewModel.navigateToHeroSlideAction(slide)
                    }
                }
            )
        }

        // =========================================================================
        // 3. QUICK ACTION DISCOVERY HUB
        // =========================================================================
        item(key = "quick_action_hub") {
            HomeQuickActionHub(
                onLiveClick = {
                    if (userRole.canAccessTier(ContentTier.STANDARD_PPV_MAIN_CARD)) {
                        viewModel.selectTab(AppTab.LIVE_ARENA)
                    } else {
                        val singleTier = viewModel.ppvTiers.firstOrNull()
                        viewModel.openCheckoutDialog(singleTier)
                    }
                },
                onPpvClick = {
                    val singleTier = viewModel.ppvTiers.firstOrNull()
                    viewModel.openCheckoutDialog(singleTier)
                },
                onFightCardClick = { viewModel.selectTab(AppTab.FIGHT_CARD) },
                onVipClick = {
                    val singleTier = viewModel.ppvTiers.firstOrNull()
                    viewModel.openCheckoutDialog(singleTier)
                },
                onSettingsClick = { viewModel.selectTab(AppTab.SETTINGS) },
                onFightersClick = { viewModel.selectTab(AppTab.FIGHTERS) },
                onRegistryClick = { viewModel.openAthleteRegistry(0) }
            )
        }

        // =========================================================================
        // 3.5. ETHIOPIAN COMBAT ATHLETE REGISTRY PROMO BANNER
        // =========================================================================
        item(key = "athlete_registry_promo_banner") {
            com.example.ui.components.AthleteRegistryPromoBanner(
                onRegisterClick = { viewModel.openAthleteRegistry(0) },
                onViewRosterClick = { viewModel.openAthleteRegistry(1) }
            )
        }

        // =========================================================================
        // 4. PREVIOUS FIGHT VIDEOS, HIGHLIGHTS & ARCHIVE SECTION
        // =========================================================================
        item(key = "previous_videos_header") {
            SectionHeader(
                title = "FIGHT ARCHIVE & HIGHLIGHTS",
                subtitle = "Official YouTube playlist replays, knockouts & camp features",
                badge = "${previousVideos.size} Videos",
                badgeColor = EtfcGold
            )
        }

        item(key = "video_category_filter_chips") {
            VideoCategoryFilterRow(
                selectedCategory = uiState.selectedVideoCategory,
                onCategorySelected = { viewModel.selectVideoCategory(it) }
            )
        }

        // Video list items
        items(
            items = filteredVideos,
            key = { it.id }
        ) { video ->
            PreviousVideoListItem(
                video = video,
                onWatch = { viewModel.openYouTubePlayer(video) },
                onDownload = { viewModel.downloadPreviousVideo(video) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        // =========================================================================
        // 5. PROMOS, NEWS BLOGS & OFFICIAL ANNOUNCEMENTS SECTION
        // =========================================================================
        item(key = "news_section_header") {
            Spacer(modifier = Modifier.height(16.dp))
            SectionHeader(
                title = "ETFC NEWS WIRE & PRESS RELEASES",
                subtitle = "Official fight announcements, athlete features & broadcast updates",
                badge = "UPDATED",
                badgeColor = EtfcRedPrimary
            )
        }

        // Featured Headline Article Card
        val featuredArticle = newsArticles.find { it.isFeatured } ?: newsArticles.firstOrNull()
        if (featuredArticle != null) {
            item(key = "featured_news_article") {
                FeaturedNewsArticleCard(
                    article = featuredArticle,
                    onClick = { viewModel.openNewsArticle(featuredArticle) },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }

        // Secondary News Articles List
        val regularArticles = newsArticles.filter { it.id != featuredArticle?.id }
        items(
            items = regularArticles,
            key = { it.id }
        ) { article ->
            NewsArticleListItem(
                article = article,
                onClick = { viewModel.openNewsArticle(article) },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 5.dp)
            )
        }

        // =========================================================================
        // 6. SPONSORSHIP & MOBILE MONEY PARTNERSHIP BANNER
        // =========================================================================
        item(key = "mobile_money_trust_banner") {
            Spacer(modifier = Modifier.height(12.dp))
            MobileMoneyPartnershipBanner(
                onPpvExplore = {
                    val singleTier = viewModel.ppvTiers.firstOrNull()
                    viewModel.openCheckoutDialog(singleTier)
                },
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }

    // =========================================================================
    // DIALOGS: VOD VIDEO PLAYER MODAL & NEWS ARTICLE READER
    // =========================================================================
    if (uiState.activePlayingVod != null) {
        VideoPlayerModalDialog(
            video = uiState.activePlayingVod!!,
            onDismiss = { viewModel.playPreviousVideo(null) },
            onDownload = {
                viewModel.downloadPreviousVideo(uiState.activePlayingVod!!)
            }
        )
    }

    if (uiState.selectedNewsArticle != null) {
        NewsArticleReaderDialog(
            article = uiState.selectedNewsArticle!!,
            onDismiss = { viewModel.openNewsArticle(null) },
            onExploreBout = {
                viewModel.openNewsArticle(null)
                viewModel.selectTab(AppTab.FIGHT_CARD)
            }
        )
    }
}

// =============================================================================
// 3. QUICK ACTION DISCOVERY HUB
// =============================================================================

@Composable
fun HomeQuickActionHub(
    onLiveClick: () -> Unit,
    onPpvClick: () -> Unit,
    onFightCardClick: () -> Unit,
    onVipClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onFightersClick: () -> Unit,
    onRegistryClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            item {
                QuickActionPill(
                    icon = Icons.Default.HowToReg,
                    label = "Fighter Registry",
                    badge = "OPEN",
                    badgeColor = EtfcGold,
                    onClick = onRegistryClick,
                    accentColor = EtfcGold
                )
            }
            item {
                QuickActionPill(
                    icon = Icons.Default.LocalActivity,
                    label = "ETFC 003 Pass",
                    badge = "600 ETB",
                    badgeColor = EtfcGold,
                    onClick = onPpvClick,
                    accentColor = EtfcGold
                )
            }
            item {
                QuickActionPill(
                    icon = Icons.Default.SportsMma,
                    label = "Fight Card",
                    badge = "OCTAGON",
                    badgeColor = EtfcRedPrimary,
                    onClick = onFightCardClick,
                    accentColor = EtfcTextPrimary
                )
            }
            item {
                QuickActionPill(
                    icon = Icons.Default.TrendingUp,
                    label = "Fighters Roster",
                    badge = "TOP 10",
                    badgeColor = EtfcSuccess,
                    onClick = onFightersClick,
                    accentColor = EtfcGold
                )
            }
            item {
                QuickActionPill(
                    icon = Icons.Default.Settings,
                    label = "Settings & Profile",
                    badge = null,
                    badgeColor = null,
                    onClick = onSettingsClick,
                    accentColor = EtfcTextSecondary
                )
            }
        }
    }
}

@Composable
fun QuickActionPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    badge: String?,
    badgeColor: Color?,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = EtfcCardInner,
        border = BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = Modifier
            .height(42.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = accentColor,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextPrimary
            )
            if (badge != null && badgeColor != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }
            }
        }
    }
}

// =============================================================================
// 4. PREVIOUS FIGHT VIDEOS SECTION & FILTER
// =============================================================================

@Composable
fun VideoCategoryFilterRow(
    selectedCategory: VideoCategory,
    onCategorySelected: (VideoCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(VideoCategory.entries) { category ->
            val isSelected = selectedCategory == category
            FilterChip(
                selected = isSelected,
                onClick = { onCategorySelected(category) },
                label = {
                    Text(
                        text = category.displayName,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = EtfcGold,
                    selectedLabelColor = Color.Black,
                    containerColor = EtfcCardInner,
                    labelColor = EtfcTextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = isSelected,
                    borderColor = if (isSelected) EtfcGold else EtfcBorder
                )
            )
        }
    }
}

@Composable
fun PreviousVideoListItem(
    video: PreviousFightVideo,
    onWatch: () -> Unit,
    onDownload: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onWatch() }
            .testTag("vod_card_${video.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Video Thumbnail with Duration Badge & Play Overlay
            Box(
                modifier = Modifier
                    .width(128.dp)
                    .height(82.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14141E))
                    .border(0.5.dp, EtfcBorder, RoundedCornerShape(8.dp))
            ) {
                val thumb = video.thumbnailUrl.ifBlank {
                    if (!video.youtubeVideoId.isNullOrBlank()) "https://img.youtube.com/vi/${video.youtubeVideoId}/hqdefault.jpg" else ""
                }
                if (thumb.isNotBlank()) {
                    val resolvedThumb = com.example.ui.util.ImageResolver.resolve(thumb)
                    AsyncImage(
                        model = resolvedThumb,
                        contentDescription = video.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Semi-transparent gradient scrim overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    Color.Black.copy(alpha = 0.65f)
                                )
                            )
                        )
                )

                // YouTube Red Accent / Category Icon on Top-Start
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Red.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(4.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(10.dp)
                        )
                        Text(
                            text = "YT",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                // Play Icon Center
                Surface(
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.65f),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.8f)),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = EtfcGold,
                        modifier = Modifier
                            .size(28.dp)
                            .padding(4.dp)
                    )
                }

                // Duration Badge (Bottom Right)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color.Black.copy(alpha = 0.85f),
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp)
                ) {
                    Text(
                        text = video.durationText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                    )
                }

                // HD/4K Badge (Bottom Left)
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = EtfcCardInner.copy(alpha = 0.9f),
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(4.dp)
                ) {
                    Text(
                        text = video.resolutionLabel,
                        fontSize = 7.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold,
                        modifier = Modifier.padding(horizontal = 3.dp, vertical = 1.dp)
                    )
                }
            }

            // Video Meta & Description
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EtfcGoldGlow
                    ) {
                        Text(
                            text = video.eventName,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }

                    Text(
                        text = video.publishedTimeAgo,
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = video.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "${video.resultMethod} • ${video.finishRoundText}",
                    fontSize = 11.sp,
                    color = EtfcTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Stats & Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${video.viewsCountStr} • ${video.fileSizeMb} MB",
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )

                    IconButton(
                        onClick = onDownload,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download Video",
                            tint = EtfcTextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}

// =============================================================================
// 5. PROMOS, NEWS BLOGS & ANNOUNCEMENTS SECTION
// =============================================================================

@Composable
fun FeaturedNewsArticleCard(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcBorderGold),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("featured_news_card_${article.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Category & Read Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(article.category.colorHex).copy(alpha = 0.2f),
                    border = BorderStroke(1.dp, Color(article.category.colorHex))
                ) {
                    Text(
                        text = article.category.displayName,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(article.category.colorHex),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = EtfcTextMuted,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "${article.readTimeMinutes} min read",
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Headline
            Text(
                text = article.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EtfcTextPrimary,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Summary
            Text(
                text = article.summary,
                fontSize = 12.sp,
                color = EtfcTextSecondary,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Author & Date + CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = article.author,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold
                    )
                    Text(
                        text = article.publishedDate,
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "READ FULL REPORT",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcGold
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = EtfcGold,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsArticleListItem(
    article: NewsArticle,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
        border = BorderStroke(1.dp, EtfcBorder),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("news_card_${article.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Category Icon Badge
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color(article.category.colorHex).copy(alpha = 0.15f),
                border = BorderStroke(1.dp, Color(article.category.colorHex).copy(alpha = 0.5f)),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = null,
                        tint = Color(article.category.colorHex),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // News Text Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = article.category.displayName,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(article.category.colorHex)
                    )
                    Text(
                        text = "•",
                        fontSize = 9.sp,
                        color = EtfcTextMuted
                    )
                    Text(
                        text = article.publishedDate,
                        fontSize = 10.sp,
                        color = EtfcTextMuted
                    )
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = article.title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcTextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = EtfcTextMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// =============================================================================
// 6. SPONSORSHIP & MOBILE MONEY PARTNERSHIP BANNER
// =============================================================================

@Composable
fun MobileMoneyPartnershipBanner(
    onPpvExplore: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcSurfaceVariant),
        border = BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "SEAMLESS EAST AFRICA MICRO-BILLING",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "1-Tap Checkout via Safaricom M-Pesa & Telebirr Mobile",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcTextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Instant Edge CDN unlock without credit card fees",
                    fontSize = 10.sp,
                    color = EtfcTextSecondary
                )
            }

            Button(
                onClick = onPpvExplore,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = EtfcCardInner),
                border = BorderStroke(1.dp, EtfcBorderGold),
                modifier = Modifier.height(36.dp)
            ) {
                Text(
                    text = "VIEW TIERS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcGold
                )
            }
        }
    }
}

// =============================================================================
// SECTION HEADER HELPER
// =============================================================================

@Composable
fun SectionHeader(
    title: String,
    subtitle: String,
    badge: String? = null,
    badgeColor: Color = EtfcGold,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = EtfcGold,
                letterSpacing = 1.sp
            )

            if (badge != null) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.2f),
                    border = BorderStroke(0.5.dp, badgeColor)
                ) {
                    Text(
                        text = badge,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        color = badgeColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }

        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = EtfcTextMuted,
            modifier = Modifier.padding(top = 1.dp)
        )
    }
}

// =============================================================================
// MODALS: VOD VIDEO PLAYER DIALOG & ARTICLE READER DIALOG
// =============================================================================

@Composable
fun VideoPlayerModalDialog(
    video: PreviousFightVideo,
    onDismiss: () -> Unit,
    onDownload: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(true) }
    var currentProgress by remember { mutableFloatStateOf(0.35f) }
    var playbackSpeed by remember { mutableFloatStateOf(1.0f) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EtfcObsidian),
            color = EtfcObsidian
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header Close Button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcGoldGlow
                        ) {
                            Text(
                                text = "ARCHIVE VOD PLAYER",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcGold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Realistic Video Player Surface
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF070709))
                        .border(1.dp, EtfcBorderGold, RoundedCornerShape(12.dp))
                ) {
                    // Matchup Canvas
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${video.fighterRedName} vs ${video.fighterBlueName}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = video.resultMethod,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${video.finishRoundText} • ${video.eventName}",
                            fontSize = 10.sp,
                            color = EtfcTextSecondary
                        )
                    }

                    // Bottom Playback Scrub Bar & Controls
                    Column(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.9f)
                                    )
                                )
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Slider(
                            value = currentProgress,
                            onValueChange = { currentProgress = it },
                            colors = SliderDefaults.colors(
                                thumbColor = EtfcGold,
                                activeTrackColor = EtfcGold,
                                inactiveTrackColor = Color.DarkGray
                            ),
                            modifier = Modifier.height(24.dp)
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { isPlaying = !isPlaying },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = if (isPlaying) "Pause" else "Play",
                                        tint = EtfcGold
                                    )
                                }

                                Text(
                                    text = "${(video.durationSeconds * currentProgress).toInt() / 60}:${((video.durationSeconds * currentProgress).toInt() % 60).toString().padStart(2, '0')} / ${video.durationText}",
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 6.dp)
                                )
                            }

                            // Speed Toggles
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(0.5f, 1.0f, 1.5f).forEach { speed ->
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = if (playbackSpeed == speed) EtfcGold else EtfcCardInner,
                                        modifier = Modifier.clickable { playbackSpeed = speed }
                                    ) {
                                        Text(
                                            text = if (speed == 0.5f) "0.5x Slow-Mo" else "${speed}x",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (playbackSpeed == speed) Color.Black else EtfcTextSecondary,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title & Metadata
                Text(
                    text = video.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = EtfcTextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = video.subtitle,
                    fontSize = 13.sp,
                    color = EtfcTextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons (Download, Share)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = onDownload,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcCardInner,
                            contentColor = EtfcGold
                        ),
                        border = BorderStroke(1.dp, EtfcBorderGold),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "SAVE OFFLINE (${video.fileSizeMb} MB)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    OutlinedButton(
                        onClick = onDismiss,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, EtfcBorderSubtle),
                        modifier = Modifier.height(44.dp)
                    ) {
                        Text(
                            text = "CLOSE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextSecondary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun NewsArticleReaderDialog(
    article: NewsArticle,
    onDismiss: () -> Unit,
    onExploreBout: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .background(EtfcObsidian),
            color = EtfcObsidian
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(article.category.colorHex).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(article.category.colorHex))
                    ) {
                        Text(
                            text = article.category.displayName,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(article.category.colorHex),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Article Headline
                Text(
                    text = article.title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcTextPrimary,
                    lineHeight = 26.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Author & Date Meta
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "By ${article.author}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold
                        )
                        Text(
                            text = "${article.authorRole} • ${article.publishedDate}",
                            fontSize = 11.sp,
                            color = EtfcTextMuted
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = EtfcCardInner
                    ) {
                        Text(
                            text = "${article.readTimeMinutes} MIN READ",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextSecondary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = EtfcBorder
                )

                // Full Content
                Text(
                    text = article.fullContent,
                    fontSize = 14.sp,
                    color = Color(0xFFE0E0E0),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Tags
                if (article.tags.isNotEmpty()) {
                    Text(
                        text = "TOPICS & ATHLETES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcGold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        article.tags.forEach { tag ->
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = EtfcCardInner,
                                border = BorderStroke(0.5.dp, EtfcBorder)
                            ) {
                                Text(
                                    text = "#$tag",
                                    fontSize = 10.sp,
                                    color = EtfcTextSecondary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onExploreBout,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                ) {
                    Text(
                        text = "EXPLORE UPCOMING FIGHT CARD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}
