package com.leandro.unibook.attendance

import androidx.compose.runtime.Composable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.ChurchSituation
import com.leandro.unibook.attendance.presentation.AttendanceUiState
import com.leandro.unibook.attendance.presentation.AttendanceViewModel
import org.koin.compose.viewmodel.koinViewModel

private val Navy = Color(0xFF10234E)
private val Gold = Color(0xFFE5A51B)
private val Background = Color(0xFFF7F8FC)

@Composable
fun FormScreen(onBack: () -> Unit = {}) {
    val viewModel: AttendanceViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    AttendanceScreen(
        uiState = uiState,
        onSave = viewModel::save,
        onBack = onBack
    )
}

@Composable
fun AttendanceScreen(
    uiState: AttendanceUiState = AttendanceUiState.Idle,
    onSave: (AttendanceParams) -> Unit,
    onBack: () -> Unit = {}
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    var firstVisit by remember { mutableStateOf(false) }
    var away by remember { mutableStateOf(false) }
    var member by remember { mutableStateOf(false) }

    var mainProblem by remember { mutableStateOf("") }
    var attendanceDate by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
            }
            Column {
                Text(
                    "Atendimento",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Navy
                )
                Text(
                    "Ficha de acolhimento pastoral",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // DADOS PESSOAIS
            FormSection("Dados pessoais") {
                FormInput("Nome completo", name, { name = it })
                FormInput(
                    "Telefone / Celular",
                    phone,
                    { phone = it }
                )
            }

            // SITUAÇÃO NA IGREJA
            FormSection("Situação na igreja") {
                Column {
                    FormCheck(
                        "Primeira vez",
                        firstVisit
                    ) { firstVisit = it }

                    FormCheck(
                        "Afastado(a)",
                        away
                    ) { away = it }

                    FormCheck(
                        "Membro",
                        member
                    ) { member = it }
                }
            }

            // PRINCIPAL NECESSIDADE
            FormSection("Qual é o seu maior problema?") {
                Text(
                    "Conte como podemos ajudar você.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )

                OutlinedTextField(
                    value = mainProblem,
                    onValueChange = { mainProblem = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text("Descreva sua necessidade...")
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // REGISTRO
            FormSection("Registro do atendimento") {
                FormInput(
                    "Data do atendimento",
                    attendanceDate,
                    { attendanceDate = it },
                    placeholder = "AAAA-MM-DD"
                )
            }

            Spacer(Modifier.height(20.dp))
        }

        // BOTÃO SALVAR
        Surface(
            shadowElevation = 8.dp,
            color = Color.White
        ) {
            Column {
                Button(
                    onClick = {
                        onSave(
                            AttendanceParams(
                                nome = name,
                                telefone = phone,
                                situacaoNaIgreja = buildList {
                                    if (firstVisit) add(ChurchSituation.PRIMEIRA_VEZ)
                                    if (away) add(ChurchSituation.AFASTADO)
                                    if (member) add(ChurchSituation.MEMBRO)
                                },
                                maiorProblema = mainProblem,
                                dataAtendimento = attendanceDate
                            )
                        )
                    },
                    enabled = uiState !is AttendanceUiState.Loading,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(54.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Navy
                    ),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    when (uiState) {
                        AttendanceUiState.Loading -> {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("Enviando...")
                        }
                        else -> Text(
                            "Salvar atendimento",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                when (val state = uiState) {
                    is AttendanceUiState.Error -> Text(
                        state.message,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                    AttendanceUiState.Success -> Text(
                        "Atendimento salvo com sucesso.",
                        color = Color(0xFF2E7D32),
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                    )
                    else -> Unit
                }
            }
        }
    }
}

@Composable
private fun FormSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = Navy
            )

            Spacer(Modifier.height(14.dp))

            content()
        }
    }
}

@Composable
private fun FormInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "Digite aqui"
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
    ) {
        Text(
            label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = Navy
        )

        Spacer(Modifier.height(5.dp))

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, fontSize = 14.sp)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp)
        )
    }
}

@Composable
private fun FormCheck(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(
                checkedColor = Navy
            )
        )

        Text(
            title,
            color = Navy,
            fontSize = 14.sp
        )
    }
}
