package com.example.ui.components

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
import androidx.compose.foundation.interaction.collectIsDraggedAsState
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SmartDisplay
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.Bout
import com.example.data.models.HeroSlide
import com.example.data.models.HeroSlideType
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Persistent Arena Event Banner for ETFC 003:
 * Displays a live countdown timer, match details, and 'Buy Access' CTA.
 * Only rendered when active events exist in the Firestore matches collection.
 */
@Composable
fun Etfc003LiveCountdownStripBanner(
    isPassUnlocked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    eventName: String = "ETFC 003: Champions Collide",
    venue: String = "Adwa Museum Arena, Addis Ababa",
    targetTimestampMillis: Long = System.currentTimeMillis() + (90L * 24 * 3600 * 1000)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "banner_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.45f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    // Calculate real remaining seconds down to target timestamp
    var secondsRemaining by remember {
        val diff = (targetTimestampMillis - System.currentTimeMillis()) / 1000L
        mutableLongStateOf(if (diff > 0) diff else 86400L * 89 + 3600L * 18 + 60L * 42 + 25L)
    }

    LaunchedEffect(targetTimestampMillis) {
        while (secondsRemaining > 0L) {
            delay(1000L)
            val diff = (targetTimestampMillis - System.currentTimeMillis()) / 1000L
            secondsRemaining = if (diff > 0) diff else 0L
        }
    }

    val days = secondsRemaining / 86400
    val hours = (secondsRemaining % 86400) / 3600
    val minutes = (secondsRemaining % 3600) / 60
    val seconds = secondsRemaining % 60
    val countdownText = String.format("%02dd : %02dh : %02dm : %02ds", days, hours, minutes, seconds)

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcSurface),
        border = BorderStroke(1.2.dp, if (isPassUnlocked) EtfcSuccess else EtfcBorderGold),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .testTag("etfc003_strip_banner")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Event Title & Pulsing Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = (if (isPassUnlocked) EtfcSuccess else EtfcGold).copy(alpha = pulseAlpha),
                        modifier = Modifier.size(10.dp)
                    ) {}

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ETFC 003",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcGold,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EtfcCardInner
                            ) {
                                Text(
                                    text = "3 MONTHS OUT",
                                    fontSize = 8.5.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EtfcGold,
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Text(
                            text = if (isPassUnlocked) "PPV ACCESS PASS ACTIVE" else "NOVEMBER 28, 2026 • ADWA ARENA",
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isPassUnlocked) EtfcSuccess else EtfcTextMuted
                        )
                    }
                }

                // Right: Buy Access CTA Button
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isPassUnlocked) EtfcSuccess else EtfcGold,
                    shadowElevation = 4.dp,
                    modifier = Modifier.clickable { onClick() }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        if (!isPassUnlocked) {
                            Icon(
                                imageVector = Icons.Default.LocalActivity,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Text(
                            text = if (isPassUnlocked) "PASS ACTIVE" else "BUY ACCESS (600 ETB)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Countdown Strip & Co-Main Highlight
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color.Black.copy(alpha = 0.65f),
                border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Timer",
                            tint = EtfcGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "FIGHT NIGHT IN",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcTextMuted,
                            letterSpacing = 0.5.sp
                        )
                    }

                    Text(
                        text = countdownText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

/**
 * Dominant Top Live Event Section:
 * Automatically promoted to the absolute top of the home screen feed
 * whenever a live broadcast/bout is in progress.
 */
@Composable
fun DominantLiveEventTopSection(
    bout: Bout,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onNavigateToLiveArena: () -> Unit,
    onOpenPpvCheckout: () -> Unit,
    isPassUnlocked: Boolean,
    liveRound: Int,
    liveClockSec: Int,
    bitrateKbps: Int,
    crowdCheerCount: Int,
    onCheer: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(750),
            repeatMode = RepeatMode.Reverse
        ),
        label = "live_pulse_alpha"
    )

    Card(
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcSurface),
        border = BorderStroke(1.dp, EtfcBorderGold),
        modifier = modifier
            .fillMaxWidth()
            .testTag("dominant_live_event_card")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            // Header: Live Indicator & Clock & Expansion Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pulsing Red Live Badge
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(EtfcLiveRed.copy(alpha = pulseAlpha))
                    )
                    Text(
                        text = "LIVE NOW",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcLiveRed,
                        letterSpacing = 1.2.sp
                    )

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EtfcCardInner,
                        border = BorderStroke(0.5.dp, EtfcBorder)
                    ) {
                        Text(
                            text = "ROUND $liveRound • ${liveClockSec / 60}:${(liveClockSec % 60).toString().padStart(2, '0')}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }

                IconButton(
                    onClick = onToggleExpand,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Collapse Live Preview" else "Expand Live Preview",
                        tint = EtfcTextSecondary
                    )
                }
            }

            // Bout Title & Division
            Text(
                text = "${bout.fighterRed.flagEmoji} ${bout.fighterRed.name} vs ${bout.fighterBlue.name} ${bout.fighterBlue.flagEmoji}",
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                color = EtfcTextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 6.dp)
            )

            Text(
                text = "${bout.weightClass.label} Championship • ${bout.eventVenue}",
                fontSize = 12.sp,
                color = EtfcTextSecondary
            )

            // Animated Expandable Live Arena Preview Box
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Simulated Live Arena Canvas Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF0C0C12))
                            .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(14.dp))
                            .clickable { onNavigateToLiveArena() }
                    ) {
                        // Ambient Radial Glow
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            Color(0xFF241A2E),
                                            Color(0xFF09090D)
                                        )
                                    )
                                )
                        )

                        // Center Fight Faceoff Visualization
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                // Red Fighter Pill
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = EtfcRedPrimary.copy(alpha = 0.2f),
                                        border = BorderStroke(1.5.dp, EtfcRedPrimary),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = bout.fighterRed.name.take(1),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = EtfcRedPrimary
                                            )
                                        }
                                    }
                                    Text(
                                        text = bout.fighterRed.name.split(" ").lastOrNull() ?: bout.fighterRed.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "RED",
                                        fontSize = 9.sp,
                                        color = EtfcRedPrimary,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                // VS Pulse Badge
                                Surface(
                                    shape = CircleShape,
                                    color = EtfcGold.copy(alpha = 0.15f),
                                    border = BorderStroke(1.dp, EtfcGold)
                                ) {
                                    Text(
                                        text = "VS",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black,
                                        color = EtfcGold,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    )
                                }

                                // Blue Fighter Pill
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Surface(
                                        shape = CircleShape,
                                        color = EtfcBlueCorner.copy(alpha = 0.2f),
                                        border = BorderStroke(1.5.dp, EtfcBlueCorner),
                                        modifier = Modifier.size(46.dp)
                                    ) {
                                        Box(contentAlignment = Alignment.Center) {
                                            Text(
                                                text = bout.fighterBlue.name.take(1),
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Black,
                                                color = EtfcBlueCorner
                                            )
                                        }
                                    }
                                    Text(
                                        text = bout.fighterBlue.name.split(" ").lastOrNull() ?: bout.fighterBlue.name,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "BLUE",
                                        fontSize = 9.sp,
                                        color = EtfcBlueCorner,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "TAP TO ENTER LIVE OCTAGON ARENA",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = EtfcTextSecondary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Stream Quality & Bitrate Overlay Tag
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color.Black.copy(alpha = 0.8f),
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(EtfcSuccess)
                                )
                                Text(
                                    text = "1080p • $bitrateKbps Kbps",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        // Crowd Cheers Button
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.Black.copy(alpha = 0.8f),
                            border = BorderStroke(0.5.dp, EtfcBorderGold),
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .clickable { onCheer() }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(text = "🔥", fontSize = 12.sp)
                                Text(
                                    text = "$crowdCheerCount Cheers",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EtfcGold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = onNavigateToLiveArena,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("enter_live_arena_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tv,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "ENTER ARENA & LIVE CHAT",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        if (!isPassUnlocked) {
                            OutlinedButton(
                                onClick = onOpenPpvCheckout,
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, EtfcRedPrimary),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcRedPrimary),
                                modifier = Modifier.height(46.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "UNLOCK PPV",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
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
 * Pager-Based Hero Section Component:
 * Horizontally cycles through upcoming match cards, event teasers, and CTAs.
 */
@Composable
fun HeroSection(
    slides: List<HeroSlide>,
    onCtaClick: (HeroSlide) -> Unit,
    onSecondaryCtaClick: (HeroSlide) -> Unit,
    modifier: Modifier = Modifier
) {
    if (slides.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val coroutineScope = rememberCoroutineScope()
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()

    // Auto-advance loop when user is not manually dragging
    LaunchedEffect(isDragged, slides.size) {
        if (!isDragged && slides.size > 1) {
            while (true) {
                delay(5500)
                val nextPage = (pagerState.currentPage + 1) % slides.size
                pagerState.animateScrollToPage(nextPage)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_section_pager")
    ) {
        // Hero Section Header: Title & Page Indicator Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(EtfcGold)
                )
                Text(
                    text = "FEATURED SHOWDOWNS & PROMOS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 1.sp
                )
            }

            // Interactive Segmented Indicator
            HeroPagerIndicator(
                pagerState = pagerState,
                pageCount = slides.size,
                onSelectPage = { index ->
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(index)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Horizontal Pager Carousel
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(238.dp)
        ) { pageIndex ->
            val slide = slides[pageIndex]
            HeroSlideCardByType(
                slide = slide,
                onPrimaryCta = { onCtaClick(slide) },
                onSecondaryCta = { onSecondaryCtaClick(slide) }
            )
        }
    }
}

/**
 * Dispatches distinct high-craft card visuals based on the HeroSlideType.
 */
@Composable
fun HeroSlideCardByType(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (slide.type) {
        HeroSlideType.MATCH_PROMO_POSTER -> {
            MatchPromoPosterHeroCard(
                slide = slide,
                onPrimaryCta = onPrimaryCta,
                onSecondaryCta = onSecondaryCta,
                modifier = modifier
            )
        }
        HeroSlideType.UPCOMING_MATCH -> {
            UpcomingMatchHeroCard(
                slide = slide,
                onPrimaryCta = onPrimaryCta,
                onSecondaryCta = onSecondaryCta,
                modifier = modifier
            )
        }
        HeroSlideType.EARLY_BIRD_PPV -> {
            EarlyBirdPpvHeroCard(
                slide = slide,
                onPrimaryCta = onPrimaryCta,
                onSecondaryCta = onSecondaryCta,
                modifier = modifier
            )
        }
        HeroSlideType.EVENT_PROMO -> {
            EventTeaserHeroCard(
                slide = slide,
                onPrimaryCta = onPrimaryCta,
                onSecondaryCta = onSecondaryCta,
                modifier = modifier
            )
        }
        HeroSlideType.VIP_PASS_PROMO -> {
            VipPassHeroCard(
                slide = slide,
                onPrimaryCta = onPrimaryCta,
                onSecondaryCta = onSecondaryCta,
                modifier = modifier
            )
        }
    }
}

// =============================================================================
// 0. OFFICIAL MATCH PROMO POSTER HERO CARD (ADWA FIGHT NIGHT)
// =============================================================================

@Composable
fun MatchPromoPosterHeroCard(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.5.dp, EtfcGold),
        modifier = modifier
            .fillMaxSize()
            .testTag("match_promo_poster_${slide.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A200F),
                            Color(0xFF141210),
                            EtfcObsidian
                        )
                    )
                )
        ) {
            // If custom poster image URL exists, render it with smooth dark gradient overlay
            if (!slide.posterImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            } else {
                // Background subtle grid/octagon glow fallback
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    EtfcGold.copy(alpha = 0.15f),
                                    Color.Transparent
                                ),
                                radius = 450f
                            )
                        )
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header: ETFC 002 Badge + Discipline Type Badge + Venue
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcGold,
                            modifier = Modifier.padding(end = 2.dp)
                        ) {
                            Text(
                                text = slide.eventEdition,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = when (slide.matchCategoryBadge) {
                                "MMA BOUT", "MMA MATCH" -> EtfcRedPrimary.copy(alpha = 0.25f)
                                "MUAY THAI MATCH" -> EtfcBlueCorner.copy(alpha = 0.25f)
                                else -> EtfcGold.copy(alpha = 0.2f)
                            },
                            border = BorderStroke(
                                1.dp,
                                when (slide.matchCategoryBadge) {
                                    "MMA BOUT", "MMA MATCH" -> EtfcRedPrimary
                                    "MUAY THAI MATCH" -> EtfcBlueCorner
                                    else -> EtfcGold
                                }
                            )
                        ) {
                            Text(
                                text = slide.matchCategoryBadge ?: "OFFICIAL MATCH",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = when (slide.matchCategoryBadge) {
                                    "MMA BOUT", "MMA MATCH" -> EtfcRedPrimary
                                    "MUAY THAI MATCH" -> EtfcBlueCorner
                                    else -> EtfcGold
                                },
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle)
                    ) {
                        Text(
                            text = slide.eventDateOrCountdown,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Poster Center: Fighter Names & Versus Layout
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // ADWA FIGHT NIGHT Title Banner
                    Text(
                        text = "⚔️ ADWA FIGHT NIGHT ⚔️",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcGold,
                        letterSpacing = 1.2.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Red Corner Fighter
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(slide.redFlag, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = slide.redFighterName ?: "RED CORNER",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EtfcTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "RED CORNER",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = EtfcRedPrimary
                            )
                        }

                        // Center VS Emblem
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EtfcGold)
                                .border(1.5.dp, Color.White, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "VS",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black
                            )
                        }

                        // Blue Corner Fighter
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.End
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = slide.blueFighterName ?: "BLUE CORNER",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Black,
                                    color = EtfcTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(slide.blueFlag, fontSize = 14.sp)
                            }
                            Text(
                                text = "BLUE CORNER",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = EtfcBlueCorner
                            )
                        }
                    }

                    if (slide.hasChampionshipBelt) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "👑 ETFC GOLD CHAMPIONSHIP BELT ON THE LINE 👑",
                                fontSize = 8.5.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcGold
                            )
                        }
                    } else if (slide.weightClassText != null) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = slide.weightClassText,
                            fontSize = 8.5.sp,
                            fontWeight = FontWeight.Medium,
                            color = EtfcTextMuted
                        )
                    }
                }

                // Bottom Sponsors & CTA Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Sponsors Bar from Poster
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "SPONSORS: TELEBIRR • M-PESA • GOFERE • KURIFTU RESORTS • SWEAT BOX",
                            fontSize = 7.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onPrimaryCta,
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            modifier = Modifier
                                .weight(1.3f)
                                .height(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SportsMma,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = slide.primaryCtaText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1
                            )
                        }

                        if (slide.secondaryCtaText != null) {
                            OutlinedButton(
                                onClick = onSecondaryCta,
                                shape = RoundedCornerShape(8.dp),
                                border = BorderStroke(1.dp, EtfcBorderGold),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = EtfcGold
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                                modifier = Modifier
                                    .weight(0.9f)
                                    .height(36.dp)
                            ) {
                                Text(
                                    text = slide.secondaryCtaText,
                                    fontSize = 9.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 1. UPCOMING MATCH HERO CARD
// =============================================================================

@Composable
fun UpcomingMatchHeroCard(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.6f)),
        modifier = modifier
            .fillMaxSize()
            .testTag("hero_slide_${slide.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF261E14),
                            EtfcCardInner,
                            EtfcSurface
                        )
                    )
                )
        ) {
            if (!slide.posterImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EtfcGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, EtfcGold)
                    ) {
                        Text(
                            text = slide.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcGold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color.Black.copy(alpha = 0.6f),
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = EtfcGold,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = slide.eventDateOrCountdown,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                // Fighter Matchup Faceoff Display
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Red Fighter Side
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "🔴 ${slide.redFighterName ?: "Champion"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Undefeated Titleholder",
                            fontSize = 11.sp,
                            color = EtfcRedPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    // VS Pill
                    Surface(
                        shape = CircleShape,
                        color = EtfcGold.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, EtfcGold),
                        modifier = Modifier.padding(horizontal = 8.dp)
                    ) {
                        Text(
                            text = "VS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcGold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Blue Fighter Side
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = "${slide.blueFighterName ?: "Challenger"} 🔵",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "#1 Ranked Contender",
                            fontSize = 11.sp,
                            color = EtfcBlueCorner,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.End
                        )
                    }
                }

                // Venue & Match Details
                Text(
                    text = "${slide.weightClassText ?: "Championship"} • ${slide.venueOrLocation}",
                    fontSize = 11.sp,
                    color = EtfcTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPrimaryCta,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("hero_primary_cta_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsMma,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slide.primaryCtaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }

                    if (slide.secondaryCtaText != null) {
                        OutlinedButton(
                            onClick = onSecondaryCta,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = slide.secondaryCtaText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 2. EARLY BIRD PPV CTA CARD (TELEBIRR & M-PESA PROMO)
// =============================================================================

@Composable
fun EarlyBirdPpvHeroCard(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.7f)),
        modifier = modifier
            .fillMaxSize()
            .testTag("hero_slide_${slide.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EtfcRedPrimary.copy(alpha = 0.2f),
                            EtfcCardInner,
                            EtfcSurface
                        )
                    )
                )
        ) {
            if (!slide.posterImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EtfcRedPrimary.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, EtfcRedPrimary)
                    ) {
                        Text(
                            text = slide.tag,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EtfcSuccessBg,
                        border = BorderStroke(0.5.dp, EtfcSuccess)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.FlashOn,
                                contentDescription = null,
                                tint = EtfcSuccess,
                                modifier = Modifier.size(11.dp)
                            )
                            Text(
                                text = slide.discountText ?: "35% OFF",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcSuccess
                            )
                        }
                    }
                }

                // Main Title & Value Proposition
                Column {
                    Text(
                        text = slide.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = slide.subtitle,
                        fontSize = 11.sp,
                        color = EtfcTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "⚡ Instant 1-Tap Telebirr & Safaricom M-Pesa",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold
                    )
                }

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPrimaryCta,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcRedPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("hero_primary_cta_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalActivity,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slide.primaryCtaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }

                    if (slide.secondaryCtaText != null) {
                        OutlinedButton(
                            onClick = onSecondaryCta,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = slide.secondaryCtaText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 3. EVENT TEASER PROMO CARD
// =============================================================================

@Composable
fun EventTeaserHeroCard(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, Color(0xFF38BDF8).copy(alpha = 0.5f)),
        modifier = modifier
            .fillMaxSize()
            .testTag("hero_slide_${slide.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0C2438),
                            EtfcCardInner,
                            EtfcSurface
                        )
                    )
                )
        ) {
            if (!slide.posterImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = Color(0xFF38BDF8).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, Color(0xFF38BDF8))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartDisplay,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = slide.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }

                    Text(
                        text = "4K HDR PROMO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcTextMuted
                    )
                }

                // Center Title & Teaser Synopsis
                Column {
                    Text(
                        text = slide.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = slide.subtitle,
                        fontSize = 11.sp,
                        color = EtfcTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                }

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPrimaryCta,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF38BDF8),
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("hero_primary_cta_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slide.primaryCtaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }

                    if (slide.secondaryCtaText != null) {
                        OutlinedButton(
                            onClick = onSecondaryCta,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = slide.secondaryCtaText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// 4. VIP PASS PROMO HERO CARD
// =============================================================================

@Composable
fun VipPassHeroCard(
    slide: HeroSlide,
    onPrimaryCta: () -> Unit,
    onSecondaryCta: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, EtfcGold),
        modifier = modifier
            .fillMaxSize()
            .testTag("hero_slide_${slide.id}")
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            EtfcGold.copy(alpha = 0.2f),
                            EtfcCardInner,
                            EtfcSurface
                        )
                    )
                )
        ) {
            if (!slide.posterImageUrl.isNullOrBlank()) {
                AsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Black.copy(alpha = 0.55f),
                                    Color.Black.copy(alpha = 0.75f),
                                    Color.Black.copy(alpha = 0.92f)
                                )
                            )
                        )
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Tag Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = EtfcGold.copy(alpha = 0.25f),
                        border = BorderStroke(1.dp, EtfcGold)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.WorkspacePremium,
                                contentDescription = null,
                                tint = EtfcGold,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                text = slide.tag,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcGold
                            )
                        }
                    }

                    Text(
                        text = "ALL-ACCESS SEASON PASS",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold
                    )
                }

                // Title & Subtitle
                Column {
                    Text(
                        text = slide.title,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = slide.subtitle,
                        fontSize = 11.sp,
                        color = EtfcTextSecondary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        lineHeight = 15.sp
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "★ 4K Quad-Cam + Live Corner Mics + Backstage",
                        fontSize = 10.sp,
                        color = EtfcGold,
                        fontWeight = FontWeight.Medium
                    )
                }

                // CTA Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onPrimaryCta,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp)
                            .testTag("hero_primary_cta_${slide.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.WorkspacePremium,
                            contentDescription = null,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = slide.primaryCtaText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 1
                        )
                    }

                    if (slide.secondaryCtaText != null) {
                        OutlinedButton(
                            onClick = onSecondaryCta,
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, EtfcBorderSubtle),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                text = slide.secondaryCtaText,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}

// =============================================================================
// HERO PAGER SEGMENTED INDICATOR
// =============================================================================

@Composable
fun HeroPagerIndicator(
    pagerState: PagerState,
    pageCount: Int,
    onSelectPage: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pageCount) { index ->
            val isSelected = pagerState.currentPage == index
            Box(
                modifier = Modifier
                    .height(6.dp)
                    .width(if (isSelected) 20.dp else 6.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) EtfcGold else EtfcBorderSubtle
                    )
                    .clickable { onSelectPage(index) }
            )
        }
    }
}
