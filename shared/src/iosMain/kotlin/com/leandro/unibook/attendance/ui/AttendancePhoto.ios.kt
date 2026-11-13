package com.leandro.unibook.attendance.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import org.jetbrains.skia.Image

actual fun decodePhotoBitmap(imageBytes: ByteArray): ImageBitmap? =
    try {
        Image.makeFromEncoded(imageBytes).toComposeImageBitmap()
    } catch (_: Exception) {
        null
    }
