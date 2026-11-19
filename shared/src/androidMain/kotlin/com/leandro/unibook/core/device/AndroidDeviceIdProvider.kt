package com.leandro.unibook.core.device

import android.content.Context
import com.leandro.unibook.core.security.SecurePreferences
import java.util.UUID

class AndroidDeviceIdProvider(
    private val context: Context
) : DeviceIdProvider {

    override fun getDeviceId(): String {
        val prefs = SecurePreferences(context, "unibook_device_prefs")
        var deviceId = prefs.getString(KEY_DEVICE_ID)
        if (deviceId.isNullOrBlank()) {
            deviceId = UUID.randomUUID().toString()
            prefs.putString(KEY_DEVICE_ID, deviceId)
        }
        return deviceId
    }

    private companion object {
        const val KEY_DEVICE_ID = "KEY_DEVICE_ID"
    }
}
