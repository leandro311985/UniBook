package com.leandro.unibook.attendance.presentation.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.leandro.unibook.ui.theme.AppColors
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun AttendancePhoto(
    photoBase64: String,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(photoBase64) {
        if (photoBase64.isNotBlank()) {
            decodePhotoBytes(photoBase64)?.let(::decodePhotoBitmap)
        } else {
            null
        }
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Foto do atendimento",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modifier.background(Color(0xFFF0F4F8)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Face24DpIcon,
                contentDescription = "Sem foto",
                tint = AppColors.Primary.copy(alpha = 0.6f),
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

internal fun decodePhotoBytes(photoBase64: String): ByteArray? {
    val value = photoBase64.trim()
    val payload = if (value.startsWith("data:", ignoreCase = true)) {
        val separator = value.indexOf(',')
        if (separator < 0 || !value.substring(0, separator).contains(";base64", ignoreCase = true)) {
            return null
        }
        value.substring(separator + 1)
    } else {
        value
    }.filterNot { it.isWhitespace() }

    return try {
        if ('-' in payload || '_' in payload) {
            Base64.UrlSafe.decode(payload)
        } else {
            Base64.decode(payload)
        }
    } catch (_: Exception) {
        null
    }
}

expect fun decodePhotoBitmap(imageBytes: ByteArray): ImageBitmap?
