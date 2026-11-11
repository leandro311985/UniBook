package com.leandro.unibook.attendance.presentation

sealed interface AttendanceUiState {
    data object Idle : AttendanceUiState
    data object Loading : AttendanceUiState
    data object Success : AttendanceUiState
    data class Error(val message: String) : AttendanceUiState
}
