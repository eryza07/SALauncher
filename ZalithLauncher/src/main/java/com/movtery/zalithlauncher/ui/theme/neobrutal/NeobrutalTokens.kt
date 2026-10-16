/*
 * SA Launcher - Neobrutalism Design System Tokens
 * Based on UI/UX Product Requirement Document
 */

package com.movtery.zalithlauncher.ui.theme.neobrutal

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object NeobrutalTokens {
    // 2.1 Color Palette Tokens (Preset D: Flame Sunset)
    val CanvasColor = Color(0xFFFFFDF7)           // Soft warm sand cream background
    val CanvasColorAlt = Color(0xFFFFFBF0)        // Clean warm cream canvas
    val CardWhite = Color(0xFFFFFFFF)             // Card background
    val BorderHard = Color(0xFF000000)            // Strict black outline for all cards & buttons
    val ShadowHard = Color(0xFF000000)            // Solid hard drop shadow (no blur)
    
    val AccentPrimary = Color(0xFFFF6B00)         // Flame Orange (Hero CTA, Play Button, Brand)
    val AccentSecondary = Color(0xFF1E293B)       // Dark Slate Blue (Header bar, active chip)
    val AccentMint = Color(0xFFFFD600)            // Radiant Gold (Online status, RAM OK, safe)
    val AccentPink = Color(0xFFFF4757)            // Coral Red (Settings tab, crash alert, badge)
    val AccentOrange = Color(0xFFFF8C32)          // Vivid Amber (Renderer selector, instance tag)
    
    val TextPrimary = Color(0xFF000000)           // Maximum contrast title text
    val TextMuted = Color(0xFF4B4B4B)             // Secondary description / metadata text
    val GridDotColor = Color(0x28000000)          // Retro polka-dot canvas grid

    // 2.2 Elevation & Shadow Dimensions
    val BorderStandard: Dp = 2.5.dp
    val BorderHeavy: Dp = 3.dp
    val ShadowStandard: Dp = 4.dp
    val ShadowHero: Dp = 6.dp
    
    // 2.3 Corner Radius
    val RadiusCard: Dp = 12.dp
    val RadiusButton: Dp = 10.dp
    val RadiusPill: Dp = 999.dp
}
