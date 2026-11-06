package com.leandro.unibook.auth.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.auth.ui.CustomTextField
import com.leandro.unibook.auth.ui.DarkGeometricHeader
import com.leandro.unibook.ui.theme.AppColors

@Composable
fun SignUpScreen(
    uiState: SignUpUiState = SignUpUiState.Idle,
    onNavigateToLogin: () -> Unit,
    onSignUpClick: (firstName: String, lastName: String, email: String, pass: String) -> Unit = { _, _, _, _ -> }
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val isLoading = uiState is SignUpUiState.Loading
    val errorMessage = (uiState as? SignUpUiState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Primary)
    ) {
        // Header with back arrow & title "Criar Conta"
        DarkGeometricHeader(
            modifier = Modifier.height(180.dp),
            title = "Criar Conta",
            onBackClick = onNavigateToLogin
        )

        // White Container overlapping the header
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 130.dp)
                .clip(RoundedCornerShape(topStart = 40.dp))
                .background(AppColors.CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (!errorMessage.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFFFFEBEE))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Text(
                            text = errorMessage,
                            color = Color(0xFFC62828),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                CustomTextField(
                    label = "Nome",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Seu nome",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    label = "Sobrenome",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Seu sobrenome",
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    label = "E-mail",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "seu@email.com",
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    label = "Senha",
                    value = password,
                    onValueChange = { password = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Next
                    )
                )

                Spacer(modifier = Modifier.height(14.dp))

                CustomTextField(
                    label = "Confirmar senha",
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    placeholder = "••••••••",
                    isPassword = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Password,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (!isLoading) {
                                focusManager.clearFocus()
                                onSignUpClick(firstName, lastName, email, password)
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (!isLoading) {
                            focusManager.clearFocus()
                            onSignUpClick(firstName, lastName, email, password)
                        }
                    },
                    enabled = !isLoading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.Primary,
                        contentColor = AppColors.TextOnPrimary
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.5.dp
                        )
                    } else {
                        Text(
                            text = "Criar Conta",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Já tem uma conta? ",
                        fontSize = 14.sp,
                        color = AppColors.TextSecondary
                    )
                    Text(
                        text = "Entrar",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.ActionText,
                        modifier = Modifier.clickable { onNavigateToLogin() }
                    )
                }
            }
        }
    }
}
