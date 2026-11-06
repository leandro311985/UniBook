package com.leandro.unibook

import androidx.compose.animation.Crossfade
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import com.leandro.unibook.auth.presentation.SignUpUiState
import com.leandro.unibook.auth.presentation.SignUpViewModel
import com.leandro.unibook.auth.presentation.LoginViewModel
import com.leandro.unibook.auth.presentation.LoginScreen
import com.leandro.unibook.auth.presentation.LoginUiState
import com.leandro.unibook.auth.presentation.SignUpScreen
import com.leandro.unibook.home.HomeScreen
import org.koin.compose.KoinContext
import org.koin.compose.viewmodel.koinViewModel

enum class AuthScreenState {
    LOGIN,
    SIGN_UP,
    HOME
}

@Composable
@Preview
fun App() {
    KoinContext {
        var currentScreen by remember { mutableStateOf(AuthScreenState.LOGIN) }
        var successMessage by remember { mutableStateOf<String?>(null) }

        MaterialTheme {
            Crossfade(targetState = currentScreen) { screen ->
                when (screen) {
                    AuthScreenState.LOGIN -> {
                        val viewModel: LoginViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsState()

                        LaunchedEffect(uiState) {
                            val state = uiState
                            if (state is LoginUiState.Success) {
                                successMessage = null
                                viewModel.resetState()
                                currentScreen = AuthScreenState.HOME
                            }
                        }

                        LoginScreen(
                            uiState = uiState,
                            successMessage = successMessage,
                            onNavigateToSignUp = {
                                successMessage = null
                                viewModel.resetState()
                                currentScreen = AuthScreenState.SIGN_UP
                            },
                            onLoginClick = { email, pass ->
                                viewModel.onLogin(email, pass)
                            }
                        )
                    }
                    AuthScreenState.SIGN_UP -> {
                        val viewModel: SignUpViewModel = koinViewModel()
                        val uiState by viewModel.uiState.collectAsState()

                        LaunchedEffect(uiState) {
                            val state = uiState
                            if (state is SignUpUiState.Success) {
                                successMessage = state.message
                                viewModel.resetState()
                                currentScreen = AuthScreenState.LOGIN
                            }
                        }

                        SignUpScreen(
                            uiState = uiState,
                            onNavigateToLogin = {
                                viewModel.resetState()
                                currentScreen = AuthScreenState.LOGIN
                            },
                            onSignUpClick = { firstName, lastName, email, pass ->
                                viewModel.onRegister(
                                    nome = "$firstName $lastName".trim(),
                                    email = email,
                                    senha = pass,
                                    confirmSenha = pass
                                )
                            }
                        )
                    }
                    AuthScreenState.HOME -> HomeScreen()
                }
            }
        }
    }
}
