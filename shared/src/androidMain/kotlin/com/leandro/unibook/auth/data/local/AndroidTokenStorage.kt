package com.leandro.unibook.auth.data.local

import android.content.Context
import android.content.SharedPreferences

class AndroidTokenStorage(context: Context) : TokenStorage {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

    override fun getToken(): String? = preferences.getString(TOKEN_KEY, null)

    override fun saveToken(token: String) {
        check(preferences.edit().putString(TOKEN_KEY, token).commit()) {
            "Não foi possível salvar o token de autenticação."
        }
    }

    override fun clearToken() {
        check(preferences.edit().remove(TOKEN_KEY).commit()) {
            "Não foi possível remover o token de autenticação."
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "unibook_auth"
        const val TOKEN_KEY = "access_token"
    }
}
