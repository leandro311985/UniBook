package com.leandro.unibook.attendance.ui

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap

actual fun decodePhotoBitmap(imageBytes: ByteArray): ImageBitmap? =
    try {
        BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)?.asImageBitmap()
    } catch (_: IllegalArgumentException) {
        null
    }
