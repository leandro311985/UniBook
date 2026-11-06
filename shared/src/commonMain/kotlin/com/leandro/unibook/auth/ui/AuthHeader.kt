package com.leandro.unibook.auth.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.ui.theme.AppColors

@Composable
fun DarkGeometricHeader(
    modifier: Modifier = Modifier,
    title: String? = null,
    onBackClick: (() -> Unit)? = null,
    showLogo: Boolean = true
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AppColors.Primary)
    ) {
        // Geometric tiled pattern background
        Canvas(modifier = Modifier.fillMaxSize()) {
            val tileSize = 70.dp.toPx()
            val columns = (size.width / tileSize).toInt() + 1
            val rows = (size.height / tileSize).toInt() + 1
            val patternColor = AppColors.HeaderPattern

            clipRect(0f, 0f, size.width, size.height) {
                for (row in 0 until rows) {
                    for (col in 0 until columns) {
                        val left = col * tileSize
                        val top = row * tileSize
                        val type = (row * 3 + col * 7) % 4
                        when (type) {
                            0 -> {
                                drawArc(
                                    color = patternColor,
                                    startAngle = 0f,
                                    sweepAngle = 90f,
                                    useCenter = true,
                                    topLeft = Offset(left, top),
                                    size = Size(tileSize * 2, tileSize * 2)
                                )
                            }
                            1 -> {
                                drawCircle(
                                    color = patternColor,
                                    radius = tileSize / 2,
                                    center = Offset(left + tileSize / 2, top + tileSize / 2)
                                )
                            }
                            2 -> {
                                drawArc(
                                    color = patternColor,
                                    startAngle = 180f,
                                    sweepAngle = 90f,
                                    useCenter = true,
                                    topLeft = Offset(left - tileSize, top),
                                    size = Size(tileSize * 2, tileSize * 2)
                                )
                            }
                            3 -> {
                                drawRoundRect(
                                    color = patternColor,
                                    topLeft = Offset(left + 8f, top + 8f),
                                    size = Size(tileSize - 16f, tileSize - 16f),
                                    cornerRadius = CornerRadius(tileSize / 4, tileSize / 4)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Top Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 20.dp)
        ) {
            if (onBackClick != null) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Canvas(modifier = Modifier.size(24.dp)) {
                        val path = Path().apply {
                            moveTo(size.width * 0.7f, size.height * 0.2f)
                            lineTo(size.width * 0.3f, size.height * 0.5f)
                            lineTo(size.width * 0.7f, size.height * 0.8f)
                        }
                        drawPath(
                            path = path,
                            color = Color.White,
                            style = Stroke(width = 3.dp.toPx())
                        )
                    }
                }
            }

            if (title != null) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (showLogo) {
                // App Logo
                AppLogo(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

@Composable
fun AppLogo(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(72.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(36.dp)) {
            val path = Path().apply {
                val w = size.width
                val h = size.height
                moveTo(w * 0.2f, h * 0.2f)
                cubicTo(w * 0.8f, h * 0.1f, w * 0.9f, h * 0.6f, w * 0.8f, h * 0.8f)
                cubicTo(w * 0.6f, h * 0.9f, w * 0.1f, h * 0.8f, w * 0.2f, h * 0.2f)
                close()
            }
            drawPath(path = path, color = AppColors.Primary)
            drawCircle(
                color = Color.White,
                radius = size.width * 0.12f,
                center = Offset(size.width * 0.45f, size.height * 0.45f)
            )
        }
    }
}
