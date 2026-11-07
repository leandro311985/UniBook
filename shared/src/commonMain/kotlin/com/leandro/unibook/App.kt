package com.leandro.unibook

import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.leandro.unibook.attendance.FormScreen
import com.leandro.unibook.home.HomeScreen
import org.koin.compose.KoinContext

enum class AppScreenState {
    HOME,
    FORM
}

@Composable
@Preview
fun App() {
    KoinContext {
        var currentScreen by remember { mutableStateOf(AppScreenState.HOME) }

        MaterialTheme {
            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    AppScreenState.HOME -> HomeScreen(
                        onAttendanceClick = { currentScreen = AppScreenState.FORM }
                    )
                    AppScreenState.FORM -> FormScreen(
                        onBack = { currentScreen = AppScreenState.HOME }
                    )
                }
            }
        }
    }
}
