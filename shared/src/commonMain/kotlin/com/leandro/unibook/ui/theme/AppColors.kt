package com.leandro.unibook.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Cores centralizadas do aplicativo.
 * Altere a cor [Primary] aqui para atualizar o tema em todo o aplicativo!
 */
object AppColors {
    // Cor Principal do App (Azul Marinho / Navy Blue #000080)
    val Primary = Color(0xFF000080)
    
    // Cor usada nos elementos gráficos do padrão do cabeçalho
    val HeaderPattern = Color(0xFF1F1F9E)
    
    // Cores de Superfície e Fundo
    val Background = Color(0xFFD6E5F2)
    val CardBackground = Color(0xFFFFFFFF)
    val InputBackground = Color(0xFFF7F7F9)
    val InputBorder = Color(0xFFEEEEEE)
    
    // Cores de Texto
    val TextPrimary = Color(0xFF141414)
    val TextSecondary = Color(0xFF6E6E6E)
    val TextPlaceholder = Color(0xFFA0A0A0)
    val TextOnPrimary = Color(0xFFFFFFFF)
    
    // Links / Destaques
    val ActionText = Primary
}
