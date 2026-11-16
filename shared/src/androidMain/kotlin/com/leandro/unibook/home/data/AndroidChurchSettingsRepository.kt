package com.leandro.unibook.home.data

import android.content.Context

class AndroidChurchSettingsRepository(
    private val context: Context
) : ChurchSettingsRepository {

    private val prefs = context.getSharedPreferences("unibook_church_prefs", Context.MODE_PRIVATE)

    override fun getChurchName(): String {
        return prefs.getString("KEY_CHURCH_NAME", null) ?: "Igreja Central - Sede"
    }

    override fun saveChurchName(name: String) {
        prefs.edit().putString("KEY_CHURCH_NAME", name).apply()
    }

    override fun getChurchPhoto(): String {
        return prefs.getString("KEY_CHURCH_PHOTO", null) ?: ""
    }

    override fun saveChurchPhoto(photoBase64: String) {
        prefs.edit().putString("KEY_CHURCH_PHOTO", photoBase64).apply()
    }
}
