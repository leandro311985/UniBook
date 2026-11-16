package com.leandro.unibook.di

import com.leandro.unibook.attendance.data.local.AttendanceLocalDataSource
import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.data.repository.AttendanceRepositoryImpl
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import com.leandro.unibook.attendance.domain.usecase.CreateAttendanceUseCase
import com.leandro.unibook.attendance.domain.usecase.GetAttendancesUseCase
import com.leandro.unibook.attendance.domain.usecase.UpdateAttendanceUseCase
import com.leandro.unibook.attendance.presentation.AttendanceViewModel
import com.leandro.unibook.home.presentation.HomeViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val attendanceModule = module {
    single<HttpClient> {
        HttpClient {
            expectSuccess = true
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 90_000L // 90s para suportar cold-start do Render
                connectTimeoutMillis = 90_000L
                socketTimeoutMillis = 90_000L
            }
            install(HttpRequestRetry) {
                maxRetries = 3
                retryIf { _, response -> !response.status.value.let { it in 200..299 } }
                exponentialDelay()
            }
        }
    }
    single { AttendanceLocalDataSource() }
    single { AttendanceApiService(client = get(), deviceIdProvider = getOrNull()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(apiService = get(), localDataSource = get()) }
    single { CreateAttendanceUseCase(repository = get()) }
    single { UpdateAttendanceUseCase(repository = get()) }
    single { GetAttendancesUseCase(repository = get()) }
    factory { AttendanceViewModel(createAttendance = get(), updateAttendance = get()) }
    factory { HomeViewModel(getAttendances = get(), churchSettingsRepository = getOrNull()) }
}
