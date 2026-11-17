package com.leandro.unibook.di

import com.leandro.unibook.attendance.data.local.AndroidAttendanceStorageRepository
import com.leandro.unibook.attendance.data.local.AttendanceStorageRepository
import com.leandro.unibook.core.device.AndroidDeviceIdProvider
import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.home.data.AndroidChurchSettingsRepository
import com.leandro.unibook.home.data.AndroidUserProfileRepository
import com.leandro.unibook.home.data.ChurchSettingsRepository
import com.leandro.unibook.home.data.UserProfileRepository
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { AndroidDeviceIdProvider(context = get()) }
    single<ChurchSettingsRepository> { AndroidChurchSettingsRepository(context = get()) }
    single<UserProfileRepository> { AndroidUserProfileRepository(context = get()) }
    single<AttendanceStorageRepository> { AndroidAttendanceStorageRepository(context = get()) }
}
