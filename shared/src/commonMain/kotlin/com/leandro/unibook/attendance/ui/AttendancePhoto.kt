package com.leandro.unibook.attendance.ui

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@Composable
fun AttendancePhoto(
    photoBase64: String,
    modifier: Modifier = Modifier
) {
    val imageBitmap = remember(photoBase64) {
        decodePhotoBytes(photoBase64)?.let(::decodePhotoBitmap)
    }

    if (imageBitmap != null) {
        Image(
            bitmap = imageBitmap,
            contentDescription = "Foto do atendimento",
            modifier = modifier,
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalEncodingApi::class)
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
    }.filterNot(Char::isWhitespace)

    return try {
        if ('-' in payload || '_' in payload) {
            Base64.UrlSafe.decode(payload)
        } else {
            Base64.Default.decode(payload)
        }
    } catch (_: IllegalArgumentException) {
        null
    }
}

expect fun decodePhotoBitmap(imageBytes: ByteArray): ImageBitmap?
