package com.leandro.unibook.auth.presentation

import com.leandro.unibook.auth.domain.model.LoginSession

sealed interface LoginUiState {
    data object Idle : LoginUiState
    data object Loading : LoginUiState
    data class Success(val session: LoginSession) : LoginUiState
    data class Error(val message: String) : LoginUiState
}
