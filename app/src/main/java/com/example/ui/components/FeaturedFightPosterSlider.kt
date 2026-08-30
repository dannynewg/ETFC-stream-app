package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.LocalActivity
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
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
import coil.compose.SubcomposeAsyncImage
import com.example.data.local.MatchEntity
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
 * Horizontally Scrolling Image Slider Component that uses Coil to load remote posters
 * and overlays high-impact titles, fighter matchups, championship belts, and watch CTA triggers.
 */
@Composable
fun FeaturedFightPosterSlider(
    slides: List<HeroSlide>,
    modifier: Modifier = Modifier,
    onWatchLiveClick: (HeroSlide) -> Unit = {},
    onOrderPpvClick: (HeroSlide) -> Unit = {},
    onSlideClick: (HeroSlide) -> Unit = {}
) {
    if (slides.isEmpty()) return

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val isDragged by pagerState.interactionSource.collectIsDraggedAsState()
    val coroutineScope = rememberCoroutineScope()

    // Auto-scroll every 6 seconds when not dragged
    LaunchedEffect(isDragged, slides.size) {
        if (!isDragged && slides.size > 1) {
            while (true) {
                delay(6000)
                val nextPage = (pagerState.currentPage + 1) % slides.size
                pagerState.animateScrollToPage(nextPage, animationSpec = tween(700))
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("featured_fight_poster_slider")
    ) {
        // Horizontally scrolling pager with edge peek
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 12.dp,
            modifier = Modifier
                .fillMaxWidth()
                .height(310.dp)
        ) { pageIndex ->
            val slide = slides[pageIndex]
            FeaturedFightPosterCard(
                slide = slide,
                onWatchLiveClick = { onWatchLiveClick(slide) },
                onOrderPpvClick = { onOrderPpvClick(slide) },
                onCardClick = { onSlideClick(slide) }
            )
        }

        // Indicator dots below carousel
        if (slides.size > 1) {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                slides.indices.forEach { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotColor = if (isSelected) EtfcGold else EtfcBorderSubtle
                    val dotWidth = if (isSelected) 24.dp else 8.dp

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 3.dp)
                            .height(6.dp)
                            .width(dotWidth)
                            .clip(RoundedCornerShape(3.dp))
                            .background(dotColor)
                            .clickable {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            }
                    )
                }
            }
        }
    }
}

/**
 * Individual Fight Poster Card with Coil Remote Image Loading and Title Overlay
 */
@Composable
fun FeaturedFightPosterCard(
    slide: HeroSlide,
    modifier: Modifier = Modifier,
    onWatchLiveClick: () -> Unit = {},
    onOrderPpvClick: () -> Unit = {},
    onCardClick: () -> Unit = {}
) {
    val borderColor = if (slide.hasChampionshipBelt) EtfcBorderGold else EtfcBorder
    val glowColor = if (slide.hasChampionshipBelt) EtfcGoldGlow else Color.Transparent

    Card(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, borderColor, RoundedCornerShape(18.dp))
            .clickable { onCardClick() }
            .testTag("featured_poster_card_${slide.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Remote Poster Loaded via Coil with Placeholder and Fallback
            if (!slide.posterImageUrl.isNullOrBlank()) {
                SubcomposeAsyncImage(
                    model = slide.posterImageUrl,
                    contentDescription = slide.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    loading = {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(EtfcSurfaceVariant, EtfcObsidian)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                color = EtfcGold,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    },
                    error = {
                        // Fallback arena background with subtle fight graphics
                        FallbackArenaPosterBackground(slide = slide)
                    }
                )
            } else {
                FallbackArenaPosterBackground(slide = slide)
            }

            // Dark Multi-Stop Gradient Scrim for crystal clear typography overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            0.0f to Color.Black.copy(alpha = 0.65f),
                            0.35f to Color.Black.copy(alpha = 0.40f),
                            0.65f to Color.Black.copy(alpha = 0.85f),
                            1.0f to Color.Black.copy(alpha = 0.98f)
                        )
                    )
            )

            // Content Overlay Layout
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Row: Badges, Edition, Championship Belt
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Tag Badge (e.g. LIVE PPV, FEATURED, MAIN EVENT)
                    Surface(
                        color = Color(slide.badgeColorHex).copy(alpha = 0.92f),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (slide.badgeColorHex == 0xFFEF4444) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                            }
                            Text(
                                text = slide.tag.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.8.sp
                            )
                        }
                    }

                    // Championship Belt or Edition tag
                    if (slide.hasChampionshipBelt) {
                        Surface(
                            color = EtfcGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, EtfcGold)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.EmojiEvents,
                                    contentDescription = "Championship Belt",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "TITLE BOUT",
                                    color = EtfcGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    } else {
                        Text(
                            text = slide.eventEdition,
                            color = EtfcTextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Bottom Content: Matchup Title, Fighter Names, Venue, CTA Buttons
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Match Category or Weight Class
                    if (!slide.weightClassText.isNullOrBlank()) {
                        Text(
                            text = slide.weightClassText.uppercase(),
                            color = EtfcGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }

                    // Fighter Head-to-Head Display or Main Title
                    if (!slide.redFighterName.isNullOrBlank() && !slide.blueFighterName.isNullOrBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "${slide.redFlag} ${slide.redFighterName}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            Text(
                                text = " VS ",
                                color = EtfcGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.padding(horizontal = 6.dp)
                            )
                            Text(
                                text = "${slide.blueFighterName} ${slide.blueFlag}",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                        }
                    } else {
                        Text(
                            text = slide.title,
                            color = Color.White,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Black,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Subtitle & Venue
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = slide.eventDateOrCountdown,
                            color = EtfcTextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "•",
                            color = EtfcTextMuted,
                            fontSize = 12.sp
                        )
                        Text(
                            text = slide.venueOrLocation,
                            color = EtfcTextMuted,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Primary CTA
                        Button(
                            onClick = { onWatchLiveClick() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (slide.badgeColorHex == 0xFFEF4444) EtfcRedPrimary else EtfcGold
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("primary_cta_${slide.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (slide.badgeColorHex == 0xFFEF4444) Color.White else EtfcObsidian,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = slide.primaryCtaText.uppercase(),
                                color = if (slide.badgeColorHex == 0xFFEF4444) Color.White else EtfcObsidian,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Secondary CTA (e.g. PPV / Preview)
                        if (!slide.secondaryCtaText.isNullOrBlank()) {
                            OutlinedButton(
                                onClick = { onOrderPpvClick() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color.White
                                ),
                                border = BorderStroke(1.dp, EtfcBorder),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier
                                    .height(38.dp)
                                    .testTag("secondary_cta_${slide.id}")
                            ) {
                                Text(
                                    text = slide.secondaryCtaText.uppercase(),
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
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
 * Fallback Graphic Background for Arena Fights when offline or without custom image
 */
@Composable
private fun FallbackArenaPosterBackground(slide: HeroSlide) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        EtfcObsidian,
                        Color(0xFF1E112A),
                        Color(0xFF0F172A),
                        EtfcObsidian
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Red & Blue Corner Ambient Glows
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(EtfcRedPrimary.copy(alpha = 0.25f), Color.Transparent)
                        )
                    )
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(Color.Transparent, EtfcBlueCorner.copy(alpha = 0.25f))
                        )
                    )
            )
        }

        // Center Watermark Icon
        Icon(
            imageVector = Icons.Default.SportsMma,
            contentDescription = null,
            tint = Color.White.copy(alpha = 0.08f),
            modifier = Modifier.size(140.dp)
        )
    }
}
