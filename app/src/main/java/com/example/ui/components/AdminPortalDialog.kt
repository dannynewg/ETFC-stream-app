package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.models.*
import com.example.data.remote.YouTubeVideoMetadata
import com.example.ui.theme.*
import com.example.ui.viewmodel.EtfcViewModel
import java.util.UUID

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
    val dynamicMatches by viewModel.dynamicMatches.collectAsState()

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
                    border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.3f)),
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(EtfcGold.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Admin CMS",
                                    tint = EtfcGold,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Column {
                                Text(
                                    text = "ETFC ADMIN & CMS PORTAL",
                                    color = EtfcGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Content Management & SQLite Sync Engine",
                                    color = EtfcTextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier
                                .size(36.dp)
                                .testTag("btn_close_admin_portal")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = EtfcTextPrimary
                            )
                        }
                    }
                }

                // CMS Tab Row
                val tabs = listOf(
                    "🥊 Fighters",
                    "⚔️ Match Cards",
                    "🖼️ Hero Slider",
                    "🎬 Videos",
                    "📢 Alerts & News",
                    "🐙 Sync"
                )

                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = EtfcCard,
                    contentColor = EtfcGold,
                    edgePadding = 12.dp,
                    indicator = { tabPositions ->
                        if (selectedTab < tabPositions.size) {
                            TabRowDefaults.SecondaryIndicator(
                                modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                height = 3.dp,
                                color = EtfcGold
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) EtfcGold else EtfcTextSecondary
                                )
                            },
                            modifier = Modifier.testTag("admin_tab_$index")
                        )
                    }
                }

                // Tab Content Body
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    when (selectedTab) {
                        0 -> AdminFightersTab(
                            fighters = dynamicFighters,
                            onSaveFighter = { viewModel.saveFighter(it) },
                            onDeleteFighter = { viewModel.deleteFighter(it) }
                        )
                        1 -> AdminMatchCardsTab(
                            matches = dynamicMatches,
                            allFighters = dynamicFighters,
                            onSaveMatch = { bout, order -> viewModel.saveMatchCard(bout, order) },
                            onDeleteMatch = { viewModel.deleteMatchCard(it) },
                            onFetchYouTube = { url, cb -> viewModel.fetchYouTubeMetadata(url, cb) }
                        )
                        2 -> AdminHeroSlidesTab(
                            slides = dynamicSlides,
                            onSaveSlide = { viewModel.saveHeroSlide(it) },
                            onDeleteSlide = { viewModel.deleteHeroSlide(it) }
                        )
                        3 -> AdminVideosTab(
                            videos = dynamicVideos,
                            onSaveVideo = { viewModel.savePreviousVideo(it) },
                            onDeleteVideo = { viewModel.deletePreviousVideo(it) },
                            onFetchYouTube = { url, cb -> viewModel.fetchYouTubeMetadata(url, cb) }
                        )
                        4 -> AdminNewsTab(
                            articles = dynamicNews,
                            onSaveArticle = { viewModel.saveNewsAnnouncement(it) },
                            onDeleteArticle = { viewModel.deleteNewsAnnouncement(it) },
                            notificationManager = viewModel.notificationManager
                        )
                        5 -> AdminGitHubSyncTab(
                            viewModel = viewModel,
                            uiState = uiState
                        )
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 0: FIGHTERS ROSTER CMS (Full CRUD with Local File Picker)
   ========================================================================== */

@Composable
fun AdminFightersTab(
    fighters: List<Fighter>,
    onSaveFighter: (Fighter) -> Unit,
    onDeleteFighter: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingFighterId by remember { mutableStateOf<String?>(null) }

    // Form Fields
    var name by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var countryName by remember { mutableStateOf("Ethiopia") }
    var flagEmoji by remember { mutableStateOf("🇪🇹") }
    var selectedDivision by remember { mutableStateOf(WeightClass.MIDDLEWEIGHT) }
    var winsText by remember { mutableStateOf("10") }
    var lossesText by remember { mutableStateOf("1") }
    var drawsText by remember { mutableStateOf("0") }
    var kosText by remember { mutableStateOf("7") }
    var submissionsText by remember { mutableStateOf("2") }
    var reachInchesText by remember { mutableStateOf("74") }
    var heightCmText by remember { mutableStateOf("182") }
    var stance by remember { mutableStateOf("Orthodox") }
    var bio by remember { mutableStateOf("") }
    var signatureMove by remember { mutableStateOf("Overhand Right") }
    var isChampion by remember { mutableStateOf(false) }
    var rankText by remember { mutableStateOf("1") }
    var photoUrl by remember { mutableStateOf<String?>(null) }

    fun populateForEdit(f: Fighter) {
        editingFighterId = f.id
        name = f.name
        nickname = f.nickname
        countryName = f.countryName
        flagEmoji = f.flagEmoji
        selectedDivision = f.division
        winsText = f.wins.toString()
        lossesText = f.losses.toString()
        drawsText = f.draws.toString()
        kosText = f.kos.toString()
        submissionsText = f.submissions.toString()
        reachInchesText = f.reachInches.toString()
        heightCmText = f.heightCm.toString()
        stance = f.stance
        bio = f.bio
        signatureMove = f.signatureMove
        isChampion = f.isChampion
        rankText = (f.rank ?: 1).toString()
        photoUrl = f.photoUrl
        isEditing = true
    }

    fun resetForm() {
        editingFighterId = null
        name = ""
        nickname = ""
        countryName = "Ethiopia"
        flagEmoji = "🇪🇹"
        selectedDivision = WeightClass.MIDDLEWEIGHT
        winsText = "8"
        lossesText = "1"
        drawsText = "0"
        kosText = "5"
        submissionsText = "1"
        reachInchesText = "73"
        heightCmText = "180"
        stance = "Orthodox"
        bio = ""
        signatureMove = "Hook Kick"
        isChampion = false
        rankText = "2"
        photoUrl = null
        isEditing = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FIGHTERS ROSTER CMS",
                        color = EtfcGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${fighters.size} Registered Athletes in SQLite Database",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        if (isEditing) resetForm() else {
                            resetForm()
                            isEditing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) EtfcCardInner else EtfcGold,
                        contentColor = if (isEditing) EtfcTextPrimary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_toggle_add_fighter")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEditing) "CANCEL" else "ADD FIGHTER",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Add / Edit Fighter Form
        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (editingFighterId != null) "✏️ EDIT FIGHTER DETAILS" else "➕ ADD NEW FIGHTER TO ROSTER",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        // Fighter Profile Image Local File Picker
                        EtfcImagePicker(
                            selectedImageUrl = photoUrl,
                            onImageSelected = { photoUrl = it },
                            label = "Fighter Profile Picture (Local File Upload)",
                            helperText = "Pick photo from device storage or gallery for avatar & tale-of-the-tape",
                            prefix = "fighter_${name.filter { it.isLetterOrDigit() }.lowercase().ifBlank { "new" }}",
                            isCircular = true
                        )

                        // Name & Nickname
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = name,
                                onValueChange = { name = it },
                                label = { Text("Full Name *", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_fighter_name"),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = nickname,
                                onValueChange = { nickname = it },
                                label = { Text("Nickname", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("input_fighter_nickname"),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Country & Flag
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = countryName,
                                onValueChange = { countryName = it },
                                label = { Text("Country", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.4f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = flagEmoji,
                                onValueChange = { flagEmoji = it },
                                label = { Text("Flag Emoji", fontSize = 11.sp) },
                                modifier = Modifier.weight(0.6f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Weight Class Division Selector
                        Text("Weight Class / Division", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(WeightClass.entries) { wc ->
                                FilterChip(
                                    selected = selectedDivision == wc,
                                    onClick = { selectedDivision = wc },
                                    label = { Text(wc.label, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    )
                                )
                            }
                        }

                        // Record: Wins, Losses, Draws, KOs, Submissions
                        Text("Fight Record & Finishes", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            OutlinedTextField(
                                value = winsText,
                                onValueChange = { winsText = it },
                                label = { Text("Wins", fontSize = 9.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = lossesText,
                                onValueChange = { lossesText = it },
                                label = { Text("Losses", fontSize = 9.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = drawsText,
                                onValueChange = { drawsText = it },
                                label = { Text("Draws", fontSize = 9.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = kosText,
                                onValueChange = { kosText = it },
                                label = { Text("KOs", fontSize = 9.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = submissionsText,
                                onValueChange = { submissionsText = it },
                                label = { Text("Subs", fontSize = 9.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Physical Stats: Reach, Height, Stance
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = reachInchesText,
                                onValueChange = { reachInchesText = it },
                                label = { Text("Reach (in)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = heightCmText,
                                onValueChange = { heightCmText = it },
                                label = { Text("Height (cm)", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = stance,
                                onValueChange = { stance = it },
                                label = { Text("Stance", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Bio & Signature Move
                        OutlinedTextField(
                            value = signatureMove,
                            onValueChange = { signatureMove = it },
                            label = { Text("Signature Technique", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = bio,
                            onValueChange = { bio = it },
                            label = { Text("Athlete Bio / Background", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            minLines = 2,
                            maxLines = 3
                        )

                        // Championship & Rank Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Checkbox(
                                    checked = isChampion,
                                    onCheckedChange = { isChampion = it },
                                    colors = CheckboxDefaults.colors(
                                        checkedColor = EtfcGold,
                                        checkmarkColor = Color.Black
                                    )
                                )
                                Text("Division Champion 👑", color = EtfcTextPrimary, fontSize = 11.5.sp)
                            }

                            OutlinedTextField(
                                value = rankText,
                                onValueChange = { rankText = it },
                                label = { Text("Rank #", fontSize = 10.sp) },
                                modifier = Modifier.width(90.dp),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Submit Save Button
                        Button(
                            onClick = {
                                if (name.isNotBlank()) {
                                    val id = editingFighterId ?: "fighter_${UUID.randomUUID().toString().take(8)}"
                                    val newFighter = Fighter(
                                        id = id,
                                        name = name.trim(),
                                        nickname = nickname.trim(),
                                        countryName = countryName.trim(),
                                        flagEmoji = flagEmoji.trim(),
                                        division = selectedDivision,
                                        wins = winsText.toIntOrNull() ?: 0,
                                        losses = lossesText.toIntOrNull() ?: 0,
                                        draws = drawsText.toIntOrNull() ?: 0,
                                        kos = kosText.toIntOrNull() ?: 0,
                                        submissions = submissionsText.toIntOrNull() ?: 0,
                                        strikeAccuracyPercent = 65,
                                        takedownDefensePercent = 75,
                                        reachInches = reachInchesText.toIntOrNull() ?: 72,
                                        heightCm = heightCmText.toIntOrNull() ?: 180,
                                        stance = stance.trim().ifBlank { "Orthodox" },
                                        isChampion = isChampion,
                                        rank = rankText.toIntOrNull() ?: 1,
                                        bio = bio.trim().ifBlank { "ETFC Championship Contender." },
                                        signatureMove = signatureMove.trim().ifBlank { "Right Cross" },
                                        primaryColorHex = if (isChampion) 0xFFFFD700 else 0xFFEF4444,
                                        photoUrl = photoUrl
                                    )
                                    onSaveFighter(newFighter)
                                    resetForm()
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
                                .testTag("btn_save_fighter_sqlite")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (editingFighterId != null) "UPDATE FIGHTER IN SQLITE" else "SAVE FIGHTER TO SQLITE",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // List of Active Fighters
        items(fighters, key = { it.id }) { fighter ->
            FighterAdminCard(
                fighter = fighter,
                onEdit = { populateForEdit(fighter) },
                onDelete = { onDeleteFighter(fighter.id) }
            )
        }
    }
}

@Composable
fun FighterAdminCard(
    fighter: Fighter,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, if (fighter.isChampion) EtfcGold.copy(alpha = 0.5f) else EtfcBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Profile Photo / Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EtfcSurfaceVariant)
                    .border(1.5.dp, if (fighter.isChampion) EtfcGold else EtfcBorderSubtle, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (!fighter.photoUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = fighter.photoUrl,
                        contentDescription = fighter.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Text(
                        text = fighter.name.take(2).uppercase(),
                        color = EtfcGold,
                        fontWeight = FontWeight.Black,
                        fontSize = 16.sp
                    )
                }
            }

            // Info Column
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "${fighter.flagEmoji} ${fighter.name}",
                        color = EtfcTextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (fighter.isChampion) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = EtfcGold.copy(alpha = 0.2f),
                            border = BorderStroke(0.5.dp, EtfcGold)
                        ) {
                            Text(
                                text = "CHAMP",
                                color = EtfcGold,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                            )
                        }
                    }
                }

                if (fighter.nickname.isNotBlank()) {
                    Text(
                        text = "\"${fighter.nickname}\"",
                        color = EtfcGold,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "${fighter.division.label} • ${fighter.wins}W - ${fighter.losses}L (${fighter.kos} KOs) • ${fighter.stance}",
                    color = EtfcTextMuted,
                    fontSize = 10.sp
                )

                if (fighter.photoUrl?.startsWith("file://") == true) {
                    Text(
                        text = "📍 Local File Asset",
                        color = EtfcGold,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Action Buttons
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                IconButton(
                    onClick = onEdit,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Fighter",
                        tint = EtfcGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete Fighter",
                        tint = EtfcRedPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 1: FIGHT CARDS & MATCHES CMS (Full CRUD with Local File Picker)
   ========================================================================== */

@Composable
fun AdminMatchCardsTab(
    matches: List<Bout>,
    allFighters: List<Fighter>,
    onSaveMatch: (Bout, Int) -> Unit,
    onDeleteMatch: (String) -> Unit,
    onFetchYouTube: (String, (YouTubeVideoMetadata?) -> Unit) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingMatchId by remember { mutableStateOf<String?>(null) }

    // Form State
    var eventName by remember { mutableStateOf("ETFC 002: Battle of Adwa") }
    var venue by remember { mutableStateOf("Adwa 00 Museum Arena, Addis Ababa") }
    var selectedDivision by remember { mutableStateOf(WeightClass.MIDDLEWEIGHT) }
    var redFighterId by remember { mutableStateOf(allFighters.firstOrNull()?.id ?: "") }
    var blueFighterId by remember { mutableStateOf(allFighters.getOrNull(1)?.id ?: "") }
    var boutType by remember { mutableStateOf(BoutType.MAIN_EVENT) }
    var isTitleFight by remember { mutableStateOf(true) }
    var status by remember { mutableStateOf(BoutStatus.UPCOMING) }
    var winnerId by remember { mutableStateOf<String?>(null) }
    var resultMethod by remember { mutableStateOf("KO (Round 2, 3:45)") }
    var posterImageUrl by remember { mutableStateOf<String?>(null) }
    var youtubeUrl by remember { mutableStateOf("") }
    var isFetchingYouTube by remember { mutableStateOf(false) }

    fun populateForEdit(b: Bout) {
        editingMatchId = b.id
        eventName = b.eventName
        venue = b.eventVenue
        selectedDivision = b.weightClass
        redFighterId = b.fighterRed.id
        blueFighterId = b.fighterBlue.id
        boutType = b.boutType
        isTitleFight = b.isTitleFight
        status = b.status
        winnerId = b.winnerId
        resultMethod = b.resultMethod ?: ""
        posterImageUrl = b.fighterRed.photoUrl
        isEditing = true
    }

    fun resetForm() {
        editingMatchId = null
        eventName = "ETFC 002: Adwa Fight Night"
        venue = "Adwa 00 Museum Arena"
        selectedDivision = WeightClass.MIDDLEWEIGHT
        redFighterId = allFighters.firstOrNull()?.id ?: ""
        blueFighterId = allFighters.getOrNull(1)?.id ?: ""
        boutType = BoutType.MAIN_CARD
        isTitleFight = false
        status = BoutStatus.UPCOMING
        winnerId = null
        resultMethod = ""
        posterImageUrl = null
        youtubeUrl = ""
        isEditing = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "FIGHT CARDS & BOUTS CMS",
                        color = EtfcGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${matches.size} Fight Cards Scheduled in Database",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = {
                        if (isEditing) resetForm() else {
                            resetForm()
                            isEditing = true
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) EtfcCardInner else EtfcGold,
                        contentColor = if (isEditing) EtfcTextPrimary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("btn_toggle_add_match")
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isEditing) "CANCEL" else "ADD MATCH",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Form Section
        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (editingMatchId != null) "✏️ EDIT MATCH CARD" else "➕ ADD NEW MATCH CARD",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        // Match Poster Local File Picker
                        EtfcImagePicker(
                            selectedImageUrl = posterImageUrl,
                            onImageSelected = { posterImageUrl = it },
                            label = "Match Card Poster / Artwork (Local Upload)",
                            helperText = "Select event fight card artwork from device storage",
                            prefix = "match_poster"
                        )

                        // Event Name & Venue
                        OutlinedTextField(
                            value = eventName,
                            onValueChange = { eventName = it },
                            label = { Text("Event Name & Edition", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = venue,
                            onValueChange = { venue = it },
                            label = { Text("Venue & Location", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        // Fighters Selection Row
                        Text("Select Red & Blue Corner Athletes", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("🔴 RED CORNER", color = EtfcRedPrimary, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(4.dp))
                                allFighters.take(4).forEach { f ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (redFighterId == f.id) EtfcRedPrimary.copy(alpha = 0.3f) else EtfcCardInner,
                                        border = BorderStroke(1.dp, if (redFighterId == f.id) EtfcRedPrimary else EtfcBorderSubtle),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clickable { redFighterId = f.id }
                                    ) {
                                        Text(
                                            text = "${f.flagEmoji} ${f.name}",
                                            color = EtfcTextPrimary,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (redFighterId == f.id) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("🔵 BLUE CORNER", color = EtfcBlueCorner, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(4.dp))
                                allFighters.takeLast(4).forEach { f ->
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (blueFighterId == f.id) EtfcBlueCorner.copy(alpha = 0.3f) else EtfcCardInner,
                                        border = BorderStroke(1.dp, if (blueFighterId == f.id) EtfcBlueCorner else EtfcBorderSubtle),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 2.dp)
                                            .clickable { blueFighterId = f.id }
                                    ) {
                                        Text(
                                            text = "${f.flagEmoji} ${f.name}",
                                            color = EtfcTextPrimary,
                                            fontSize = 10.5.sp,
                                            fontWeight = if (blueFighterId == f.id) FontWeight.Bold else FontWeight.Normal,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Bout Type & Title Fight
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf(BoutType.MAIN_EVENT, BoutType.CO_MAIN, BoutType.MAIN_CARD).forEach { bt ->
                                    FilterChip(
                                        selected = boutType == bt,
                                        onClick = { boutType = bt },
                                        label = { Text(bt.name.replace("_", " "), fontSize = 9.sp) },
                                        colors = FilterChipDefaults.filterChipColors(
                                            selectedContainerColor = EtfcGold,
                                            selectedLabelColor = Color.Black,
                                            containerColor = EtfcCardInner,
                                            labelColor = EtfcTextSecondary
                                        )
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(
                                    checked = isTitleFight,
                                    onCheckedChange = { isTitleFight = it },
                                    colors = CheckboxDefaults.colors(checkedColor = EtfcGold)
                                )
                                Text("Title Fight 👑", color = EtfcTextPrimary, fontSize = 11.sp)
                            }
                        }

                        // Status & Result
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            BoutStatus.entries.forEach { st ->
                                FilterChip(
                                    selected = status == st,
                                    onClick = { status = st },
                                    label = { Text(st.name, fontSize = 9.5.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = if (st == BoutStatus.LIVE) EtfcLiveRed else EtfcGold,
                                        selectedLabelColor = Color.White,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    )
                                )
                            }
                        }

                        if (status == BoutStatus.COMPLETED) {
                            OutlinedTextField(
                                value = resultMethod,
                                onValueChange = { resultMethod = it },
                                label = { Text("Result Method & Finish Details", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // YouTube Video Replay Link with Auto-Fetch
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = youtubeUrl,
                                onValueChange = { youtubeUrl = it },
                                label = { Text("YouTube Video / Stream URL", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (youtubeUrl.isNotBlank()) {
                                        isFetchingYouTube = true
                                        onFetchYouTube(youtubeUrl) { meta ->
                                            isFetchingYouTube = false
                                            if (meta != null && posterImageUrl.isNullOrBlank()) {
                                                posterImageUrl = meta.maxResThumbnailUrl ?: meta.hqThumbnailUrl
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcCardInner,
                                    contentColor = EtfcGold
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                if (isFetchingYouTube) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = EtfcGold)
                                } else {
                                    Text("FETCH", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Submit Save Button
                        Button(
                            onClick = {
                                val redF = allFighters.firstOrNull { it.id == redFighterId } ?: allFighters.first()
                                val blueF = allFighters.firstOrNull { it.id == blueFighterId } ?: allFighters.last()
                                val id = editingMatchId ?: "match_${UUID.randomUUID().toString().take(8)}"

                                val bout = Bout(
                                    id = id,
                                    eventName = eventName.trim(),
                                    eventVenue = venue.trim(),
                                    boutType = boutType,
                                    weightClass = selectedDivision,
                                    matchCategory = MatchCategory.MMA,
                                    matchTitle = if (isTitleFight) "${selectedDivision.label.uppercase()} CHAMPIONSHIP" else "${selectedDivision.label.uppercase()} BOUT",
                                    fighterRed = redF.copy(photoUrl = posterImageUrl ?: redF.photoUrl),
                                    fighterBlue = blueF,
                                    isTitleFight = isTitleFight,
                                    scheduledRounds = if (isTitleFight || boutType == BoutType.MAIN_EVENT) 5 else 3,
                                    status = status,
                                    liveRound = 1,
                                    liveClockSeconds = 300,
                                    winnerId = winnerId,
                                    resultMethod = resultMethod.ifBlank { null }
                                )
                                onSaveMatch(bout, 0)
                                resetForm()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .testTag("btn_save_match_sqlite")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (editingMatchId != null) "UPDATE MATCH CARD IN SQLITE" else "SAVE MATCH CARD TO SQLITE",
                                fontWeight = FontWeight.Black,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        // List of Active Match Cards
        items(matches, key = { it.id }) { match ->
            MatchAdminCard(
                bout = match,
                onEdit = { populateForEdit(match) },
                onDelete = { onDeleteMatch(match.id) }
            )
        }
    }
}

@Composable
fun MatchAdminCard(
    bout: Bout,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        border = BorderStroke(1.dp, if (bout.isTitleFight) EtfcGold.copy(alpha = 0.6f) else EtfcBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (bout.boutType) {
                        BoutType.MAIN_EVENT -> EtfcGold.copy(alpha = 0.2f)
                        BoutType.CO_MAIN -> EtfcRedPrimary.copy(alpha = 0.2f)
                        else -> EtfcSurfaceVariant
                    },
                    border = BorderStroke(0.5.dp, if (bout.boutType == BoutType.MAIN_EVENT) EtfcGold else EtfcBorderSubtle)
                ) {
                    Text(
                        text = bout.boutType.name.replace("_", " "),
                        color = if (bout.boutType == BoutType.MAIN_EVENT) EtfcGold else EtfcTextPrimary,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (bout.status) {
                        BoutStatus.LIVE -> EtfcLiveRed
                        BoutStatus.COMPLETED -> EtfcSurfaceVariant
                        else -> EtfcCardInner
                    }
                ) {
                    Text(
                        text = bout.status.name,
                        color = Color.White,
                        fontSize = 8.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            // Matchup Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${bout.fighterRed.flagEmoji} ${bout.fighterRed.name}",
                        color = EtfcRedPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "vs",
                        color = EtfcTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${bout.fighterBlue.flagEmoji} ${bout.fighterBlue.name}",
                        color = EtfcBlueCorner,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = bout.weightClass.label,
                        color = EtfcTextSecondary,
                        fontSize = 10.sp
                    )
                    Text(
                        text = bout.eventName,
                        color = EtfcTextMuted,
                        fontSize = 9.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        IconButton(onClick = onEdit, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Match",
                                tint = EtfcGold,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                        IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Match",
                                tint = EtfcRedPrimary,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 2: HERO SLIDER POSTERS CMS (Local File Picker)
   ========================================================================== */

@Composable
fun AdminHeroSlidesTab(
    slides: List<HeroSlide>,
    onSaveSlide: (HeroSlide) -> Unit,
    onDeleteSlide: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingSlideId by remember { mutableStateOf<String?>(null) }

    var tag by remember { mutableStateOf("MAIN EVENT") }
    var title by remember { mutableStateOf("SEDO vs JHONNY") }
    var subtitle by remember { mutableStateOf("ETFC Middleweight Championship Unification") }
    var ctaText by remember { mutableStateOf("WATCH LIVE") }
    var posterImageUrl by remember { mutableStateOf<String?>(null) }
    var redFighterName by remember { mutableStateOf("Sedo Demolisher") }
    var blueFighterName by remember { mutableStateOf("Jhonny El Fuego") }

    fun populate(s: HeroSlide) {
        editingSlideId = s.id
        tag = s.tag
        title = s.title
        subtitle = s.subtitle
        ctaText = s.primaryCtaText
        posterImageUrl = s.posterImageUrl
        redFighterName = s.redFighterName ?: ""
        blueFighterName = s.blueFighterName ?: ""
        isEditing = true
    }

    fun reset() {
        editingSlideId = null
        tag = "MAIN EVENT"
        title = "ADWA CHAMPIONSHIP"
        subtitle = "Historic Title Unification Fight Night"
        ctaText = "WATCH LIVE"
        posterImageUrl = null
        isEditing = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "HERO POSTER SLIDER CMS",
                        color = EtfcGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${slides.size} Live Posters Active in Carousel",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { if (isEditing) reset() else isEditing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) EtfcCardInner else EtfcGold,
                        contentColor = if (isEditing) EtfcTextPrimary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isEditing) "CANCEL" else "ADD POSTER", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (editingSlideId != null) "✏️ EDIT HERO POSTER" else "➕ ADD NEW HERO POSTER",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        // Poster Local File Picker
                        EtfcImagePicker(
                            selectedImageUrl = posterImageUrl,
                            onImageSelected = { posterImageUrl = it },
                            label = "Hero Banner Poster (Local File Upload)",
                            helperText = "Select high-resolution 16:9 or 21:9 fight poster from device storage",
                            prefix = "hero_poster"
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = tag,
                                onValueChange = { tag = it },
                                label = { Text("Badge Tag", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                            OutlinedTextField(
                                value = ctaText,
                                onValueChange = { ctaText = it },
                                label = { Text("CTA Button Text", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Headline Title", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Subtitle / Fight Description", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        Button(
                            onClick = {
                                val slide = HeroSlide(
                                    id = editingSlideId ?: "slide_${UUID.randomUUID().toString().take(8)}",
                                    type = HeroSlideType.MATCH_PROMO_POSTER,
                                    tag = tag.trim(),
                                    title = title.trim(),
                                    subtitle = subtitle.trim(),
                                    eventDateOrCountdown = "LIVE NOW",
                                    venueOrLocation = "Adwa 00 Museum Arena",
                                    primaryCtaText = ctaText.trim(),
                                    redFighterName = redFighterName,
                                    blueFighterName = blueFighterName,
                                    posterImageUrl = posterImageUrl
                                )
                                onSaveSlide(slide)
                                reset()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SAVE HERO POSTER TO SQLITE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(slides, key = { it.id }) { slide ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp, 44.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        if (!slide.posterImageUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = slide.posterImageUrl,
                                contentDescription = slide.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            Icon(Icons.Default.Image, contentDescription = null, tint = EtfcTextMuted)
                        }
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = slide.title,
                            color = EtfcGold,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = slide.subtitle,
                            color = EtfcTextMuted,
                            fontSize = 10.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    IconButton(onClick = { populate(slide) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EtfcGold, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = { onDeleteSlide(slide.id) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 3: VIDEO VAULT & YOUTUBE CMS (oEmbed Auto-Fetch & Local Thumbnail)
   ========================================================================== */

@Composable
fun AdminVideosTab(
    videos: List<PreviousFightVideo>,
    onSaveVideo: (PreviousFightVideo) -> Unit,
    onDeleteVideo: (String) -> Unit,
    onFetchYouTube: (String, (YouTubeVideoMetadata?) -> Unit) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingVideoId by remember { mutableStateOf<String?>(null) }

    var youtubeUrl by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var subtitle by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("ETFC 002: Battle of Adwa") }
    var category by remember { mutableStateOf(VideoCategory.FULL_REPLAY) }
    var durationText by remember { mutableStateOf("12:30") }
    var fighterRed by remember { mutableStateOf("Sedo Demolisher") }
    var fighterBlue by remember { mutableStateOf("Jhonny El Fuego") }
    var resultMethod by remember { mutableStateOf("KO (Right Hook)") }
    var customThumbnailUrl by remember { mutableStateOf<String?>(null) }
    var isFetching by remember { mutableStateOf(false) }

    fun populate(v: PreviousFightVideo) {
        editingVideoId = v.id
        youtubeUrl = v.youtubeVideoId ?: ""
        title = v.title
        subtitle = v.subtitle
        eventName = v.eventName
        category = v.category
        durationText = v.durationText
        fighterRed = v.fighterRedName
        fighterBlue = v.fighterBlueName
        resultMethod = v.resultMethod
        customThumbnailUrl = v.thumbnailUrl
        isEditing = true
    }

    fun reset() {
        editingVideoId = null
        youtubeUrl = ""
        title = ""
        subtitle = ""
        eventName = "ETFC 002: Adwa Fight Night"
        category = VideoCategory.FULL_REPLAY
        durationText = "14:10"
        fighterRed = "Sedo Demolisher"
        fighterBlue = "Jhonny El Fuego"
        resultMethod = "KO (Overhand Right)"
        customThumbnailUrl = null
        isEditing = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "VIDEO VAULT & YOUTUBE CMS",
                        color = EtfcGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${videos.size} Match Replays & Highlights in SQLite Room",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { if (isEditing) reset() else isEditing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) EtfcCardInner else EtfcGold,
                        contentColor = if (isEditing) EtfcTextPrimary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isEditing) "CANCEL" else "ADD VIDEO", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (editingVideoId != null) "✏️ EDIT FIGHT VIDEO" else "➕ ADD VIDEO REPLAY / HIGHLIGHT",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        // YouTube URL + Auto Fetch Metadata button
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = youtubeUrl,
                                onValueChange = { youtubeUrl = it },
                                label = { Text("YouTube URL or Video ID", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )

                            Button(
                                onClick = {
                                    if (youtubeUrl.isNotBlank()) {
                                        isFetching = true
                                        onFetchYouTube(youtubeUrl) { meta ->
                                            isFetching = false
                                            if (meta != null) {
                                                if (title.isBlank()) title = meta.title
                                                if (customThumbnailUrl.isNullOrBlank()) {
                                                    customThumbnailUrl = meta.maxResThumbnailUrl ?: meta.hqThumbnailUrl
                                                }
                                            }
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = EtfcGold,
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(50.dp)
                            ) {
                                if (isFetching) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black)
                                } else {
                                    Text("AUTO FETCH", fontSize = 9.5.sp, fontWeight = FontWeight.Black)
                                }
                            }
                        }

                        // Local File Picker for Custom Thumbnail Override
                        EtfcImagePicker(
                            selectedImageUrl = customThumbnailUrl,
                            onImageSelected = { customThumbnailUrl = it },
                            label = "Custom Video Thumbnail (Local Upload / Override)",
                            helperText = "Select custom local image or keep YouTube fetched thumbnail",
                            prefix = "video_thumb"
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Video Title", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = subtitle,
                            onValueChange = { subtitle = it },
                            label = { Text("Subtitle / Matchup Headline", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = eventName,
                                onValueChange = { eventName = it },
                                label = { Text("Event Name", fontSize = 11.sp) },
                                modifier = Modifier.weight(1.2f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )

                            OutlinedTextField(
                                value = durationText,
                                onValueChange = { durationText = it },
                                label = { Text("Duration", fontSize = 11.sp) },
                                modifier = Modifier.weight(0.8f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        // Category Selector
                        Text("Video Category", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(VideoCategory.entries) { cat ->
                                FilterChip(
                                    selected = category == cat,
                                    onClick = { category = cat },
                                    label = { Text(cat.displayName, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = EtfcGold,
                                        selectedLabelColor = Color.Black,
                                        containerColor = EtfcCardInner,
                                        labelColor = EtfcTextSecondary
                                    )
                                )
                            }
                        }

                        Button(
                            onClick = {
                                val vid = PreviousFightVideo(
                                    id = editingVideoId ?: "vid_${UUID.randomUUID().toString().take(8)}",
                                    title = title.trim().ifBlank { "ETFC Match Replay" },
                                    subtitle = subtitle.trim().ifBlank { "Ethiopian Top Fight Championship" },
                                    eventName = eventName.trim(),
                                    category = category,
                                    durationText = durationText.trim(),
                                    durationSeconds = 720,
                                    viewsCountStr = "12.4K views",
                                    publishedTimeAgo = "Just now",
                                    fighterRedName = fighterRed,
                                    fighterBlueName = fighterBlue,
                                    resultMethod = resultMethod,
                                    finishRoundText = "Round 2",
                                    fileSizeMb = 145,
                                    resolutionLabel = "1080p FHD",
                                    isTitleFight = true,
                                    contentTier = ContentTier.FREE_PRELIMS,
                                    likesCount = 340,
                                    thumbnailUrl = customThumbnailUrl ?: "https://img.youtube.com/vi/dQw4w9WgXcQ/hqdefault.jpg",
                                    youtubeVideoId = youtubeUrl.ifBlank { null }
                                )
                                onSaveVideo(vid)
                                reset()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SAVE VIDEO TO SQLITE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(videos, key = { it.id }) { video ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(76.dp, 48.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color.Black),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = video.title,
                            color = EtfcTextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${video.category.displayName} • ${video.durationText}",
                            color = EtfcGold,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(onClick = { populate(video) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EtfcGold, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = { onDeleteVideo(video.id) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 4: ALERTS & NEWS CMS (Local File Picker & Notification Engine)
   ========================================================================== */

@Composable
fun AdminNewsTab(
    articles: List<NewsArticle>,
    onSaveArticle: (NewsArticle) -> Unit,
    onDeleteArticle: (String) -> Unit,
    notificationManager: com.example.notifications.EtfcNotificationManager
) {
    var isEditing by remember { mutableStateOf(false) }
    var editingArticleId by remember { mutableStateOf<String?>(null) }

    var title by remember { mutableStateOf("") }
    var summary by remember { mutableStateOf("") }
    var fullContent by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(NewsCategory.PRESS_RELEASE) }
    var author by remember { mutableStateOf("ETFC Media Desk") }
    var isFeatured by remember { mutableStateOf(true) }
    var imageUrl by remember { mutableStateOf<String?>(null) }

    fun populate(a: NewsArticle) {
        editingArticleId = a.id
        title = a.title
        summary = a.summary
        fullContent = a.fullContent
        category = a.category
        author = a.author
        isFeatured = a.isFeatured
        imageUrl = a.imageUrl
        isEditing = true
    }

    fun reset() {
        editingArticleId = null
        title = ""
        summary = ""
        fullContent = ""
        category = NewsCategory.PRESS_RELEASE
        author = "ETFC Media Desk"
        isFeatured = true
        imageUrl = null
        isEditing = false
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ALERTS & NEWS CMS",
                        color = EtfcGold,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "${articles.size} Published Press Releases & Fight Bulletins",
                        color = EtfcTextMuted,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = { if (isEditing) reset() else isEditing = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isEditing) EtfcCardInner else EtfcGold,
                        contentColor = if (isEditing) EtfcTextPrimary else Color.Black
                    ),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isEditing) Icons.Default.Close else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isEditing) "CANCEL" else "NEW ARTICLE", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Push Notifications Dispatcher
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                border = BorderStroke(1.dp, EtfcBorderGold.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp))
                        Text("DEVICE NOTIFICATION DISPATCHER", color = EtfcGold, fontSize = 11.5.sp, fontWeight = FontWeight.Black)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                notificationManager.sendBoutStartingNotification(
                                    fighterRed = "Sedo Demolisher",
                                    fighterBlue = "Jhonny El Fuego",
                                    matchTitle = "ETFC 002: Battle of Adwa",
                                    boutId = "bout_1"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("🥊 Bout Start Alert", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = {
                                notificationManager.sendKoFinishNotification(
                                    winner = "Sedo Demolisher",
                                    loser = "Jhonny El Fuego",
                                    method = "KO (Overhand Right, R2 2:40)",
                                    round = 2,
                                    time = "2:40"
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("⚡ KO Alert", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        if (isEditing) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard),
                    border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.6f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = if (editingArticleId != null) "✏️ EDIT ANNOUNCEMENT" else "➕ PUBLISH NEW ANNOUNCEMENT",
                            color = EtfcGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )

                        // News Banner Local File Picker
                        EtfcImagePicker(
                            selectedImageUrl = imageUrl,
                            onImageSelected = { imageUrl = it },
                            label = "News Banner Artwork (Local File Upload)",
                            helperText = "Select announcement headline artwork from device storage",
                            prefix = "news_banner"
                        )

                        OutlinedTextField(
                            value = title,
                            onValueChange = { title = it },
                            label = { Text("Headline Title", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = summary,
                            onValueChange = { summary = it },
                            label = { Text("Executive Summary / Lead", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = fullContent,
                            onValueChange = { fullContent = it },
                            label = { Text("Full Article Body", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth(),
                            colors = getAdminTextFieldColors(),
                            minLines = 3,
                            maxLines = 5
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = author,
                                onValueChange = { author = it },
                                label = { Text("Author / Source", fontSize = 11.sp) },
                                modifier = Modifier.weight(1f),
                                colors = getAdminTextFieldColors(),
                                singleLine = true
                            )
                        }

                        Button(
                            onClick = {
                                val art = NewsArticle(
                                    id = editingArticleId ?: "news_${UUID.randomUUID().toString().take(8)}",
                                    title = title.trim().ifBlank { "ETFC Official Bulletin" },
                                    summary = summary.trim().ifBlank { "Official statement from ETFC Executive Board." },
                                    fullContent = fullContent.trim().ifBlank { summary },
                                    category = category,
                                    author = author.trim(),
                                    authorRole = "ETFC Desk",
                                    publishedDate = "October 2026",
                                    readTimeMinutes = 3,
                                    isFeatured = isFeatured,
                                    tags = listOf("ETFC002", "MMA", "Championship"),
                                    relatedFighterNames = emptyList(),
                                    imageUrl = imageUrl
                                )
                                onSaveArticle(art)
                                reset()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("PUBLISH TO SQLITE & SYNC", fontWeight = FontWeight.Black, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        items(articles, key = { it.id }) { article ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = article.title,
                            color = EtfcTextPrimary,
                            fontSize = 12.5.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${article.category.displayName} • by ${article.author}",
                            color = EtfcGold,
                            fontSize = 10.sp
                        )
                    }

                    IconButton(onClick = { populate(article) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = EtfcGold, modifier = Modifier.size(15.dp))
                    }
                    IconButton(onClick = { onDeleteArticle(article.id) }, modifier = Modifier.size(30.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = EtfcRedPrimary, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

/* ==========================================================================
   TAB 5: GITHUB REMOTE & SQLITE SYNC
   ========================================================================== */

@Composable
fun AdminGitHubSyncTab(
    viewModel: EtfcViewModel,
    uiState: com.example.ui.viewmodel.EtfcUiState
) {
    val context = LocalContext.current
    var customUrl by remember { mutableStateOf("https://raw.githubusercontent.com/etfc-official/etfc-app-config/main/etfc_content_sync.json") }
    var jsonPayloadInput by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "GITHUB REMOTE & SQLITE SYNC ENGINE",
                color = EtfcGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Bi-directional JSON sync between GitHub repository and Room database",
                color = EtfcTextMuted,
                fontSize = 11.sp
            )
        }

        if (uiState.githubSyncStatusMessage != null) {
            item {
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = EtfcSurfaceVariant),
                    border = BorderStroke(1.dp, EtfcGold)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = uiState.githubSyncStatusMessage,
                            color = EtfcTextPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.dismissGitHubSyncStatus() }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = EtfcTextMuted)
                        }
                    }
                }
            }
        }

        // Fetch Remote URL Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("🌐 1. Fetch & Upsert from GitHub Raw URL", color = EtfcGold, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)

                    OutlinedTextField(
                        value = customUrl,
                        onValueChange = { customUrl = it },
                        label = { Text("Raw GitHub JSON URL", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = getAdminTextFieldColors(),
                        singleLine = true
                    )

                    Button(
                        onClick = { viewModel.fetchAndSyncFromRemoteUrl(customUrl) },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.CloudDownload, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("FETCH & SYNC FROM GITHUB", fontWeight = FontWeight.Black, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Export Database to JSON
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorderSubtle)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("📦 2. Export Current SQLite Database as JSON", color = EtfcGold, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)

                    Button(
                        onClick = {
                            val exported = viewModel.exportCurrentDataAsGitHubJson()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("ETFC Export", exported))
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcCardInner, contentColor = EtfcGold),
                        border = BorderStroke(1.dp, EtfcGold),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("EXPORT & COPY JSON PAYLOAD", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }
        }

        // Reset to Defaults
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text("⚠️ 3. Factory Reset Content", color = EtfcRedPrimary, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)

                    Button(
                        onClick = { viewModel.resetDynamicDataToDefaults() },
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary.copy(alpha = 0.2f), contentColor = EtfcRedPrimary),
                        border = BorderStroke(1.dp, EtfcRedPrimary),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("RESET ALL TABLES TO DEFAULT", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun getAdminTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EtfcGold,
    unfocusedBorderColor = EtfcBorderSubtle,
    focusedTextColor = EtfcTextPrimary,
    unfocusedTextColor = EtfcTextPrimary,
    cursorColor = EtfcGold,
    focusedLabelColor = EtfcGold,
    unfocusedLabelColor = EtfcTextMuted,
    focusedContainerColor = EtfcCardInner,
    unfocusedContainerColor = EtfcCardInner
)
