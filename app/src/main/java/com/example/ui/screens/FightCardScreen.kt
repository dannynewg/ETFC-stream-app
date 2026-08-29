package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Bout
import com.example.data.models.BoutStatus
import com.example.data.models.Fighter
import com.example.data.models.MatchCategory
import com.example.data.models.StreamQuality
import com.example.ui.components.AvatarCorner
import com.example.ui.components.FighterAvatarView
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcLiveRed
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

@Composable
fun FightCardScreen(
    bouts: List<Bout>,
    onSelectBoutForStream: (Bout) -> Unit,
    onOpenTaleOfTheTape: (Fighter, Fighter) -> Unit,
    onDownloadBout: (Bout, StreamQuality) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategoryFilter by remember { mutableStateOf<MatchCategory?>(null) }

    val filteredBouts = remember(selectedCategoryFilter, bouts) {
        if (selectedCategoryFilter == null) {
            bouts
        } else {
            bouts.filter { it.matchCategory == selectedCategoryFilter }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Official Event Banner: ETFC 002 Adwa Fight Night
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EtfcGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                colors = CardDefaults.cardColors(containerColor = EtfcCard)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF241A04), EtfcCard)
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = EtfcLiveRed,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "🔴 LIVE EVENT",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = EtfcGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "ETFC 002 OFFICIAL CARD",
                                    color = EtfcGold,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 0.8.sp,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "ADWA FIGHT NIGHT",
                            color = EtfcGold,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.2.sp
                        )

                        Text(
                            text = "MAIN EVENT: SEDO vs JHONNY • MIDDLE WEIGHT TITLE",
                            color = EtfcTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "ADWA 00 MUSEUM",
                                    color = EtfcTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "AUGUST 27 • 8:00 PM EAT",
                                    color = EtfcTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Discipline Filters: All (10), MMA (4), Boxing (3), Muay Thai (3)
        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isSelected = selectedCategoryFilter == null
                    Surface(
                        modifier = Modifier
                            .clickable { selectedCategoryFilter = null }
                            .testTag("filter_all_bouts"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) EtfcGold else EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EtfcGold else EtfcBorderSubtle
                        )
                    ) {
                        Text(
                            text = "All Bouts (${bouts.size})",
                            color = if (isSelected) Color.Black else EtfcTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                items(MatchCategory.entries) { category ->
                    val isSelected = selectedCategoryFilter == category
                    val count = bouts.count { it.matchCategory == category }
                    Surface(
                        modifier = Modifier
                            .clickable { selectedCategoryFilter = category }
                            .testTag("filter_${category.name.lowercase()}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) EtfcGold else EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) EtfcGold else EtfcBorderSubtle
                        )
                    ) {
                        Text(
                            text = "${category.label} ($count)",
                            color = if (isSelected) Color.Black else EtfcTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }

        // Fight Cards List
        items(filteredBouts) { bout ->
            BoutCardItem(
                bout = bout,
                onWatchLive = { onSelectBoutForStream(bout) },
                onTaleOfTheTape = { onOpenTaleOfTheTape(bout.fighterRed, bout.fighterBlue) },
                onDownload = { onDownloadBout(bout, StreamQuality.SAVER_240P) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun BoutCardItem(
    bout: Bout,
    onWatchLive: () -> Unit,
    onTaleOfTheTape: () -> Unit,
    onDownload: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (bout.boutType == com.example.data.models.BoutType.MAIN_EVENT) EtfcGold.copy(alpha = 0.6f) else EtfcBorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = EtfcCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Bout Category & Title Header
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
                        color = Color(bout.boutType.badgeColorHex).copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(bout.boutType.badgeColorHex))
                    ) {
                        Text(
                            text = bout.boutType.label,
                            color = Color(bout.boutType.badgeColorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Surface(
                        color = EtfcCardInner,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle)
                    ) {
                        Text(
                            text = bout.matchTitle,
                            color = EtfcGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.5.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    if (bout.isTitleFight) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = EtfcGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "TITLE FIGHT",
                            color = EtfcGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )
                    } else {
                        Text(
                            text = "${bout.scheduledRounds} Rounds",
                            color = EtfcTextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Fighters Face-Off Row with High-Resolution Stylized Portraits
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Red Corner Fighter (Avatar + Details)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FighterAvatarView(
                        fighter = bout.fighterRed,
                        size = 48.dp,
                        corner = AvatarCorner.RED
                    )
                    Column {
                        Text(
                            text = bout.fighterRed.name.uppercase(),
                            color = EtfcTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "\"${bout.fighterRed.nickname}\"",
                            color = EtfcRedPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "${bout.fighterRed.wins}-${bout.fighterRed.losses}-${bout.fighterRed.draws}",
                            color = EtfcTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // VS Badge in Center
                Surface(
                    shape = CircleShape,
                    color = EtfcCardInner,
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f)),
                    modifier = Modifier.padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "VS",
                        color = EtfcGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                // Blue Corner Fighter (Details + Avatar)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = bout.fighterBlue.name.uppercase(),
                            color = EtfcTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "\"${bout.fighterBlue.nickname}\"",
                            color = EtfcBlueCorner,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "${bout.fighterBlue.wins}-${bout.fighterBlue.losses}-${bout.fighterBlue.draws}",
                            color = EtfcTextSecondary,
                            fontSize = 10.sp,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    FighterAvatarView(
                        fighter = bout.fighterBlue,
                        size = 48.dp,
                        corner = AvatarCorner.BLUE
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWatchLive,
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("watch_bout_${bout.id}_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EtfcGold,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (bout.status == BoutStatus.LIVE) "Watch Live" else "Stream Fight",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color.Black
                    )
                }

                OutlinedButton(
                    onClick = onTaleOfTheTape,
                    modifier = Modifier
                        .weight(1.1f)
                        .height(38.dp)
                        .testTag("tale_bout_${bout.id}_btn"),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextPrimary),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle)
                ) {
                    Icon(
                        imageVector = Icons.Default.Compare,
                        contentDescription = null,
                        tint = EtfcGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Tale of Tape", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                }

                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                    modifier = Modifier
                        .size(38.dp)
                        .clickable { onDownload() }
                        .testTag("download_bout_${bout.id}_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download",
                            tint = EtfcBlueCorner,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}
