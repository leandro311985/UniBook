package com.leandro.unibook.home.presentation

import com.leandro.unibook.attendance.domain.model.AttendanceItem

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val attendances: List<AttendanceItem>) : HomeUiState
    data class Error(val message: String) : HomeUiState
}
