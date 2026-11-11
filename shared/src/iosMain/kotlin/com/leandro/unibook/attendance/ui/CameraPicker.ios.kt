package com.leandro.unibook.attendance.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.UIKit.UIApplication
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.UIKit.UIImagePickerController
import platform.UIKit.UIImagePickerControllerCameraCaptureMode
import platform.UIKit.UIImagePickerControllerDelegateProtocol
import platform.UIKit.UIImagePickerControllerOriginalImage
import platform.UIKit.UIImagePickerControllerSourceType
import platform.UIKit.UINavigationControllerDelegateProtocol
import platform.UIKit.UIViewController
import platform.darwin.NSObject

private val Navy = Color(0xFF10234E)

@Composable
actual fun CameraPhotoPicker(
    photoBase64: String,
    onPhotoCaptured: (base64: String) -> Unit
) {
    var pickerDelegate by remember { mutableStateOf<CameraPickerDelegate?>(null) }
    var cameraUnavailable by remember { mutableStateOf(false) }

    fun launchCamera() {
        val cameraSource = UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        if (!UIImagePickerController.isSourceTypeAvailable(cameraSource)) {
            cameraUnavailable = true
            return
        }

        val picker = UIImagePickerController()
        picker.sourceType = cameraSource
        picker.cameraCaptureMode =
            UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
        val delegate = CameraPickerDelegate(
            onPhotoCaptured = onPhotoCaptured,
            onFinished = { pickerDelegate = null },
            onPhotoCaptureFailed = { cameraUnavailable = true }
        )
        picker.delegate = delegate
        pickerDelegate = delegate

        var presenter = UIApplication.sharedApplication.keyWindow?.rootViewController
        while (presenter?.presentedViewController != null) {
            presenter = presenter.presentedViewController
        }
        if (presenter == null) {
            pickerDelegate = null
            cameraUnavailable = true
            return
        }
        presenter.presentViewController(picker, animated = true, completion = null)
        cameraUnavailable = false
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (photoBase64.isNotEmpty()) {
            Text(
                text = "Foto capturada",
                color = Navy,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = ::launchCamera,
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.size(6.dp))
                    Text("Tirar outra foto", fontSize = 12.sp)
                }
                IconButton(onClick = { onPhotoCaptured("") }) {
                    Icon(Icons.Default.Delete, contentDescription = "Remover foto", tint = Color(0xFFC62828))
                }
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFFF0F4F8))
                    .clickable(onClick = ::launchCamera)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = null, tint = Navy, modifier = Modifier.size(28.dp))
                Spacer(Modifier.size(12.dp))
                Text(
                    "Tirar foto da pessoa",
                    color = Navy,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        if (cameraUnavailable) {
            Spacer(Modifier.height(8.dp))
            Text(
                "Não foi possível acessar ou processar a foto da câmera.",
                color = Color(0xFFC62828),
                fontSize = 12.sp
            )
        }
    }
}

private class CameraPickerDelegate(
    private val onPhotoCaptured: (String) -> Unit,
    private val onFinished: () -> Unit,
    private val onPhotoCaptureFailed: () -> Unit
) : NSObject(), UIImagePickerControllerDelegateProtocol, UINavigationControllerDelegateProtocol {
    override fun imagePickerController(
        picker: UIImagePickerController,
        didFinishPickingMediaWithInfo: Map<Any?, *>
    ) {
        val image = didFinishPickingMediaWithInfo.get(UIImagePickerControllerOriginalImage) as? UIImage
        val imageData: NSData? = image?.let { UIImageJPEGRepresentation(it, 0.7) }
        if (imageData != null) {
            onPhotoCaptured(imageData.base64EncodedStringWithOptions(0u))
        } else {
            onPhotoCaptureFailed()
        }
        picker.dismissViewControllerAnimated(true) {
            onFinished()
        }
    }

    override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
        picker.dismissViewControllerAnimated(true) {
            onFinished()
        }
    }
}
