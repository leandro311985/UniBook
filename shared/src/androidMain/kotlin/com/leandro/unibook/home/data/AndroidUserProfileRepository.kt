package com.leandro.unibook.home.data

import android.content.Context

class AndroidUserProfileRepository(
    context: Context
) : UserProfileRepository {

    private val prefs = context.getSharedPreferences("unibook_user_profile_prefs", Context.MODE_PRIVATE)

    override fun getNickname(): String =
        prefs.getString("KEY_USER_NICKNAME", null).orEmpty()

    override fun getMinistry(): String =
        prefs.getString("KEY_USER_MINISTRY", null).orEmpty()

    override fun saveProfile(nickname: String, ministry: String) {
        prefs.edit()
            .putString("KEY_USER_NICKNAME", nickname)
            .putString("KEY_USER_MINISTRY", ministry)
            .apply()
    }
}
