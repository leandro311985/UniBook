package com.leandro.unibook.auth.data.local

import platform.Foundation.NSUserDefaults

class IosTokenStorage : TokenStorage {
    override fun getToken(): String? = NSUserDefaults.standardUserDefaults.stringForKey(TOKEN_KEY)

    override fun saveToken(token: String) {
        NSUserDefaults.standardUserDefaults.setObject(token, forKey = TOKEN_KEY)
    }

    override fun clearToken() {
        NSUserDefaults.standardUserDefaults.removeObjectForKey(TOKEN_KEY)
    }

    private companion object {
        const val TOKEN_KEY = "unibook_access_token"
    }
}
