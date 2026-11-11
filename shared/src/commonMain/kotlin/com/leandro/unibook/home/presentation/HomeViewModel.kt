package com.leandro.unibook.home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandro.unibook.attendance.domain.usecase.GetAttendancesUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getAttendances: GetAttendancesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        observeAttendances()
        loadAttendances()
    }

    private fun observeAttendances() {
        viewModelScope.launch {
            getAttendances.observe()
                .catch { e -> _uiState.value = HomeUiState.Error(e.message ?: "Erro ao carregar dados locais.") }
                .collect { list ->
                    _uiState.value = HomeUiState.Success(list)
                }
        }
    }

    fun loadAttendances() {
        viewModelScope.launch {
            try {
                getAttendances()
            } catch (e: Exception) {
                // Erro de rede tratado graciosamente exibindo dados locais salvos
            }
        }
    }
}
