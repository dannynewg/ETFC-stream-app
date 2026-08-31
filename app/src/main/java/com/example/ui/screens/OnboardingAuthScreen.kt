package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
import com.example.ui.viewmodel.EtfcViewModel
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderGold
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
fun OnboardingAuthScreen(
    viewModel: EtfcViewModel,
    onDismiss: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    OnboardingAuthScreen(
        errorMessage = uiState.authErrorMessage,
        demoUsers = uiState.allDemoUsers,
        onLogin = { id, pass ->
            viewModel.login(id, pass)
            onDismiss()
        },
        onRegister = { email, handle, name, pass, phone, code, flag ->
            viewModel.register(email, handle, name, pass, phone, code, flag)
            onDismiss()
        },
        onQuickSwitchUser = {
            viewModel.quickSwitchUser(it)
            onDismiss()
        },
        onContinueAsGuest = {
            onDismiss()
        }
    )
}

@Composable
fun OnboardingAuthScreen(
    errorMessage: String?,
    demoUsers: List<UserAccount>,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String, String, String) -> Unit,
    onQuickSwitchUser: (UserAccount) -> Unit,
    onContinueAsGuest: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Sign Up

    // Sign In Fields
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    // Register Fields
    var regEmail by remember { mutableStateOf("") }
    var regFullName by remember { mutableStateOf("") }
    var regHandle by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .testTag("onboarding_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo & Header
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.Black,
                border = BorderStroke(1.5.dp, EtfcGold),
                modifier = Modifier.size(76.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_etfc_logo),
                        contentDescription = "ETFC Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ETFC",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcGold,
                    letterSpacing = 1.5.sp
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "STREAM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = EtfcTextPrimary,
                    letterSpacing = 1.5.sp
                )
            }

            Text(
                text = "Official Ethiopian Fight Championship Hub",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = EtfcTextMuted,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Auth Card
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                border = BorderStroke(1.dp, EtfcBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Tab Selector: Sign In / Create Account
                    TabRow(
                        selectedTabIndex = selectedTab,
                        containerColor = EtfcCardInner,
                        contentColor = EtfcGold,
                        indicator = { tabPositions ->
                            TabRowDefaults.SecondaryIndicator(
                                Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                                color = EtfcGold,
                                height = 3.dp
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                    ) {
                        Tab(
                            selected = selectedTab == 0,
                            onClick = { selectedTab = 0 },
                            text = {
                                Text(
                                    "Sign In",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 0) EtfcGold else EtfcTextMuted
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_signin")
                        )
                        Tab(
                            selected = selectedTab == 1,
                            onClick = { selectedTab = 1 },
                            text = {
                                Text(
                                    "Sign Up",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 1) EtfcGold else EtfcTextMuted
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_signup")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtfcRedPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = errorMessage,
                                color = Color(0xFFFF6B6B),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (selectedTab == 0) {
                        // ================= SIGN IN TAB =================
                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = { loginIdentifier = it },
                            label = { Text("Email, @handle or Phone", fontSize = 12.sp) },
                            placeholder = { Text("e.g. dannynewg@gmail.com", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_identifier_field")
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = loginPassword,
                            onValueChange = { loginPassword = it },
                            label = { Text("Password", fontSize = 12.sp) },
                            placeholder = { Text("Enter password", fontSize = 12.sp) },
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
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("login_password_field")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = { onLogin(loginIdentifier, loginPassword) },
                            enabled = loginIdentifier.isNotBlank() && loginPassword.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black,
                                disabledContainerColor = EtfcCardInner,
                                disabledContentColor = EtfcTextMuted
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("login_submit_btn")
                        ) {
                            Text("SIGN IN TO ETFC", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    } else {
                        // ================= SIGN UP TAB =================
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = { regFullName = it },
                            label = { Text("Full Name", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Daniel Bekele", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_name_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = { regEmail = it },
                            label = { Text("Email Address", fontSize = 12.sp) },
                            placeholder = { Text("e.g. dannynewg@gmail.com", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_email_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regHandle,
                            onValueChange = { regHandle = it },
                            label = { Text("Arena Handle (@username)", fontSize = 12.sp) },
                            placeholder = { Text("e.g. @danny_etfc", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.SportsMma, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_handle_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPhone,
                            onValueChange = { regPhone = it },
                            label = { Text("Phone Number (+251)", fontSize = 12.sp) },
                            placeholder = { Text("911234567", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Phone, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_phone_field")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = { regPassword = it },
                            label = { Text("Create Password (min 6 chars)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = EtfcGold,
                                unfocusedBorderColor = EtfcBorder,
                                focusedTextColor = EtfcTextPrimary,
                                unfocusedTextColor = EtfcTextPrimary,
                                focusedContainerColor = EtfcCardInner,
                                unfocusedContainerColor = EtfcCardInner
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("reg_password_field")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                onRegister(regEmail, regHandle, regFullName, regPassword, regPhone, "ET", "🇪🇹")
                            },
                            enabled = regEmail.isNotBlank() && regPassword.length >= 4 && regFullName.isNotBlank(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black,
                                disabledContainerColor = EtfcCardInner,
                                disabledContentColor = EtfcTextMuted
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(46.dp)
                                .testTag("register_submit_btn")
                        ) {
                            Text("CREATE ETFC ACCOUNT", fontWeight = FontWeight.Black, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Continue as Guest Button
            OutlinedButton(
                onClick = onContinueAsGuest,
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, EtfcBorderGold),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = EtfcGold),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("continue_as_guest_btn")
            ) {
                Text("EXPLORE AS FAN / GUEST", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Accounts Section (Instant 1-Tap Login)
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "QUICK DEMO ACCOUNTS (1-TAP LOGIN)",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = EtfcTextMuted,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                demoUsers.take(3).forEach { user ->
                    val isSuperAdmin = user.email.equals("dannynewg@gmail.com", ignoreCase = true)
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = EtfcCardInner,
                        border = BorderStroke(1.dp, if (isSuperAdmin) EtfcGold else EtfcBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clickable { onQuickSwitchUser(user) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = user.flagEmoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = user.displayName,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSuperAdmin) EtfcGold else EtfcTextPrimary
                                    )
                                    Text(
                                        text = user.email,
                                        fontSize = 10.sp,
                                        color = EtfcTextMuted
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (isSuperAdmin) EtfcGold.copy(alpha = 0.2f) else EtfcSurfaceVariant
                            ) {
                                Text(
                                    text = if (isSuperAdmin) "👑 SUPERADMIN" else user.role.badgeTitle,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isSuperAdmin) EtfcGold else EtfcTextSecondary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
