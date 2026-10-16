/*
 * SA Launcher - Neobrutalism Components & Modifiers
 * Based on UI/UX Product Requirement Document & Visual Specifications
 */

package com.movtery.zalithlauncher.ui.theme.neobrutal

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt

/**
 * 1. Retro Polka-Dot Canvas Grid Background
 * Paints the classic neobrutal canvas grid across the screen
 */
@Composable
fun NeobrutalDottedCanvas(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeobrutalTokens.CanvasColor,
    dotColor: Color = NeobrutalTokens.GridDotColor,
    dotRadius: Dp = 1.5.dp,
    gridSpacing: Dp = 18.dp,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundColor)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacingPx = gridSpacing.toPx()
            val radiusPx = dotRadius.toPx()
            val cols = (size.width / spacingPx).toInt() + 1
            val rows = (size.height / spacingPx).toInt() + 1

            for (i in 0 until cols) {
                for (j in 0 until rows) {
                    drawCircle(
                        color = dotColor,
                        radius = radiusPx,
                        center = Offset(i * spacingPx, j * spacingPx)
                    )
                }
            }
        }
        content()
    }
}

/**
 * 2. Neobrutal Container / Card
 * Card with solid thick black border and hard drop shadow (no blur)
 */
@Composable
fun NeobrutalCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeobrutalTokens.CardWhite,
    borderColor: Color = NeobrutalTokens.BorderHard,
    borderWidth: Dp = NeobrutalTokens.BorderStandard,
    shadowColor: Color = NeobrutalTokens.ShadowHard,
    shadowOffset: Dp = NeobrutalTokens.ShadowStandard,
    cornerRadius: Dp = NeobrutalTokens.RadiusCard,
    content: @Composable BoxScope.() -> Unit
) {
    val shape: Shape = RoundedCornerShape(cornerRadius)

    Box(modifier = modifier) {
        // Hard solid shadow
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, shape = shape)
            )
        }

        // Foreground card with border
        Box(
            modifier = Modifier
                .background(backgroundColor, shape = shape)
                .border(borderWidth, borderColor, shape = shape)
                .clip(shape)
        ) {
            content()
        }
    }
}

/**
 * 3. Neobrutal Button with Mechanical Click Feedback
 * Shifts position diagonally when pressed and flattens shadow
 */
@Composable
fun NeobrutalButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeobrutalTokens.AccentPrimary,
    borderColor: Color = NeobrutalTokens.BorderHard,
    borderWidth: Dp = NeobrutalTokens.BorderStandard,
    shadowColor: Color = NeobrutalTokens.ShadowHard,
    shadowOffset: Dp = NeobrutalTokens.ShadowStandard,
    cornerRadius: Dp = NeobrutalTokens.RadiusButton,
    enabled: Boolean = true,
    content: @Composable BoxScope.() -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val view = LocalView.current

    // Trigger haptic tick on press
    LaunchedEffect(isPressed) {
        if (isPressed) {
            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
        }
    }

    val currentOffset by animateDpAsState(
        targetValue = if (isPressed) shadowOffset else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "ButtonPressOffset"
    )

    val currentShadow by animateDpAsState(
        targetValue = if (isPressed) 0.dp else shadowOffset,
        animationSpec = tween(durationMillis = 60),
        label = "ButtonShadowOffset"
    )

    val shape = RoundedCornerShape(cornerRadius)

    Box(modifier = modifier) {
        // Shadow underneath
        if (shadowOffset > 0.dp) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .offset(x = shadowOffset, y = shadowOffset)
                    .background(shadowColor, shape = shape)
            )
        }

        // Pressable top body
        Box(
            modifier = Modifier
                .offset { IntOffset(currentOffset.roundToPx(), currentOffset.roundToPx()) }
                .background(backgroundColor, shape = shape)
                .border(borderWidth, borderColor, shape = shape)
                .clip(shape)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    enabled = enabled,
                    onClick = onClick
                )
        ) {
            content()
        }
    }
}

/**
 * 4. Neobrutal Capsule Pill Badge
 */
@Composable
fun NeobrutalPillBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = NeobrutalTokens.CardWhite,
    textColor: Color = NeobrutalTokens.TextPrimary,
    borderColor: Color = NeobrutalTokens.BorderHard,
    borderWidth: Dp = 2.dp
) {
    val shape = RoundedCornerShape(NeobrutalTokens.RadiusPill)
    Box(
        modifier = modifier
            .background(backgroundColor, shape = shape)
            .border(borderWidth, borderColor, shape = shape)
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1
        )
    }
}

/**
 * 5. Retro Window Box with Title Bar
 */
@Composable
fun NeobrutalWindowBox(
    title: String,
    modifier: Modifier = Modifier,
    headerColor: Color = NeobrutalTokens.AccentSecondary,
    headerTextColor: Color = Color.White,
    statusPill: String? = null,
    statusPillColor: Color = NeobrutalTokens.AccentMint,
    onCloseClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    NeobrutalCard(
        modifier = modifier,
        backgroundColor = NeobrutalTokens.CardWhite,
        shadowOffset = NeobrutalTokens.ShadowStandard,
        cornerRadius = NeobrutalTokens.RadiusCard
    ) {
        Column {
            // Window Header Bar
            Row(
                modifier = Modifier
                    .background(headerColor)
                    .border(width = 0.dp, color = Color.Transparent)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = headerTextColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.weight(1f)
                )

                if (statusPill != null) {
                    NeobrutalPillBadge(
                        text = statusPill,
                        backgroundColor = statusPillColor,
                        textColor = Color.Black
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                }

                if (onCloseClick != null) {
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .background(Color.White, RoundedCornerShape(4.dp))
                            .border(1.5.dp, Color.Black, RoundedCornerShape(4.dp))
                            .clickable { onCloseClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "×",
                            color = Color.Black,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            // Divider line below header
            Box(
                modifier = Modifier
                    .background(NeobrutalTokens.BorderHard)
                    .size(width = 2000.dp, height = 2.dp)
            )

            // Card Body
            Box(modifier = Modifier.padding(10.dp)) {
                content()
            }
        }
    }
}
