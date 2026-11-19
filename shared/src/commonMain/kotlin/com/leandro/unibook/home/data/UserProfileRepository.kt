package com.leandro.unibook.home.data

interface UserProfileRepository {
    fun getNickname(): String
    fun getPhone(): String
    fun getMinistry(): String
    fun saveProfile(nickname: String, phone: String, ministry: String)
}
