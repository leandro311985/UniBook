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
        decodeBase64(photoBase64)?.let(::decodePhotoBitmap)
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
private fun decodeBase64(photoBase64: String): ByteArray? =
    try {
        Base64.Default.decode(photoBase64)
    } catch (_: IllegalArgumentException) {
        null
    }

expect fun decodePhotoBitmap(imageBytes: ByteArray): ImageBitmap?
