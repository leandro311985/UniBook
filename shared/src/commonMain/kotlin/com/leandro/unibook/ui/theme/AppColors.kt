package com.leandro.unibook.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.material3.lightColorScheme

/**
 * Cores centralizadas do aplicativo.
 * Altere a cor [Primary] aqui para atualizar o tema em todo o aplicativo!
 */
object AppColors {
    val Primary = Color(0xFF13315C)
    val HeaderPattern = Color(0xFF244A78)
    val Background = Primary
    val CardBackground = Color(0xFFFFFFFF)
    val SurfaceSecondary = Color(0xFFEAF1F8)
    val InputBackground = CardBackground
    val InputBorder = Color(0xFFCBD5E1)

    val TextPrimary = Color(0xFF1E293B)
    val TextSecondary = Color(0xFF64748B)
    val TextPlaceholder = Color(0xFF64748B)
    val TextOnPrimary = Color(0xFFFFFFFF)

    val Accent = Color(0xFFF2B84B)
    val Success = Color(0xFF237A4B)
    val SuccessContainer = Color(0xFFEAF5EE)
    val Error = Color(0xFFB42318)
    val ErrorContainer = Color(0xFFFEE4E2)
    val ActionText = Primary

    val ColorScheme = lightColorScheme(
        primary = Primary,
        onPrimary = TextOnPrimary,
        secondary = Accent,
        onSecondary = Primary,
        tertiary = HeaderPattern,
        onTertiary = TextOnPrimary,
        background = Background,
        onBackground = TextPrimary,
        surface = CardBackground,
        onSurface = TextPrimary,
        surfaceVariant = SurfaceSecondary,
        onSurfaceVariant = TextSecondary,
        error = Error,
        onError = TextOnPrimary,
        errorContainer = ErrorContainer,
        onErrorContainer = Error,
        outline = InputBorder
    )
}
