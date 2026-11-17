package com.leandro.unibook.attendance

import androidx.compose.runtime.Composable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.leandro.unibook.attendance.domain.model.AttendanceParams
import com.leandro.unibook.attendance.domain.model.AttendanceItem
import com.leandro.unibook.attendance.domain.model.ChurchSituation
import com.leandro.unibook.attendance.presentation.AttendanceUiState
import com.leandro.unibook.attendance.presentation.AttendanceViewModel
import com.leandro.unibook.attendance.presentation.ui.CameraPhotoPicker
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import org.koin.compose.viewmodel.koinViewModel

private val Navy = Color(0xFF10234E)
private val Gold = Color(0xFFE5A51B)
private val Background = Color(0xFFF7F8FC)

@Composable
fun FormScreen(
    attendance: AttendanceItem? = null,
    onBack: () -> Unit = {}
) {
    val viewModel: AttendanceViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsState()

    // Reseta o ViewModel ao entrar na tela
    LaunchedEffect(attendance) {
        viewModel.resetState()
    }

    AttendanceScreen(
        attendance = attendance,
        uiState = uiState,
        onSave = { params -> viewModel.save(params, attendance?.id) },
        onBack = {
            viewModel.resetState()
            onBack()
        }
    )
}

@Composable
fun AttendanceScreen(
    attendance: AttendanceItem? = null,
    uiState: AttendanceUiState = AttendanceUiState.Idle,
    onSave: (AttendanceParams) -> Unit,
    onBack: () -> Unit = {}
) {
    var photoBase64 by remember(attendance) { mutableStateOf(attendance?.fotoBase64.orEmpty()) }
    var name by remember(attendance) { mutableStateOf(attendance?.nome.orEmpty()) }
    var address by remember(attendance) { mutableStateOf(attendance?.endereco.orEmpty()) }
    var neighborhood by remember(attendance) { mutableStateOf(attendance?.bairro.orEmpty()) }
    var phone by remember(attendance) { mutableStateOf(attendance?.telefone.orEmpty()) }
    var email by remember(attendance) { mutableStateOf(attendance?.email.orEmpty()) }

    var firstVisit by remember(attendance) {
        mutableStateOf("PRIMEIRA_VEZ" in attendance?.situacaoNaIgreja.orEmpty())
    }
    var away by remember(attendance) {
        mutableStateOf("AFASTADO" in attendance?.situacaoNaIgreja.orEmpty())
    }
    var member by remember(attendance) {
        mutableStateOf("MEMBRO" in attendance?.situacaoNaIgreja.orEmpty())
    }

    var mainProblem by remember(attendance) {
        mutableStateOf(attendance?.maiorProblema.orEmpty())
    }
    var cultsToAttend by remember(attendance) {
        mutableStateOf(attendance?.cultosAFrequentar.orEmpty())
    }
    var responsiblePerson by remember(attendance) {
        mutableStateOf(attendance?.responsavelAcompanhamento.orEmpty())
    }
    var attendanceDate by remember(attendance) {
        mutableStateOf(attendance?.dataAtendimento ?: todayDate())
    }
    var pastorApproval by remember(attendance) {
        mutableStateOf(attendance?.vistoDoPastor ?: "Pendente")
    }

    val scrollState = rememberScrollState()

    // Efeito para rolar suavemente até o topo ao receber um erro ou sucesso
    LaunchedEffect(uiState) {
        if (uiState is AttendanceUiState.Error || uiState is AttendanceUiState.Success) {
            scrollState.animateScrollTo(0)
        }
    }

    // Se o formulário foi salvo com sucesso num novo cadastro, limpa os campos para o próximo
    LaunchedEffect(uiState) {
        if (uiState is AttendanceUiState.Success && attendance == null) {
            photoBase64 = ""
            name = ""
            address = ""
            neighborhood = ""
            phone = ""
            email = ""
            firstVisit = false
            away = false
            member = false
            mainProblem = ""
            cultsToAttend = ""
            responsiblePerson = ""
            attendanceDate = todayDate()
            pastorApproval = "Pendente"
        }
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            Surface(
                shadowElevation = 4.dp,
                color = Color.White
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = Navy
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Column {
                        Text(
                            if (attendance == null) "Novo Atendimento" else "Editar Atendimento",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Navy
                        )
                        Text(
                            "Ficha de acolhimento",
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    when (val state = uiState) {
                        is AttendanceUiState.Error -> {
                            Text(
                                text = "⚠️ ${state.message}",
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        AttendanceUiState.Success -> {
                            Text(
                                text = "✅ Atendimento salvo com sucesso!",
                                color = Color(0xFF2E7D32),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        else -> Unit
                    }

                    Button(
                        onClick = {
                            onSave(
                                AttendanceParams(
                                    nome = name,
                                    endereco = address,
                                    bairro = neighborhood,
                                    telefone = phone,
                                    email = email,
                                    situacaoNaIgreja = buildList {
                                        if (firstVisit) add(ChurchSituation.PRIMEIRA_VEZ)
                                        if (away) add(ChurchSituation.AFASTADO)
                                        if (member) add(ChurchSituation.MEMBRO)
                                    },
                                    maiorProblema = mainProblem,
                                    cultosAFrequentar = cultsToAttend,
                                    responsavelAcompanhamento = responsiblePerson,
                                    dataAtendimento = attendanceDate,
                                    vistoDoPastor = pastorApproval,
                                    fotoBase64 = photoBase64
                                )
                            )
                        },
                        enabled = uiState !is AttendanceUiState.Loading,
                        modifier = Modifier
                            .fillMaxWidth()
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
                                Text("Conectando e enviando...")
                            }
                            else -> Text(
                                if (attendance == null) "Salvar atendimento" else "Salvar alterações",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(scrollState)
                .padding(16.dp)
        ) {
            // BANNER DE FEEDBACK DE ERRO OU SUCESSO NO TOPO
            when (val state = uiState) {
                is AttendanceUiState.Error -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = Color(0xFFC62828),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = state.message,
                                color = Color(0xFFC62828),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                AttendanceUiState.Success -> {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.CheckCircleOutline,
                                contentDescription = null,
                                tint = Color(0xFF2E7D32),
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Atendimento salvo com sucesso!",
                                color = Color(0xFF2E7D32),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
                else -> Unit
            }

            // CAPTURA DE FOTO
            FormSection("Foto da Pessoa (Câmera)") {
                CameraPhotoPicker(
                    photoBase64 = photoBase64,
                    onPhotoCaptured = { photoBase64 = it }
                )
            }

            // DADOS PESSOAIS
            FormSection("Dados pessoais") {
                FormInput("Nome completo *", name, { name = it }, placeholder = "Maria Silva")
                FormInput("Endereço", address, { address = it }, placeholder = "Rua das Flores, 123")
                FormInput("Bairro", neighborhood, { neighborhood = it }, placeholder = "Centro")
                FormInput("Telefone / Celular *", phone, { phone = it }, placeholder = "11999999999")
                FormInput("E-mail", email, { email = it }, placeholder = "maria@example.com")
            }

            // SITUAÇÃO NA IGREJA
            FormSection("Situação na igreja") {
                Column {
                    FormCheck("Primeira vez", firstVisit) { firstVisit = it }
                    FormCheck("Afastado(a)", away) { away = it }
                    FormCheck("Membro", member) { member = it }
                }
            }

            // PRINCIPAL NECESSIDADE
            FormSection("Qual é o seu maior problema / necessidade?") {
                Text(
                    "Descreva como a igreja pode orar e acompanhar.",
                    fontSize = 13.sp,
                    color = Color.Gray
                )
                Spacer(Modifier.height(8.dp))

                OutlinedTextField(
                    value = mainProblem,
                    onValueChange = { mainProblem = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    placeholder = {
                        Text("Descreva a necessidade ou pedido de oração...")
                    },
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // ACOMPANHAMENTO PASTORAL
            FormSection("Acompanhamento pastoral") {
                FormInput("Cultos a frequentar", cultsToAttend, { cultsToAttend = it }, placeholder = "Ex: Domingo, Quarta")
                FormInput("Responsável pelo acompanhamento", responsiblePerson, { responsiblePerson = it }, placeholder = "Nome do servo / obreiro")

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Data do atendimento *", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Navy)
                    Text(
                        "Hoje",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Gold,
                        modifier = Modifier
                            .clickable { attendanceDate = todayDate() }
                            .padding(4.dp)
                    )
                }
                Spacer(Modifier.height(4.dp))
                OutlinedTextField(
                    value = attendanceDate,
                    onValueChange = { attendanceDate = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("AAAA-MM-DD", fontSize = 14.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(Modifier.height(12.dp))
                FormInput("Visto do Pastor", pastorApproval, { pastorApproval = it }, placeholder = "Pendente")
            }

            Spacer(Modifier.height(20.dp))
        }
    }
}

private fun todayDate(): String =
    Clock.System.todayIn(TimeZone.currentSystemDefault()).toString()

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
