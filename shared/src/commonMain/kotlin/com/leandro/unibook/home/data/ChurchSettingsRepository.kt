package com.leandro.unibook.home.data

interface ChurchSettingsRepository {
    fun getChurchName(): String
    fun saveChurchName(name: String)
    fun getChurchPhoto(): String
    fun saveChurchPhoto(photoBase64: String)
}
