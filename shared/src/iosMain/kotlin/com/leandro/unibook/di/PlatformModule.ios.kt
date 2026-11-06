package com.leandro.unibook.di

import com.leandro.unibook.auth.data.local.IosTokenStorage
import com.leandro.unibook.auth.data.local.TokenStorage
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single<TokenStorage> { IosTokenStorage() }
}
