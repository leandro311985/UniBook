package com.leandro.unibook.home.data

import platform.Foundation.NSUserDefaults

class IosUserProfileRepository : UserProfileRepository {

    private val defaults = NSUserDefaults.standardUserDefaults

    override fun getNickname(): String =
        defaults.stringForKey("KEY_USER_NICKNAME") ?: ""

    override fun getPhone(): String =
        defaults.stringForKey("KEY_USER_PHONE") ?: ""

    override fun getMinistry(): String =
        defaults.stringForKey("KEY_USER_MINISTRY") ?: ""

    override fun saveProfile(nickname: String, phone: String, ministry: String) {
        defaults.setObject(nickname, "KEY_USER_NICKNAME")
        defaults.setObject(phone, "KEY_USER_PHONE")
        defaults.setObject(ministry, "KEY_USER_MINISTRY")
    }
}
