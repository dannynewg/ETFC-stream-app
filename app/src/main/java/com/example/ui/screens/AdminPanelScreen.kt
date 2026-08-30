package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.models.*
import com.example.data.remote.YouTubeVideoMetadata
import com.example.ui.theme.*
import com.example.ui.viewmodel.EtfcViewModel
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelScreen(
    viewModel: EtfcViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    val dynamicSlides by viewModel.dynamicHeroSlides.collectAsState()
    val dynamicVideos by viewModel.dynamicPreviousVideos.collectAsState()
    val dynamicNews by viewModel.dynamicNewsArticles.collectAsState()
    val dynamicFighters by viewModel.dynamicFighters.collectAsState()
    val dynamicMatches by viewModel.dynamicMatches.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }

    // Dialog State Controllers
    var isAddFighterDialogOpen by remember { mutableStateOf(false) }
    var fighterToEdit by remember { mutableStateOf<Fighter?>(null) }

    var isAddMatchDialogOpen by remember { mutableStateOf(false) }
    var matchToEdit by remember { mutableStateOf<Bout?>(null) }

    var isAddSlideDialogOpen by remember { mutableStateOf(false) }
    var isAddVideoDialogOpen by remember { mutableStateOf(false) }
    var isAddNewsDialogOpen by remember { mutableStateOf(false) }

    val tabs = listOf(
        "🥊 Fighters" to dynamicFighters.size,
        "⚔️ Match Cards" to dynamicMatches.size,
        "🖼️ Hero Slider" to dynamicSlides.size,
        "🎬 Fight Videos" to dynamicVideos.size,
        "📢 News" to dynamicNews.size,
        "🐙 GitHub Sync" to 0
    )

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .testTag("admin_panel_screen"),
        containerColor = EtfcObsidian,
        topBar = {
            Surface(
                color = EtfcSurface,
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
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
                            IconButton(
                                onClick = onNavigateBack,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EtfcCard)
                                    .testTag("admin_back_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Back",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EtfcGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin CMS",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ETFC CMS & ADMIN PANEL",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 15.sp,
                                        color = EtfcGold,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(4.dp),
                                        color = EtfcRedPrimary
                                    ) {
                                        Text(
                                            text = "LIVE SQLite",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            color = Color.White,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Full Database Lifecycle • Instant Multi-Screen Propagation",
                                    fontSize = 11.sp,
                                    color = EtfcTextMuted
                                )
                            }
                        }

                        IconButton(
                            onClick = onNavigateBack,
                            modifier = Modifier.testTag("admin_close_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = EtfcTextSecondary
                            )
                        }
                    }

                    // Primary Tab Row
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = EtfcSurface,
                        contentColor = EtfcGold,
                        edgePadding = 12.dp,
                        indicator = { tabPositions ->
                            if (selectedTab < tabPositions.size) {
                                TabRowDefaults.SecondaryIndicator(
                                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                    color = EtfcGold,
                                    height = 3.dp
                                )
                            }
                        },
                        divider = {
                            HorizontalDivider(color = EtfcBorderGold.copy(alpha = 0.2f), thickness = 1.dp)
                        }
                    ) {
                        tabs.forEachIndexed { index, (title, count) ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                modifier = Modifier.testTag("admin_tab_$index"),
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(
                                            text = title,
                                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                            fontSize = 13.sp,
                                            color = if (selectedTab == index) EtfcGold else EtfcTextSecondary
                                        )
                                        if (count > 0) {
                                            Surface(
                                                shape = RoundedCornerShape(10.dp),
                                                color = if (selectedTab == index) EtfcGold.copy(alpha = 0.2f) else EtfcCard
                                            ) {
                                                Text(
                                                    text = "$count",
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (selectedTab == index) EtfcGold else EtfcTextMuted,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }
                }
            }
        },
        floatingActionButton = {
            when (selectedTab) {
                0 -> {
                    ExtendedFloatingActionButton(
                        onClick = {
                            fighterToEdit = null
                            isAddFighterDialogOpen = true
                        },
                        containerColor = EtfcGold,
                        contentColor = Color.Black,
                        icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                        text = { Text("ADD FIGHTER", fontWeight = FontWeight.Black) },
                        modifier = Modifier.testTag("fab_add_fighter")
                    )
                }
                1 -> {
                    ExtendedFloatingActionButton(
                        onClick = {
                            matchToEdit = null
                            isAddMatchDialogOpen = true
                        },
                        containerColor = EtfcRedPrimary,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.SportsMma, contentDescription = null) },
                        text = { Text("ADD MATCH CARD", fontWeight = FontWeight.Black) },
                        modifier = Modifier.testTag("fab_add_match")
                    )
                }
                2 -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddSlideDialogOpen = true },
                        containerColor = EtfcGold,
                        contentColor = Color.Black,
                        icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null) },
                        text = { Text("ADD PROMO SLIDE", fontWeight = FontWeight.Black) },
                        modifier = Modifier.testTag("fab_add_slide")
                    )
                }
                3 -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddVideoDialogOpen = true },
                        containerColor = EtfcRedPrimary,
                        contentColor = Color.White,
                        icon = { Icon(Icons.Default.VideoCall, contentDescription = null) },
                        text = { Text("ADD FIGHT VIDEO", fontWeight = FontWeight.Black) },
                        modifier = Modifier.testTag("fab_add_video")
                    )
                }
                4 -> {
                    ExtendedFloatingActionButton(
                        onClick = { isAddNewsDialogOpen = true },
                        containerColor = EtfcGold,
                        contentColor = Color.Black,
                        icon = { Icon(Icons.Default.PostAdd, contentDescription = null) },
                        text = { Text("ADD ANNOUNCEMENT", fontWeight = FontWeight.Black) },
                        modifier = Modifier.testTag("fab_add_news")
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(EtfcObsidian)
        ) {
            when (selectedTab) {
                0 -> {
                    AdminFightersManagementTab(
                        fighters = dynamicFighters,
                        onEditFighter = { fighter ->
                            fighterToEdit = fighter
                            isAddFighterDialogOpen = true
                        },
                        onDeleteFighter = { fighterId -> viewModel.deleteFighter(fighterId) }
                    )
                }
                1 -> {
                    AdminMatchesManagementTab(
                        matches = dynamicMatches,
                        fighters = dynamicFighters,
                        onEditMatch = { bout ->
                            matchToEdit = bout
                            isAddMatchDialogOpen = true
                        },
                        onDeleteMatch = { matchId -> viewModel.deleteMatchCard(matchId) }
                    )
                }
                2 -> {
                    AdminHeroSlidesTab(
                        slides = dynamicSlides,
                        onDeleteSlide = { viewModel.deleteHeroSlide(it) }
                    )
                }
                3 -> {
                    AdminVideosTab(
                        videos = dynamicVideos,
                        onDeleteVideo = { viewModel.deletePreviousVideo(it) }
                    )
                }
                4 -> {
                    AdminNewsTab(
                        articles = dynamicNews,
                        onDeleteArticle = { viewModel.deleteNewsAnnouncement(it) }
                    )
                }
                5 -> {
                    AdminGitHubSyncTab(
                        viewModel = viewModel
                    )
                }
            }
        }
    }

    // =========================================================================
    // DIALOG: ADD / EDIT FIGHTER WITH LOCAL FILE IMAGE PICKER
    // =========================================================================
    if (isAddFighterDialogOpen) {
        FighterAddEditDialog(
            existingFighter = fighterToEdit,
            onDismiss = {
                isAddFighterDialogOpen = false
                fighterToEdit = null
            },
            onSave = { fighter ->
                viewModel.saveFighter(fighter)
                isAddFighterDialogOpen = false
                fighterToEdit = null
            }
        )
    }

    // =========================================================================
    // DIALOG: ADD / EDIT MATCH CARD WITH LOCAL POSTER PICKER & ROSTER SELECTION
    // =========================================================================
    if (isAddMatchDialogOpen) {
        MatchAddEditDialog(
            existingBout = matchToEdit,
            availableFighters = dynamicFighters,
            onDismiss = {
                isAddMatchDialogOpen = false
                matchToEdit = null
            },
            onSave = { bout ->
                viewModel.saveMatchCard(bout)
                isAddMatchDialogOpen = false
                matchToEdit = null
            }
        )
    }

    // =========================================================================
    // DIALOG: ADD HERO SLIDE
    // =========================================================================
    if (isAddSlideDialogOpen) {
        AddHeroSlideDialog(
            onDismiss = { isAddSlideDialogOpen = false },
            onAddSlide = { slide, isPinned ->
                viewModel.postMatchPromoPoster(slide, isPinned)
                isAddSlideDialogOpen = false
            }
        )
    }

    // =========================================================================
    // DIALOG: ADD VIDEO
    // =========================================================================
    if (isAddVideoDialogOpen) {
        AddVideoDialog(
            viewModel = viewModel,
            onDismiss = { isAddVideoDialogOpen = false },
            onAddVideo = { video ->
                viewModel.addPreviousFightVideo(video)
                isAddVideoDialogOpen = false
            }
        )
    }

    // =========================================================================
    // DIALOG: ADD NEWS ANNOUNCEMENT
    // =========================================================================
    if (isAddNewsDialogOpen) {
        AddNewsDialog(
            onDismiss = { isAddNewsDialogOpen = false },
            onAddNews = { article ->
                viewModel.postNewsAnnouncement(article)
                isAddNewsDialogOpen = false
            }
        )
    }
}

// =============================================================================
// TAB 0: FIGHTERS MANAGEMENT (LAZY COLUMN WITH FULL CRUD & LOCAL IMAGE PICKER)
// =============================================================================

@Composable
fun AdminFightersManagementTab(
    fighters: List<Fighter>,
    onEditFighter: (Fighter) -> Unit,
    onDeleteFighter: (String) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDivision by remember { mutableStateOf<WeightClass?>(null) }

    val filteredFighters = remember(fighters, searchQuery, selectedDivision) {
        fighters.filter { f ->
            val matchesQuery = searchQuery.isBlank() ||
                f.name.contains(searchQuery, ignoreCase = true) ||
                f.nickname.contains(searchQuery, ignoreCase = true) ||
                f.countryName.contains(searchQuery, ignoreCase = true)
            val matchesDivision = selectedDivision == null || f.division == selectedDivision
            matchesQuery && matchesDivision
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_fighters_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🥊 ATHLETE ROSTER & CRM DIRECTORY",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcGold,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Manage fighter bios, records, weight divisions, reach, stances and local photos. Updates propagate immediately across Home, Fight Card, Tale of the Tape, and Live Stream.",
                        fontSize = 11.sp,
                        color = EtfcTextMuted
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_fighter_search"),
                        placeholder = { Text("Search fighters by name, alias, country...", color = EtfcTextMuted, fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = EtfcGold) },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear", tint = EtfcTextSecondary)
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            unfocusedBorderColor = EtfcBorderGold.copy(alpha = 0.3f),
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary,
                            cursorColor = EtfcGold
                        ),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )

                    // Division Filter Chips
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            FilterChip(
                                selected = selectedDivision == null,
                                onClick = { selectedDivision = null },
                                label = { Text("All Divisions (${fighters.size})", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EtfcGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = EtfcSurface,
                                    labelColor = EtfcTextSecondary
                                )
                            )
                        }
                        items(WeightClass.entries.toTypedArray()) { division ->
                            val count = fighters.count { it.division == division }
                            FilterChip(
                                selected = selectedDivision == division,
                                onClick = { selectedDivision = if (selectedDivision == division) null else division },
                                label = { Text("${division.label} ($count)", fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EtfcGold,
                                    selectedLabelColor = Color.Black,
                                    containerColor = EtfcSurface,
                                    labelColor = EtfcTextSecondary
                                )
                            )
                        }
                    }
                }
            }
        }

        if (filteredFighters.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PersonOff,
                            contentDescription = null,
                            tint = EtfcTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No fighters found",
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tap the '+ ADD FIGHTER' button below to create a new fighter profile.",
                            color = EtfcTextMuted,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        } else {
            items(filteredFighters, key = { it.id }) { fighter ->
                AdminFighterCardItem(
                    fighter = fighter,
                    onEdit = { onEditFighter(fighter) },
                    onDelete = { onDeleteFighter(fighter.id) }
                )
            }
        }
    }
}

@Composable
fun AdminFighterCardItem(
    fighter: Fighter,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(
            1.dp,
            if (fighter.isChampion) EtfcGold else EtfcBorderGold.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_fighter_item_${fighter.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Fighter Avatar / Photo Preview
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(EtfcSurface)
                        .border(
                            2.dp,
                            if (fighter.isChampion) EtfcGold else Color(fighter.primaryColorHex),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (!fighter.photoUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = fighter.photoUrl,
                            contentDescription = fighter.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(
                            text = fighter.name.take(2).uppercase(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = EtfcGold
                        )
                    }
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = fighter.flagEmoji,
                            fontSize = 14.sp
                        )
                        Text(
                            text = fighter.name,
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = EtfcTextPrimary
                        )
                        if (fighter.isChampion) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EtfcGold
                            ) {
                                Text(
                                    text = "🏆 CHAMPION",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        } else if (fighter.rank != null) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EtfcCardInner
                            ) {
                                Text(
                                    text = "#${fighter.rank}",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EtfcGold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }

                    if (fighter.nickname.isNotEmpty()) {
                        Text(
                            text = "\"${fighter.nickname}\"",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcGold
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = fighter.division.label,
                            fontSize = 11.sp,
                            color = EtfcTextSecondary
                        )
                        Text("•", color = EtfcTextMuted, fontSize = 10.sp)
                        Text(
                            text = "${fighter.wins}W - ${fighter.losses}L - ${fighter.draws}D (${fighter.kos} KO)",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcSuccess
                        )
                        Text("•", color = EtfcTextMuted, fontSize = 10.sp)
                        Text(
                            text = fighter.stance,
                            fontSize = 11.sp,
                            color = EtfcTextMuted
                        )
                    }
                }
            }

            if (fighter.signatureMove.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EtfcSurface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Bolt,
                            contentDescription = null,
                            tint = EtfcGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Signature: ${fighter.signatureMove}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = EtfcTextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = EtfcBorderGold.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcRedPrimary),
                    border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_delete_fighter_${fighter.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DELETE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EtfcGold,
                        contentColor = Color.Black
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_edit_fighter_${fighter.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("EDIT / PHOTO", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Fighter?", fontWeight = FontWeight.Black, color = EtfcGold) },
            text = {
                Text(
                    "Are you sure you want to delete ${fighter.name}? This will remove them from Room SQLite and update all app rosters.",
                    color = EtfcTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary)
                ) {
                    Text("DELETE", fontWeight = FontWeight.Black, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("CANCEL", color = EtfcTextSecondary)
                }
            },
            containerColor = EtfcCard,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

// =============================================================================
// TAB 1: MATCH CARDS MANAGEMENT (LAZY COLUMN WITH FULL CRUD & LOCAL POSTER PICKER)
// =============================================================================

@Composable
fun AdminMatchesManagementTab(
    matches: List<Bout>,
    fighters: List<Fighter>,
    onEditMatch: (Bout) -> Unit,
    onDeleteMatch: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_matches_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "⚔️ BOUTS & FIGHT CARDS CMS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcRedPrimary,
                        letterSpacing = 0.8.sp
                    )
                    Text(
                        text = "Create, edit and manage fight night bouts. Pair dynamic fighters from your database, set title fights, upload custom match posters via local image picker, and publish live results.",
                        fontSize = 11.sp,
                        color = EtfcTextMuted
                    )
                }
            }
        }

        if (matches.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.SportsMma,
                            contentDescription = null,
                            tint = EtfcTextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "No match cards found",
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextSecondary,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Tap '+ ADD MATCH CARD' below to schedule a new bout.",
                            color = EtfcTextMuted,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            items(matches, key = { it.id }) { bout ->
                AdminMatchCardItem(
                    bout = bout,
                    onEdit = { onEditMatch(bout) },
                    onDelete = { onDeleteMatch(bout.id) }
                )
            }
        }
    }
}

@Composable
fun AdminMatchCardItem(
    bout: Bout,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(
            1.dp,
            if (bout.isTitleFight) EtfcGold else EtfcBorderGold.copy(alpha = 0.25f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_match_item_${bout.id}")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Badges Header
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
                        color = when (bout.boutType) {
                            BoutType.MAIN_EVENT -> EtfcRedPrimary
                            BoutType.CO_MAIN -> EtfcGold
                            else -> EtfcCardInner
                        }
                    ) {
                        Text(
                            text = bout.boutType.label.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            color = if (bout.boutType == BoutType.CO_MAIN) Color.Black else Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    if (bout.isTitleFight) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcGold
                        ) {
                            Text(
                                text = "🏆 TITLE FIGHT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (bout.status) {
                        BoutStatus.LIVE -> EtfcRedPrimary
                        BoutStatus.COMPLETED -> EtfcSuccess
                        BoutStatus.UPCOMING -> EtfcCardInner
                    }
                ) {
                    Text(
                        text = when (bout.status) {
                            BoutStatus.LIVE -> "🔴 LIVE NOW"
                            BoutStatus.COMPLETED -> "🏁 COMPLETED"
                            BoutStatus.UPCOMING -> "⏳ UPCOMING"
                        },
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fighters Face-Off Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Red Corner Fighter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EtfcSurface)
                            .border(2.dp, EtfcRedPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!bout.fighterRed.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = bout.fighterRed.photoUrl,
                                contentDescription = bout.fighterRed.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = bout.fighterRed.name.take(2).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcRedPrimary
                            )
                        }
                    }

                    Column {
                        Text(
                            text = "${bout.fighterRed.flagEmoji} ${bout.fighterRed.name}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = EtfcTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "Red Corner • ${bout.fighterRed.wins}-${bout.fighterRed.losses}",
                            fontSize = 10.sp,
                            color = EtfcRedPrimary
                        )
                    }
                }

                Text(
                    text = "VS",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = EtfcGold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )

                // Blue Corner Fighter
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${bout.fighterBlue.name} ${bout.fighterBlue.flagEmoji}",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = EtfcTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = "Blue Corner • ${bout.fighterBlue.wins}-${bout.fighterBlue.losses}",
                            fontSize = 10.sp,
                            color = EtfcBlueCorner,
                            textAlign = TextAlign.End
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(EtfcSurface)
                            .border(2.dp, EtfcBlueCorner, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!bout.fighterBlue.photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = bout.fighterBlue.photoUrl,
                                contentDescription = bout.fighterBlue.name,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Text(
                                text = bout.fighterBlue.name.take(2).uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                color = EtfcBlueCorner
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Details Line
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${bout.weightClass.label} • ${bout.scheduledRounds} Rounds",
                    fontSize = 11.sp,
                    color = EtfcTextSecondary
                )
                if (bout.eventVenue.isNotEmpty()) {
                    Text("•", color = EtfcTextMuted, fontSize = 10.sp)
                    Text(
                        text = bout.eventVenue,
                        fontSize = 11.sp,
                        color = EtfcTextMuted,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (bout.winnerId != null) {
                val winningFighter = if (bout.winnerId == bout.fighterRed.id) bout.fighterRed else bout.fighterBlue
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = EtfcSuccessBg,
                    border = BorderStroke(1.dp, EtfcSuccess.copy(alpha = 0.4f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = EtfcGold,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Winner: ${winningFighter.name} via ${bout.resultMethod ?: "Decision"}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcSuccess
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = EtfcBorderGold.copy(alpha = 0.15f))
            Spacer(modifier = Modifier.height(8.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showDeleteConfirm = true },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcRedPrimary),
                    border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.4f)),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_delete_match_${bout.id}")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("DELETE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EtfcRedPrimary,
                        contentColor = Color.White
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("admin_edit_match_${bout.id}")
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("EDIT BOUT", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }
    }

    if (showDeleteConfirm) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Match Card?", fontWeight = FontWeight.Black, color = EtfcGold) },
            text = {
                Text(
                    "Are you sure you want to delete ${bout.fighterRed.name} vs ${bout.fighterBlue.name}? This will remove it from Room SQLite database.",
                    color = EtfcTextSecondary,
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary)
                ) {
                    Text("DELETE", fontWeight = FontWeight.Black, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("CANCEL", color = EtfcTextSecondary)
                }
            },
            containerColor = EtfcCard,
            shape = RoundedCornerShape(14.dp)
        )
    }
}

// =============================================================================
// DIALOG: ADD / EDIT FIGHTER WITH LOCAL FILE IMAGE PICKER
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FighterAddEditDialog(
    existingFighter: Fighter?,
    onDismiss: () -> Unit,
    onSave: (Fighter) -> Unit
) {
    val isEditMode = existingFighter != null

    var name by remember { mutableStateOf(existingFighter?.name ?: "") }
    var nickname by remember { mutableStateOf(existingFighter?.nickname ?: "") }
    var country by remember { mutableStateOf(existingFighter?.countryName ?: "Ethiopia") }
    var flag by remember { mutableStateOf(existingFighter?.flagEmoji ?: "🇪🇹") }
    var division by remember { mutableStateOf(existingFighter?.division ?: WeightClass.MIDDLEWEIGHT) }
    var stance by remember { mutableStateOf(existingFighter?.stance ?: "Orthodox") }
    var reachInches by remember { mutableStateOf(existingFighter?.reachInches?.toString() ?: "74") }
    var heightCm by remember { mutableStateOf(existingFighter?.heightCm?.toString() ?: "185") }
    var wins by remember { mutableStateOf(existingFighter?.wins?.toString() ?: "5") }
    var losses by remember { mutableStateOf(existingFighter?.losses?.toString() ?: "0") }
    var draws by remember { mutableStateOf(existingFighter?.draws?.toString() ?: "0") }
    var kos by remember { mutableStateOf(existingFighter?.kos?.toString() ?: "3") }
    var submissions by remember { mutableStateOf(existingFighter?.submissions?.toString() ?: "1") }
    var isChampion by remember { mutableStateOf(existingFighter?.isChampion ?: false) }
    var rank by remember { mutableStateOf(existingFighter?.rank?.toString() ?: "1") }
    var bio by remember { mutableStateOf(existingFighter?.bio ?: "") }
    var signatureMove by remember { mutableStateOf(existingFighter?.signatureMove ?: "") }
    var photoUriString by remember { mutableStateOf(existingFighter?.photoUrl ?: "") }

    var divisionExpanded by remember { mutableStateOf(false) }
    var stanceExpanded by remember { mutableStateOf(false) }

    // Android Local File / Gallery Picker
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        containerColor = EtfcCard,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Edit else Icons.Default.PersonAdd,
                    contentDescription = null,
                    tint = EtfcGold
                )
                Text(
                    text = if (isEditMode) "Edit Fighter Profile" else "Add New Fighter",
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Photo Picker Section
                item {
                    Card(
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = EtfcSurface),
                        border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f))
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "ATHLETE PROFILE PHOTO (LOCAL FILE)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = EtfcGold
                            )

                            // Preview Avatar
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(EtfcObsidian)
                                    .border(2.dp, EtfcGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (photoUriString.isNotEmpty()) {
                                    AsyncImage(
                                        model = photoUriString,
                                        contentDescription = "Fighter Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = EtfcTextMuted,
                                        modifier = Modifier.size(36.dp)
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Button(
                                    onClick = { imagePickerLauncher.launch("image/*") },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EtfcGold,
                                        contentColor = Color.Black
                                    ),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                    modifier = Modifier.testTag("btn_pick_fighter_photo")
                                ) {
                                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Pick Local Photo", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                if (photoUriString.isNotEmpty()) {
                                    OutlinedButton(
                                        onClick = { photoUriString = "" },
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcRedPrimary),
                                        border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.5f)),
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Clear", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }

                // Name & Nickname
                item {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Fighter Full Name *") },
                        modifier = Modifier.fillMaxWidth().testTag("input_fighter_name"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            focusedLabelColor = EtfcGold
                        )
                    )
                }

                item {
                    OutlinedTextField(
                        value = nickname,
                        onValueChange = { nickname = it },
                        label = { Text("Nickname / Alias (e.g., Demolisher)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            focusedLabelColor = EtfcGold
                        )
                    )
                }

                // Country & Flag
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = country,
                            onValueChange = { country = it },
                            label = { Text("Country") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                        OutlinedTextField(
                            value = flag,
                            onValueChange = { flag = it },
                            label = { Text("Flag Emoji") },
                            modifier = Modifier.width(90.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                    }
                }

                // Weight Division Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = divisionExpanded,
                        onExpandedChange = { divisionExpanded = !divisionExpanded }
                    ) {
                        OutlinedTextField(
                            value = division.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Weight Division") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = divisionExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = divisionExpanded,
                            onDismissRequest = { divisionExpanded = false }
                        ) {
                            WeightClass.entries.forEach { wc ->
                                DropdownMenuItem(
                                    text = { Text(wc.label) },
                                    onClick = {
                                        division = wc
                                        divisionExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Stance Dropdown & Reach / Height
                item {
                    ExposedDropdownMenuBox(
                        expanded = stanceExpanded,
                        onExpandedChange = { stanceExpanded = !stanceExpanded }
                    ) {
                        OutlinedTextField(
                            value = stance,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Fighting Stance") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stanceExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = stanceExpanded,
                            onDismissRequest = { stanceExpanded = false }
                        ) {
                            listOf("Orthodox", "Southpaw", "Switch").forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s) },
                                    onClick = {
                                        stance = s
                                        stanceExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = reachInches,
                            onValueChange = { reachInches = it },
                            label = { Text("Reach (in)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                        OutlinedTextField(
                            value = heightCm,
                            onValueChange = { heightCm = it },
                            label = { Text("Height (cm)") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                focusedLabelColor = EtfcGold
                            )
                        )
                    }
                }

                // Record Row (Wins, Losses, Draws, KOs, Subs)
                item {
                    Text(
                        text = "FIGHT RECORD & FINISHES",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = wins,
                            onValueChange = { wins = it },
                            label = { Text("W") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = losses,
                            onValueChange = { losses = it },
                            label = { Text("L") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = draws,
                            onValueChange = { draws = it },
                            label = { Text("D") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = kos,
                            onValueChange = { kos = it },
                            label = { Text("KO") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        OutlinedTextField(
                            value = submissions,
                            onValueChange = { submissions = it },
                            label = { Text("SUB") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }
                }

                // Champion Switch & Rank
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = isChampion,
                                onCheckedChange = { isChampion = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = EtfcGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Current Champion", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        if (!isChampion) {
                            OutlinedTextField(
                                value = rank,
                                onValueChange = { rank = it },
                                label = { Text("Rank #") },
                                modifier = Modifier.width(80.dp),
                                singleLine = true
                            )
                        }
                    }
                }

                // Signature Move & Bio
                item {
                    OutlinedTextField(
                        value = signatureMove,
                        onValueChange = { signatureMove = it },
                        label = { Text("Signature Technique (e.g. Habesha Spinning Hook)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = { Text("Athlete Bio & Background") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 4
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        val id = existingFighter?.id ?: "f_${name.lowercase().replace(" ", "_")}_${UUID.randomUUID().toString().take(4)}"
                        val newFighter = Fighter(
                            id = id,
                            name = name.trim(),
                            nickname = nickname.trim(),
                            countryName = country.trim(),
                            flagEmoji = flag.trim(),
                            division = division,
                            wins = wins.toIntOrNull() ?: 0,
                            losses = losses.toIntOrNull() ?: 0,
                            draws = draws.toIntOrNull() ?: 0,
                            kos = kos.toIntOrNull() ?: 0,
                            submissions = submissions.toIntOrNull() ?: 0,
                            strikeAccuracyPercent = existingFighter?.strikeAccuracyPercent ?: 65,
                            takedownDefensePercent = existingFighter?.takedownDefensePercent ?: 75,
                            reachInches = reachInches.toIntOrNull() ?: 72,
                            heightCm = heightCm.toIntOrNull() ?: 180,
                            stance = stance,
                            isChampion = isChampion,
                            rank = if (isChampion) null else (rank.toIntOrNull() ?: 1),
                            bio = bio.trim(),
                            signatureMove = signatureMove.trim(),
                            primaryColorHex = existingFighter?.primaryColorHex ?: 0xFFEF4444,
                            photoUrl = photoUriString.ifBlank { null }
                        )
                        onSave(newFighter)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EtfcGold,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("btn_save_fighter_dialog")
            ) {
                Text(if (isEditMode) "UPDATE PROFILE" else "SAVE FIGHTER", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = EtfcTextSecondary)
            }
        }
    )
}

// =============================================================================
// DIALOG: ADD / EDIT MATCH CARD WITH ROSTER SELECTION
// =============================================================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchAddEditDialog(
    existingBout: Bout?,
    availableFighters: List<Fighter>,
    onDismiss: () -> Unit,
    onSave: (Bout) -> Unit
) {
    val isEditMode = existingBout != null

    var selectedRedFighter by remember {
        mutableStateOf(
            existingBout?.fighterRed ?: availableFighters.firstOrNull() ?: Fighter(
                id = "f_temp_red",
                name = "Red Fighter",
                nickname = "",
                countryName = "Ethiopia",
                flagEmoji = "🇪🇹",
                division = WeightClass.MIDDLEWEIGHT,
                wins = 5,
                losses = 0,
                draws = 0,
                kos = 3,
                submissions = 1,
                strikeAccuracyPercent = 65,
                takedownDefensePercent = 75,
                reachInches = 74,
                heightCm = 185,
                stance = "Orthodox",
                isChampion = false,
                rank = 1,
                bio = "",
                signatureMove = "",
                primaryColorHex = 0xFFEF4444
            )
        )
    }

    var selectedBlueFighter by remember {
        mutableStateOf(
            existingBout?.fighterBlue ?: availableFighters.getOrNull(1) ?: Fighter(
                id = "f_temp_blue",
                name = "Blue Fighter",
                nickname = "",
                countryName = "Uganda",
                flagEmoji = "🇺🇬",
                division = WeightClass.MIDDLEWEIGHT,
                wins = 4,
                losses = 1,
                draws = 0,
                kos = 2,
                submissions = 1,
                strikeAccuracyPercent = 60,
                takedownDefensePercent = 70,
                reachInches = 73,
                heightCm = 183,
                stance = "Southpaw",
                isChampion = false,
                rank = 2,
                bio = "",
                signatureMove = "",
                primaryColorHex = 0xFF38BDF8
            )
        )
    }

    var eventName by remember { mutableStateOf(existingBout?.eventName ?: "ETFC 002: Adwa Fight Night") }
    var venue by remember { mutableStateOf(existingBout?.eventVenue ?: "Adwa 00 Museum Arena, Addis Ababa") }
    var weightClass by remember { mutableStateOf(existingBout?.weightClass ?: WeightClass.MIDDLEWEIGHT) }
    var boutType by remember { mutableStateOf(existingBout?.boutType ?: BoutType.MAIN_CARD) }
    var isTitleFight by remember { mutableStateOf(existingBout?.isTitleFight ?: false) }
    var scheduledRounds by remember { mutableStateOf(existingBout?.scheduledRounds?.toString() ?: "3") }
    var status by remember { mutableStateOf(existingBout?.status ?: BoutStatus.UPCOMING) }
    var resultMethod by remember { mutableStateOf(existingBout?.resultMethod ?: "") }
    var selectedWinnerRedOrBlue by remember {
        mutableStateOf(
            when {
                existingBout?.winnerId == selectedRedFighter.id -> "RED"
                existingBout?.winnerId == selectedBlueFighter.id -> "BLUE"
                else -> "NONE"
            }
        )
    }

    var redFighterExpanded by remember { mutableStateOf(false) }
    var blueFighterExpanded by remember { mutableStateOf(false) }
    var weightExpanded by remember { mutableStateOf(false) }
    var boutTypeExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth(0.95f),
        containerColor = EtfcCard,
        shape = RoundedCornerShape(16.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.SportsMma,
                    contentDescription = null,
                    tint = EtfcRedPrimary
                )
                Text(
                    text = if (isEditMode) "Edit Match Card" else "Create Match Card",
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Red Corner Fighter Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = redFighterExpanded,
                        onExpandedChange = { redFighterExpanded = !redFighterExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${selectedRedFighter.flagEmoji} ${selectedRedFighter.name} (${selectedRedFighter.division.label})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("🔴 Red Corner Fighter *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = redFighterExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_red_fighter"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcRedPrimary,
                                focusedLabelColor = EtfcRedPrimary
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = redFighterExpanded,
                            onDismissRequest = { redFighterExpanded = false }
                        ) {
                            availableFighters.forEach { fighter ->
                                DropdownMenuItem(
                                    text = { Text("${fighter.flagEmoji} ${fighter.name} (${fighter.division.label})") },
                                    onClick = {
                                        selectedRedFighter = fighter
                                        weightClass = fighter.division
                                        redFighterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Blue Corner Fighter Dropdown
                item {
                    ExposedDropdownMenuBox(
                        expanded = blueFighterExpanded,
                        onExpandedChange = { blueFighterExpanded = !blueFighterExpanded }
                    ) {
                        OutlinedTextField(
                            value = "${selectedBlueFighter.flagEmoji} ${selectedBlueFighter.name} (${selectedBlueFighter.division.label})",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("🔵 Blue Corner Fighter *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = blueFighterExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                                .testTag("select_blue_fighter"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcBlueCorner,
                                focusedLabelColor = EtfcBlueCorner
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = blueFighterExpanded,
                            onDismissRequest = { blueFighterExpanded = false }
                        ) {
                            availableFighters.forEach { fighter ->
                                DropdownMenuItem(
                                    text = { Text("${fighter.flagEmoji} ${fighter.name} (${fighter.division.label})") },
                                    onClick = {
                                        selectedBlueFighter = fighter
                                        blueFighterExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Weight Class & Bout Type
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ExposedDropdownMenuBox(
                            expanded = weightExpanded,
                            onExpandedChange = { weightExpanded = !weightExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = weightClass.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Division") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = weightExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = weightExpanded,
                                onDismissRequest = { weightExpanded = false }
                            ) {
                                WeightClass.entries.forEach { wc ->
                                    DropdownMenuItem(
                                        text = { Text(wc.label) },
                                        onClick = {
                                            weightClass = wc
                                            weightExpanded = false
                                        }
                                    )
                                }
                            }
                        }

                        ExposedDropdownMenuBox(
                            expanded = boutTypeExpanded,
                            onExpandedChange = { boutTypeExpanded = !boutTypeExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = boutType.label,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Bout Type") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = boutTypeExpanded) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor()
                            )
                            ExposedDropdownMenu(
                                expanded = boutTypeExpanded,
                                onDismissRequest = { boutTypeExpanded = false }
                            ) {
                                BoutType.entries.forEach { bt ->
                                    DropdownMenuItem(
                                        text = { Text(bt.label) },
                                        onClick = {
                                            boutType = bt
                                            if (bt == BoutType.MAIN_EVENT) {
                                                scheduledRounds = "5"
                                            }
                                            boutTypeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Title Fight & Scheduled Rounds
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Switch(
                                checked = isTitleFight,
                                onCheckedChange = {
                                    isTitleFight = it
                                    if (it) scheduledRounds = "5"
                                },
                                colors = SwitchDefaults.colors(checkedThumbColor = EtfcGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Championship Bout", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }

                        OutlinedTextField(
                            value = scheduledRounds,
                            onValueChange = { scheduledRounds = it },
                            label = { Text("Rounds") },
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                    }
                }

                // Venue & Event Name
                item {
                    OutlinedTextField(
                        value = eventName,
                        onValueChange = { eventName = it },
                        label = { Text("Event Name (e.g. ETFC 002: Adwa)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                item {
                    OutlinedTextField(
                        value = venue,
                        onValueChange = { venue = it },
                        label = { Text("Venue / Arena") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                // Match Status & Results
                item {
                    ExposedDropdownMenuBox(
                        expanded = statusExpanded,
                        onExpandedChange = { statusExpanded = !statusExpanded }
                    ) {
                        OutlinedTextField(
                            value = when (status) {
                                BoutStatus.UPCOMING -> "⏳ Upcoming"
                                BoutStatus.LIVE -> "🔴 Live Now"
                                BoutStatus.COMPLETED -> "🏁 Completed"
                            },
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Bout Status") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = statusExpanded,
                            onDismissRequest = { statusExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("⏳ Upcoming") },
                                onClick = {
                                    status = BoutStatus.UPCOMING
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🔴 Live Now") },
                                onClick = {
                                    status = BoutStatus.LIVE
                                    statusExpanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🏁 Completed") },
                                onClick = {
                                    status = BoutStatus.COMPLETED
                                    statusExpanded = false
                                }
                            )
                        }
                    }
                }

                if (status == BoutStatus.COMPLETED) {
                    item {
                        Card(
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = EtfcSurface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("DECLARE BOUT WINNER & METHOD", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = EtfcGold)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    FilterChip(
                                        selected = selectedWinnerRedOrBlue == "RED",
                                        onClick = { selectedWinnerRedOrBlue = "RED" },
                                        label = { Text("🔴 ${selectedRedFighter.name}", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = selectedWinnerRedOrBlue == "BLUE",
                                        onClick = { selectedWinnerRedOrBlue = "BLUE" },
                                        label = { Text("🔵 ${selectedBlueFighter.name}", fontSize = 11.sp) },
                                        modifier = Modifier.weight(1f)
                                    )
                                    FilterChip(
                                        selected = selectedWinnerRedOrBlue == "NONE",
                                        onClick = { selectedWinnerRedOrBlue = "NONE" },
                                        label = { Text("Draw / NC", fontSize = 11.sp) }
                                    )
                                }

                                OutlinedTextField(
                                    value = resultMethod,
                                    onValueChange = { resultMethod = it },
                                    label = { Text("Method (e.g. KO (Head Kick) R2 3:45)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val id = existingBout?.id ?: "bout_${selectedRedFighter.id}_${selectedBlueFighter.id}_${UUID.randomUUID().toString().take(4)}"
                    val winnerId = when (selectedWinnerRedOrBlue) {
                        "RED" -> selectedRedFighter.id
                        "BLUE" -> selectedBlueFighter.id
                        else -> null
                    }
                    val newBout = Bout(
                        id = id,
                        eventName = eventName.trim(),
                        eventVenue = venue.trim(),
                        boutType = boutType,
                        weightClass = weightClass,
                        matchCategory = MatchCategory.MMA,
                        matchTitle = "${weightClass.label.uppercase()} BOUT",
                        fighterRed = selectedRedFighter,
                        fighterBlue = selectedBlueFighter,
                        isTitleFight = isTitleFight,
                        scheduledRounds = scheduledRounds.toIntOrNull() ?: 3,
                        status = status,
                        winnerId = winnerId,
                        resultMethod = resultMethod.ifBlank { if (winnerId != null) "Decision" else null }
                    )
                    onSave(newBout)
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = EtfcRedPrimary,
                    contentColor = Color.White
                ),
                modifier = Modifier.testTag("btn_save_match_dialog")
            ) {
                Text(if (isEditMode) "UPDATE MATCH" else "SAVE MATCH CARD", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = EtfcTextSecondary)
            }
        }
    )
}

// =============================================================================
// TAB 2, 3, 4, 5: HERO SLIDES, VIDEOS, NEWS, GITHUB SYNC
// =============================================================================

@Composable
fun AdminHeroSlidesTab(
    slides: List<HeroSlide>,
    onDeleteSlide: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_slides_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        items(slides, key = { it.id }) { slide ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (!slide.posterImageUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = slide.posterImageUrl,
                            contentDescription = slide.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcGold
                        ) {
                            Text(
                                text = slide.tag,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(onClick = { onDeleteSlide(slide.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = slide.title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EtfcTextPrimary)
                    Text(text = slide.subtitle, fontSize = 12.sp, color = EtfcTextSecondary)
                    Text(text = slide.venueOrLocation, fontSize = 10.sp, color = EtfcTextMuted)
                }
            }
        }
    }
}

@Composable
fun AdminVideosTab(
    videos: List<PreviousFightVideo>,
    onDeleteVideo: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_videos_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        items(videos, key = { it.id }) { video ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp, 50.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(EtfcSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        if (video.thumbnailUrl.isNotEmpty()) {
                            AsyncImage(
                                model = video.thumbnailUrl,
                                contentDescription = video.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Icon(Icons.Default.PlayCircleFilled, contentDescription = null, tint = EtfcRedPrimary)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = video.title, fontWeight = FontWeight.Bold, fontSize = 13.sp, color = EtfcTextPrimary, maxLines = 2)
                        Text(text = "${video.viewsCountStr} • ${video.durationText}", fontSize = 10.sp, color = EtfcTextMuted)
                    }

                    IconButton(onClick = { onDeleteVideo(video.id) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary)
                    }
                }
            }
        }
    }
}

@Composable
fun AdminNewsTab(
    articles: List<NewsArticle>,
    onDeleteArticle: (String) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_news_list"),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        items(articles, key = { it.id }) { article ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcRedPrimary
                        ) {
                            Text(
                                text = article.category.name,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        IconButton(onClick = { onDeleteArticle(article.id) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = article.title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = EtfcTextPrimary)
                    Text(text = article.summary, fontSize = 12.sp, color = EtfcTextSecondary, maxLines = 2)
                    Text(text = "${article.author} • ${article.publishedDate}", fontSize = 10.sp, color = EtfcTextMuted)
                }
            }
        }
    }
}

@Composable
fun AdminGitHubSyncTab(
    viewModel: EtfcViewModel
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    var inputJson by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_github_sync_tab"),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 88.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🐙 GITHUB REPOSITORY CONTENT ENGINE",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = EtfcGold
                    )
                    Text(
                        text = "Export the entire SQLite database to version-controlled JSON schema, or sync updates from GitHub and remote endpoints.",
                        fontSize = 11.sp,
                        color = EtfcTextMuted
                    )

                    Button(
                        onClick = {
                            val exported = viewModel.exportCurrentDataAsGitHubJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ETFC_JSON", exported))
                            inputJson = exported
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcGold,
                            contentColor = Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("btn_export_github_json")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("EXPORT DB TO JSON & COPY TO CLIPBOARD", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "📥 IMPORT / SYNC FROM JSON PAYLOAD",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = EtfcTextPrimary
                    )

                    OutlinedTextField(
                        value = inputJson,
                        onValueChange = { inputJson = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .testTag("input_github_json"),
                        placeholder = { Text("Paste exported JSON schema here...", fontSize = 11.sp, color = EtfcTextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = EtfcGold,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary
                        )
                    )

                    Button(
                        onClick = {
                            if (inputJson.isNotBlank()) {
                                viewModel.syncFromGitHubJson(inputJson)
                            }
                        },
                        enabled = inputJson.isNotBlank() && !uiState.isSyncingGitHub,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = EtfcRedPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("btn_import_github_json")
                    ) {
                        Text(if (uiState.isSyncingGitHub) "SYNCING..." else "PARSE & IMPORT TO SQLITE", fontWeight = FontWeight.Black)
                    }

                    if (uiState.githubSyncStatusMessage != null) {
                        Text(
                            text = uiState.githubSyncStatusMessage!!,
                            fontSize = 11.sp,
                            color = EtfcGold,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

// Dialog helper implementations for Hero Slides, Videos, News
@Composable
fun AddHeroSlideDialog(
    onDismiss: () -> Unit,
    onAddSlide: (HeroSlide, Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var tag by remember { mutableStateOf("LIVE EVENT") }
    var location by remember { mutableStateOf("Adwa 00 Museum Arena, Addis Ababa") }
    var dateText by remember { mutableStateOf("SATURDAY • 8:00 PM EAT") }
    var ctaText by remember { mutableStateOf("ORDER PPV PASS") }
    var isPinned by remember { mutableStateOf(false) }
    var posterUriString by remember { mutableStateOf("") }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) posterUriString = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EtfcCard,
        title = { Text("Add Promo Slide", fontWeight = FontWeight.Black, color = EtfcGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { pickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick Local Poster", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (posterUriString.isNotEmpty()) {
                    AsyncImage(
                        model = posterUriString,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title *") }, singleLine = true)
                OutlinedTextField(value = subtitle, onValueChange = { subtitle = it }, label = { Text("Subtitle") }, singleLine = true)
                OutlinedTextField(value = tag, onValueChange = { tag = it }, label = { Text("Badge Tag") }, singleLine = true)
                OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val slide = HeroSlide(
                            id = "slide_${UUID.randomUUID().toString().take(6)}",
                            type = HeroSlideType.UPCOMING_MATCH,
                            tag = tag,
                            title = title,
                            subtitle = subtitle,
                            eventDateOrCountdown = dateText,
                            venueOrLocation = location,
                            primaryCtaText = ctaText,
                            posterImageUrl = posterUriString.ifBlank { null }
                        )
                        onAddSlide(slide, isPinned)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
            ) {
                Text("ADD SLIDE", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = EtfcTextSecondary) }
        }
    )
}

@Composable
fun AddVideoDialog(
    viewModel: EtfcViewModel,
    onDismiss: () -> Unit,
    onAddVideo: (PreviousFightVideo) -> Unit
) {
    var videoUrlOrId by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var fightersText by remember { mutableStateOf("") }
    var duration by remember { mutableStateOf("12:45") }
    var thumbnail by remember { mutableStateOf("") }
    var isFetching by remember { mutableStateOf(false) }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) thumbnail = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EtfcCard,
        title = { Text("Add Fight Video & Archive", fontWeight = FontWeight.Black, color = EtfcRedPrimary) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = videoUrlOrId,
                    onValueChange = {
                        videoUrlOrId = it
                        if (it.length > 5) {
                            isFetching = true
                            viewModel.fetchYouTubeMetadata(it) { meta ->
                                isFetching = false
                                if (meta != null) {
                                    title = meta.title
                                    thumbnail = meta.thumbnailUrl
                                }
                            }
                        }
                    },
                    label = { Text("YouTube URL or Video ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { pickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcCardInner, contentColor = EtfcGold)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Or Pick Local Thumbnail", fontSize = 11.sp)
                }

                if (thumbnail.isNotEmpty()) {
                    AsyncImage(
                        model = thumbnail,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Video Title *") }, singleLine = true)
                OutlinedTextField(value = fightersText, onValueChange = { fightersText = it }, label = { Text("Fighters (e.g. Sedo vs Jhonny)") }, singleLine = true)
                OutlinedTextField(value = duration, onValueChange = { duration = it }, label = { Text("Duration (e.g. 15:30)") }, singleLine = true)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val video = PreviousFightVideo(
                            id = "vid_${UUID.randomUUID().toString().take(6)}",
                            title = title,
                            subtitle = fightersText,
                            eventName = "ETFC 002: Adwa Fight Night",
                            category = VideoCategory.FULL_REPLAY,
                            durationText = duration,
                            durationSeconds = 720,
                            viewsCountStr = "1.5K views",
                            publishedTimeAgo = "Just now",
                            fighterRedName = fightersText,
                            fighterBlueName = "",
                            resultMethod = "KO Round 2",
                            finishRoundText = "Round 2",
                            fileSizeMb = 150,
                            thumbnailUrl = thumbnail,
                            youtubeVideoId = videoUrlOrId.takeIf { !it.contains("http") } ?: "73P6qS14uP0"
                        )
                        onAddVideo(video)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary, contentColor = Color.White)
            ) {
                Text("SAVE VIDEO", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = EtfcTextSecondary) }
        }
    )
}

@Composable
fun AddNewsDialog(
    onDismiss: () -> Unit,
    onAddNews: (NewsArticle) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var fullContent by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("ETFC Media Team") }
    var bannerUriString by remember { mutableStateOf("") }

    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) bannerUriString = uri.toString()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = EtfcCard,
        title = { Text("Add News Announcement", fontWeight = FontWeight.Black, color = EtfcGold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { pickerLauncher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
                ) {
                    Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Pick Local Banner Image", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                if (bannerUriString.isNotEmpty()) {
                    AsyncImage(
                        model = bannerUriString,
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth().height(80.dp).clip(RoundedCornerShape(6.dp)),
                        contentScale = ContentScale.Crop
                    )
                }

                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Headline Title *") }, singleLine = true)
                OutlinedTextField(value = summary, onValueChange = { summary = it }, label = { Text("Short Summary") }, maxLines = 2)
                OutlinedTextField(value = fullContent, onValueChange = { fullContent = it }, label = { Text("Full Article Content") }, maxLines = 4)
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        val article = NewsArticle(
                            id = "news_${UUID.randomUUID().toString().take(6)}",
                            title = title,
                            summary = summary,
                            fullContent = fullContent,
                            category = NewsCategory.PRESS_RELEASE,
                            author = author,
                            authorRole = "ETFC Official Press",
                            publishedDate = "TODAY",
                            readTimeMinutes = 3,
                            imageUrl = bannerUriString.ifBlank { null },
                            isFeatured = true
                        )
                        onAddNews(article)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
            ) {
                Text("PUBLISH", fontWeight = FontWeight.Black)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("CANCEL", color = EtfcTextSecondary) }
        }
    )
}
