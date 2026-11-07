package com.leandro.unibook.attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.usecase.CreateAttendanceUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AttendanceViewModel(
    private val createAttendance: CreateAttendanceUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow<AttendanceUiState>(AttendanceUiState.Idle)
    val uiState: StateFlow<AttendanceUiState> = _uiState.asStateFlow()

    fun save(params: AttendanceParams) {
        viewModelScope.launch {
            _uiState.value = AttendanceUiState.Loading
            _uiState.value = when (val result = createAttendance(params)) {
                AttendanceResult.Success -> AttendanceUiState.Success
                is AttendanceResult.Error -> AttendanceUiState.Error(result.message)
            }
        }
    }
}
