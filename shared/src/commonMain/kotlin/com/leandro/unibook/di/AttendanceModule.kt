package com.leandro.unibook.di

import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.data.repository.AttendanceRepositoryImpl
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import com.leandro.unibook.attendance.domain.usecase.CreateAttendanceUseCase
import com.leandro.unibook.attendance.presentation.AttendanceViewModel
import io.ktor.client.HttpClient
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
        }
    }
    single { AttendanceApiService(client = get()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(apiService = get()) }
    single { CreateAttendanceUseCase(repository = get()) }
    factory { AttendanceViewModel(createAttendance = get()) }
}
