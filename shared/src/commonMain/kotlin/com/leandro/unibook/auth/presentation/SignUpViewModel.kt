package com.leandro.unibook.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.leandro.unibook.auth.domain.model.AuthResult
import com.leandro.unibook.auth.domain.model.RegisterParams
import com.leandro.unibook.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignUpViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<SignUpUiState>(SignUpUiState.Idle)
    val uiState: StateFlow<SignUpUiState> = _uiState.asStateFlow()

    fun onRegister(nome: String, email: String, senha: String, confirmSenha: String) {
        if (senha != confirmSenha) {
            _uiState.value = SignUpUiState.Error("As senhas não coincidem.")
            return
        }

        viewModelScope.launch {
            _uiState.value = SignUpUiState.Loading

            val params = RegisterParams(
                nome = nome,
                email = email,
                senha = senha
            )

            when (val result = registerUseCase(params)) {
                is AuthResult.Success -> {
                    _uiState.value = SignUpUiState.Success(result.data)
                }
                is AuthResult.Error -> {
                    _uiState.value = SignUpUiState.Error(result.message)
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = SignUpUiState.Idle
    }
}
