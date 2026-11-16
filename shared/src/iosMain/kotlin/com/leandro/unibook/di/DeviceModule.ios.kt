package com.leandro.unibook.di

import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.core.device.IosDeviceIdProvider
import com.leandro.unibook.home.data.ChurchSettingsRepository
import com.leandro.unibook.home.data.IosChurchSettingsRepository
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { IosDeviceIdProvider() }
    single<ChurchSettingsRepository> { IosChurchSettingsRepository() }
}
