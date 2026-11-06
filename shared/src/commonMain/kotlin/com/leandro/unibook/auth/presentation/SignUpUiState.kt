package com.leandro.unibook.auth.presentation

sealed interface SignUpUiState {
    data object Idle : SignUpUiState
    data object Loading : SignUpUiState
    data class Success(val message: String = "Cadastro realizado com sucesso!") : SignUpUiState
    data class Error(val message: String) : SignUpUiState
}
