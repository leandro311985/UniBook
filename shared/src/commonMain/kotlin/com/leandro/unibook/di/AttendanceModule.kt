package com.leandro.unibook.di

import com.leandro.unibook.attendance.data.remote.AttendanceApiService
import com.leandro.unibook.attendance.data.repository.AttendanceRepositoryImpl
import com.leandro.unibook.attendance.domain.repository.AttendanceRepository
import com.leandro.unibook.attendance.domain.usecase.CreateAttendanceUseCase
import com.leandro.unibook.attendance.presentation.AttendanceViewModel
import com.leandro.unibook.auth.data.remote.AuthApiService
import io.ktor.client.HttpClient
import org.koin.dsl.module

val attendanceModule = module {
    single<HttpClient> {
        AuthApiService.createDefaultHttpClient(tokenStorage = get())
    }
    single { AttendanceApiService(client = get()) }
    single<AttendanceRepository> { AttendanceRepositoryImpl(apiService = get()) }
    single { CreateAttendanceUseCase(repository = get()) }
    factory { AttendanceViewModel(createAttendance = get()) }
}
