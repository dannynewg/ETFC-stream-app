package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsMma
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.models.Fighter
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcBorder
import com.example.ui.theme.EtfcBorderSubtle
import com.example.ui.theme.EtfcCard
import com.example.ui.theme.EtfcCardInner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcObsidian
import com.example.ui.theme.EtfcRedPrimary
import com.example.ui.theme.EtfcSurface
import com.example.ui.theme.EtfcSurfaceVariant
import com.example.ui.theme.EtfcTextMuted
import com.example.ui.theme.EtfcTextPrimary
import com.example.ui.theme.EtfcTextSecondary

@Composable
fun TaleOfTheTapeDialog(
    fighterRed: Fighter,
    fighterBlue: Fighter,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .border(1.dp, EtfcBorder, RoundedCornerShape(20.dp))
                .testTag("tale_of_the_tape_dialog"),
            colors = CardDefaults.cardColors(containerColor = EtfcObsidian)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header with Title & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TALE OF THE TAPE",
                            color = EtfcGold,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "${fighterRed.division.label} Division (${fighterRed.division.limitLbs} lbs)",
                            color = EtfcTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp).testTag("close_tape_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = EtfcTextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Fighters Face-Off Banners
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Red Fighter Header
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FighterAvatarView(
                            fighter = fighterRed,
                            size = 56.dp,
                            corner = AvatarCorner.RED
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fighterRed.name.uppercase(),
                            color = EtfcTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"${fighterRed.nickname}\"",
                            color = EtfcRedPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        if (fighterRed.isChampion) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = EtfcGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "👑 CHAMPION",
                                    color = EtfcGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else if (fighterRed.rank != null) {
                            Text(
                                text = "#${fighterRed.rank} Contender",
                                color = EtfcTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Center VS Badge
                    Surface(
                        shape = CircleShape,
                        color = EtfcCardInner,
                        border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold)
                    ) {
                        Text(
                            text = "VS",
                            color = EtfcGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    // Blue Fighter Header
                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        FighterAvatarView(
                            fighter = fighterBlue,
                            size = 56.dp,
                            corner = AvatarCorner.BLUE
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = fighterBlue.name.uppercase(),
                            color = EtfcTextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "\"${fighterBlue.nickname}\"",
                            color = EtfcBlueCorner,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )
                        if (fighterBlue.isChampion) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Surface(
                                color = EtfcGold.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(4.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold.copy(alpha = 0.4f))
                            ) {
                                Text(
                                    text = "👑 CHAMPION",
                                    color = EtfcGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        } else if (fighterBlue.rank != null) {
                            Text(
                                text = "#${fighterBlue.rank} Contender",
                                color = EtfcTextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Stats Metrics Grid
                ComparisonRow(
                    label = "PRO RECORD",
                    redVal = "${fighterRed.wins}-${fighterRed.losses}-${fighterRed.draws}",
                    blueVal = "${fighterBlue.wins}-${fighterBlue.losses}-${fighterBlue.draws}"
                )

                ComparisonRow(
                    label = "KNOCKOUT FINISHES",
                    redVal = "${fighterRed.kos} KOs (${if (fighterRed.wins > 0) (fighterRed.kos * 100 / fighterRed.wins) else 0}%)",
                    blueVal = "${fighterBlue.kos} KOs (${if (fighterBlue.wins > 0) (fighterBlue.kos * 100 / fighterBlue.wins) else 0}%)"
                )

                ComparisonRow(
                    label = "SUBMISSIONS",
                    redVal = "${fighterRed.submissions}",
                    blueVal = "${fighterBlue.submissions}"
                )

                ComparisonRow(
                    label = "HEIGHT",
                    redVal = "${fighterRed.heightCm} cm",
                    blueVal = "${fighterBlue.heightCm} cm"
                )

                ComparisonRow(
                    label = "REACH",
                    redVal = "${fighterRed.reachInches}\"",
                    blueVal = "${fighterBlue.reachInches}\""
                )

                ComparisonRow(
                    label = "STANCE",
                    redVal = fighterRed.stance,
                    blueVal = fighterBlue.stance
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Striking Accuracy Gauge
                Text(
                    text = "STRIKING ACCURACY",
                    color = EtfcTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${fighterRed.strikeAccuracyPercent}%",
                        color = EtfcRedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(36.dp)
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { fighterRed.strikeAccuracyPercent / 100f },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = EtfcRedPrimary,
                            trackColor = EtfcCardInner
                        )
                        LinearProgressIndicator(
                            progress = { fighterBlue.strikeAccuracyPercent / 100f },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = EtfcBlueCorner,
                            trackColor = EtfcCardInner
                        )
                    }
                    Text(
                        text = "${fighterBlue.strikeAccuracyPercent}%",
                        color = EtfcBlueCorner,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Takedown Defense Gauge
                Text(
                    text = "TAKEDOWN DEFENSE",
                    color = EtfcTextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${fighterRed.takedownDefensePercent}%",
                        color = EtfcRedPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(36.dp)
                    )
                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        LinearProgressIndicator(
                            progress = { fighterRed.takedownDefensePercent / 100f },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = EtfcRedPrimary,
                            trackColor = EtfcCardInner
                        )
                        LinearProgressIndicator(
                            progress = { fighterBlue.takedownDefensePercent / 100f },
                            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
                            color = EtfcBlueCorner,
                            trackColor = EtfcCardInner
                        )
                    }
                    Text(
                        text = "${fighterBlue.takedownDefensePercent}%",
                        color = EtfcBlueCorner,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.End
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Signature Moves
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EtfcCardInner)
                        .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(8.dp))
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("SIGNATURE MOVE", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(fighterRed.signatureMove, color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                        Text("SIGNATURE MOVE", color = EtfcTextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        Text(fighterBlue.signatureMove, color = EtfcTextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.End)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth().testTag("tape_done_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = EtfcCardInner),
                    border = androidx.compose.foundation.BorderStroke(1.dp, EtfcBorderSubtle),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Close Comparison", color = EtfcTextPrimary, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun ComparisonRow(
    label: String,
    redVal: String,
    blueVal: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(EtfcCardInner)
            .border(1.dp, EtfcBorderSubtle, RoundedCornerShape(6.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = redVal,
            color = EtfcTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = label,
            color = EtfcTextMuted,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1.2f)
        )
        Text(
            text = blueVal,
            color = EtfcTextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}
