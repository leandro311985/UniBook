package com.leandro.unibook.home.data

import android.content.Context
import com.leandro.unibook.core.security.SecurePreferences

class AndroidChurchSettingsRepository(
    private val context: Context
) : ChurchSettingsRepository {

    private val prefs = SecurePreferences(context, "unibook_church_prefs")

    override fun getChurchName(): String {
        return prefs.getString(KEY_CHURCH_NAME) ?: "Igreja Central - Sede"
    }

    override fun saveChurchName(name: String) {
        prefs.putString(KEY_CHURCH_NAME, name)
    }

    override fun getChurchPhoto(): String {
        return prefs.getString(KEY_CHURCH_PHOTO) ?: ""
    }

    override fun saveChurchPhoto(photoBase64: String) {
        prefs.putString(KEY_CHURCH_PHOTO, photoBase64)
    }

    private companion object {
        const val KEY_CHURCH_NAME = "KEY_CHURCH_NAME"
        const val KEY_CHURCH_PHOTO = "KEY_CHURCH_PHOTO"
    }
}
