package com.leandro.unibook.di

import com.leandro.unibook.core.device.AndroidDeviceIdProvider
import com.leandro.unibook.core.device.DeviceIdProvider
import org.koin.dsl.module

actual val platformDeviceModule = module {
    single<DeviceIdProvider> { AndroidDeviceIdProvider(context = get()) }
}
