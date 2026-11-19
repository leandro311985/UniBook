package com.leandro.unibook

import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.leandro.unibook.attendance.FormScreen
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.usecase.DeleteAttendanceUseCase
import com.leandro.unibook.home.AttendanceDetailsScreen
import com.leandro.unibook.home.HomeScreen
import org.koin.compose.koinInject
import org.koin.compose.KoinContext

enum class AppScreenState {
    HOME,
    FORM,
    DETAILS
}

@Composable
@Preview
fun App() {
    KoinContext {
        var currentScreen by remember { mutableStateOf(AppScreenState.HOME) }
        var selectedAttendance by remember { mutableStateOf<AttendanceItem?>(null) }
        val deleteAttendance: DeleteAttendanceUseCase = koinInject()

        MaterialTheme {
            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    AppScreenState.HOME -> HomeScreen(
                        onAttendanceClick = {
                            selectedAttendance = null
                            currentScreen = AppScreenState.FORM
                        },
                        onViewAttendance = { attendance ->
                            selectedAttendance = attendance
                            currentScreen = AppScreenState.DETAILS
                        },
                        onEditAttendance = { attendance ->
                            selectedAttendance = attendance
                            currentScreen = AppScreenState.FORM
                        }
                    )
                    AppScreenState.FORM -> FormScreen(
                        attendance = selectedAttendance,
                        onBack = { currentScreen = AppScreenState.HOME }
                    )
                    AppScreenState.DETAILS -> selectedAttendance?.let { attendance ->
                        AttendanceDetailsScreen(
                            attendance = attendance,
                            onBack = { currentScreen = AppScreenState.HOME },
                            onEdit = { currentScreen = AppScreenState.FORM },
                            onDelete = { deleteAttendance(attendance.id) },
                            onDeleted = { currentScreen = AppScreenState.HOME }
                        )
                    } ?: HomeScreen()
                }
            }
        }
    }
}
