package com.leandro.unibook.attendance.presentation.ui

import androidx.compose.runtime.Composable

@Composable
expect fun CameraPhotoPicker(
    photoBase64: String,
    onPhotoCaptured: (base64: String) -> Unit
)
