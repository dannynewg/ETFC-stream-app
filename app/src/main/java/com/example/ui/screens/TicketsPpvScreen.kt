package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PpvPassEntity
import com.example.data.models.PaymentProvider
import com.example.data.models.PpvTier
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TicketsPpvScreen(
    ppvTiers: List<PpvTier>,
    activePass: PpvPassEntity?,
    allPasses: List<PpvPassEntity>,
    onSelectTierToBuy: (PpvTier) -> Unit
) {
    val dateFormatter = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault())

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(EtfcObsidian)
            .padding(horizontal = 14.dp)
            .testTag("ppv_tickets_screen"),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Column {
                Text(
                    text = "PAY-PER-VIEW & ACCESS TICKETS",
                    color = EtfcGold,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = "Instant billing via Safaricom M-Pesa & Telebirr API webhooks",
                    color = EtfcTextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        // Active Ticket Pass Card (if present)
        item {
            if (activePass != null) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .border(1.dp, EtfcSuccess.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = EtfcCard)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color(0xFF0C2415), EtfcCard)
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ConfirmationNumber,
                                    contentDescription = null,
                                    tint = EtfcSuccess,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "DIGITAL PASS TOKEN",
                                    color = EtfcSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Surface(
                                color = EtfcSuccess,
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = "VERIFIED ACTIVE",
                                    color = Color.Black,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = activePass.title,
                            color = EtfcTextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = activePass.eventName,
                            color = EtfcTextSecondary,
                            fontSize = 12.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("TRANSACTION REF", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(activePass.transactionRef, color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column {
                                Text("PROVIDER", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text(activePass.paymentProvider, color = EtfcBlueCorner, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("AMOUNT PAID", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Text("${activePass.priceCurrency} ${activePass.amountPaid.toInt()}", color = EtfcGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Signature Token
                        Surface(
                            color = EtfcCardInner,
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = EtfcGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = activePass.signatureToken,
                                    color = EtfcTextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }

        // Available PPV Passes
        item {
            Text(
                text = "AVAILABLE PACKAGES",
                color = EtfcTextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.8.sp
            )
        }

        items(ppvTiers) { tier ->
            PpvTierCard(
                tier = tier,
                onBuyClick = { onSelectTierToBuy(tier) }
            )
        }

        // Local Gateways Explainer Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = EtfcCard),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "EAST AFRICAN MOBILE MONEY ECOSYSTEM",
                        color = EtfcGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ETFC Stream billing services communicate with Safaricom M-Pesa (Kenya) and Telebirr (Ethiopia) webhook endpoints. Upon PIN submission, cryptographic tokens unlock your stream in under 2 seconds.",
                        color = EtfcTextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = TelebirrBlue.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, TelebirrBlue.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "🇪🇹 Telebirr ETB Instant",
                                color = TelebirrBlue,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                        Surface(
                            color = MpesaGreen.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(6.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MpesaGreen.copy(alpha = 0.3f))
                        ) {
                            Text(
                                text = "🇰🇪 M-Pesa KES STK Push",
                                color = MpesaGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun PpvTierCard(
    tier: PpvTier,
    onBuyClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (tier.isPopular) EtfcGold else EtfcBorderSubtle,
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = EtfcCard)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = tier.title,
                    color = EtfcTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Black
                )

                if (tier.badge != null) {
                    Surface(
                        color = if (tier.isPopular) EtfcGold else EtfcCardInner,
                        shape = RoundedCornerShape(4.dp),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (tier.isPopular) EtfcGold else EtfcBorderSubtle
                        )
                    ) {
                        Text(
                            text = tier.badge,
                            color = if (tier.isPopular) Color.Black else EtfcTextSecondary,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Text(
                text = tier.subtitle,
                color = EtfcTextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Row
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "ETB ${tier.priceEtb}",
                    color = EtfcGold,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "/ KES ${tier.priceKes}",
                    color = EtfcTextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Features checklist
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                tier.features.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = EtfcSuccess,
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = feature,
                            color = EtfcTextPrimary,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onBuyClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("buy_tier_${tier.id}_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (tier.isPopular) EtfcGold else EtfcCardInner,
                    contentColor = if (tier.isPopular) Color.Black else EtfcTextPrimary
                ),
                border = if (!tier.isPopular) androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle) else null,
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = if (tier.isPopular) Color.Black else EtfcTextPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Buy Pass (M-Pesa / Telebirr)",
                    color = if (tier.isPopular) Color.Black else EtfcTextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
