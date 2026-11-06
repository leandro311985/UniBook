package com.leandro.unibook.di

import com.leandro.unibook.auth.data.local.TokenStorage
import com.leandro.unibook.auth.data.remote.AuthApiService
import com.leandro.unibook.auth.data.repository.AuthRepositoryImpl
import com.leandro.unibook.auth.domain.repository.AuthRepository
import com.leandro.unibook.auth.domain.usecase.LoginUseCase
import com.leandro.unibook.auth.domain.usecase.RegisterUseCase
import com.leandro.unibook.auth.presentation.LoginViewModel
import com.leandro.unibook.auth.presentation.SignUpViewModel
import org.koin.dsl.module

val authModule = module {
    single { AuthApiService(tokenStorage = get()) }
    single<AuthRepository> {
        AuthRepositoryImpl(
            apiService = get(),
            tokenStorage = get()
        )
    }
    single { LoginUseCase(repository = get()) }
    single { RegisterUseCase(repository = get()) }
    factory { LoginViewModel(loginUseCase = get()) }
    factory { SignUpViewModel(registerUseCase = get()) }
}
