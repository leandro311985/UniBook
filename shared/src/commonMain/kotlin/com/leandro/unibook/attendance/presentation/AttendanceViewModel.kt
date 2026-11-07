package com.leandro.unibook.attendance.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceResult
import com.leandro.unibook.attendance.domain.usecase.CreateAttendanceUseCase
import kotlinx.coroutines.CancellationException
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
        if (_uiState.value is AttendanceUiState.Loading) return

        _uiState.value = AttendanceUiState.Loading
        viewModelScope.launch {
            try {
                _uiState.value = when (val result = createAttendance(params)) {
                    AttendanceResult.Success -> AttendanceUiState.Success
                    is AttendanceResult.Error -> AttendanceUiState.Error(result.message)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = AttendanceUiState.Error(
                    e.message ?: "Erro ao enviar o atendimento."
                )
            }
        }
    }
}
