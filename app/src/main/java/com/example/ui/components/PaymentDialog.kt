package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.PpvPassEntity
import com.example.data.models.PaymentProvider
import com.example.data.models.PpvTier
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
import com.example.ui.theme.MpesaGreen
import com.example.ui.theme.TelebirrBlue
import com.example.ui.viewmodel.PaymentUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PaymentCheckoutDialog(
    tier: PpvTier,
    selectedProvider: PaymentProvider,
    phoneNumber: String,
    paymentState: PaymentUiState,
    onProviderSelected: (PaymentProvider) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit,
    onSuccessDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, EtfcBorder, RoundedCornerShape(20.dp))
                .testTag("ppv_checkout_dialog"),
            colors = CardDefaults.cardColors(containerColor = EtfcObsidian)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                when (paymentState) {
                    is PaymentUiState.Success -> {
                        PaymentSuccessView(
                            pass = paymentState.pass,
                            onWatchNow = onSuccessDismiss
                        )
                    }
                    is PaymentUiState.Processing -> {
                        PaymentProcessingView(
                            provider = paymentState.provider,
                            message = paymentState.message
                        )
                    }
                    else -> {
                        // Standard Checkout Form
                        CheckoutForm(
                            tier = tier,
                            selectedProvider = selectedProvider,
                            phoneNumber = phoneNumber,
                            errorMessage = (paymentState as? PaymentUiState.Error)?.error,
                            onProviderSelected = onProviderSelected,
                            onPhoneChanged = onPhoneChanged,
                            onConfirmPayment = onConfirmPayment,
                            onDismiss = onDismiss
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CheckoutForm(
    tier: PpvTier,
    selectedProvider: PaymentProvider,
    phoneNumber: String,
    errorMessage: String?,
    onProviderSelected: (PaymentProvider) -> Unit,
    onPhoneChanged: (String) -> Unit,
    onConfirmPayment: () -> Unit,
    onDismiss: () -> Unit
) {
    // Header
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "INSTANT PPV UNLOCK",
                color = EtfcGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.8.sp
            )
            Text(
                text = "Regional Mobile Money Billing Gateway",
                color = EtfcTextSecondary,
                fontSize = 11.sp
            )
        }
        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp).testTag("close_checkout_btn")) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = EtfcTextPrimary)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Tier Summary Card
    Surface(
        color = EtfcCard,
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tier.title,
                    color = EtfcTextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = tier.subtitle,
                    color = EtfcTextSecondary,
                    fontSize = 11.sp
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = if (selectedProvider == PaymentProvider.MPESA) "KES ${tier.priceKes}" else "ETB ${tier.priceEtb}",
                    color = EtfcGold,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Instant Token",
                    color = EtfcSuccess,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Gateway Selection (Safaricom M-Pesa vs Telebirr)
    Text(
        text = "SELECT LOCAL PAYMENT PROVIDER",
        color = EtfcTextSecondary,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.6.sp,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(6.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Telebirr Button
        val isTelebirr = selectedProvider == PaymentProvider.TELEBIRR
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onProviderSelected(PaymentProvider.TELEBIRR) }
                .testTag("select_telebirr_btn"),
            shape = RoundedCornerShape(10.dp),
            color = if (isTelebirr) TelebirrBlue.copy(alpha = 0.15f) else EtfcCardInner,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isTelebirr) TelebirrBlue else EtfcBorderSubtle
            )
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🇪🇹 Telebirr",
                    color = if (isTelebirr) EtfcTextPrimary else EtfcTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "SuperApp / OTP",
                    color = EtfcTextMuted,
                    fontSize = 9.sp
                )
            }
        }

        // Safaricom M-Pesa Button
        val isMpesa = selectedProvider == PaymentProvider.MPESA
        Surface(
            modifier = Modifier
                .weight(1f)
                .clickable { onProviderSelected(PaymentProvider.MPESA) }
                .testTag("select_mpesa_btn"),
            shape = RoundedCornerShape(10.dp),
            color = if (isMpesa) MpesaGreen.copy(alpha = 0.15f) else EtfcCardInner,
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isMpesa) MpesaGreen else EtfcBorderSubtle
            )
        ) {
            Column(
                modifier = Modifier.padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "🇰🇪 M-Pesa",
                    color = if (isMpesa) EtfcTextPrimary else EtfcTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Daraja STK Push",
                    color = EtfcTextMuted,
                    fontSize = 9.sp
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Phone Input Field
    Text(
        text = if (selectedProvider == PaymentProvider.MPESA) "Safaricom M-Pesa Mobile Number" else "Ethio Telecom Telebirr Number",
        color = EtfcTextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.fillMaxWidth()
    )
    Spacer(modifier = Modifier.height(4.dp))

    OutlinedTextField(
        value = phoneNumber,
        onValueChange = onPhoneChanged,
        prefix = {
            Text(
                text = "${selectedProvider.defaultPrefix} ",
                color = EtfcGold,
                fontWeight = FontWeight.Bold
            )
        },
        placeholder = { Text("712345678", color = EtfcTextMuted) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("phone_input_field"),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = if (selectedProvider == PaymentProvider.MPESA) MpesaGreen else TelebirrBlue,
            unfocusedBorderColor = EtfcBorder,
            focusedContainerColor = EtfcCardInner,
            unfocusedContainerColor = EtfcCardInner,
            focusedTextColor = EtfcTextPrimary,
            unfocusedTextColor = EtfcTextPrimary
        ),
        shape = RoundedCornerShape(10.dp)
    )

    if (errorMessage != null) {
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(imageVector = Icons.Default.Error, contentDescription = null, tint = EtfcLiveRed, modifier = Modifier.size(14.dp))
            Text(text = errorMessage, color = EtfcLiveRed, fontSize = 11.sp)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Security & Microservice Token Badge
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(EtfcCardInner)
            .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            tint = EtfcSuccess,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Decoupled webhook token validation ensures instantaneous unlock upon PIN confirmation.",
            color = EtfcTextSecondary,
            fontSize = 10.sp
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Confirm Payment Button
    Button(
        onClick = onConfirmPayment,
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("confirm_pay_btn"),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedProvider == PaymentProvider.MPESA) MpesaGreen else TelebirrBlue
        )
    ) {
        Icon(
            imageVector = Icons.Default.Lock,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = if (selectedProvider == PaymentProvider.MPESA) "Pay KES ${tier.priceKes} via M-Pesa" else "Pay ETB ${tier.priceEtb} via Telebirr",
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun PaymentProcessingView(
    provider: PaymentProvider,
    message: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = if (provider == PaymentProvider.MPESA) MpesaGreen else TelebirrBlue,
            modifier = Modifier.size(48.dp),
            strokeWidth = 4.dp
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Processing ${provider.displayName} Webhook...",
            color = EtfcTextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = message,
            color = EtfcTextSecondary,
            fontSize = 12.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun PaymentSuccessView(
    pass: PpvPassEntity,
    onWatchNow: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.CheckCircle,
            contentDescription = "Success",
            tint = EtfcSuccess,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "PPV PASS UNLOCKED!",
            color = EtfcGold,
            fontSize = 18.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
        )
        Text(
            text = pass.eventName,
            color = EtfcTextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Digital Ticket Pass Voucher Card
        Surface(
            color = EtfcCard,
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcSuccess.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("TRANSACTION REF", color = EtfcTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(pass.transactionRef, color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("PROVIDER", color = EtfcTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(pass.paymentProvider, color = EtfcBlueCorner, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("STATUS", color = EtfcTextSecondary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text("VERIFIED & ACTIVE", color = EtfcSuccess, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Cryptographic token badge
                Surface(
                    color = EtfcCardInner,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(imageVector = Icons.Default.QrCode, contentDescription = null, tint = EtfcGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = pass.signatureToken,
                            color = EtfcTextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onWatchNow,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("watch_live_after_checkout_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = EtfcRedPrimary),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Watch Live Title Fight Now", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }
    }
}
