package com.leandro.unibook.home.data

import android.content.Context
import com.leandro.unibook.core.security.SecurePreferences

class AndroidUserProfileRepository(
    context: Context
) : UserProfileRepository {

    private val prefs = SecurePreferences(context, "unibook_user_profile_prefs")

    override fun getNickname(): String =
        prefs.getString(KEY_USER_NICKNAME).orEmpty()

    override fun getPhone(): String =
        prefs.getString(KEY_USER_PHONE).orEmpty()

    override fun getMinistry(): String =
        prefs.getString(KEY_USER_MINISTRY).orEmpty()

    override fun saveProfile(nickname: String, phone: String, ministry: String) {
        prefs.putString(KEY_USER_NICKNAME, nickname)
        prefs.putString(KEY_USER_PHONE, phone)
        prefs.putString(KEY_USER_MINISTRY, ministry)
    }

    private companion object {
        const val KEY_USER_MINISTRY = "KEY_USER_MINISTRY"
        const val KEY_USER_NICKNAME = "KEY_USER_NICKNAME"
        const val KEY_USER_PHONE = "KEY_USER_PHONE"
    }
}
