package com.leandro.unibook.home.data

import platform.Foundation.NSUserDefaults

class IosChurchSettingsRepository : ChurchSettingsRepository {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getChurchName(): String {
        return defaults.stringForKey("KEY_CHURCH_NAME") ?: "Igreja Central - Sede"
    }

    override fun saveChurchName(name: String) {
        defaults.setObject(name, "KEY_CHURCH_NAME")
    }

    override fun getChurchPhoto(): String {
        return defaults.stringForKey("KEY_CHURCH_PHOTO") ?: ""
    }

    override fun saveChurchPhoto(photoBase64: String) {
        defaults.setObject(photoBase64, "KEY_CHURCH_PHOTO")
    }
}
