package com.leandro.unibook.auth.data.local

interface TokenStorage {
    fun getToken(): String?
    fun saveToken(token: String)
    fun clearToken()
}
