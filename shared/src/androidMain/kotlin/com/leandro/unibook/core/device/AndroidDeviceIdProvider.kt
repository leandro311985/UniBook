package com.leandro.unibook.core.device

import android.content.Context
import java.util.UUID

class AndroidDeviceIdProvider(
    private val context: Context
) : DeviceIdProvider {

    override fun getDeviceId(): String {
        val prefs = context.getSharedPreferences("unibook_device_prefs", Context.MODE_PRIVATE)
        var deviceId = prefs.getString("KEY_DEVICE_ID", null)
        if (deviceId.isNullOrBlank()) {
            deviceId = UUID.randomUUID().toString()
            prefs.edit().putString("KEY_DEVICE_ID", deviceId).apply()
        }
        return deviceId
    }
}
