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
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.FavoriteFighterEntity
import com.example.data.models.Fighter
import com.example.data.models.MatchCategory
import com.example.data.models.WeightClass
import com.example.ui.components.AvatarCorner
import com.example.ui.components.FighterAvatarView
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

@Composable
fun FightersScreen(
    fighters: List<Fighter>,
    favoriteFighters: List<FavoriteFighterEntity>,
    selectedDivision: WeightClass?,
    searchQuery: String,
    onSearchChanged: (String) -> Unit,
    onDivisionSelected: (WeightClass?) -> Unit,
    onToggleFavorite: (Fighter, Boolean) -> Unit,
    onCompareFighter: (Fighter) -> Unit,
    modifier: Modifier = Modifier
) {
    val favoriteIds = remember(favoriteFighters) {
        favoriteFighters.map { it.fighterId }.toSet()
    }
    val filteredFighters = remember(fighters, selectedDivision, searchQuery) {
        fighters.filter { fighter ->
            val matchesDivision = selectedDivision == null || fighter.division == selectedDivision
            val matchesSearch = searchQuery.isBlank() ||
                    fighter.name.contains(searchQuery, ignoreCase = true) ||
                    fighter.nickname.contains(searchQuery, ignoreCase = true) ||
                    fighter.countryName.contains(searchQuery, ignoreCase = true)
            matchesDivision && matchesSearch
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Text(
                    text = "ETFC 002 FIGHTER ROSTER",
                    color = EtfcGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Adwa Fight Night competitors: MMA, Boxing & Muay Thai athletes",
                    color = EtfcTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Search Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChanged,
                placeholder = { Text("Search fighters by name, nickname, or nation...", color = EtfcTextMuted, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EtfcTextSecondary, modifier = Modifier.size(18.dp))
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_fighters_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EtfcGold,
                    unfocusedBorderColor = EtfcBorder,
                    focusedContainerColor = EtfcCardInner,
                    unfocusedContainerColor = EtfcCardInner,
                    focusedTextColor = EtfcTextPrimary,
                    unfocusedTextColor = EtfcTextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )
        }

        // Weight Class Division Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    val isAll = selectedDivision == null
                    Surface(
                        modifier = Modifier.clickable { onDivisionSelected(null) },
                        shape = RoundedCornerShape(20.dp),
                        color = if (isAll) EtfcGold else EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAll) EtfcGold else EtfcBorderSubtle)
                    ) {
                        Text(
                            text = "All Divisions (${fighters.size})",
                            color = if (isAll) Color.Black else EtfcTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }

                items(WeightClass.entries) { division ->
                    val isSelected = selectedDivision == division
                    val count = fighters.count { it.division == division }
                    if (count > 0) {
                        Surface(
                            modifier = Modifier.clickable { onDivisionSelected(division) },
                            shape = RoundedCornerShape(20.dp),
                            color = if (isSelected) EtfcGold else EtfcCardInner,
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) EtfcGold else EtfcBorderSubtle)
                        ) {
                            Text(
                                text = "${division.label} ($count)",
                                color = if (isSelected) Color.Black else EtfcTextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
            }
        }

        // Fighters List
        items(filteredFighters) { fighter ->
            val isFavorite = favoriteIds.contains(fighter.id)
            FighterCardItem(
                fighter = fighter,
                isFavorite = isFavorite,
                onToggleFavorite = { onToggleFavorite(fighter, !isFavorite) },
                onCompare = { onCompareFighter(fighter) }
            )
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun FighterCardItem(
    fighter: Fighter,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onCompare: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (fighter.isChampion) EtfcGold.copy(alpha = 0.5f) else EtfcBorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = EtfcCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Portrait, Name, Belt, and Favorite Star
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    FighterAvatarView(
                        fighter = fighter,
                        size = 52.dp,
                        corner = if (fighter.isChampion) AvatarCorner.GOLD else AvatarCorner.NONE
                    )
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = fighter.name.uppercase(),
                                color = EtfcTextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Black
                            )
                            if (fighter.isChampion) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    color = EtfcGold.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(4.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "👑 CHAMPION",
                                        color = EtfcGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "\"${fighter.nickname}\" • ${fighter.countryName}",
                            color = EtfcGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(top = 2.dp)
                        ) {
                            Surface(
                                color = EtfcCardInner,
                                shape = RoundedCornerShape(3.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle)
                            ) {
                                Text(
                                    text = fighter.discipline.label,
                                    color = EtfcBlueCorner,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Surface(
                                color = EtfcCardInner,
                                shape = RoundedCornerShape(3.dp),
                                border = androidx.compose.foundation.BorderStroke(0.5.dp, EtfcBorderSubtle)
                            ) {
                                Text(
                                    text = fighter.division.label,
                                    color = EtfcTextSecondary,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onToggleFavorite,
                    modifier = Modifier.size(36.dp).testTag("fav_fighter_${fighter.id}_btn")
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) EtfcGold else EtfcTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stats Grid
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(EtfcCardInner)
                    .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("RECORD", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${fighter.wins}-${fighter.losses}-${fighter.draws}", color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("KO / FINISH", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${fighter.kos} KOs", color = EtfcRedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("HEIGHT", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${fighter.heightCm} cm", color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("REACH", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("${fighter.reachInches}\"", color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bio description
            Text(
                text = fighter.bio,
                color = EtfcTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Signature: ${fighter.signatureMove}",
                    color = EtfcGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "Stance: ${fighter.stance}",
                    color = EtfcTextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            var showMetaHistory by remember { mutableStateOf(false) }

            // Meta-Engine Deep Dive & Compare Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    color = if (showMetaHistory) EtfcGold else EtfcCardInner,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (showMetaHistory) EtfcGold else EtfcBorderSubtle),
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showMetaHistory = !showMetaHistory }
                        .testTag("meta_engine_toggle_${fighter.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (showMetaHistory) "📊 Hide Meta-Engine" else "📊 Meta-Engine Analytics",
                            color = if (showMetaHistory) Color.Black else EtfcGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                    modifier = Modifier
                        .clickable { onCompare() }
                        .testTag("compare_btn_${fighter.id}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Compare,
                            contentDescription = "Compare",
                            tint = EtfcTextSecondary,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Tale of Tape",
                            color = EtfcTextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Expanded Meta-Engine Analytics Drawer
            if (showMetaHistory) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "ROOM META-ENGINE: FIGHTER TELEMETRY",
                            color = EtfcGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("Striking Accuracy", color = EtfcTextMuted, fontSize = 9.sp)
                                Text("${fighter.strikeAccuracyPercent}%", color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Takedown Defense", color = EtfcTextMuted, fontSize = 9.sp)
                                Text("${fighter.takedownDefensePercent}%", color = EtfcSuccess, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Finish Rate", color = EtfcTextMuted, fontSize = 9.sp)
                                val finishRate = if (fighter.wins > 0) ((fighter.kos + fighter.submissions) * 100 / fighter.wins) else 0
                                Text("$finishRate%", color = EtfcRedPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("Submissions", color = EtfcTextMuted, fontSize = 9.sp)
                                Text("${fighter.submissions}", color = EtfcBlueCorner, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(EtfcBorderSubtle))
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "CAREER WEIGHT CLASS HISTORY",
                            color = EtfcTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "• ${fighter.division.label} (Current): ${fighter.wins}-${fighter.losses}-${fighter.draws} • ${if (fighter.isChampion) "Champion" else "Contender"}",
                            color = EtfcTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}
