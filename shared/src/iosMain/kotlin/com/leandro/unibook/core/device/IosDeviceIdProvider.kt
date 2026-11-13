package com.leandro.unibook.core.device

import platform.Foundation.NSUserDefaults
import platform.UIKit.UIDevice

class IosDeviceIdProvider : DeviceIdProvider {
    override fun getDeviceId(): String {
        val vendorId = UIDevice.currentDevice.identifierForVendor?.UUIDString
        if (!vendorId.isNullOrBlank()) return vendorId

        val defaults = NSUserDefaults.standardUserDefaults
        var savedId = defaults.stringForKey("KEY_DEVICE_ID")
        if (savedId.isNullOrBlank()) {
            savedId = platform.Foundation.NSUUID().UUIDString
            defaults.setObject(savedId, "KEY_DEVICE_ID")
        }
        return savedId
    }
}
