package com.leandro.unibook.di

import com.leandro.unibook.attendance.data.local.AttendanceStorageRepository
import com.leandro.unibook.attendance.data.local.IosAttendanceStorageRepository
import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.core.device.IosDeviceIdProvider
import com.leandro.unibook.home.data.ChurchSettingsRepository
import com.leandro.unibook.home.data.IosChurchSettingsRepository
import com.leandro.unibook.home.data.IosUserProfileRepository
import com.leandro.unibook.home.data.UserProfileRepository
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { IosDeviceIdProvider() }
    single<ChurchSettingsRepository> { IosChurchSettingsRepository() }
    single<UserProfileRepository> { IosUserProfileRepository() }
    single<AttendanceStorageRepository> { IosAttendanceStorageRepository() }
}
