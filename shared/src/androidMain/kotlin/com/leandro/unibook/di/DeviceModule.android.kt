package com.leandro.unibook.di

import com.leandro.unibook.core.device.AndroidDeviceIdProvider
import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.home.data.AndroidChurchSettingsRepository
import com.leandro.unibook.home.data.ChurchSettingsRepository
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { AndroidDeviceIdProvider(context = get()) }
    single<ChurchSettingsRepository> { AndroidChurchSettingsRepository(context = get()) }
}
