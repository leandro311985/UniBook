package com.leandro.unibook.core.security

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.ByteBuffer
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

class SecurePreferences(
    context: Context,
    name: String
) {
    private val preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE)
    private val secretKey by lazy(::getOrCreateSecretKey)

    init {
        migratePlaintextValues()
    }

    fun getString(key: String, defaultValue: String? = null): String? {
        val value = preferences.getString(key, null) ?: return defaultValue
        if (value.startsWith(ENCRYPTED_PREFIX)) {
            return decrypt(value.removePrefix(ENCRYPTED_PREFIX))
        }

        putString(key, value)
        return value
    }

    fun putString(key: String, value: String) {
        preferences.edit()
            .putString(key, ENCRYPTED_PREFIX + encrypt(value))
            .apply()
    }

    private fun migratePlaintextValues() {
        val plaintext = preferences.all
            .filter { (_, value) -> value is String && !value.startsWith(ENCRYPTED_PREFIX) }
        if (plaintext.isEmpty()) return

        val editor = preferences.edit()
        plaintext.forEach { (key, value) ->
            editor.putString(key, ENCRYPTED_PREFIX + encrypt(value as String))
        }
        editor.apply()
    }

    private fun encrypt(value: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey)
        val encryptedBytes = cipher.doFinal(value.toByteArray(Charsets.UTF_8))
        val combined = ByteBuffer.allocate(cipher.iv.size + encryptedBytes.size)
            .put(cipher.iv)
            .put(encryptedBytes)
            .array()
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    private fun decrypt(value: String): String {
        val combined = Base64.decode(value, Base64.NO_WRAP)
        require(combined.size > IV_LENGTH_BYTES) { "Encrypted preference value is invalid." }
        val buffer = ByteBuffer.wrap(combined)
        val iv = ByteArray(IV_LENGTH_BYTES).also(buffer::get)
        val encryptedBytes = ByteArray(buffer.remaining()).also(buffer::get)
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(TAG_LENGTH_BITS, iv))
        return cipher.doFinal(encryptedBytes).toString(Charsets.UTF_8)
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE).apply { load(null) }
        (keyStore.getKey(KEY_ALIAS, null) as? SecretKey)?.let { return it }

        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE)
        keyGenerator.init(
            KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setRandomizedEncryptionRequired(true)
                .build()
        )
        return keyGenerator.generateKey()
    }

    private companion object {
        const val ANDROID_KEY_STORE = "AndroidKeyStore"
        const val ENCRYPTED_PREFIX = "encrypted:v1:"
        const val IV_LENGTH_BYTES = 12
        const val KEY_ALIAS = "com.leandro.unibook.preferences"
        const val TAG_LENGTH_BITS = 128
        const val TRANSFORMATION = "AES/GCM/NoPadding"
    }
}
