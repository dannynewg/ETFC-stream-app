package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Fighter
import com.example.data.models.FighterHairStyle
import com.example.ui.theme.EtfcBlueCorner
import com.example.ui.theme.EtfcGold
import com.example.ui.theme.EtfcRedPrimary

enum class AvatarCorner {
    NONE,
    RED,
    BLUE,
    GOLD
}

@Composable
fun FighterAvatarView(
    fighter: Fighter,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    corner: AvatarCorner = AvatarCorner.NONE,
    showFlag: Boolean = true,
    showChampionCrown: Boolean = true
) {
    val borderColor = when (corner) {
        AvatarCorner.RED -> EtfcRedPrimary
        AvatarCorner.BLUE -> EtfcBlueCorner
        AvatarCorner.GOLD -> EtfcGold
        AvatarCorner.NONE -> if (fighter.isChampion) EtfcGold else Color(0xFF333333)
    }

    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        // Outer avatar container
        Surface(
            shape = CircleShape,
            color = Color(0xFF141414),
            border = androidx.compose.foundation.BorderStroke(
                width = if (fighter.isChampion || corner != AvatarCorner.NONE) 2.dp else 1.dp,
                color = borderColor
            ),
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
        ) {
            Canvas(modifier = Modifier.size(size)) {
                drawFighterPortrait(fighter = fighter, canvasSize = this.size)
            }
        }

        // Championship Crown Badge
        if (showChampionCrown && fighter.isChampion) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF111111),
                border = androidx.compose.foundation.BorderStroke(1.dp, EtfcGold),
                modifier = Modifier
                    .size(size * 0.36f)
                    .align(Alignment.TopEnd)
                    .offset(x = 2.dp, y = (-2).dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "👑",
                        fontSize = (size.value * 0.18f).sp
                    )
                }
            }
        }

        // Nationality Flag Badge
        if (showFlag && fighter.flagEmoji.isNotBlank()) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF111111),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, Color(0xFF444444)),
                modifier = Modifier
                    .size(size * 0.34f)
                    .align(Alignment.BottomEnd)
                    .offset(x = 1.dp, y = 1.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = fighter.flagEmoji,
                        fontSize = (size.value * 0.17f).sp
                    )
                }
            }
        }
    }
}

private fun DrawScope.drawFighterPortrait(fighter: Fighter, canvasSize: Size) {
    val w = canvasSize.width
    val h = canvasSize.height
    val cx = w / 2f
    val cy = h / 2f

    // 1. Studio backdrop gradient
    drawRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color(0xFF282418),
                Color(0xFF12110D),
                Color(0xFF070707)
            ),
            center = Offset(cx, cy),
            radius = w * 0.8f
        )
    )

    // 2. Athletic muscular shoulders / torso
    val skinTone = Color(0xFF6B452B) // Warm rich skin tone
    val skinToneDark = Color(0xFF4C2F1C)
    val hairDark = Color(0xFF16110D)
    val hairBlack = Color(0xFF0A0806)

    val bodyPath = Path().apply {
        moveTo(cx - w * 0.44f, h)
        quadraticBezierTo(cx - w * 0.35f, h * 0.68f, cx - w * 0.16f, h * 0.62f)
        lineTo(cx - w * 0.16f, h * 0.50f)
        lineTo(cx + w * 0.16f, h * 0.50f)
        lineTo(cx + w * 0.16f, h * 0.62f)
        quadraticBezierTo(cx + w * 0.35f, h * 0.68f, cx + w * 0.44f, h)
        close()
    }
    drawPath(bodyPath, skinToneDark)

    // Neck & Trap highlights
    val neckPath = Path().apply {
        moveTo(cx - w * 0.14f, h * 0.72f)
        lineTo(cx - w * 0.13f, h * 0.48f)
        lineTo(cx + w * 0.13f, h * 0.48f)
        lineTo(cx + w * 0.14f, h * 0.72f)
        close()
    }
    drawPath(neckPath, skinTone)

    // 3. Head Shape
    val headRadius = w * 0.22f
    val headCenterY = h * 0.40f

    // 4. Special Hair & Style Customization per fighter photo
    when (fighter.hairStyle) {
        FighterHairStyle.HIJAB_HOOD -> {
            // Zahara - Black/Navy Sports Hijab covering hair and neck
            val hijabPath = Path().apply {
                moveTo(cx - w * 0.32f, h * 0.78f)
                quadraticBezierTo(cx - w * 0.32f, headCenterY - headRadius * 1.3f, cx, headCenterY - headRadius * 1.35f)
                quadraticBezierTo(cx + w * 0.32f, headCenterY - headRadius * 1.3f, cx + w * 0.32f, h * 0.78f)
                lineTo(cx + w * 0.18f, h * 0.85f)
                lineTo(cx - w * 0.18f, h * 0.85f)
                close()
            }
            drawPath(hijabPath, Color(0xFF182230))
            drawPath(hijabPath, Color(0xFF28364A), style = Stroke(width = 1.5f))

            // Oval face cutout
            drawOval(
                color = skinTone,
                topLeft = Offset(cx - headRadius * 0.78f, headCenterY - headRadius * 0.85f),
                size = Size(headRadius * 1.56f, headRadius * 1.85f)
            )
        }

        FighterHairStyle.BALD_BEARD -> {
            // Sedo - Clean bald athletic head with robust beard
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
            // Jawline & Beard
            val beardPath = Path().apply {
                moveTo(cx - headRadius * 0.85f, headCenterY + headRadius * 0.15f)
                quadraticBezierTo(cx, headCenterY + headRadius * 1.25f, cx + headRadius * 0.85f, headCenterY + headRadius * 0.15f)
                quadraticBezierTo(cx, headCenterY + headRadius * 0.95f, cx - headRadius * 0.85f, headCenterY + headRadius * 0.15f)
                close()
            }
            drawPath(beardPath, hairBlack)
            // Mustache
            drawRoundRect(
                color = hairBlack,
                topLeft = Offset(cx - headRadius * 0.4f, headCenterY + headRadius * 0.35f),
                size = Size(headRadius * 0.8f, headRadius * 0.22f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }

        FighterHairStyle.CURLY_BEARD -> {
            // Jhonny - Big voluminous curly hair + full beard + championship gold
            // Hair puff background
            drawCircle(
                color = hairBlack,
                radius = headRadius * 1.25f,
                center = Offset(cx, headCenterY - headRadius * 0.25f)
            )
            drawCircle(
                color = skinTone,
                radius = headRadius * 0.95f,
                center = Offset(cx, headCenterY)
            )
            // Thick beard
            val beardPath = Path().apply {
                moveTo(cx - headRadius * 0.9f, headCenterY)
                quadraticBezierTo(cx, headCenterY + headRadius * 1.35f, cx + headRadius * 0.9f, headCenterY)
                quadraticBezierTo(cx, headCenterY + headRadius * 0.9f, cx - headRadius * 0.9f, headCenterY)
                close()
            }
            drawPath(beardPath, hairBlack)
            // Gold belt accent on shoulder
            drawRect(
                color = EtfcGold,
                topLeft = Offset(cx + w * 0.18f, h * 0.72f),
                size = Size(w * 0.26f, h * 0.28f)
            )
        }

        FighterHairStyle.HIGH_TOP -> {
            // Stephen - Vertical High-top fade / curly top
            val highTopPath = Path().apply {
                moveTo(cx - headRadius * 0.85f, headCenterY - headRadius * 0.1f)
                lineTo(cx - headRadius * 0.75f, headCenterY - headRadius * 1.45f)
                quadraticBezierTo(cx, headCenterY - headRadius * 1.55f, cx + headRadius * 0.75f, headCenterY - headRadius * 1.45f)
                lineTo(cx + headRadius * 0.85f, headCenterY - headRadius * 0.1f)
                close()
            }
            drawPath(highTopPath, hairBlack)
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
        }

        FighterHairStyle.BRAIDS_TIED -> {
            // Yabsira / Robel - Braids or top-knot
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
            drawArc(
                color = hairBlack,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - headRadius, headCenterY - headRadius * 1.1f),
                size = Size(headRadius * 2f, headRadius * 1.1f)
            )
            // Top knot / hair tie
            drawCircle(
                color = hairDark,
                radius = headRadius * 0.35f,
                center = Offset(cx, headCenterY - headRadius * 1.15f)
            )
        }

        FighterHairStyle.SHAVED_CLEAN -> {
            // Boyka / Surafel - Very short buzz / shaved athletic cut
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
            drawArc(
                color = hairDark.copy(alpha = 0.5f),
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - headRadius, headCenterY - headRadius),
                size = Size(headRadius * 2f, headRadius * 0.75f)
            )
            // Goatee
            drawCircle(
                color = hairBlack,
                radius = headRadius * 0.22f,
                center = Offset(cx, headCenterY + headRadius * 0.78f)
            )
        }

        FighterHairStyle.CURLY_SHORT -> {
            // Esubalew / Habtamu - Short afro / tight athletic curls
            drawCircle(
                color = hairBlack,
                radius = headRadius * 1.12f,
                center = Offset(cx, headCenterY - headRadius * 0.15f)
            )
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
            // Light beard / mustache
            drawRoundRect(
                color = hairBlack,
                topLeft = Offset(cx - headRadius * 0.4f, headCenterY + headRadius * 0.4f),
                size = Size(headRadius * 0.8f, headRadius * 0.18f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )
        }

        FighterHairStyle.FADE_BEARD -> {
            // Elezar, Edris, Desalegn, etc. - Clean fade haircut + styled beard
            drawArc(
                color = hairBlack,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = true,
                topLeft = Offset(cx - headRadius, headCenterY - headRadius * 1.05f),
                size = Size(headRadius * 2f, headRadius * 1.05f)
            )
            drawCircle(
                color = skinTone,
                radius = headRadius,
                center = Offset(cx, headCenterY)
            )
            // Styled beard
            val beardPath = Path().apply {
                moveTo(cx - headRadius * 0.85f, headCenterY + headRadius * 0.2f)
                quadraticBezierTo(cx, headCenterY + headRadius * 1.25f, cx + headRadius * 0.85f, headCenterY + headRadius * 0.2f)
                quadraticBezierTo(cx, headCenterY + headRadius * 0.85f, cx - headRadius * 0.85f, headCenterY + headRadius * 0.2f)
                close()
            }
            drawPath(beardPath, hairBlack)
        }
    }

    // 5. Facial features (Brows & Eyes silhouette)
    val eyeColor = Color(0xFF1E140C)
    drawRoundRect(
        color = eyeColor,
        topLeft = Offset(cx - headRadius * 0.55f, headCenterY - headRadius * 0.12f),
        size = Size(headRadius * 0.32f, headRadius * 0.12f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )
    drawRoundRect(
        color = eyeColor,
        topLeft = Offset(cx + headRadius * 0.23f, headCenterY - headRadius * 0.12f),
        size = Size(headRadius * 0.32f, headRadius * 0.12f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2f, 2f)
    )

    // 6. Initials Monogram Watermark at bottom
    val initials = fighter.name.split(" ").mapNotNull { it.firstOrNull()?.toString() }.take(2).joinToString("")
}
