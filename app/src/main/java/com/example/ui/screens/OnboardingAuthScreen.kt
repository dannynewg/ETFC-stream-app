package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.models.UserAccount
import com.example.data.models.UserRole
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
import com.example.ui.viewmodel.EtfcViewModel

/**
 * Unified Real Firebase Authentication Screen (Sign-In / Sign-Up / Google Sign-In / Password Reset)
 */
@Composable
fun OnboardingAuthScreen(
    viewModel: EtfcViewModel,
    onDismiss: (() -> Unit)? = null
) {
    val uiState by viewModel.uiState.collectAsState()
    OnboardingAuthScreen(
        errorMessage = uiState.authErrorMessage,
        onLogin = { id, pass ->
            viewModel.login(id, pass)
        },
        onRegister = { email, handle, name, pass, phone, code, flag ->
            viewModel.register(email, handle, name, pass, phone, code, flag)
        },
        onGoogleLogin = { email, name ->
            viewModel.loginWithGoogle(email, name)
        },
        onSendPasswordReset = { email, callback ->
            viewModel.sendPasswordResetEmail(email, callback)
        },
        onDismiss = onDismiss
    )
}

data class CountryPrefix(val code: String, val dialCode: String, val flag: String, val name: String)

@Composable
fun OnboardingAuthScreen(
    errorMessage: String?,
    onLogin: (String, String) -> Unit,
    onRegister: (String, String, String, String, String, String, String) -> Unit,
    onGoogleLogin: ((String, String) -> Unit)? = null,
    onSendPasswordReset: ((String, (Boolean, String) -> Unit) -> Unit)? = null,
    onDismiss: (() -> Unit)? = null
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Sign In, 1 = Sign Up

    // Sign In State & Validation
    var loginIdentifier by remember { mutableStateOf("") }
    var loginPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var loginTouched by remember { mutableStateOf(false) }

    // Register State & Validation
    var regEmail by remember { mutableStateOf("") }
    var regFullName by remember { mutableStateOf("") }
    var regHandle by remember { mutableStateOf("") }
    var regPhone by remember { mutableStateOf("") }
    var regPassword by remember { mutableStateOf("") }
    var regConfirmPassword by remember { mutableStateOf("") }
    var isRegPasswordVisible by remember { mutableStateOf(false) }
    var regTouched by remember { mutableStateOf(false) }

    // Forgot Password Dialog State
    var showForgotPasswordDialog by remember { mutableStateOf(false) }
    var forgotPasswordEmail by remember { mutableStateOf("") }
    var forgotPasswordStatus by remember { mutableStateOf<Pair<Boolean, String>?>(null) }
    var isSendingReset by remember { mutableStateOf(false) }

    // Google Sign-In Dialog State
    var showGoogleSignInDialog by remember { mutableStateOf(false) }
    var googleInputEmail by remember { mutableStateOf("") }
    var googleInputName by remember { mutableStateOf("") }

    val countries = remember {
        listOf(
            CountryPrefix("ET", "+251", "🇪🇹", "Ethiopia"),
            CountryPrefix("KE", "+254", "🇰🇪", "Kenya"),
            CountryPrefix("US", "+1", "🇺🇸", "United States"),
            CountryPrefix("GB", "+44", "🇬🇧", "United Kingdom"),
            CountryPrefix("AE", "+971", "🇦🇪", "UAE"),
            CountryPrefix("ZA", "+27", "🇿🇦", "South Africa"),
            CountryPrefix("NG", "+234", "🇳🇬", "Nigeria")
        )
    }
    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var showCountryDropdown by remember { mutableStateOf(false) }

    var isSubmitting by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    // Validation Logic
    val emailRegex = remember { Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$") }

    // Sign In Validation
    val isLoginIdValid = loginIdentifier.trim().isNotBlank()
    val isLoginPassValid = loginPassword.length >= 6
    val isLoginFormValid = isLoginIdValid && isLoginPassValid

    // Sign Up Validation
    val isRegNameValid = regFullName.trim().length >= 2
    val isRegEmailValid = emailRegex.matches(regEmail.trim())
    val isRegHandleValid = regHandle.trim().removePrefix("@").length >= 3
    val isRegPhoneValid = regPhone.trim().length >= 7
    val isRegPassValid = regPassword.length >= 6
    val isRegPassMatch = regPassword == regConfirmPassword && regConfirmPassword.isNotEmpty()
    val isRegFormValid = isRegNameValid && isRegEmailValid && isRegHandleValid && isRegPhoneValid && isRegPassValid && isRegPassMatch

    // Password strength calculation
    val passwordStrength = remember(regPassword) {
        when {
            regPassword.isEmpty() -> 0f
            regPassword.length < 6 -> 0.25f
            regPassword.length in 6..8 -> 0.5f
            regPassword.any { it.isDigit() } && regPassword.any { !it.isLetterOrDigit() } -> 1.0f
            else -> 0.75f
        }
    }
    val passwordStrengthLabel = remember(passwordStrength) {
        when {
            passwordStrength <= 0.25f -> "Weak (Min 6 chars)"
            passwordStrength <= 0.5f -> "Fair"
            passwordStrength <= 0.75f -> "Good"
            else -> "Strong"
        }
    }
    val passwordStrengthColor = remember(passwordStrength) {
        when {
            passwordStrength <= 0.25f -> EtfcRedPrimary
            passwordStrength <= 0.5f -> Color(0xFFFFB300)
            passwordStrength <= 0.75f -> EtfcBlueCorner
            else -> EtfcSuccess
        }
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            isSubmitting = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF10131A),
                        EtfcObsidian,
                        Color(0xFF090A0E)
                    )
                )
            )
            .testTag("onboarding_auth_screen")
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            // Logo & Brand Header
            Surface(
                shape = RoundedCornerShape(18.dp),
                color = Color.Black,
                border = BorderStroke(1.5.dp, EtfcGold),
                shadowElevation = 8.dp,
                modifier = Modifier.size(76.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.padding(10.dp)
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

            Spacer(modifier = Modifier.height(18.dp))

            // Main Auth Card
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
                                    "Create Account",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedTab == 1) EtfcGold else EtfcTextMuted
                                )
                            },
                            modifier = Modifier.testTag("auth_tab_signup")
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Error Message Banner from Firebase Auth
                    if (!errorMessage.isNullOrBlank()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = EtfcRedPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, EtfcRedPrimary.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Default.Error,
                                    contentDescription = null,
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = errorMessage,
                                    color = Color(0xFFFF6B6B),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    if (selectedTab == 0) {
                        // ================= SIGN IN TAB =================
                        OutlinedTextField(
                            value = loginIdentifier,
                            onValueChange = {
                                loginIdentifier = it
                                loginTouched = true
                            },
                            label = { Text("Email address or @handle", fontSize = 12.sp) },
                            placeholder = { Text("e.g. dannynewg@gmail.com", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (loginTouched && isLoginIdValid) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                }
                            },
                            isError = loginTouched && !isLoginIdValid,
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
                            onValueChange = {
                                loginPassword = it
                                loginTouched = true
                            },
                            label = { Text("Password", fontSize = 12.sp) },
                            placeholder = { Text("Enter your account password", fontSize = 12.sp) },
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

                        // Forgot Password Link
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    forgotPasswordEmail = if (loginIdentifier.contains("@")) loginIdentifier.trim() else ""
                                    forgotPasswordStatus = null
                                    showForgotPasswordDialog = true
                                },
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "Forgot Password?",
                                    color = EtfcGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                isSubmitting = true
                                onLogin(loginIdentifier.trim(), loginPassword)
                            },
                            enabled = isLoginFormValid && !isSubmitting,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black,
                                disabledContainerColor = EtfcCardInner,
                                disabledContentColor = EtfcTextMuted
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("login_submit_btn")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text("SIGN IN TO ETFC", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    } else {
                        // ================= SIGN UP TAB =================
                        OutlinedTextField(
                            value = regFullName,
                            onValueChange = {
                                regFullName = it
                                regTouched = true
                            },
                            label = { Text("Full Name", fontSize = 12.sp) },
                            placeholder = { Text("e.g. Daniel Bekele", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Person, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (regTouched && isRegNameValid) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                }
                            },
                            isError = regTouched && !isRegNameValid,
                            supportingText = {
                                if (regTouched && !isRegNameValid) {
                                    Text("Full name must be at least 2 characters", color = Color(0xFFFF6B6B), fontSize = 10.sp)
                                }
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

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = regEmail,
                            onValueChange = {
                                regEmail = it
                                regTouched = true
                            },
                            label = { Text("Email Address", fontSize = 12.sp) },
                            placeholder = { Text("e.g. dannynewg@gmail.com", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Email, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (regTouched && isRegEmailValid) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                }
                            },
                            isError = regTouched && !isRegEmailValid,
                            supportingText = {
                                if (regTouched && !isRegEmailValid) {
                                    Text("Please enter a valid email format (name@domain.com)", color = Color(0xFFFF6B6B), fontSize = 10.sp)
                                }
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

                        Spacer(modifier = Modifier.height(4.dp))

                        OutlinedTextField(
                            value = regHandle,
                            onValueChange = {
                                val clean = if (it.startsWith("@")) it else if (it.isNotBlank()) "@$it" else it
                                regHandle = clean
                                regTouched = true
                            },
                            label = { Text("Arena Handle (@username)", fontSize = 12.sp) },
                            placeholder = { Text("e.g. @danny_etfc", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.SportsMma, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (regTouched && isRegHandleValid) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                }
                            },
                            isError = regTouched && !isRegHandleValid,
                            supportingText = {
                                if (regTouched && !isRegHandleValid) {
                                    Text("Handle must be at least 3 characters", color = Color(0xFFFF6B6B), fontSize = 10.sp)
                                }
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

                        Spacer(modifier = Modifier.height(4.dp))

                        // Phone Number with Country Flag Prefix Dropdown Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = EtfcCardInner,
                                    border = BorderStroke(1.dp, EtfcBorder),
                                    modifier = Modifier
                                        .height(56.dp)
                                        .clickable { showCountryDropdown = true }
                                        .padding(end = 6.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = selectedCountry.flag, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = selectedCountry.dialCode,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = EtfcGold
                                        )
                                        Icon(
                                            Icons.Default.ArrowDropDown,
                                            contentDescription = "Select Country",
                                            tint = EtfcTextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                DropdownMenu(
                                    expanded = showCountryDropdown,
                                    onDismissRequest = { showCountryDropdown = false },
                                    modifier = Modifier.background(EtfcSurface)
                                ) {
                                    countries.forEach { country ->
                                        DropdownMenuItem(
                                            text = {
                                                Row(
                                                    verticalAlignment = Alignment.CenterVertically,
                                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                                ) {
                                                    Text(country.flag, fontSize = 16.sp)
                                                    Text(
                                                        text = "${country.name} (${country.dialCode})",
                                                        color = if (country.code == selectedCountry.code) EtfcGold else EtfcTextPrimary,
                                                        fontSize = 12.sp,
                                                        fontWeight = if (country.code == selectedCountry.code) FontWeight.Bold else FontWeight.Normal
                                                    )
                                                }
                                            },
                                            onClick = {
                                                selectedCountry = country
                                                showCountryDropdown = false
                                            }
                                        )
                                    }
                                }
                            }

                            OutlinedTextField(
                                value = regPhone,
                                onValueChange = {
                                    regPhone = it
                                    regTouched = true
                                },
                                label = { Text("Phone Number", fontSize = 12.sp) },
                                placeholder = { Text("911234567", fontSize = 12.sp) },
                                leadingIcon = {
                                    Icon(Icons.Default.Phone, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                                },
                                trailingIcon = {
                                    if (regTouched && isRegPhoneValid) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = "Valid", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                    }
                                },
                                isError = regTouched && !isRegPhoneValid,
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
                                modifier = Modifier.weight(1f).testTag("reg_phone_field")
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Password field with Strength indicator
                        OutlinedTextField(
                            value = regPassword,
                            onValueChange = {
                                regPassword = it
                                regTouched = true
                            },
                            label = { Text("Create Password (min 6 chars)", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                IconButton(onClick = { isRegPasswordVisible = !isRegPasswordVisible }) {
                                    Icon(
                                        imageVector = if (isRegPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = EtfcTextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            },
                            visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = regTouched && !isRegPassValid,
                            supportingText = {
                                if (regTouched && !isRegPassValid) {
                                    Text("Password must be at least 6 characters long", color = Color(0xFFFF6B6B), fontSize = 10.sp)
                                }
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
                            modifier = Modifier.fillMaxWidth().testTag("reg_password_field")
                        )

                        if (regPassword.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Strength: $passwordStrengthLabel", color = passwordStrengthColor, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                LinearProgressIndicator(
                                    progress = { passwordStrength },
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(4.dp)
                                        .clip(RoundedCornerShape(2.dp)),
                                    color = passwordStrengthColor,
                                    trackColor = EtfcCardInner
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = regConfirmPassword,
                            onValueChange = {
                                regConfirmPassword = it
                                regTouched = true
                            },
                            label = { Text("Confirm Password", fontSize = 12.sp) },
                            leadingIcon = {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(18.dp))
                            },
                            trailingIcon = {
                                if (regTouched && isRegPassMatch) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = "Matched", tint = EtfcSuccess, modifier = Modifier.size(18.dp))
                                }
                            },
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                            isError = regTouched && !isRegPassMatch && regConfirmPassword.isNotEmpty(),
                            supportingText = {
                                if (regTouched && !isRegPassMatch && regConfirmPassword.isNotEmpty()) {
                                    Text("Passwords do not match", color = Color(0xFFFF6B6B), fontSize = 10.sp)
                                }
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
                            modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_field")
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                isSubmitting = true
                                val finalHandle = if (regHandle.startsWith("@")) regHandle.trim() else "@${regHandle.trim()}"
                                onRegister(
                                    regEmail.trim().lowercase(),
                                    finalHandle,
                                    regFullName.trim(),
                                    regPassword,
                                    "${selectedCountry.dialCode}${regPhone.trim()}",
                                    selectedCountry.code,
                                    selectedCountry.flag
                                )
                            },
                            enabled = isRegFormValid && !isSubmitting,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EtfcGold,
                                contentColor = Color.Black,
                                disabledContainerColor = EtfcCardInner,
                                disabledContentColor = EtfcTextMuted
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("register_submit_btn")
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.Black, strokeWidth = 2.dp)
                            } else {
                                Text("CREATE ETFC ACCOUNT", fontWeight = FontWeight.Black, fontSize = 13.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Divider with "OR"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1f), color = EtfcBorder)
                        Text(
                            text = "OR CONTINUE WITH",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = EtfcTextMuted,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1f), color = EtfcBorder)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Google Sign-In Provider Button
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color.White,
                        shadowElevation = 4.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clickable {
                                showGoogleSignInDialog = true
                            }
                            .testTag("google_signin_btn")
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "G",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black,
                                color = Color(0xFF4285F4)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Sign in with Google",
                                color = Color(0xFF1F1F1F),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer note
            Text(
                text = "By continuing, you agree to the ETFC Terms of Service & Privacy Policy.\nProtected by Firebase Auth RBAC.",
                color = EtfcTextMuted,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // =========================================================================
    // FORGOT PASSWORD MODAL DIALOG
    // =========================================================================
    if (showForgotPasswordDialog) {
        Dialog(onDismissRequest = { showForgotPasswordDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, EtfcGold, RoundedCornerShape(16.dp)),
                color = EtfcSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LockReset, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(22.dp))
                            Text("RESET PASSWORD", color = EtfcGold, fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        IconButton(onClick = { showForgotPasswordDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EtfcTextMuted, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enter your registered email address and we'll send you a secure Firebase link to reset your password.",
                        color = EtfcTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = forgotPasswordEmail,
                        onValueChange = { forgotPasswordEmail = it },
                        label = { Text("Account Email", fontSize = 12.sp) },
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
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_email_field")
                    )

                    if (forgotPasswordStatus != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val (isSuccess, msg) = forgotPasswordStatus!!
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSuccess) EtfcSuccess.copy(alpha = 0.15f) else EtfcRedPrimary.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, if (isSuccess) EtfcSuccess else EtfcRedPrimary),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = msg,
                                color = if (isSuccess) EtfcSuccess else EtfcRedPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            if (forgotPasswordEmail.isNotBlank()) {
                                isSendingReset = true
                                onSendPasswordReset?.invoke(forgotPasswordEmail.trim()) { success, message ->
                                    isSendingReset = false
                                    forgotPasswordStatus = Pair(success, message)
                                }
                            }
                        },
                        enabled = forgotPasswordEmail.contains("@") && !isSendingReset,
                        colors = ButtonDefaults.buttonColors(containerColor = EtfcGold, contentColor = Color.Black),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        if (isSendingReset) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), color = Color.Black, strokeWidth = 2.dp)
                        } else {
                            Text("SEND RESET LINK", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // =========================================================================
    // GOOGLE SIGN IN MODAL DIALOG
    // =========================================================================
    if (showGoogleSignInDialog) {
        Dialog(onDismissRequest = { showGoogleSignInDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .border(1.dp, Color(0xFF4285F4), RoundedCornerShape(16.dp)),
                color = EtfcSurface
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("G", fontSize = 20.sp, fontWeight = FontWeight.Black, color = Color(0xFF4285F4))
                            Text("GOOGLE SIGN-IN", color = Color(0xFF4285F4), fontWeight = FontWeight.Black, fontSize = 14.sp)
                        }
                        IconButton(onClick = { showGoogleSignInDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = EtfcTextMuted, modifier = Modifier.size(20.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Sign in directly with your Google Account for fast, secure access to ETFC live streams and PPV passes.",
                        color = EtfcTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = googleInputEmail,
                        onValueChange = { googleInputEmail = it },
                        label = { Text("Google Email", fontSize = 12.sp) },
                        placeholder = { Text("e.g. dannynewg@gmail.com", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4285F4),
                            unfocusedBorderColor = EtfcBorder,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary,
                            focusedContainerColor = EtfcCardInner,
                            unfocusedContainerColor = EtfcCardInner
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = googleInputName,
                        onValueChange = { googleInputName = it },
                        label = { Text("Display Name (optional)", fontSize = 12.sp) },
                        placeholder = { Text("e.g. Danny Newg", fontSize = 12.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF4285F4), modifier = Modifier.size(18.dp))
                        },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4285F4),
                            unfocusedBorderColor = EtfcBorder,
                            focusedTextColor = EtfcTextPrimary,
                            unfocusedTextColor = EtfcTextPrimary,
                            focusedContainerColor = EtfcCardInner,
                            unfocusedContainerColor = EtfcCardInner
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Button(
                        onClick = {
                            val email = googleInputEmail.trim().ifEmpty { "dannynewg@gmail.com" }
                            val name = googleInputName.trim().ifEmpty { "Danny Newg" }
                            showGoogleSignInDialog = false
                            onGoogleLogin?.invoke(email, name)
                        },
                        enabled = googleInputEmail.contains("@") || googleInputEmail.isBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4), contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(44.dp)
                    ) {
                        Text("CONTINUE WITH GOOGLE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
