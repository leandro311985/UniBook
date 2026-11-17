package com.leandro.unibook.home.data

interface UserProfileRepository {
    fun getNickname(): String
    fun getMinistry(): String
    fun saveProfile(nickname: String, ministry: String)
}
