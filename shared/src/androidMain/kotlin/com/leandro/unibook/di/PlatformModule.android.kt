package com.leandro.unibook.di

import com.leandro.unibook.auth.data.local.AndroidTokenStorage
import com.leandro.unibook.auth.data.local.TokenStorage
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<TokenStorage> { AndroidTokenStorage(androidContext()) }
}
