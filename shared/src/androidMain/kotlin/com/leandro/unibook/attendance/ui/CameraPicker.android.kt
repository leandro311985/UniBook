package com.leandro.unibook.attendance.presentation.ui

import android.graphics.Bitmap
import android.util.Base64
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.presentation.ui.decodePhotoBitmap
import com.leandro.unibook.attendance.presentation.ui.decodePhotoBytes
import java.io.ByteArrayOutputStream

@Composable
actual fun CameraPhotoPicker(
    photoBase64: String,
    onPhotoCaptured: (base64: String) -> Unit
) {
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val base64 = bitmap.toScaledBase64String()
            onPhotoCaptured(base64)
        }
    }

    val imageBitmap = remember(photoBase64) {
        if (photoBase64.isNotBlank()) {
            val bytes = decodePhotoBytes(photoBase64)
            if (bytes != null) {
                decodePhotoBitmap(bytes)
            } else {
                null
            }
        } else {
            null
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (imageBitmap != null) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color(0xFF10234E), CircleShape)
            ) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = "Foto capturada",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { cameraLauncher.launch(null) },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("Tirar outra foto", fontSize = 12.sp)
                }

                IconButton(
                    onClick = { onPhotoCaptured("") }
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover", tint = Color(0xFFC62828))
                }
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F4F8))
                    .clickable { cameraLauncher.launch(null) },
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.CameraAlt,
                        contentDescription = "Capturar foto",
                        tint = Color(0xFF10234E),
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Tirar Foto da Pessoa (Câmera)",
                        color = Color(0xFF10234E),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

private fun Bitmap.toScaledBase64String(): String {
    val maxDimension = 500
    val scaledBitmap = if (width > maxDimension || height > maxDimension) {
        val aspect = width.toFloat() / height.toFloat()
        val (newW, newH) = if (width >= height) {
            maxDimension to (maxDimension / aspect).toInt()
        } else {
            (maxDimension * aspect).toInt() to maxDimension
        }
        Bitmap.createScaledBitmap(this, newW, newH, true)
    } else {
        this
    }

    val outputStream = ByteArrayOutputStream()
    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 65, outputStream)
    val byteArray = outputStream.toByteArray()
    return Base64.encodeToString(byteArray, Base64.NO_WRAP)
}
