package com.leandro.unibook.home.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.tooling.preview.Preview
import com.leandro.unibook.ui.theme.AppColors

@Composable
fun FundoAzulComCurva(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.Primary)
            .drawBehind {
                val width = size.width
                val height = size.height

                val path = Path().apply {
                    // Começa na borda esquerda, perto da parte inferior
                    moveTo(0f, height * 0.90f)

                    // Desenha uma curva suave subindo em direção ao canto inferior direito
                    cubicTo(
                        x1 = width * 0.45f, y1 = height * 0.94f, // Ponto de controle 1 (mantém baixo à esquerda)
                        x2 = width * 0.65f, y2 = height * 0.72f, // Ponto de controle 2 (puxa a curva para cima)
                        x3 = width,         y3 = height * 0.74f  // Ponto final na borda direita
                    )

                    // Fecha o desenho pelos cantos inferiores da tela
                    lineTo(width, height)
                    lineTo(0f, height)
                    close()
                }

                drawPath(
                    path = path,
                    color = AppColors.HeaderPattern
                )
            },
        content = content
    )
}

@Composable
@Preview(showBackground = true)
private fun FundoAzulComCurvaPreview() {
    FundoAzulComCurva()
}
