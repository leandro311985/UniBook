package com.leandro.unibook.di

import com.leandro.unibook.core.device.DeviceIdProvider
import com.leandro.unibook.core.device.IosDeviceIdProvider
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { IosDeviceIdProvider() }
}
