package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.ContentTier
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
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

@Composable
fun AuthDialog(
    isVisible: Boolean,
    currentTab: Int,
    errorMessage: String?,
    demoUsers: List<UserAccount>,
    onTabSelected: (Int) -> Unit,
    onLogin: (identifier: String, password: String) -> Unit,
    onRegister: (email: String, handle: String, name: String, pass: String, phone: String, code: String, flag: String) -> Unit,
    onQuickSwitchUser: (UserAccount) -> Unit,
    onDismiss: () -> Unit
) {
    if (!isVisible) return

    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Form State
    var regEmail by remember { mutableStateOf("") }
    var regHandle by remember { mutableStateOf("") }
    var regDisplayName by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("911234567") }
    var regFlag by remember { mutableStateOf("🇪🇹") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, EtfcGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                .testTag("auth_dialog_surface"),
            color = EtfcSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtfcGold.copy(alpha = 0.15f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold)
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
                            Text(
                                text = "ETFC ARENA ACCESS",
                                color = EtfcGold,
                                fontWeight = FontWeight.Black,
                                fontSize = 15.sp,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Authentication & RBAC Security",
                                color = EtfcTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("auth_dialog_close_btn")) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EtfcTextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Tab Switcher (Sign In vs Register)
                TabRow(
                    selectedTabIndex = currentTab,
                    containerColor = EtfcCardInner,
                    contentColor = EtfcGold,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[currentTab]),
                            color = EtfcGold,
                            height = 2.dp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = currentTab == 0,
                        onClick = { onTabSelected(0) },
                        modifier = Modifier.testTag("auth_tab_signin"),
                        text = {
                            Text(
                                text = "SIGN IN",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (currentTab == 0) EtfcGold else EtfcTextMuted
                            )
                        }
                    )
                    Tab(
                        selected = currentTab == 1,
                        onClick = { onTabSelected(1) },
                        modifier = Modifier.testTag("auth_tab_register"),
                        text = {
                            Text(
                                text = "CREATE ACCOUNT",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (currentTab == 1) EtfcGold else EtfcTextMuted
                            )
                        }
                    )
                }

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        color = EtfcRedPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcRedPrimary)
                    ) {
                        Text(
                            text = errorMessage,
                            color = EtfcRedPrimary,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (currentTab == 0) {
                    // SIGN IN FORM
                    OutlinedTextField(
                        value = loginIdentifier,
                        onValueChange = { loginIdentifier = it },
                        label = { Text("Email or @handle", color = EtfcTextMuted, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_identifier_input"),
                        colors = getAuthTextFieldColors(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = loginPassword,
                        onValueChange = { loginPassword = it },
                        label = { Text("Password", color = EtfcTextMuted, fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                        },
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = EtfcTextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        },
                        visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("login_password_input"),
                        colors = getAuthTextFieldColors(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = { onLogin(loginIdentifier, loginPassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("login_submit_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("SIGN IN TO ETFC", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.5.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Fast Demo Accounts 1-Tap Switcher
                    Text(
                        text = "QUICK TEST DEMO ACCOUNTS (1-TAP RBAC):",
                        color = EtfcTextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        demoUsers.take(4).forEach { demo ->
                            val roleColor = Color(demo.role.colorHex)
                            Surface(
                                color = EtfcCardInner,
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onQuickSwitchUser(demo) }
                                    .testTag("quick_login_${demo.handle.removePrefix("@")}")
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = demo.role.badgeEmoji, fontSize = 14.sp)
                                        Column {
                                            Text(
                                                text = "${demo.displayName} (${demo.handle})",
                                                color = EtfcTextPrimary,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Role: ${demo.role.label}",
                                                color = roleColor,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }
                                    Surface(
                                        color = roleColor.copy(alpha = 0.15f),
                                        shape = RoundedCornerShape(6.dp),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, roleColor.copy(alpha = 0.6f))
                                    ) {
                                        Text(
                                            text = demo.role.badgeTitle,
                                            color = roleColor,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // CREATE ACCOUNT FORM
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            OutlinedTextField(
                                value = regDisplayName,
                                onValueChange = { regDisplayName = it },
                                label = { Text("Display Name (e.g. Yonas)", color = EtfcTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_name_input"),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regHandle,
                                onValueChange = { regHandle = it },
                                label = { Text("Handle (e.g. @sheger_striker)", color = EtfcTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Text("@", color = EtfcGold, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 12.dp)) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_handle_input"),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regEmail,
                                onValueChange = { regEmail = it },
                                label = { Text("Email Address", color = EtfcTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = { regPhone = it },
                                label = { Text("Phone (Telebirr / M-Pesa)", color = EtfcTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp)) },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        item {
                            OutlinedTextField(
                                value = regPassword,
                                onValueChange = { regPassword = it },
                                label = { Text("Password (min 4 characters)", color = EtfcTextMuted, fontSize = 11.sp) },
                                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp)) },
                                visualTransformation = PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth().testTag("reg_pass_input"),
                                colors = getAuthTextFieldColors(),
                                shape = RoundedCornerShape(8.dp)
                            )
                        }

                        item {
                            // Country flag selector
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Country / Flag:", color = EtfcTextSecondary, fontSize = 11.sp)
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    listOf("🇪🇹", "🇰🇪", "🇺🇬", "🇷🇼", "🇹🇿").forEach { flag ->
                                        Surface(
                                            color = if (regFlag == flag) EtfcGold.copy(alpha = 0.2f) else EtfcCardInner,
                                            shape = RoundedCornerShape(6.dp),
                                            border = androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                if (regFlag == flag) EtfcGold else EtfcBorderSubtle
                                            ),
                                            modifier = Modifier
                                                .clickable { regFlag = flag }
                                                .padding(2.dp)
                                        ) {
                                            Text(text = flag, fontSize = 16.sp, modifier = Modifier.padding(4.dp))
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            Spacer(modifier = Modifier.height(4.dp))
                            Button(
                                onClick = {
                                    onRegister(regEmail, regHandle, regDisplayName, regPassword, regPhone, "+251", regFlag)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(46.dp)
                                    .testTag("register_submit_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary, contentColor = Color.White),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("REGISTER ACCOUNT", fontWeight = FontWeight.Bold, fontSize = 13.sp, letterSpacing = 0.5.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileDrawer(
    isVisible: Boolean,
    currentUser: UserAccount?,
    demoUsers: List<UserAccount>,
    onUpgradeRole: (UserRole) -> Unit,
    onQuickSwitchUser: (UserAccount) -> Unit,
    onOpenAuthDialog: () -> Unit,
    onLogout: () -> Unit,
    onDismiss: () -> Unit,
    onOpenAdminPortal: (() -> Unit)? = null
) {
    if (!isVisible) return

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = EtfcSurface,
        dragHandle = {
            Surface(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .width(40.dp)
                    .height(4.dp),
                shape = CircleShape,
                color = EtfcBorderSubtle
            ) {}
        }
    ) {
        val currentRole = currentUser?.role ?: UserRole.GUEST
        val roleColor = Color(currentRole.colorHex)

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .testTag("user_profile_sheet")
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = roleColor.copy(alpha = 0.2f),
                        border = androidx.compose.foundation.BorderStroke(2.dp, roleColor),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(text = currentRole.badgeEmoji, fontSize = 22.sp)
                        }
                    }
                    Column {
                        Text(
                            text = currentUser?.displayName ?: "Guest Fan",
                            color = EtfcTextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp
                        )
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = currentUser?.flagEmoji ?: "🇪🇹", fontSize = 12.sp)
                            Text(
                                text = currentUser?.handle ?: "@guest",
                                color = EtfcGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Current Tier Badge
                Surface(
                    color = roleColor.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, roleColor)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = currentRole.badgeEmoji, fontSize = 12.sp)
                        Text(
                            text = currentRole.badgeTitle,
                            color = Color(currentRole.colorHex),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RBAC Access Overview Card
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCardInner),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "ROLE-BASED ACCESS CONTROL (RBAC):",
                        color = EtfcGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    val tiers = listOf(
                        ContentTier.FREE_PRELIMS to "Free Prelims & Audio",
                        ContentTier.STANDARD_PPV_MAIN_CARD to "PPV Main Card (1080p)",
                        ContentTier.VIP_RINGSIDE_EXCLUSIVE to "VIP Multi-Cam & Ref Cam",
                        ContentTier.OFFLINE_VAULT to "Offline Match Downloads",
                        ContentTier.MODERATION_TOOLS to "Live Chat Mod Shield"
                    )

                    tiers.forEach { (tier, label) ->
                        val hasAccess = currentRole.canAccessTier(tier)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = label, color = if (hasAccess) EtfcTextPrimary else EtfcTextMuted, fontSize = 11.sp)
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (hasAccess) EtfcSuccess.copy(alpha = 0.15f) else Color.White.copy(alpha = 0.05f)
                            ) {
                                Text(
                                    text = if (hasAccess) "UNLOCKED ✓" else "LOCKED 🔒",
                                    color = if (hasAccess) EtfcSuccess else EtfcTextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Security Token preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Session Auth Token:", color = EtfcTextMuted, fontSize = 9.sp)
                        Text(
                            text = currentUser?.authToken?.take(22)?.plus("...") ?: "ANONYMOUS",
                            color = EtfcBlueCorner,
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Role Switcher (for testing & demo purposes)
            Text(
                text = "SWITCH ACTIVE PROFILE / ROLE:",
                color = EtfcTextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                demoUsers.take(4).forEach { demo ->
                    val isCurrent = demo.userId == currentUser?.userId
                    val roleColor = Color(demo.role.colorHex)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCurrent) roleColor.copy(alpha = 0.2f) else EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCurrent) roleColor else EtfcBorderSubtle
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onQuickSwitchUser(demo) }
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(text = demo.role.badgeEmoji, fontSize = 16.sp)
                            Text(
                                text = demo.role.badgeTitle.take(6),
                                color = if (isCurrent) roleColor else EtfcTextPrimary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Action Buttons
            if (onOpenAdminPortal != null) {
                Button(
                    onClick = {
                        onDismiss()
                        onOpenAdminPortal()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("drawer_open_admin_portal_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black)
                ) {
                    Icon(imageVector = Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("OPEN ADMIN CMS & GITHUB SYNC", fontWeight = FontWeight.Black, fontSize = 11.5.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onOpenAuthDialog,
                    modifier = Modifier.weight(1f).height(44.dp).testTag("profile_switch_account_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcGold),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold)
                ) {
                    Text("SIGN IN / NEW", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                Button(
                    onClick = onLogout,
                    modifier = Modifier.weight(1f).height(44.dp).testTag("profile_logout_btn"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary, contentColor = Color.White)
                ) {
                    Text("LOG OUT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun getAuthTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EtfcGold,
    unfocusedBorderColor = EtfcBorderSubtle,
    focusedContainerColor = EtfcCardInner,
    unfocusedContainerColor = EtfcCardInner,
    focusedTextColor = EtfcTextPrimary,
    unfocusedTextColor = EtfcTextPrimary
)
