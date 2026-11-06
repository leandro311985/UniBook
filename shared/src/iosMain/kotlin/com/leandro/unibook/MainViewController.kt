package com.leandro.unibook

import androidx.compose.ui.window.ComposeUIViewController
import com.leandro.unibook.di.initKoin

fun MainViewController() = run {
    initKoin()
    ComposeUIViewController { App() }
}