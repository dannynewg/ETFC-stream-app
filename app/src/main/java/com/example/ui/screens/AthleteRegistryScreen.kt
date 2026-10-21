package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import coil.compose.AsyncImage
import com.example.data.models.RegisteredAthlete
import com.example.data.models.defaultEthiopianAthletes
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSuccess
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary
import com.example.ui.util.ImageResolver
import com.example.ui.viewmodel.EtfcViewModel

enum class RegistryTab {
    REGISTRATION_FORM,
    ACTIVE_ROSTER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AthleteRegistryScreen(
    viewModel: EtfcViewModel,
    initialTab: Int = 0,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveAthletes by viewModel.registeredAthletes.collectAsState()
    var currentTab by remember { mutableStateOf(if (initialTab == 1) RegistryTab.ACTIVE_ROSTER else RegistryTab.REGISTRATION_FORM) }
    var submittedAthleteForConfirmation by remember { mutableStateOf<RegisteredAthlete?>(null) }
    var selectedAthleteForDetail by remember { mutableStateOf<RegisteredAthlete?>(null) }
    var showSuccessToast by remember { mutableStateOf<String?>(null) }

    // Form fields state
    var fullName by remember { mutableStateOf("") }
    var amharicName by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("Male") }
    var dob by remember { mutableStateOf("1998-05-12") }
    var region by remember { mutableStateOf("Addis Ababa") }
    var phoneNumber by remember { mutableStateOf("+251 911 ") }
    var nationalId by remember { mutableStateOf("") }

    var primarySport by remember { mutableStateOf("Mixed Martial Arts (MMA)") }
    var competitionLevel by remember { mutableStateOf("Semi-Pro / Showcase") }
    var weightClass by remember { mutableStateOf("Middleweight (84 kg)") }
    var stance by remember { mutableStateOf("Orthodox") }
    var clubAffiliation by remember { mutableStateOf("") }
    var coachName by remember { mutableStateOf("") }
    var coachPhone by remember { mutableStateOf("") }

    var proWins by remember { mutableIntStateOf(0) }
    var proLosses by remember { mutableIntStateOf(0) }
    var proDraws by remember { mutableIntStateOf(0) }
    var proFinishes by remember { mutableIntStateOf(0) }
    var amateurRecord by remember { mutableStateOf("0-0-0") }
    var heightCm by remember { mutableIntStateOf(182) }
    var reachCm by remember { mutableIntStateOf(185) }

    var notableTitles by remember { mutableStateOf("") }
    var beltRank by remember { mutableStateOf("") }
    var licenseNo by remember { mutableStateOf("") }

    var lastOpponent by remember { mutableStateOf("") }
    var lastEvent by remember { mutableStateOf("") }
    var lastResult by remember { mutableStateOf("") }

    var footageUrl by remember { mutableStateOf("") }
    var medicalStatus by remember { mutableStateOf("Fit & Medically Cleared") }
    var consentChecked by remember { mutableStateOf(false) }

    var photoUriString by remember { mutableStateOf<String?>(null) }

    // Photo picker launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUriString = uri.toString()
            showSuccessToast = "Profile photo selected!"
        }
    }

    // Function to populate sample demo data
    fun fillDemoData() {
        fullName = "Yonatan Girma"
        amharicName = "ዮናታን ግርማ"
        nickname = "Johnny Jujitsu"
        gender = "Male"
        dob = "1997-04-12"
        region = "Addis Ababa"
        phoneNumber = "+251 911 234 567"
        nationalId = "ET-F-8892019"
        primarySport = "Mixed Martial Arts (MMA)"
        competitionLevel = "Semi-Pro / Showcase"
        weightClass = "Middleweight (84 kg)"
        stance = "Orthodox"
        clubAffiliation = "Sweatbox MMA / Independent"
        coachName = "Coach Robel"
        coachPhone = "+251 912 345 678"
        proWins = 2
        proLosses = 0
        proDraws = 0
        proFinishes = 2
        amateurRecord = "6-1-0"
        heightCm = 182
        reachCm = 185
        notableTitles = "ETFC 002 (Adwa Fight Night) Main Event Winner; ETFC 001 Showcase Victor"
        beltRank = "BJJ Purple Belt"
        licenseNo = "ETFC-MMA-2024-002"
        lastOpponent = "Sedo Mohammed"
        lastEvent = "ETFC 002 Adwa Fight Night"
        lastResult = "Win via Technical Submission (R2)"
        footageUrl = "https://youtu.be/AbhialeWe7U"
        medicalStatus = "Fit & Medically Cleared"
        consentChecked = true
        photoUriString = "android.resource://com.example/drawable/profile_jhonny"
        showSuccessToast = "Loaded demo data for 'Yonatan Girma'!"
    }

    // Reset Form
    fun clearForm() {
        fullName = ""
        amharicName = ""
        nickname = ""
        gender = "Male"
        dob = "1998-01-01"
        region = "Addis Ababa"
        phoneNumber = "+251 9"
        nationalId = ""
        primarySport = "Mixed Martial Arts (MMA)"
        competitionLevel = "Semi-Pro / Showcase"
        weightClass = "Middleweight (84 kg)"
        stance = "Orthodox"
        clubAffiliation = ""
        coachName = ""
        coachPhone = ""
        proWins = 0
        proLosses = 0
        proDraws = 0
        proFinishes = 0
        amateurRecord = "0-0-0"
        heightCm = 180
        reachCm = 180
        notableTitles = ""
        beltRank = ""
        licenseNo = ""
        lastOpponent = ""
        lastEvent = ""
        lastResult = ""
        footageUrl = ""
        medicalStatus = "Fit & Medically Cleared"
        consentChecked = false
        photoUriString = null
    }

    // Submit handler: registers athlete for ETFC vetting and league placement
    fun handleSubmit() {
        if (fullName.isBlank()) {
            showSuccessToast = "Please enter athlete full name."
            return
        }
        if (!consentChecked) {
            showSuccessToast = "Please accept the athlete declaration."
            return
        }

        val newAthlete = RegisteredAthlete(
            athleteId = "ETH-ETFC-${String.format("%03d", liveAthletes.size + 1)}",
            fullName = fullName.trim(),
            amharicName = amharicName.trim(),
            nickname = nickname.trim(),
            gender = gender,
            dob = dob,
            region = region,
            phoneNumber = phoneNumber.trim(),
            nationalId = nationalId.trim(),
            primarySport = primarySport,
            competitionLevel = competitionLevel,
            weightClass = weightClass.trim(),
            stance = stance,
            clubAffiliation = clubAffiliation.ifBlank { "Independent" }.trim(),
            coachName = coachName.trim(),
            coachPhone = coachPhone.trim(),
            proWins = proWins,
            proLosses = proLosses,
            proDraws = proDraws,
            proFinishes = proFinishes,
            amateurRecord = amateurRecord.trim(),
            heightCm = heightCm,
            reachCm = reachCm,
            notableTitles = notableTitles.ifBlank { "Registered Contender" }.trim(),
            beltRank = beltRank.trim(),
            licenseNo = licenseNo.ifBlank { "ETFC-REG-${System.currentTimeMillis() % 100000}" }.trim(),
            lastOpponent = lastOpponent.trim(),
            lastEvent = lastEvent.trim(),
            lastResult = lastResult.trim(),
            footageUrl = footageUrl.trim(),
            medicalStatus = medicalStatus,
            profilePhotoUri = photoUriString,
            status = "Vetting In Progress",
            leagueAssignment = "Pending Credential Review"
        )

        // Submit to Firestore in real-time
        viewModel.submitAthleteRegistration(newAthlete)

        // Save to SQLite database so the athlete appears in matchmaking records
        viewModel.saveFighter(newAthlete.toFighter())

        // Show official vetting confirmation modal
        submittedAthleteForConfirmation = newAthlete
        showSuccessToast = "Application submitted for vetting! ETFC team will assign your fight league."
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(EtfcObsidian)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // =========================================================================
            // TOP HEADER BAR
            // =========================================================================
            Surface(
                color = EtfcSurface,
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            IconButton(
                                onClick = onDismiss,
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(EtfcCardInner, CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = EtfcGold
                                )
                            }

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "ETFC",
                                        color = EtfcGold,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 18.sp,
                                        letterSpacing = 1.sp
                                    )
                                    Surface(
                                        color = EtfcGold.copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(4.dp),
                                        border = BorderStroke(0.5.dp, EtfcGold)
                                    ) {
                                        Text(
                                            text = "ATHLETE PORTAL",
                                            color = EtfcGold,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Black,
                                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "Ethiopian Combat Athlete Registration & League Placement",
                                    color = EtfcTextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tab Navigation Switcher
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(EtfcCardInner, RoundedCornerShape(10.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Registration Form Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentTab = RegistryTab.REGISTRATION_FORM },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentTab == RegistryTab.REGISTRATION_FORM) EtfcGold else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.HowToReg,
                                    contentDescription = null,
                                    tint = if (currentTab == RegistryTab.REGISTRATION_FORM) Color.Black else EtfcTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Fighter Registration",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (currentTab == RegistryTab.REGISTRATION_FORM) Color.Black else EtfcTextSecondary
                                )
                            }
                        }

                        // Active Roster Tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { currentTab = RegistryTab.ACTIVE_ROSTER },
                            shape = RoundedCornerShape(8.dp),
                            color = if (currentTab == RegistryTab.ACTIVE_ROSTER) EtfcGold else Color.Transparent
                        ) {
                            Row(
                                modifier = Modifier.padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.People,
                                    contentDescription = null,
                                    tint = if (currentTab == RegistryTab.ACTIVE_ROSTER) Color.Black else EtfcTextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Active Roster",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (currentTab == RegistryTab.ACTIVE_ROSTER) Color.Black else EtfcTextSecondary
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (currentTab == RegistryTab.ACTIVE_ROSTER) Color.Black.copy(alpha = 0.2f) else EtfcGold.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "${liveAthletes.size}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (currentTab == RegistryTab.ACTIVE_ROSTER) Color.Black else EtfcGold,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // =========================================================================
            // TAB CONTENT
            // =========================================================================
            when (currentTab) {
                RegistryTab.REGISTRATION_FORM -> {
                    RegistrationFormTab(
                        fullName = fullName,
                        onFullNameChange = { fullName = it },
                        amharicName = amharicName,
                        onAmharicNameChange = { amharicName = it },
                        nickname = nickname,
                        onNicknameChange = { nickname = it },
                        gender = gender,
                        onGenderChange = { gender = it },
                        dob = dob,
                        onDobChange = { dob = it },
                        region = region,
                        onRegionChange = { region = it },
                        phoneNumber = phoneNumber,
                        onPhoneNumberChange = { phoneNumber = it },
                        nationalId = nationalId,
                        onNationalIdChange = { nationalId = it },
                        primarySport = primarySport,
                        onPrimarySportChange = { primarySport = it },
                        competitionLevel = competitionLevel,
                        onCompetitionLevelChange = { competitionLevel = it },
                        weightClass = weightClass,
                        onWeightClassChange = { weightClass = it },
                        stance = stance,
                        onStanceChange = { stance = it },
                        clubAffiliation = clubAffiliation,
                        onClubAffiliationChange = { clubAffiliation = it },
                        coachName = coachName,
                        onCoachNameChange = { coachName = it },
                        coachPhone = coachPhone,
                        onCoachPhoneChange = { coachPhone = it },
                        proWins = proWins,
                        onProWinsChange = { proWins = it },
                        proLosses = proLosses,
                        onProLossesChange = { proLosses = it },
                        proDraws = proDraws,
                        onProDrawsChange = { proDraws = it },
                        proFinishes = proFinishes,
                        onProFinishesChange = { proFinishes = it },
                        amateurRecord = amateurRecord,
                        onAmateurRecordChange = { amateurRecord = it },
                        heightCm = heightCm,
                        onHeightCmChange = { heightCm = it },
                        reachCm = reachCm,
                        onReachCmChange = { reachCm = it },
                        notableTitles = notableTitles,
                        onNotableTitlesChange = { notableTitles = it },
                        beltRank = beltRank,
                        onBeltRankChange = { beltRank = it },
                        licenseNo = licenseNo,
                        onLicenseNoChange = { licenseNo = it },
                        lastOpponent = lastOpponent,
                        onLastOpponentChange = { lastOpponent = it },
                        lastEvent = lastEvent,
                        onLastEventChange = { lastEvent = it },
                        lastResult = lastResult,
                        onLastResultChange = { lastResult = it },
                        footageUrl = footageUrl,
                        onFootageUrlChange = { footageUrl = it },
                        medicalStatus = medicalStatus,
                        onMedicalStatusChange = { medicalStatus = it },
                        consentChecked = consentChecked,
                        onConsentCheckedChange = { consentChecked = it },
                        photoUriString = photoUriString,
                        onPickPhoto = { imagePickerLauncher.launch("image/*") },
                        onRemovePhoto = { photoUriString = null },
                        onFillDemo = { fillDemoData() },
                        onClearForm = { clearForm() },
                        onSubmit = { handleSubmit() }
                    )
                }

                RegistryTab.ACTIVE_ROSTER -> {
                    ActiveRosterTab(
                        athletes = liveAthletes,
                        onSelectAthlete = { selectedAthleteForDetail = it }
                    )
                }
            }
        }

        // Feedback toast message overlay
        AnimatedVisibility(
            visible = showSuccessToast != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp)
        ) {
            Surface(
                color = EtfcCardInner,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, EtfcGold),
                shadowElevation = 8.dp,
                modifier = Modifier.padding(horizontal = 24.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                    Text(
                        text = showSuccessToast ?: "",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = { showSuccessToast = null },
                        modifier = Modifier.size(20.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EtfcTextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }

        // Official Vetting Confirmation Modal (Shown right after submission)
        submittedAthleteForConfirmation?.let { athlete ->
            AthleteVettingConfirmationModal(
                athlete = athlete,
                onDismiss = { submittedAthleteForConfirmation = null }
            )
        }

        // Athlete Profile Detail Modal (Shown when tapping athlete in roster)
        selectedAthleteForDetail?.let { athlete ->
            AthleteProfileDetailModal(
                athlete = athlete,
                onDismiss = { selectedAthleteForDetail = null }
            )
        }
    }
}

// =========================================================================
// TAB 1: REGISTRATION FORM VIEW
// =========================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RegistrationFormTab(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    amharicName: String,
    onAmharicNameChange: (String) -> Unit,
    nickname: String,
    onNicknameChange: (String) -> Unit,
    gender: String,
    onGenderChange: (String) -> Unit,
    dob: String,
    onDobChange: (String) -> Unit,
    region: String,
    onRegionChange: (String) -> Unit,
    phoneNumber: String,
    onPhoneNumberChange: (String) -> Unit,
    nationalId: String,
    onNationalIdChange: (String) -> Unit,
    primarySport: String,
    onPrimarySportChange: (String) -> Unit,
    competitionLevel: String,
    onCompetitionLevelChange: (String) -> Unit,
    weightClass: String,
    onWeightClassChange: (String) -> Unit,
    stance: String,
    onStanceChange: (String) -> Unit,
    clubAffiliation: String,
    onClubAffiliationChange: (String) -> Unit,
    coachName: String,
    onCoachNameChange: (String) -> Unit,
    coachPhone: String,
    onCoachPhoneChange: (String) -> Unit,
    proWins: Int,
    onProWinsChange: (Int) -> Unit,
    proLosses: Int,
    onProLossesChange: (Int) -> Unit,
    proDraws: Int,
    onProDrawsChange: (Int) -> Unit,
    proFinishes: Int,
    onProFinishesChange: (Int) -> Unit,
    amateurRecord: String,
    onAmateurRecordChange: (String) -> Unit,
    heightCm: Int,
    onHeightCmChange: (Int) -> Unit,
    reachCm: Int,
    onReachCmChange: (Int) -> Unit,
    notableTitles: String,
    onNotableTitlesChange: (String) -> Unit,
    beltRank: String,
    onBeltRankChange: (String) -> Unit,
    licenseNo: String,
    onLicenseNoChange: (String) -> Unit,
    lastOpponent: String,
    onLastOpponentChange: (String) -> Unit,
    lastEvent: String,
    onLastEventChange: (String) -> Unit,
    lastResult: String,
    onLastResultChange: (String) -> Unit,
    footageUrl: String,
    onFootageUrlChange: (String) -> Unit,
    medicalStatus: String,
    onMedicalStatusChange: (String) -> Unit,
    consentChecked: Boolean,
    onConsentCheckedChange: (Boolean) -> Unit,
    photoUriString: String?,
    onPickPhoto: () -> Unit,
    onRemovePhoto: () -> Unit,
    onFillDemo: () -> Unit,
    onClearForm: () -> Unit,
    onSubmit: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Quick Action Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onFillDemo,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcGold),
                    border = BorderStroke(1.dp, EtfcGold),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Demo: Auto-Fill Fighter", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                OutlinedButton(
                    onClick = onClearForm,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcTextMuted),
                    border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Clear", fontSize = 11.sp)
                }
            }
        }

        // =========================================================================
        // SECTION 1: ATHLETE IDENTITY & PROFILE PHOTO
        // =========================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeaderTag(number = "01", title = "Athlete Identity & Profile Photo", amharic = "የአትሌቱ ፎቶ፣ የግል መረጃ እና አድራሻ")

                    // Photo Dropzone / Picker Box
                    Surface(
                        color = EtfcCardInner,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, if (photoUriString != null) EtfcGold else EtfcBorderSubtle),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onPickPhoto() }
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(EtfcObsidian)
                                    .border(2.dp, EtfcGold, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                val resolvedPhoto = ImageResolver.resolve(photoUriString)
                                if (resolvedPhoto != null) {
                                    AsyncImage(
                                        model = resolvedPhoto,
                                        contentDescription = "Athlete Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Icon(imageVector = Icons.Default.AddAPhoto, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(24.dp))
                                        Text("PHOTO", fontSize = 8.sp, fontWeight = FontWeight.Bold, color = EtfcGold)
                                    }
                                }
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text("Official Fighter Headshot", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                                Text("Upload clear headshot, fight stance, or weigh-in picture.", fontSize = 11.sp, color = EtfcTextMuted)

                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Button(
                                        onClick = onPickPhoto,
                                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Select Photo", fontSize = 10.sp, fontWeight = FontWeight.Black)
                                    }

                                    if (photoUriString != null) {
                                        OutlinedButton(
                                            onClick = onRemovePhoto,
                                            colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcRedPrimary),
                                            border = BorderStroke(0.5.dp, EtfcRedPrimary),
                                            shape = RoundedCornerShape(6.dp),
                                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                            modifier = Modifier.height(30.dp)
                                        ) {
                                            Text("Remove", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Form text fields
                    EtfcFormField(label = "Full Legal Name (Latin / English) *", value = fullName, onValueChange = onFullNameChange, placeholder = "e.g., Yonatan Girma")
                    EtfcFormField(label = "ሙሉ ስም (Amharic)", value = amharicName, onValueChange = onAmharicNameChange, placeholder = "ምሳሌ፡ ዮናታን ግርማ")
                    EtfcFormField(label = "Fighter Nickname / Ring Moniker", value = nickname, onValueChange = onNicknameChange, placeholder = "e.g., 'Johnny Jujitsu', 'The Demolisher'")

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcDropdownField(
                            label = "Gender *",
                            selectedValue = gender,
                            options = listOf("Male", "Female"),
                            onSelect = onGenderChange,
                            modifier = Modifier.weight(1f)
                        )
                        EtfcFormField(
                            label = "Date of Birth *",
                            value = dob,
                            onValueChange = onDobChange,
                            placeholder = "YYYY-MM-DD",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    EtfcDropdownField(
                        label = "Region / City Base *",
                        selectedValue = region,
                        options = listOf(
                            "Addis Ababa", "Oromia", "Amhara", "Sidama", "South Ethiopia",
                            "Central Ethiopia", "Tigray", "Dire Dawa", "Somali", "Harari",
                            "Benishangul-Gumuz", "Gambella", "Afar", "Diaspora / International"
                        ),
                        onSelect = onRegionChange
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Phone / Telegram *",
                            value = phoneNumber,
                            onValueChange = onPhoneNumberChange,
                            placeholder = "+251 9XX XXX XXX",
                            modifier = Modifier.weight(1f)
                        )
                        EtfcFormField(
                            label = "National ID / Passport *",
                            value = nationalId,
                            onValueChange = onNationalIdChange,
                            placeholder = "Fayda / Passport",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 2: DISCIPLINE, TIER & GYM AFFILIATION
        // =========================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeaderTag(number = "02", title = "Discipline & Gym Affiliation", amharic = "የውጊያ ስፖርት ዓይነት፣ ምድብ እና የክለብ ትስስር")

                    EtfcDropdownField(
                        label = "Primary Combat Sport *",
                        selectedValue = primarySport,
                        options = listOf(
                            "Mixed Martial Arts (MMA)",
                            "Boxing",
                            "Taekwondo",
                            "Karate",
                            "Muay Thai",
                            "Kickboxing",
                            "Judo",
                            "Wushu Sanda"
                        ),
                        onSelect = onPrimarySportChange
                    )

                    EtfcDropdownField(
                        label = "Competition Tier *",
                        selectedValue = competitionLevel,
                        options = listOf(
                            "Professional",
                            "Semi-Pro / Showcase",
                            "Elite Amateur",
                            "Club Amateur"
                        ),
                        onSelect = onCompetitionLevelChange
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Weight Class & KG *",
                            value = weightClass,
                            onValueChange = onWeightClassChange,
                            placeholder = "e.g., Middleweight (84 kg)",
                            modifier = Modifier.weight(1.3f)
                        )
                        EtfcDropdownField(
                            label = "Stance *",
                            selectedValue = stance,
                            options = listOf("Orthodox", "Southpaw", "Switch"),
                            onSelect = onStanceChange,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    EtfcFormField(
                        label = "Club, Academy or Military Team *",
                        value = clubAffiliation,
                        onValueChange = onClubAffiliationChange,
                        placeholder = "e.g., Sweatbox MMA, Police Club, Defence Force"
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Head Coach *",
                            value = coachName,
                            onValueChange = onCoachNameChange,
                            placeholder = "Coach Name",
                            modifier = Modifier.weight(1f)
                        )
                        EtfcFormField(
                            label = "Coach Phone *",
                            value = coachPhone,
                            onValueChange = onCoachPhoneChange,
                            placeholder = "+251 9XX XXX XXX",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 3: TALE OF THE TAPE & FIGHT RECORDS
        // =========================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeaderTag(number = "03", title = "Tale of the Tape & Records", amharic = "የውጊያ ሪከርድ፣ አሸናፊነት እና የአካል ልኬቶች")

                    Text("OFFICIAL PRO RECORD (W - L - D)", fontSize = 10.sp, fontWeight = FontWeight.Black, color = EtfcGold)

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        NumericCounterField(label = "Wins", count = proWins, onCountChange = onProWinsChange, textColor = EtfcSuccess, modifier = Modifier.weight(1f))
                        NumericCounterField(label = "Losses", count = proLosses, onCountChange = onProLossesChange, textColor = EtfcRedPrimary, modifier = Modifier.weight(1f))
                        NumericCounterField(label = "Draws", count = proDraws, onCountChange = onProDrawsChange, textColor = EtfcTextMuted, modifier = Modifier.weight(1f))
                        NumericCounterField(label = "KOs/Subs", count = proFinishes, onCountChange = onProFinishesChange, textColor = EtfcGold, modifier = Modifier.weight(1f))
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Amateur Record *",
                            value = amateurRecord,
                            onValueChange = onAmateurRecordChange,
                            placeholder = "e.g., 6-1-0",
                            modifier = Modifier.weight(1f)
                        )
                        NumericCounterField(
                            label = "Height (cm)",
                            count = heightCm,
                            onCountChange = onHeightCmChange,
                            step = 1,
                            modifier = Modifier.weight(1f)
                        )
                        NumericCounterField(
                            label = "Reach (cm)",
                            count = reachCm,
                            onCountChange = onReachCmChange,
                            step = 1,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 4: TITLES, BELTS & MARTIAL BACKGROUND
        // =========================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeaderTag(number = "04", title = "Titles, Belts & Martial Honors", amharic = "ዋንጫዎች፣ ቀበቶዎች እና የውጊያ ልምድ")

                    EtfcFormField(
                        label = "Notable Titles & Accolades *",
                        value = notableTitles,
                        onValueChange = onNotableTitlesChange,
                        placeholder = "e.g., ETFC 002 Main Event Winner, African Games Gold Medallist, National Champion",
                        singleLine = false,
                        maxLines = 3
                    )

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Belt / Martial Rank",
                            value = beltRank,
                            onValueChange = onBeltRankChange,
                            placeholder = "e.g., BJJ Purple Belt, Kru Muay Thai",
                            modifier = Modifier.weight(1f)
                        )
                        EtfcFormField(
                            label = "Federation / Club ID (Optional)",
                            value = licenseNo,
                            onValueChange = onLicenseNoChange,
                            placeholder = "e.g., ETH-BOX-2024-001",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // =========================================================================
        // SECTION 5: LAST BOUT & FOOTAGE
        // =========================================================================
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeaderTag(number = "05", title = "Last Bout & Highlight Footage", amharic = "የቅርብ ጊዜ የውጊያ ውጤት እና ቪዲዮ")

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        EtfcFormField(
                            label = "Last Opponent",
                            value = lastOpponent,
                            onValueChange = onLastOpponentChange,
                            placeholder = "Opponent Name",
                            modifier = Modifier.weight(1f)
                        )
                        EtfcFormField(
                            label = "Event / Promotion",
                            value = lastEvent,
                            onValueChange = onLastEventChange,
                            placeholder = "e.g., ETFC 002",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    EtfcFormField(
                        label = "Result & Finish Method",
                        value = lastResult,
                        onValueChange = onLastResultChange,
                        placeholder = "e.g., Win via Submission (R2)"
                    )

                    EtfcFormField(
                        label = "Highlight Reel / Footage URL",
                        value = footageUrl,
                        onValueChange = onFootageUrlChange,
                        placeholder = "https://youtube.com/watch?v=... or Google Drive"
                    )

                    EtfcDropdownField(
                        label = "Medical Clearance Status *",
                        selectedValue = medicalStatus,
                        options = listOf(
                            "Fit & Medically Cleared",
                            "Fit - Seasonal Renewal Pending",
                            "Medical Suspension / Recovery"
                        ),
                        onSelect = onMedicalStatusChange
                    )

                    // Declaration consent
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Checkbox(
                            checked = consentChecked,
                            onCheckedChange = onConsentCheckedChange,
                            colors = CheckboxDefaults.colors(
                                checkedColor = EtfcGold,
                                checkmarkColor = Color.Black
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "I hereby certify that all combat records, titles, photos, and stats submitted herein are accurate. I agree to have my credentials vetted by the ETFC Matchmaking Committee for assignment to regional or national fight leagues.",
                            fontSize = 11.sp,
                            color = EtfcTextSecondary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        // =========================================================================
        // LIVE APPLICATION PREVIEW
        // =========================================================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("REGISTRATION PREVIEW & VETTING STATUS", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Black)
                LiveFighterApplicationCard(
                    name = fullName.ifBlank { "Athlete Name" },
                    nickname = nickname.ifBlank { "The Contender" },
                    club = clubAffiliation.ifBlank { "Sweatbox MMA / Club" },
                    sport = primarySport,
                    weight = weightClass,
                    proRecord = "$proWins - $proLosses - $proDraws",
                    amateurRecord = amateurRecord,
                    titles = notableTitles.ifBlank { "Accolades and championship belts appear here." },
                    photoUri = photoUriString
                )
            }
        }

        // Submit Button & Vetting explanation banner
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = onSubmit,
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("submit_athlete_registry_btn")
                ) {
                    Icon(imageVector = Icons.Default.Send, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("SUBMIT APPLICATION FOR VETTING", fontWeight = FontWeight.Black, fontSize = 13.sp, letterSpacing = 0.5.sp)
                }

                // Vetting Process Notice
                Surface(
                    color = Color.Black.copy(alpha = 0.5f),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.5.dp, EtfcGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                        Column {
                            Text("ETFC Vetting & League Assignment Process", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Upon submission, the ETFC matchmaking team verifies your combat records and medical clearance. Once vetted, you will be assigned to a competitive fight league (Regional Contender or National Pro League) and matched for upcoming cards.",
                                color = EtfcTextMuted,
                                fontSize = 10.sp,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

// =========================================================================
// TAB 2: ACTIVE ROSTER DIRECTORY VIEW
// =========================================================================
@Composable
private fun ActiveRosterTab(
    athletes: List<RegisteredAthlete>,
    onSelectAthlete: (RegisteredAthlete) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedSportFilter by remember { mutableStateOf("ALL") }

    val filteredAthletes = remember(athletes, searchQuery, selectedSportFilter) {
        athletes.filter { athlete ->
            val matchesSport = selectedSportFilter == "ALL" || athlete.primarySport.contains(selectedSportFilter, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                athlete.fullName.contains(searchQuery, ignoreCase = true) ||
                athlete.amharicName.contains(searchQuery, ignoreCase = true) ||
                athlete.nickname.contains(searchQuery, ignoreCase = true) ||
                athlete.clubAffiliation.contains(searchQuery, ignoreCase = true) ||
                athlete.notableTitles.contains(searchQuery, ignoreCase = true)
            matchesSport && matchesSearch
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp),
        contentPadding = PaddingValues(top = 14.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Search bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search athlete by name, club, or accolade...", fontSize = 12.sp, color = EtfcTextMuted) },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = EtfcGold) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EtfcGold,
                    unfocusedBorderColor = EtfcBorderSubtle,
                    focusedContainerColor = EtfcCardInner,
                    unfocusedContainerColor = EtfcCardInner,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                )
            )
        }

        // Sport filter chips
        item {
            val sports = listOf("ALL", "Boxing", "MMA", "Taekwondo", "Muay Thai", "Karate")
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(sports) { sport ->
                    val isSelected = selectedSportFilter == sport
                    Surface(
                        modifier = Modifier.clickable { selectedSportFilter = sport },
                        shape = RoundedCornerShape(6.dp),
                        color = if (isSelected) EtfcGold else EtfcCardInner,
                        border = BorderStroke(1.dp, if (isSelected) EtfcGold else EtfcBorderSubtle)
                    ) {
                        Text(
                            text = sport,
                            color = if (isSelected) Color.Black else EtfcTextSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Count Indicator
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing ${filteredAthletes.size} registered combat athletes",
                    fontSize = 11.sp,
                    color = EtfcTextMuted,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Athlete List
        items(filteredAthletes, key = { it.athleteId }) { athlete ->
            RegisteredAthleteCard(
                athlete = athlete,
                onClick = { onSelectAthlete(athlete) }
            )
        }
    }
}

@Composable
private fun RegisteredAthleteCard(
    athlete: RegisteredAthlete,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(EtfcObsidian)
                    .border(1.5.dp, EtfcGold, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                val photo = ImageResolver.resolve(athlete.profilePhotoUri)
                if (photo != null) {
                    AsyncImage(
                        model = photo,
                        contentDescription = athlete.fullName,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(
                        text = athlete.fullName.take(2).uppercase(),
                        fontWeight = FontWeight.Black,
                        color = EtfcGold,
                        fontSize = 16.sp
                    )
                }
            }

            // Info
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = athlete.fullName,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp
                    )
                    Surface(
                        color = EtfcGold.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp),
                        border = BorderStroke(0.5.dp, EtfcGold)
                    ) {
                        Text(
                            text = athlete.primarySport.replace(Regex("\\(.*\\)"), "").trim(),
                            color = EtfcGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                        )
                    }
                }

                if (athlete.nickname.isNotBlank()) {
                    Text(
                        text = "\"${athlete.nickname}\"",
                        color = EtfcGold,
                        fontStyle = FontStyle.Italic,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "${athlete.clubAffiliation} • ${athlete.weightClass}",
                    color = EtfcTextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Pro: ${athlete.proWins}-${athlete.proLosses}-${athlete.proDraws}",
                        color = EtfcSuccess,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text("•", color = EtfcTextMuted, fontSize = 10.sp)
                    val statusColor = when {
                        athlete.status.contains("Approved", ignoreCase = true) || athlete.status.contains("Active", ignoreCase = true) -> EtfcSuccess
                        athlete.status.contains("Reject", ignoreCase = true) -> Color(0xFFEF4444)
                        else -> EtfcGold
                    }
                    Surface(
                        color = statusColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(3.dp),
                        border = BorderStroke(0.5.dp, statusColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = athlete.status,
                            color = statusColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
        }
    }
}

// =========================================================================
// LIVE FIGHTER APPLICATION PREVIEW
// =========================================================================
@Composable
private fun LiveFighterApplicationCard(
    name: String,
    nickname: String,
    club: String,
    sport: String,
    weight: String,
    proRecord: String,
    amateurRecord: String,
    titles: String,
    photoUri: String?
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = EtfcCard),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, EtfcGold.copy(alpha = 0.6f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF221A06), EtfcCard)
                    )
                )
                .padding(16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Header badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("ETFC", color = EtfcGold, fontWeight = FontWeight.Black, fontSize = 18.sp)
                        Surface(
                            color = EtfcGold.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(0.5.dp, EtfcGold)
                        ) {
                            Text("APPLICATION POOL", color = EtfcGold, fontSize = 8.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                        }
                    }
                    Surface(
                        color = Color(0xFFFAB818).copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text("PENDING VETTING", color = EtfcGold, fontSize = 9.sp, fontWeight = FontWeight.Black, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Avatar
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(EtfcObsidian)
                        .border(2.dp, EtfcGold, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    val resolved = ImageResolver.resolve(photoUri)
                    if (resolved != null) {
                        AsyncImage(
                            model = resolved,
                            contentDescription = name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(name.take(2).uppercase(), fontSize = 24.sp, fontWeight = FontWeight.Black, color = EtfcGold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(name.uppercase(), color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 0.5.sp)
                if (nickname.isNotBlank()) {
                    Text("\"$nickname\"", color = EtfcGold, fontStyle = FontStyle.Italic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Text(club, color = EtfcTextMuted, fontSize = 11.sp)

                Spacer(modifier = Modifier.height(10.dp))

                // Fight Record Strip
                Surface(
                    color = Color.Black.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("PRO RECORD", fontSize = 8.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
                            Text(proRecord, fontSize = 14.sp, color = EtfcGold, fontWeight = FontWeight.Black)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(EtfcBorderSubtle))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("AMATEUR", fontSize = 8.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
                            Text(amateurRecord, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stats Grid
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text("DISCIPLINE", fontSize = 8.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
                        Text(sport.replace(Regex("\\(.*\\)"), "").trim(), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("WEIGHT CLASS", fontSize = 8.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
                        Text(weight, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Accolades
                Surface(
                    color = Color.White.copy(alpha = 0.05f),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = titles,
                        color = EtfcTextSecondary,
                        fontSize = 10.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier.padding(8.dp),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// =========================================================================
// OFFICIAL VETTING SUBMISSION CONFIRMATION MODAL
// =========================================================================
@Composable
private fun AthleteVettingConfirmationModal(
    athlete: RegisteredAthlete,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, EtfcGold),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header Icon
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(EtfcGold.copy(alpha = 0.15f))
                            .border(1.5.dp, EtfcGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = EtfcGold,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "APPLICATION SUBMITTED",
                            color = EtfcGold,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            letterSpacing = 0.5.sp
                        )
                        Text(
                            text = "ምዝገባው ለግምገማ በተሳካ ሁኔታ ገብቷል",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    // Explanation
                    Surface(
                        color = EtfcCardInner,
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Your combat credentials, fight records, and medical clearance have been officially submitted to the ETFC Matchmaking Committee.",
                                color = Color.White,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                            Text(
                                text = "Once the ETFC team verifies your credentials, you will be assigned to an official fight league (Regional Contender or National Pro League) and scheduled for upcoming fight nights.",
                                color = EtfcTextMuted,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }

                    // Submission Details Summary
                    Surface(
                        color = Color.Black.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DetailRow(label = "ATHLETE", value = athlete.fullName)
                            DetailRow(label = "DISCIPLINE", value = athlete.primarySport.replace(Regex("\\(.*\\)"), "").trim())
                            DetailRow(label = "WEIGHT CLASS", value = athlete.weightClass)
                            DetailRow(label = "GYM / AFFILIATION", value = athlete.clubAffiliation)
                            DetailRow(label = "STATUS", value = "🟡 Under Credential Review")
                            DetailRow(label = "ASSIGNED LEAGUE", value = "⏳ Pending Verification")
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("GOT IT / RETURN TO PORTAL", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// =========================================================================
// ATHLETE PROFILE DETAIL MODAL (WHEN TAPPING ROSTER ITEM)
// =========================================================================
@Composable
private fun AthleteProfileDetailModal(
    athlete: RegisteredAthlete,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.85f))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcSurface),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.5.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("ATHLETE PROFILE & LEAGUE STATUS", color = EtfcGold, fontWeight = FontWeight.Black, fontSize = 13.sp)
                            Text("ETFC Combat Roster", color = EtfcTextMuted, fontSize = 10.sp)
                        }
                        IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    // Athlete Header Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(EtfcObsidian)
                                .border(1.5.dp, EtfcGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            val photo = ImageResolver.resolve(athlete.profilePhotoUri)
                            if (photo != null) {
                                AsyncImage(
                                    model = photo,
                                    contentDescription = athlete.fullName,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = athlete.fullName.take(2).uppercase(),
                                    fontWeight = FontWeight.Black,
                                    color = EtfcGold,
                                    fontSize = 20.sp
                                )
                            }
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(athlete.fullName, color = Color.White, fontWeight = FontWeight.Black, fontSize = 16.sp)
                            if (athlete.nickname.isNotBlank()) {
                                Text("\"${athlete.nickname}\"", color = EtfcGold, fontStyle = FontStyle.Italic, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text("${athlete.clubAffiliation} • ${athlete.region}", color = EtfcTextMuted, fontSize = 11.sp)
                        }
                    }

                    // League Placement & Status Card
                    Surface(
                        color = EtfcCardInner,
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(24.dp))
                            Column {
                                Text("LEAGUE ASSIGNMENT", fontSize = 9.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
                                Text("ETFC National Contender League", fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Black)
                                Text("Vetted for National & Regional Cards", fontSize = 10.sp, color = EtfcSuccess)
                            }
                        }
                    }

                    // Records & Specs
                    Surface(
                        color = Color.Black.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(0.5.dp, EtfcBorderSubtle),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            DetailRow(label = "DISCIPLINE", value = athlete.primarySport)
                            DetailRow(label = "WEIGHT CLASS", value = athlete.weightClass)
                            DetailRow(label = "PRO RECORD", value = "${athlete.proWins}W - ${athlete.proLosses}L - ${athlete.proDraws}D (${athlete.proFinishes} KOs/Subs)")
                            DetailRow(label = "AMATEUR RECORD", value = athlete.amateurRecord)
                            DetailRow(label = "STANCE / REACH", value = "${athlete.stance} • ${athlete.reachCm} cm")
                            DetailRow(label = "NOTABLE TITLES", value = athlete.notableTitles)
                        }
                    }

                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("CLOSE PROFILE", fontWeight = FontWeight.Black, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 10.sp, color = EtfcTextMuted, fontWeight = FontWeight.Bold)
        Text(text = value, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

// =========================================================================
// HELPER FORM FIELDS
// =========================================================================
@Composable
private fun SectionHeaderTag(number: String, title: String, amharic: String) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = EtfcGold,
            contentColor = Color.Black
        ) {
            Text(number, fontWeight = FontWeight.Black, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
        }
        Column {
            Text(title, fontWeight = FontWeight.Black, fontSize = 14.sp, color = Color.White)
            Text(amharic, fontSize = 10.sp, color = EtfcTextMuted)
        }
    }
}

@Composable
private fun EtfcFormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    singleLine: Boolean = true,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EtfcTextSecondary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, fontSize = 12.sp, color = EtfcTextMuted) },
            singleLine = singleLine,
            maxLines = maxLines,
            shape = RoundedCornerShape(8.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = EtfcGold,
                unfocusedBorderColor = EtfcBorderSubtle,
                focusedContainerColor = EtfcCardInner,
                unfocusedContainerColor = EtfcCardInner,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EtfcDropdownField(
    label: String,
    selectedValue: String,
    options: List<String>,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(label, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EtfcTextSecondary)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = it }
        ) {
            OutlinedTextField(
                value = selectedValue,
                onValueChange = {},
                readOnly = true,
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = EtfcGold,
                    unfocusedBorderColor = EtfcBorderSubtle,
                    focusedContainerColor = EtfcCardInner,
                    unfocusedContainerColor = EtfcCardInner,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.background(EtfcSurface)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = { Text(option, fontSize = 12.sp, color = if (option == selectedValue) EtfcGold else Color.White) },
                        onClick = {
                            onSelect(option)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun NumericCounterField(
    label: String,
    count: Int,
    onCountChange: (Int) -> Unit,
    step: Int = 1,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(label, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = EtfcTextMuted)
        Surface(
            color = EtfcCardInner,
            shape = RoundedCornerShape(8.dp),
            border = BorderStroke(1.dp, EtfcBorderSubtle),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = { onCountChange((count - step).coerceAtLeast(0)) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("-", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EtfcTextMuted)
                }
                Text(
                    text = "$count",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = textColor
                )
                IconButton(
                    onClick = { onCountChange(count + step) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Text("+", fontSize = 16.sp, fontWeight = FontWeight.Black, color = EtfcGold)
                }
            }
        }
    }
}
